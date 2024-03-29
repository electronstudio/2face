package uk.co.electronstudio.gopher

import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.TextColor.ANSI.*
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog
import com.googlecode.lanterna.input.KeyType.*
import com.googlecode.lanterna.input.MouseAction
import com.googlecode.lanterna.input.MouseActionType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.ExtendedTerminal
import com.googlecode.lanterna.terminal.MouseCaptureMode
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import groovy.util.ConfigObject
import groovy.util.ConfigSlurper
import java.awt.event.MouseEvent.MOUSE_CLICKED
import java.io.File
import java.net.URI
import java.net.URLDecoder
import java.util.*
import java.util.logging.Level
import javax.accessibility.AccessibleAction.CLICK
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class TextUI(startURL: URI, config: ConfigObject) {


    val keys = config["keys"] as Map<String, String>
    val colors = config["colors"] as Map<String, ArrayList<TextColor>>


    val links: ArrayList<Item> = arrayListOf()
    val lineToLinkMapping = mutableMapOf<Int, Item>()

    @Volatile
    var selectedLink = -1

    val history = Stack<URI>()


    //var url = "gopher://gemini.circumlunar.space"
    //var url = "gemini://gemini.circumlunar.space/"

    //var url = "gopher://gemini.circumlunar.space:70/0/docs/faq.txt"

    @Volatile
    var page: Document = GeminiDocument(
        """
        # Welcome to 2face, a Gemini/Gopher client written in Kotlin.
    """.trimIndent(), URI("")
    )


    var scroll = 0
    val terminalFactory = DefaultTerminalFactory()
    val terminal = terminalFactory.setForceTextTerminal(config["forceTextMode"] as Boolean).createTerminal()
    val screen = TerminalScreen(terminal)
    val textGraphics = screen.newTextGraphics()

    val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)

    var terminalSize = screen.terminalSize
    var TEXT_ROWS = terminalSize.rows - 3

    init {
        //println(colors["urlLine"]?.get(2))
        screen.startScreen()
        screen.cursorPosition = null

        val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)

        screen.terminal.addResizeListener { _, newSize ->
            run {
                terminalSize = newSize
                TEXT_ROWS = terminalSize.rows - 3
                screen.doResizeIfNecessary()
                redraw()
            }
        }



        if (terminal is SwingTerminalFrame) {
            terminal.defaultCloseOperation = SwingTerminalFrame.EXIT_ON_CLOSE
        }

        if (terminal is ExtendedTerminal) {
            log.info("EXTENDED TERMINAL")
            terminal.setTitle("2face")
            terminal.setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE)
        }


        loadPage(startURL)


        while (true) {
            screen
            redraw()
            val key = screen.readInput()
            when (key.keyType) {
                ArrowDown -> {
                    scroll++
                }
                ArrowUp -> {
                    scroll--
                }
                PageDown -> {
                    scroll += TEXT_ROWS
                }
                PageUp -> {
                    scroll -= TEXT_ROWS
                }
                ArrowLeft -> {
                    if (history.isNotEmpty()) {
                        selectedLink = -1
                        loadPage(history.pop())
                    }
                }


                Character -> {
                    val shortcut = keys["shortcuts"]!!.indexOf(key.character)
                    if (key.character == ' ') {
                        scroll += TEXT_ROWS
                    } else if (key.character == keys["url"]?.first()) {
                        editURL()
                    } else if (shortcut > -1) {
                        val link = links.getOrNull(shortcut)
                        selectedLink = shortcut
                        openLink(link)
                    }
                }
                Escape -> {}
                Backspace -> {}
                ArrowRight -> {}
                Insert -> {}
                Delete -> {}
                Home -> {}
                End -> {}
                Tab -> {}
                ReverseTab -> {}
                Enter -> {}
                F1 -> {}
                F2 -> {}
                F3 -> {}
                F4 -> {}
                F5 -> {}
                F6 -> {}
                F7 -> {}
                F8 -> {}
                F9 -> {}
                F10 -> {}
                F11 -> {}
                F12 -> {}
                F13 -> {}
                F14 -> {}
                F15 -> {}
                F16 -> {}
                F17 -> {}
                F18 -> {}
                F19 -> {}
                Unknown -> {}
                CursorLocation -> {

                }
                MouseEvent -> {
                    val mouseAction = key as MouseAction
                    when (mouseAction.actionType!!) {
                        MouseActionType.SCROLL_UP -> {
                            scroll--
                        }
                        MouseActionType.SCROLL_DOWN -> {
                            scroll++
                        }
                        MouseActionType.CLICK_DOWN -> {
                            val link = lineToLinkMapping[mouseAction.position.row]
                            selectedLink = links.indexOf(link)
                            openLink(link)
                        }
                        MouseActionType.CLICK_RELEASE -> {}

                        MouseActionType.DRAG -> {}

                        MouseActionType.MOVE -> {
                            //System.exit(0)
                            val link = lineToLinkMapping[mouseAction.position.row]
                            selectedLink = links.indexOf(link)
                        }
                    }
                }
                EOF -> {}
            }

        }
    }

    private fun openLink(link: Item?) {
        link?.url?.let {
            redraw()
            history.push(page.url)
            loadPage(it)
        }
    }

    fun editURL() {
        val input = TextInputDialog.showDialog(textGUI, "URL", "", page.url.toString()) // FIXME check decode needed
        input?.let { loadPage(URI(it)) }
    }

    @Volatile
    var futureResponse: Response? = null

    fun loadPage(url: URI) {
        log.info("Loading page: $url")


        thread {
            try {
                val response = requestDocument(url)
                if (response is GopherDocument) {
                    futureResponse = response
                } else if (response is TextDocument) {
                    futureResponse = response
                } else if (response is Document) {
                    futureResponse = response
                } else if (response is ErrorResponse) {
                    futureResponse = response
                } else {
                    futureResponse = Document(url)
                }
            } catch (e: Exception) {
                log.log(Level.SEVERE, "Error loading page", e)
                val d = Document(url)
                d.items.add(Item(e.toString(), '3'))

                futureResponse = d
            }

            //  redraw()
        }
        thread {
            val spinner = listOf("/", "/", "-", "-", "\\", "\\", "|", "|")
            var i = 0
            while (futureResponse == null) {
                put(
                    spinner[i++ % spinner.size],
                    terminalSize.columns - 1,
                    terminalSize.rows - 1,
                    colors["statusLine"]!![1],
                    colors["statusLine"]!![0]
                )
                screen.refresh();
                Thread.sleep(66)
            }
            futureResponse.let {
                if (it is Document) {
                    page = it

                } else if (it is ErrorResponse) {
                    page = Document(url)
                    page.items.add(Item(it.toString(), '3'))
                }
                futureResponse = null
                selectedLink = -1
                scroll = 0
                redraw()
            }
        }
    }

    @Synchronized
    fun put(
        txt: String, column: Int = 0, row: Int = 0,
        foreground: TextColor = WHITE, background: TextColor = DEFAULT,
        modifier: SGR? = null
        //invert: Boolean = false
    ) {
        //     if (invert) {
        //         textGraphics.foregroundColor = background
        //         textGraphics.backgroundColor = foreground
        //     } else {
        textGraphics.foregroundColor = foreground
        textGraphics.backgroundColor = background
        //     }
        textGraphics.drawLine(column, row, terminalSize.columns - 1, row, ' ')

        if (modifier != null) {
            textGraphics.putString(column, row, txt, modifier)
        } else {
            textGraphics.putString(column, row, txt)
        }
    }

    fun put(
        txt: String, column: Int = 0, row: Int = 0,
        colors: ArrayList<TextColor>
    ) {
        put(txt, column, row, colors[0], colors[1])
    }


//    @Synchronized
//    fun redraw() {
//
//        if (page.items.isEmpty()) {
//            return
//        }
//
//        scroll = scroll.coerceAtMost(page.items.size - TEXT_ROWS).coerceAtLeast(0)
//
//        screen.clear()
//        links.clear()
//        put("0", 0, 0, BLACK, CYAN)
//        //put(URLDecoder.decode(page.url,"UTF-8"), 2, 0, CYAN, DEFAULT) FIXME check decode needed
//        put(URLDecoder.decode(page.url.toString(), "UTF-8"), 2, 0, CYAN, DEFAULT)
//
//        var shortcut = 0
//
//        val items = page.items.subList(scroll, page.items.size - 1).iterator()
//
//        var i = 0
//        while (i < terminalSize.rows - 3 && items.hasNext()) {
//            //val line = page.items.getOrNull(i + scroll)
//
//            val item = items.next()
//
//            if (item.text.length > 80) {
//
//            }
//            var position_in_item = 0
//            while (position_in_item <= item.text.length) {
//
//                val url = item.url
//                val text = item.text.substring(position_in_item, Math.min(position_in_item + 80, item.text.length))
//                position_in_item += 80
//                //  }
//
//                //   line?.let {
//                //println(line.url)
//                if (url != null) {
//                    var invert = false
//                    if (shortcut < shortcuts.length) {
//                        invert = (shortcut == selectedLink)
//                        put(shortcuts[shortcut].toString(), 0, i + 1, BLACK, GREEN)
//                        shortcut++
//                        links.add(item)
//                    }
//                    put(text + if (item.gopherType == '1') "/" else "", 2, i + 1, GREEN, DEFAULT, invert)
//                } else {
//                    if (item.text.startsWith('#')) {
//                        put(text, 0, i + 1, RED)
//                    } else {
//                        put(text, 0, i + 1)
//                    }
//                }
//                i++
//            }
//
//        }
//
//        val pages = Math.ceil(page.items.size.toDouble() / TEXT_ROWS).toInt()
//        val current = ((scroll.toDouble() / TEXT_ROWS) + 1).toInt()
//
//        if (scroll + TEXT_ROWS < page.items.size) {
//            val s = "[$current/$pages]"
//            put(s, 0, terminalSize.rows - 1, CYAN, DEFAULT)
//            put("SPACE", s.length + 1, terminalSize.rows - 1, BLACK, CYAN)
//        } else {
//            put("[$pages/$pages]", 0, terminalSize.rows - 1, CYAN, DEFAULT)
//        }
//
//
//        screen.refresh();
//    }


    @Synchronized
    fun redraw() {

        if (page.items.isEmpty()) {
            return
        }


        val lines = page.splitItemsIntoLines(screen.terminalSize.columns)
        scroll = scroll.coerceAtMost(lines.size - TEXT_ROWS - 1).coerceAtLeast(0)

        screen.clear()
        links.clear()
        lineToLinkMapping.clear()
        put(keys["url"]!!, 0, 0, colors["selected"]!![0], colors["selected"]!![1])
        //put(URLDecoder.decode(page.url,"UTF-8"), 2, 0, CYAN, DEFAULT) FIXME check decode needed
        put(
            URLDecoder.decode(page.url.toString(), "UTF-8"),
            2,
            0,
            colors["urlLine"]!![0],
            colors["urlLine"]!![1],
            SGR.ITALIC
        )

        var shortcut = 0




        for (i in 0..terminalSize.rows - 3) {
            val line = lines.getOrNull(i + scroll)


            line?.let {
                val row = i + 1
                if (line.url != null) {
                    var selected = false
                    if (shortcut < keys["shortcuts"]!!.length) {
                        selected = (shortcut == selectedLink)
                        put(
                            keys["shortcuts"]!![shortcut].toString(),
                            0,
                            row,
                            colors["selected"]!![0],
                            colors["selected"]!![1]
                        )
                        shortcut++
                        links.add(line)
                        lineToLinkMapping[row] = line
                    }
                    put(
                        line.text + if (line.gopherType == '1') "/" else "", 2, row, colors[if (selected) {
                            "selected"
                        } else {
                            "link"
                        }]!!
                    )
                } else {
                    val color = if (line.preformat) {
                        colors["preformat"]!![0]
                    } else {
                        when {

                            line.text.startsWith("###") -> colors["heading3"]!![0]
                            line.text.startsWith("##") -> colors["heading2"]!![0]
                            line.text.startsWith('#') -> colors["heading1"]!![0]
                            line.text.startsWith('>') -> colors["quote"]!![0]
                            else -> colors["text"]!![0]
                        }
                    }
                    val color1 =
                        if (line.preformat) {
                            colors["preformat"]!![1]
                        } else {
                            when {
                                line.text.startsWith("###") -> colors["heading3"]!![1]
                                line.text.startsWith("##") -> colors["heading2"]!![1]
                                line.text.startsWith('#') -> colors["heading1"]!![1]
                                line.text.startsWith('>') -> colors["quote"]!![1]
                                else -> colors["text"]!![1]
                            }
                        }
                    val modifier = when {
                        line.text.startsWith('#') && !line.preformat-> SGR.BOLD
                        line.text.startsWith('>') && !line.preformat-> SGR.ITALIC
                        else -> null
                    }
                    put(line.text, 0, row, color, color1, modifier)

                }
            }
        }

        val pages = Math.ceil(lines.size.toDouble() / TEXT_ROWS).toInt()
        val current = ((scroll.toDouble() / TEXT_ROWS) + 1).toInt()

        if (scroll + TEXT_ROWS < lines.size - 1) {
            val s = "[$current/$pages]"
            put(s, 0, terminalSize.rows - 1, colors["statusLine"]!![0], colors["statusLine"]!![1])
            put("SPACE", s.length + 1, terminalSize.rows - 1, colors["selected"]!![0], colors["selected"]!![1])
            put(" ", s.length + 6, terminalSize.rows - 1, colors["statusLine"]!![0], colors["statusLine"]!![1])

        } else {
            put("[$pages/$pages]", 0, terminalSize.rows - 1, colors["statusLine"]!![0], colors["statusLine"]!![1])
        }


        screen.refresh();
    }
}
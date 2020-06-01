package uk.co.electronstudio.gopher

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.TextColor.ANSI.*
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog
import com.googlecode.lanterna.input.KeyType.*
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.net.URI
import java.net.URLDecoder
import java.util.*
import kotlin.concurrent.thread


class TextUI(startURL: URI) {

    val shortcuts = "123456789abcdefghijklmnopqrstuvwxyz"
    val links: ArrayList<Item> = arrayListOf()

    @Volatile
    var selectedLink = -1

    val history = Stack<URI>()


    //var url = "gopher://gemini.circumlunar.space"
    //var url = "gemini://gemini.circumlunar.space/"

    //var url = "gopher://gemini.circumlunar.space:70/0/docs/faq.txt"

    @Volatile
    var page: Document = Document(URI(""))


    var scroll = 0
    val terminalFactory = DefaultTerminalFactory()
    val screen = terminalFactory.createScreen()
    val textGraphics = screen.newTextGraphics()

    val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)

    var terminalSize = screen.terminalSize
    var TEXT_ROWS = terminalSize.rows - 3

    init {

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


        loadPage(startURL)


        while (true) {
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
                    val shortcut = shortcuts.indexOf(key.character)
                    if (key.character == ' ') {
                        scroll += TEXT_ROWS
                    } else if (key.character == '0') {
                        editURL()
                    } else if (shortcut > -1) {
                        val link = links.getOrNull(shortcut)


                        link?.url?.let {
                            selectedLink = shortcut
                            redraw()
                            history.push(page.url)
                            loadPage(it)
                        }
                    }
                }
                Escape -> TODO()
                Backspace -> TODO()
                ArrowRight -> TODO()
                Insert -> TODO()
                Delete -> TODO()
                Home -> TODO()
                End -> TODO()
                Tab -> TODO()
                ReverseTab -> TODO()
                Enter -> TODO()
                F1 -> TODO()
                F2 -> TODO()
                F3 -> TODO()
                F4 -> TODO()
                F5 -> TODO()
                F6 -> TODO()
                F7 -> TODO()
                F8 -> TODO()
                F9 -> TODO()
                F10 -> TODO()
                F11 -> TODO()
                F12 -> TODO()
                F13 -> TODO()
                F14 -> TODO()
                F15 -> TODO()
                F16 -> TODO()
                F17 -> TODO()
                F18 -> TODO()
                F19 -> TODO()
                Unknown -> TODO()
                CursorLocation -> TODO()
                MouseEvent -> TODO()
                EOF -> TODO()
            }

        }
    }

    fun editURL() {
        val input = TextInputDialog.showDialog(textGUI, "URL", "", page.url.toString()) // FIXME check decode needed
        input?.let { loadPage(URI(it)) }
    }

    @Volatile
    var futureResponse: Response? = null

    fun loadPage(url: URI) {
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
                put(spinner[i++ % spinner.size], terminalSize.columns - 1, terminalSize.rows - 1, BLACK, CYAN)
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
                redraw()
            }
        }
    }

    @Synchronized
    fun put(
        txt: String, column: Int = 0, row: Int = 0,
        foreground: TextColor = WHITE, background: TextColor = DEFAULT, invert: Boolean = false
    ) {
        if (invert) {
            textGraphics.foregroundColor = background
            textGraphics.backgroundColor = foreground
        } else {
            textGraphics.foregroundColor = foreground
            textGraphics.backgroundColor = background
        }
        textGraphics.putString(column, row, txt)
    }


    @Synchronized
    fun redraw() {

        scroll = scroll.coerceAtMost(page.items.size - TEXT_ROWS).coerceAtLeast(0)

        screen.clear()
        links.clear()
        put("0", 0, 0, BLACK, CYAN)
        //put(URLDecoder.decode(page.url,"UTF-8"), 2, 0, CYAN, DEFAULT) FIXME check decode needed
        put(URLDecoder.decode(page.url.toString(),"UTF-8"), 2, 0, CYAN, DEFAULT)

        var shortcut = 0

        for (i in 0..terminalSize.rows - 3) {
            val line = page.items.getOrNull(i + scroll)


            line?.let {
                //println(line.url)
                if (line.url != null) {
                    var invert = false
                    if (shortcut < shortcuts.length) {
                        invert = (shortcut == selectedLink)
                        put(shortcuts[shortcut].toString(), 0, i + 1, BLACK, GREEN)
                        shortcut++
                        links.add(line)
                    }
                    put(line.text + if (line.gopherType == '1') "/" else "", 2, i + 1, GREEN, DEFAULT, invert)
                } else {
                    if (line.text.startsWith('#')) {
                        put(line.text, 0, i + 1, RED)
                    } else {
                        put(line.text, 0, i + 1)
                    }
                }
            }
        }

        val pages = Math.ceil(page.items.size.toDouble() / TEXT_ROWS).toInt()
        val current = ((scroll.toDouble() / TEXT_ROWS) + 1).toInt()

        if (scroll + TEXT_ROWS < page.items.size) {
            val s = "[$current/$pages]"
            put(s, 0, terminalSize.rows - 1, CYAN, DEFAULT)
            put("SPACE", s.length + 1, terminalSize.rows - 1, BLACK, CYAN)
        } else {
            put("[$pages/$pages]", 0, terminalSize.rows - 1, CYAN, DEFAULT)
        }


        screen.refresh();
    }
}
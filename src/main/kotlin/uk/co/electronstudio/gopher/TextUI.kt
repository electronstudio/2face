package uk.co.electronstudio.gopher

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.TextColor.ANSI.*
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.dialogs.MessageDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import java.util.*
import kotlin.collections.ArrayList


class TextUI() {

    val shortcuts="123456789abcdefghijklmnopqrstuvwxyz"
    val links: ArrayList<Item> = arrayListOf()

    val history = Stack<String>()


    //var url = "gopher://gemini.circumlunar.space"
    //var url = "gemini://gemini.circumlunar.space/"

    //var url = "gopher://gemini.circumlunar.space:70/0/docs/faq.txt"

    lateinit var page: Document


          //  = requestDocument(url) //gopher.getURI(URI(url))




        //document.lines()




    var scroll = 0
    val terminalFactory = DefaultTerminalFactory()
    val screen = terminalFactory.createScreen()
    val textGraphics = screen.newTextGraphics()

    val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)

    val terminalSize = screen.terminalSize
    val TEXT_ROWS = terminalSize.rows-3

    init {

        screen.startScreen()
        screen.cursorPosition = null

        val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)


//        MessageDialog.showMessageDialog(
//            textGUI,
//            "MessageBox",
//            "This is a message box",
//            MessageDialogButton.OK
//        )
//
//


        loadPage("gopher://gemini.circumlunar.spaced/")






//        println("page is ${page.javaClass}")
//        println("first line is ${items[0]}")
//        println("first display is ${items[0].display}")

        //System.exit(0)




        while (true) {
            redraw()
            val key = screen.readInput()
            println(key.keyType)
            when(key.keyType) {
                KeyType.ArrowDown -> {
                    scroll++
                }
                KeyType.ArrowUp -> {
                    scroll--
                }
                KeyType.PageDown ->{
                    scroll += TEXT_ROWS
                }
                KeyType.PageUp ->{
                    scroll -= TEXT_ROWS
                }
                KeyType.ArrowLeft -> {
                    if(history.isNotEmpty()) {
                        loadPage(history.pop())
                    }
                }


                KeyType.Character -> {
                    val shortcut = shortcuts.indexOf(key.character)
                    if (key.character == ' ') {
                        scroll += TEXT_ROWS
                    } else if (shortcut > -1) {
                        val link = links.getOrNull(shortcut)
                        println(link)
                        link?.url?.let {
                            history.push(page.url)
                            loadPage(it)
                        }
                    }
                }
            }
            scroll = scroll.coerceAtLeast(0).coerceAtMost(page.items.size-TEXT_ROWS)
        }
    }

    fun loadPage(url: String){
        println("LOADPAGE $url")
        val response = requestDocument(url)
        println("RESPONSE IS $response")
        if (response is GopherDocument){
            page = response
        }
        else if (response is TextDocument){
            page = response
        }
        else{
            page = Document()
            MessageDialog.showMessageDialog(
                textGUI,
                "Error",
                response.toString(),
                MessageDialogButton.OK
            )
        }
    }

    fun put(txt: String, column: Int=0, row: Int=0, foreground: TextColor=WHITE, background: TextColor=BLACK){
        textGraphics.foregroundColor = foreground
        textGraphics.backgroundColor = background
        textGraphics.putString(column, row, txt)
    }




    fun redraw(){

        screen.clear()

        links.clear()


        put("0",0, 0, BLACK, CYAN)


        put(page.url,0, 0, CYAN, BLACK)

        var shortcut = 0


        for (i in 0..terminalSize.rows-3){
            val line = page.items.getOrNull(i+scroll)
            line?.let {
                if(line.url != null){
                    if(shortcut < shortcuts.length) {
                        put(shortcuts[shortcut++].toString(), 0, i + 1, BLACK, GREEN)
                        links.add(line)
                    }
                    put(line.text, 2, i + 1, GREEN, BLACK)
                }else {
                    if(line.text.startsWith('#')){
                        put(line.text, 0, i + 1, RED)
                    }else {
                        put(line.text, 0, i + 1)
                    }
                }
            }
        }

        val pages = Math.ceil(page.items.size.toDouble() / TEXT_ROWS).toInt()
        val current = (scroll.toDouble() / TEXT_ROWS) + 1


        put("SPACE", 10, terminalSize.rows-1, BLACK, CYAN)

        put("[$current/$pages]", 0, terminalSize.rows-1, CYAN, BLACK)


        screen.refresh();
    }
}
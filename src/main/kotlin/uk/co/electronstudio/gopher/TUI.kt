package uk.co.electronstudio.gopher

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.SimpleTheme
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.dialogs.MessageDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.io.IOException
import java.util.*


class TUI{
    init {

        val terminalFactory = DefaultTerminalFactory()
        var screen: Screen? = null

        try {
            screen = terminalFactory.createScreen()
            screen.startScreen()

            val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)


            MessageDialog.showMessageDialog(
                textGUI,
                "MessageBox",
                "This is a message box",
                MessageDialogButton.OK
            )

            while(true){}



            /*
            When our call has returned, the window is closed and no longer visible. The screen still contains the last
            state the TextGUI left it in, so we can easily add and display another window without any flickering. In
            this case, we want to shut down the whole thing and return to the ordinary prompt. We just need to stop the
            underlying Screen for this, the TextGUI system does not require any additional disassembly.
             */
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }



    }
}
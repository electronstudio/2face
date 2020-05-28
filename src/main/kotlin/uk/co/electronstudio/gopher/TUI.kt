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


//    val uri = URI("gopher://gemini.circumlunar.space")
//    println(uri.path)
//    val d = gopher.get(uri)
//    println(d)

        val terminalFactory = DefaultTerminalFactory()
        var screen: Screen? = null


        try {
            /*
            The DefaultTerminalFactory class doesn't provide any helper method for creating a Text GUI, you'll need to
             get a Screen like we did in the previous tutorial and start it so it puts the terminal in private mode.
             */
            screen = terminalFactory.createScreen()


            //screen.minimumSize = TerminalSize(80,500)
            screen.startScreen()

            /*
            There are a couple of different constructors to MultiWindowTextGUI, we are going to go with the defaults for
            most of these values. The one thing to consider is threading; with the default options, lanterna will use
            the calling thread for all UI operations which mean that you are basically letting the calling thread block
            until the GUI is shut down. There is a separate TextGUIThread implementaiton you can use if you'd like
            Lanterna to create a dedicated UI thread and not lock the caller. Just like with AWT and Swing, you should
            be scheduling any kind of UI operation to always run on the UI thread but lanterna tries to be best-effort
            if you attempt to mutate the GUI from another thread. Another default setting that will be applied is that
            the background of the GUI will be solid blue.
             */
            val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)
            //textGUI.setVirtualScreenEnabled(true)

            /*
            Creating a new window is relatively uncomplicated, you can optionally supply a title for the window
             */
            val window: Window = BasicWindow()
            window.setHints(listOf(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS))



            /*
            The window has no content initially, you need to call setComponent to populate it with something. In this
            case, and quite often in fact, you'll want to use more than one component so we'll create a composite
            'Panel' component that can hold multiple sub-components. This is where we decide what the layout manager
            should be.
             */
            val contentPanel = Panel(LinearLayout())



            val theme = SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)

            window.theme = theme


            /*
            One of the most basic components is the Label, which simply displays a static text. In the example below,
            we use the layout data field attached to each component to give the layout manager extra hints about how it
            should be placed. Obviously the layout data has to be created from the same layout manager as the container
            is using, otherwise it will be ignored.
             */
//            val title = Label("This is a label that spans two columns")
//            title.setLayoutData(
//                GridLayout.createLayoutData(
//                    GridLayout.Alignment.BEGINNING,  // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
//                    GridLayout.Alignment.BEGINNING,  // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
//                    true,  // Give the component extra horizontal space if available
//                    false,  // Give the component extra vertical space if available
//                    2,  // Horizontal span
//                    1
//                )
//            ) // Vertical span
//            contentPanel.addComponent(title)

            /*
            Since the grid has two columns, we can do something like this to add components when we don't need to
            customize them any further.
             */
            //contentPanel.addComponent(Label("Text Box (aligned)"))

            val urlBox = TextBox()

            urlBox.theme = SimpleTheme(TextColor.ANSI.CYAN, TextColor.ANSI.BLACK)

            contentPanel.addComponent(urlBox)

            val txtBox = Label("1 sdfsdaf\nsdafsdaf\nsadfsdaf\nasfda\nasdfsdfsdaf\n" +
                    "sdafsdaf\n" +
                    "sadfsdaf\n" +
                    "asfda\n" +
                    "asdfsdfsdaf\n" +
                    "sdafsdaf\n" +
                    "sadfsdaf\n" +
                    "asfda\n" +
                    "2 asdfsdfsdaf\n" +
                    "sdafsdaf\n" +
                    "sadfsdaf\n" +
                    "asfda\n" +
                    "asdfsdfsdaf\n" +
                    "sdafsdaf\n" +
                    "sadfsdaf\n" +
                    "asfda\n" +
                    "3 asdfsdfsdaf\n" +
                    "sdafsdaf\n" +
                    "sadfsdaf\n" +
                    "asfda\n" +
                    "asdfsdfsdaf\n" +
                    "sdafsdaf\n" +
                    "sadfsdaf\n" +
                    "asfda\n" +
                    "4 asdf")



            contentPanel.addComponent(txtBox)




//            contentPanel.addComponent(
//                TextBox()
//                    .setLayoutData(
//                        GridLayout.createLayoutData(
//                            GridLayout.Alignment.BEGINNING,
//                            GridLayout.Alignment.CENTER
//                        )
//                    )
//            )

            /*
            Here is an example of customizing the regular text box component so it masks the content and can work for
            password input.
             */

            contentPanel.addComponent(Label("Password Box (right aligned)"))
            contentPanel.addComponent(
                TextBox()
                    .setMask('*')
                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER))
            )

            /*
            While we are not going to demonstrate all components here, here is an example of combo-boxes, one that is
            read-only and one that is editable.
             */contentPanel.addComponent(Label("Read-only Combo Box (forced size)"))
            val timezonesAsStrings: List<String> =
                ArrayList(Arrays.asList(*TimeZone.getAvailableIDs()))
            val readOnlyComboBox = ComboBox(timezonesAsStrings)
            readOnlyComboBox.isReadOnly = true
            readOnlyComboBox.preferredSize = TerminalSize(20, 1)
            contentPanel.addComponent(readOnlyComboBox)
            contentPanel.addComponent(Label("Editable Combo Box (filled)"))
            contentPanel.addComponent(
                ComboBox("Item #1", "Item #2", "Item #3", "Item #4")
                    .setReadOnly(false)
                    .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1))
            )

            /*
            Some user interactions, like buttons, work by registering callback methods. In this example here, we're
            using one of the pre-defined dialogs when the button is triggered.
             */contentPanel.addComponent(Label("Button (centered)"))
            contentPanel.addComponent(
                Button(
                    "Button"
                ) {
                    MessageDialog.showMessageDialog(
                        textGUI,
                        "MessageBox",
                        "This is a message box",
                        MessageDialogButton.OK
                    )
                }.setLayoutData(
                    GridLayout.createLayoutData(
                        GridLayout.Alignment.CENTER,
                        GridLayout.Alignment.CENTER
                    )
                )
            )

            /*
            Close off with an empty row and a separator, then a button to close the window
             */contentPanel.addComponent(
                EmptySpace()
                    .setLayoutData(
                        GridLayout.createHorizontallyFilledLayoutData(2)
                    )
            )
            contentPanel.addComponent(
                Separator(Direction.HORIZONTAL)
                    .setLayoutData(
                        GridLayout.createHorizontallyFilledLayoutData(2)
                    )
            )
            contentPanel.addComponent(
                Button("Close", window::close).setLayoutData(
                    GridLayout.createHorizontallyEndAlignedLayoutData(2)
                )
            )

        window.setComponent(contentPanel)


            println("ROWS: ${screen.terminal.terminalSize.rows}")

         //   screen.scrollLines(0,10, -50)

            /*
            Now the window is created and fully populated. As discussed above regarding the threading model, we have the
            option to fire off the GUI here and then later on decide when we want to stop it. In order for this to work,
            you need a dedicated UI thread to run all the GUI operations, usually done by passing in a
            SeparateTextGUIThread object when you create the TextGUI. In this tutorial, we are using the conceptually
            simpler SameTextGUIThread, which essentially hijacks the caller thread and uses it as the GUI thread until
            some stop condition is met. The absolutely simplest way to do this is to simply ask lanterna to display the
            window and wait for it to be closed. This will initiate the event loop and make the GUI functional. In the
            "Close" button above, we tied a call to the close() method on the Window object when the button is
            triggered, this will then break the even loop and our call finally returns.
             */textGUI.addWindowAndWait(window)


            println("ROWS: ${txtBox.size.rows}")

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
@file:JvmName("Main")

package uk.co.electronstudio.gopher


import uk.co.electronstudio.gopher.protocols.Gemini
import uk.co.electronstudio.gopher.protocols.Gopher
import java.awt.Desktop
import java.net.URI
import java.net.URISyntaxException


fun main(args: Array<String>) {

  //  TUI()
    TextUI()
//
//    JFrame.setDefaultLookAndFeelDecorated(true)
//
//    SwingUtilities.invokeLater {
//
//        JFrame.setDefaultLookAndFeelDecorated(true)
//        JDialog.setDefaultLookAndFeelDecorated(true)



     //   UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");
//        UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel")
//        val w = SwingGUI()
//        w.setVisible(true)
  //  }
}


val gopher = Gopher()
val gemini = Gemini()

fun requestDocument(url: String): Response {
    val uri = URI(url)
    when (uri.scheme) {
        "gopher" -> {
            return gopher.getURI(uri)
        }
        "gemini" -> {
            return gemini.getURI(uri)
        }
        "http", "https" -> {
            println("OPEN $url")
            val desktop = Desktop.getDesktop()
            try {
                desktop.browse(uri)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return Document(url)
        }
        else -> {
            return Error("Don't know how to handle ${uri.scheme}")
        }
    }
}

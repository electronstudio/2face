@file:JvmName("Main")

package uk.co.electronstudio.gopher


import uk.co.electronstudio.gopher.TextUI
import uk.co.electronstudio.gopher.protocols.Gemini
import uk.co.electronstudio.gopher.protocols.Gopher
import java.net.URI


fun main(args: Array<String>) {
 //   val gopher = Gopher()
//    val uri = URI("gopher://gemini.circumlunar.space")
//    println(uri.path)
//    val d = gopher.get(uri)
//    println(d)

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
        else -> {
            return Error("Don't know how to handle ${uri.scheme}")
        }
    }
}

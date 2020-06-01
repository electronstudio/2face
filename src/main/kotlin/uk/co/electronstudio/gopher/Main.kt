@file:JvmName("Main")

package uk.co.electronstudio.gopher


import java.awt.Desktop

import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import uk.co.electronstudio.gopher.protocols.Gemini
import uk.co.electronstudio.gopher.protocols.Gopher
import java.net.URI
import java.net.URISyntaxException


fun main(args: Array<String>) {


    val parser = ArgumentParsers.newFor("2face").build()
        .description("Browser for gopher/gemini")
    parser.addArgument("url")
        .nargs("?")
        .help("Gopher or Gemini URL")


    // "gemini://gemini.circumlunar.space/"
    // "gemini://gemini.conman.org/"

    try {
        val res: Namespace = parser.parseArgs(args)
        val url = res.getString("url") ?:"gemini://gemini.conman.org/"
        println("url is $url")
        TextUI(URI(url))
    } catch (e: ArgumentParserException) {
        parser.handleError(e)
    }

  //  TUI()

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

fun requestDocument(uri: URI): Response {

    when (uri.scheme) {
        "gopher" -> {
            return gopher.getURI(uri)
        }
        "gemini" -> {
            return gemini.getURI(uri)
        }
        "http", "https" -> {
            println("OPEN $uri")
            val desktop = Desktop.getDesktop()
            try {
                desktop.browse(uri)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return Document(uri)
        }
       // "file" -> {}//TODO
        else -> {
            return ErrorResponse("Don't know how to handle ${uri.scheme}")
        }
    }
}

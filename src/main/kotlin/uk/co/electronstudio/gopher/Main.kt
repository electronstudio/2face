@file:JvmName("Main")

package uk.co.electronstudio.gopher


import java.awt.Desktop

import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
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

    try {
        val res: Namespace = parser.parseArgs(args)
        val url = res.getString("url") ?: "gopher://gopher.floodgap.com/"
        println("url is $url")
        TextUI(url)
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
       // "file" -> {}//TODO
        else -> {
            return Error("Don't know how to handle ${uri.scheme}")
        }
    }
}

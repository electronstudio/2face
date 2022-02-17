@file:JvmName("Main")

package uk.co.electronstudio.gopher


import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import uk.co.electronstudio.gopher.protocols.Gemini
import uk.co.electronstudio.gopher.protocols.Gopher
import java.awt.Desktop
import java.net.URI
import java.net.URISyntaxException
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

val LOG_LEVEL = Level.ALL
internal val log = Logger.getLogger("2face")

fun main(args: Array<String>) {

    val root: Logger = Logger.getLogger("")
    val handlers: Array<Handler> = root.getHandlers()
    for (h in handlers) {
        h.setLevel(LOG_LEVEL)
    }
    log.level = LOG_LEVEL

    val parser = ArgumentParsers.newFor("2face").build()
        .description("Browser for gopher/gemini")
    parser.addArgument("url")
        .nargs("?")
        .help("Gopher or Gemini URL")


    val default = "gemini://gemini.circumlunar.space/"
    //val default = "gemini://gemini.conman.org/"

    try {
        val res: Namespace = parser.parseArgs(args)
        val url = res.getString("url") ?: default
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
            log.fine("OPEN $uri")
            val desktop = Desktop.getDesktop()
            try {
                desktop.browse(uri)
            } catch (e: URISyntaxException) {
                log.log(Level.SEVERE, "Error opening $uri", e)
            }
            return Document(uri)
        }
       // "file" -> {}//TODO
        else -> {
            return ErrorResponse("Don't know how to handle ${uri.scheme}")
        }
    }
}

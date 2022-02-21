package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.log
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.logging.Level


    fun openWebBrowser(url: URI) {
        val myOS = System.getProperty("os.name").lowercase(Locale.getDefault())
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                val desktop = Desktop.getDesktop()
                desktop.browse(url)
            } else {
                val runtime = Runtime.getRuntime()
                if (myOS.contains("mac")) {
                    runtime.exec("open $url")
                } else {
                    runtime.exec("xdg-open $url")
                }
            }
        } catch (e: Exception) {
            log.log(Level.SEVERE, "Could not open browser", e)
        }
    }


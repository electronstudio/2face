package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.URI
import java.net.URLDecoder
import java.util.logging.Level

const val DEBUG_SLOW_NETWORK = false

class Gopher : Protocol() {
    private fun get(server: String, selector: String = "", port: Int = DEFAULT_PORT_GOPHER): String {
        if (DEBUG_SLOW_NETWORK) Thread.sleep(1000)
        val socket = Socket(server, port)
        if (DEBUG_SLOW_NETWORK) Thread.sleep(1000)
        socket.use {
            val out = PrintWriter(it.getOutputStream())
            out.use {
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                input.use {
                    val s = URLDecoder.decode(selector, "UTF-8")
                    out.print("$s\r\n")
                    out.flush()
                    val data = input.readText()
                    if (DEBUG_SLOW_NETWORK) Thread.sleep(1000)
                    return data
                }
            }
        }
    }

    private fun get(uri: URI): String {
        return get(uri.host, uri.path.drop(3), if (uri.port > -1) uri.port else DEFAULT_PORT_GOPHER)
    }

    override fun getURI(uri: URI): Response {
//        if (uri.scheme != "gopher") {
//            return GopherError("Don't know how to handle ${uri.scheme}")
//        }
        log.fine("Gopher loading: ${uri.toString()}")
        try {
            val txt = get(uri)
            if (uri.path.length < 2 || uri.path[1] == '1') {
                return GopherDocument(txt, uri)
            } else if (uri.path[1] == '0') {
                return TextDocument(txt, uri)
            } else {
                return ErrorResponse("Unknown gopher item type")
            }
        } catch (e: Exception) {
            log.log(Level.SEVERE, "Error loading gopher item", e)
            return ErrorResponse(e.toString())
        }
    }
}
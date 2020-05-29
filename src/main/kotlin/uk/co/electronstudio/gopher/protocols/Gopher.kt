package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.DEFAULT_PORT_GOPHER
import uk.co.electronstudio.gopher.Error
import uk.co.electronstudio.gopher.GopherDocument
import uk.co.electronstudio.gopher.Response
import uk.co.electronstudio.gopher.TextDocument
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.URI

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
                    out.print("$selector\r\n")
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
          try {
        val txt = get(uri)
        if (uri.path.length < 2 || uri.path[1] == '1') {
            println("its a gopher")
            return GopherDocument(txt, uri.toString())
        } else if (uri.path[1] == '0') {
            println("its a text")
            return TextDocument(txt, uri.toString())
        } else {
            return Error("Unknown gopher item type")
        }
          } catch (e: Exception) {
              return Error(e.toString())
          }
    }
}
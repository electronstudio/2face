package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.DEFAULT_PORT_GEMINI
import uk.co.electronstudio.gopher.GeminiDocument
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URI
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class Gemini : Protocol() {
    override fun getURI(uri: URI): GeminiDocument {
        try {
            val sslsocketfactory = SSLSocketFactory.getDefault()
            val socket = sslsocketfactory
                .createSocket(uri.host, DEFAULT_PORT_GEMINI) as SSLSocket

            socket.use {
                val out = PrintWriter(it.getOutputStream())
                out.use {
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                    input.use {
                        out.print("${uri.toString()}\r\n")
                        out.flush()
                        val data = input.readText()
                        println(data)
                        val data2 = input.readText()
                        println(data2)
                        return GeminiDocument(data)
                    }
                }
            }
        } catch (e: Exception) {
            return GeminiDocument(e.toString())
        }
    }
}

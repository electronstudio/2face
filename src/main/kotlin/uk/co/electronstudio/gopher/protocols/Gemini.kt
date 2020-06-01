package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URI
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class Gemini : Protocol() {
    override fun getURI(uri: URI): Response {
        try {

            System.setProperty("javax.net.debug","all")
            val protocols = arrayOf("TLSv1")
            val cipher_suites = arrayOf("TLS_AES_128_GCM_SHA256")

            val sslsocketfactory = SSLSocketFactory.getDefault()



            val socket = sslsocketfactory
                .createSocket(uri.host, DEFAULT_PORT_GEMINI) as SSLSocket

            println(socket.sslParameters.endpointIdentificationAlgorithm)
            println(socket.handshakeApplicationProtocol)
            println(socket.sslParameters.protocols.joinToString { it })
            println(socket.sslParameters.applicationProtocols.joinToString { it })
            println(socket.applicationProtocol)
            socket.addHandshakeCompletedListener({println("HANDSHAKE DONE"+it)})


//
//            socket.enabledCipherSuites.forEach { println(it) }
//            socket.enabledProtocols.forEach{println(it)}
           // socket.setEnabledProtocols(protocols)
//            socket.setEnabledCipherSuites(cipher_suites)

            socket.use {
                val out = PrintWriter(it.getOutputStream())
                out.use {
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                    input.use {
                        println("requesting: $uri")
                        out.print("${uri}\r\n")
                        //out.print("gemini.conman.org/\n\n\n\n")
                        out.flush()
                        println("socket isconnected ${socket.isConnected}")
                        //while(!input.ready()){}
                      //  val c = input.read()
                       // println("READ CHAR $c")
                        val status = input.readLine()
                        println("STATUS LINE: $status")
                        println("socket isconnected ${socket.isConnected}")
                        val code = status[0]
                        val code2 = status[1]
                        val meta = if (status.length>3) {status.substring(3)} else ""
                        when(code){
                            '1' -> {TODO()}
                            '2' -> {
                                println("success, mime type $meta")
                                val data = input.readText()
                                if(meta.contains("text/gemini")){
                                    return GeminiDocument(data, uri)
                                }else{
                                    return TextDocument(data, uri)
                                }

                            }
                            '3' -> {TODO()}
                            '4','5' -> {return ErrorResponse(meta)}
                            '6' -> {TODO()}
                            else -> {return ErrorResponse(meta)}
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ErrorResponse(e.toString())
        }
    }
}

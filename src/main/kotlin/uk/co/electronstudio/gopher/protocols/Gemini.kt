package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URI
import java.security.cert.X509Certificate
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.*

const val TLSv1_2 = "TLSv1.2"
const val TLSv1_3 = "TLSv1.3"

class Gemini : Protocol() {
    private val SSL_PROTOCOLS = arrayOf(TLSv1_3, TLSv1_2)

    private val sslSocketFactory = createInsecureSSLSocketFactory()


    fun createInsecureSSLSocketFactory(): SSLSocketFactory {
        //System.setProperty("javax.net.debug","all")
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }

                override fun checkClientTrusted(
                    certs: Array<X509Certificate>, authType: String
                ) {
                }

                override fun checkServerTrusted(
                    certs: Array<X509Certificate>, authType: String
                ) {
                    log.fine("checkServerTrusted ")
                    //log.fine(certs.joinToString { it.toString() })
                    //log.fine("SIG: " + certs[0].signature)
                }
            }
        )


        val sslContext = SSLContext.getInstance(TLSv1_3)
        sslContext.init(null, trustAllCerts, null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        return sslContext.socketFactory
    }

    override fun getURI(uri: URI): Response {
        log.fine("Gemini getURI "+uri)
        try {
            val socket = sslSocketFactory.createSocket(
                uri.host,
                if (uri.port > 0) uri.port else DEFAULT_PORT_GEMINI
            ) as SSLSocket

            socket.enabledProtocols = SSL_PROTOCOLS

            socket.addHandshakeCompletedListener {
                log.fine("HANDSHAKE DONE " + it.session.protocol)
                if (it.session.protocol != TLSv1_3) {
                    log.warning("WARNING: connected to server using TLSv1.2")
                }
            }

            socket.startHandshake()

            socket.use {
                val out = PrintWriter(it.getOutputStream())
                out.use {
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                    input.use {
                        log.fine("requesting uri: $uri")
                        out.print("${uri}\r\n")
                        out.flush()


                        //System.exit(0)
                        //while(!input.ready()){}
                        //  val c = input.read()
                        // println("READ CHAR $c")
                        val status = input.readLine()
                        log.fine("STATUS LINE: $status")

                        val code = status[0]
                        val code2 = status[1]
                        val meta = if (status.length > 3) {
                            status.substring(3)
                        } else ""
                        when (code) {
                            '1' -> {
                                TODO()
                            }
                            '2' -> {
                                println("success, mime type $meta")
                                val data = input.readText()
                                if (meta.contains("text/gemini")) {
                                    return GeminiDocument(data, uri)
                                } else {
                                    return TextDocument(data, uri)
                                }

                            }
                            '3' -> {
                                TODO()
                            }
                            '4', '5' -> {
                                return ErrorResponse(status)
                            }
                            '6' -> {
                                TODO()
                            }
                            else -> {
                                return ErrorResponse(status)
                            }
                        }

                    }
                }
            }
        } catch (e: Exception) {
            log.log(Level.SEVERE, "ERROR LOADING $uri", e)
            return ErrorResponse(e.toString())
        }
    }
}

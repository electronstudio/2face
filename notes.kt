val cipher_suites = arrayOf("TLS_AES_128_GCM_SHA256")




//            val crtFile = File("server.crt")
//            val certificate: Certificate =
//                CertificateFactory.getInstance("X.509").generateCertificate(FileInputStream(crtFile))
//// Or if the crt-file is packaged into a jar file:
//// CertificateFactory.getInstance("X.509").generateCertificate(this.class.getClassLoader().getResourceAsStream("server.crt"));
//
//
//// Or if the crt-file is packaged into a jar file:
//// CertificateFactory.getInstance("X.509").generateCertificate(this.class.getClassLoader().getResourceAsStream("server.crt"));
//            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//            keyStore.load(null, null)
//            keyStore.setCertificateEntry("server", certificate)
//
//            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//            trustManagerFactory.init(keyStore)
//
//            val sslContext = SSLContext.getInstance("TLS")
//            sslContext.init(null, trustManagerFactory.trustManagers, null)
//
//
//            val sslsocketfactory = sslContext.socketFactory
println(socket.sslParameters.endpointIdentificationAlgorithm)
println(socket.handshakeApplicationProtocol)
println(socket.sslParameters.protocols.joinToString { it })
println(socket.sslParameters.applicationProtocols.joinToString { it })
println(socket.applicationProtocol)


//            println("pre hand")
//            try{
//                socket.startHandshake()
//            }catch (e:Exception){
//                println("handshake failed")
//
//                println(socket.session.peerCertificates.joinToString { it.toString() })
//            }
//            println("psot hand")

//
//            socket.enabledCipherSuites.forEach { println(it) }
//            socket.enabledProtocols.forEach{println(it)}
// socket.setEnabledProtocols(protocols)
//            socket.setEnabledCipherSuites(cipher_suites)
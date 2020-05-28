package uk.co.electronstudio.gopher.protocols

import uk.co.electronstudio.gopher.Response
import java.net.URI

abstract class Protocol {
    abstract fun getURI(uri: URI): Response
}
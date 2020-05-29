package uk.co.electronstudio.gopher

import java.net.URI
import java.net.URLEncoder

val DEFAULT_PORT_GOPHER = 70
val DEFAULT_PORT_GEMINI = 1965

abstract class Response {}

open class Document(open val url:String = "") : Response() {
    val items: ArrayList<Item> = arrayListOf()
}

class GeminiDocument(data: String) : Document() {
}


class GopherDocument(txt: String, override val url: String) : Document() {
    init {
        txt.lines().forEach {
            if (it.length>3) items.add(Item.fromGopherLine(it))
        }
    }
}



class Item(val text: String, val type: Char, val url: String? = null) {



    companion object {
        fun fromGopherLine(line: String): Item {
            val s = line.drop(1).split('\t')
            val type = line[0]
            val display = s[0]
            val selector = s[1].trim()
            val hostName = s[2].trim()
            val port = s[3].trim().toInt()
            //println("type $type selector $selector")
            if(type=='0' || type=='1') {
                return Item(display, type,"gopher://${hostName}:${port}/${type}${URLEncoder.encode(selector,"UTF-8")}")
            }else if(type=='h' && selector.startsWith("URL:", true)){
                return Item(display, type, selector.drop(4))
            }else{
                return Item(display, type)
            }
        }
    }
}


class TextDocument(txt: String, override val url: String) : Document() {
    init {
        txt.lines().forEach { items.add(Item(it,'i')) }
    }
}

class Error(val txt: String) : Response() {
    override fun toString(): String {
        return txt
    }
}


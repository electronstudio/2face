package uk.co.electronstudio.gopher

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



class Item(val text: String, val url: String? = null) {
    companion object {
        fun fromGopherLine(line: String): Item {
            val s = line.drop(1).split('\t', limit = 4)
            val type = line[0].toString()
            val display = s[0].trim()
            val selector = s[1].trim()
            val hostName = s[2].trim()
            val port = s[3].trim().toInt()
            if(type=="0" || type=="1") {
                return Item(display, "gopher://${hostName}:${port}/${type}/${selector}")
            }else if(type=="h" && selector.startsWith("URL:", true)){
                return Item(display, selector.drop(4))
            }else{
                return Item(display)
            }
        }
    }
}


class TextDocument(txt: String, override val url: String) : Document() {
    init {
        txt.lines().forEach { items.add(Item(it)) }
    }
}

class Error(val txt: String) : Response() {
    override fun toString(): String {
        return txt
    }
}


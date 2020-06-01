package uk.co.electronstudio.gopher

import java.net.URI
import java.net.URLEncoder

val DEFAULT_PORT_GOPHER = 70
val DEFAULT_PORT_GEMINI = 1965

abstract class Response {}

open class Document(val url: URI) : Response() {
    val items: ArrayList<Item> = arrayListOf()
}



class GeminiDocument(txt: String, url: URI) : Document(url) {
    val regex = Regex("=>\\s*((\\S+)\\s*(.*))?")
    init {
        txt.lines().forEach {
            val item = createItem(it)
            item?.let {items.add(it)}
        }
    }
    var preformat = false

    fun createItem(line: String): Item? {
        val r = regex.find(line)
        if (r!=null){
            val url = this.url.resolve(r.groupValues[2])
            val txt = r.groupValues[3]
            //println("TXT $txt URL $url")

            return Item(if(txt=="") url.toString() else txt, url = url)
        }else if (line.startsWith("```")){
            preformat = !preformat
            return null
        }
        else{
            return Item(line)
        }
    }
}

class GopherDocument(txt: String,  url: URI) : Document(url) {
    init {
        txt.lines().forEach {
            if (it.length>3) items.add(createItem(it))
        }
    }

    fun createItem(line: String): Item {
        val s = line.drop(1).split('\t')
        val type = line[0]
        val display = s[0]
        val selector = s[1].trim()
        val hostName = s[2].trim()
        val port = s[3].trim().toInt()
        //println("type $type selector $selector")
        if(type=='0' || type=='1') {
            return Item(display, type,URI("gopher://${hostName}:${port}/${type}${URLEncoder.encode(selector,"UTF-8")}"))
        }else if(type=='h' && selector.startsWith("URL:", true)){
            return Item(display, type, URI(selector.drop(4)))
        }else{
            return Item(display, type)
        }
    }
}



class Item(val text: String, val gopherType: Char = '0', val url: URI? = null) {



    companion object {


    }
}


class TextDocument(txt: String,  url: URI) : Document(url) {
    init {
        txt.lines().forEach { items.add(Item(it,'i')) }
    }
}

class ErrorResponse(val txt: String) : Response() {
    override fun toString(): String {
        return txt
    }
}


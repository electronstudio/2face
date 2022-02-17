package uk.co.electronstudio.gopher

import java.net.URI
import java.net.URLEncoder
import java.util.logging.Level

val DEFAULT_PORT_GOPHER = 70
val DEFAULT_PORT_GEMINI = 1965

abstract class Response {}

open class Document(val url: URI) : Response() {
    val items: ArrayList<Item> = arrayListOf()

    fun splitItemsIntoLines(lineWidth: Int): ArrayList<Item> {
        val lines = arrayListOf<Item>()

        items.forEach {
            val words = it.text.split(' ')
            var sb = StringBuilder(words[0])
            var spaceLeft = lineWidth - words[0].length
            for (word in words.drop(1)) {
                val len = word.length
                if (len + 1 > spaceLeft) {
                    lines.add(Item(sb.toString(), it.gopherType, it.url))
                    sb = StringBuilder(word)
                    spaceLeft = lineWidth - len
                }
                else {
                    if(sb.length > 0) {
                        sb.append(' ')
                        spaceLeft--
                    }
                    sb.append(word)
                    spaceLeft -= (len)
                }
            }

            lines.add(Item(sb.toString(), it.gopherType, it.url))
        }
        return lines
    }

}



class GeminiDocument(txt: String, url: URI) : Document(url) {
    val regex = Regex("^=>\\s*((\\S+)\\s*(.*))?")
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
            val url = try {
                this.url.resolve(r.groupValues[2])
            }catch (e: Exception){
                log.log(Level.SEVERE, "error resolving url on line: $line", e)
                null
            }
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
    val errorText = when{
        txt.startsWith("4") -> "TEMPORARY FAILURE"
        txt.startsWith("41") -> "TEMPORARY FAILURE: SERVER UNAVAILABLE"
        txt.startsWith("42") -> "TEMPORARY FAILURE: CGI ERROR"
        txt.startsWith("43") -> "TEMPORARY FAILURE: PROXY ERROR"
        txt.startsWith("44") -> "TEMPORARY FAILURE: SLOW DOWN"
        txt.startsWith("5") -> "PERMANENT FAILURE"
        txt.startsWith("51") -> "PERMANENT FAILURE: NOT FOUND"
        txt.startsWith("52") -> "PERMANENT FAILURE: GONE"
        txt.startsWith("53") -> "PERMANENT FAILURE: PROXY REQUEST REFUSED"
        txt.startsWith("59") -> "PERMANENT FAILURE: BAD REQUEST"
        else -> "UNKNOWN ERROR"

    }

    override fun toString(): String {
        return errorText+" ["+txt+"]"

    }
}


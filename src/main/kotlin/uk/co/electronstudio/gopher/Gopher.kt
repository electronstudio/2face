package uk.co.electronstudio.gopher




val DEFAULT_PORT_GOPHER = 70

val DEFAULT_PORT_GEMINI = 1965

abstract class Response {}



open class Document : Response() {
    open val url = ""
    val items: ArrayList<Item> = arrayListOf()
    init {
        println("MADE DOCUMENT")
    }
}

class GeminiDocument(data: String) : Document() {

}





class GopherDocument(txt: String, override val url: String) : Document() {
    init {
        println("GOPHERDOCUMENT INIT")
        txt.lines().forEach {
            if (it.length>3) items.add(Item.fromGopherLine(it))
        }
        println("made gopherdocument with ${items.size} items")
    }
}

//class GopherMenu(val txt: String) : GopherDocument() {
//
//

//    override val items: List<Item>
//        get() = txt.lines().mapNotNull { if (it.length < 4) null else Item(it) }
//
//    override fun toString(): String {
//        return items.joinToString(separator = "\n") { it.toString() }
//    }
//}

class Item(val text: String, val url: String? = null) {
    companion object {
//        fun fromGopherFields(
//            type: String = "",
//            display: String = "",
//            selector: String = "",
//            hostName: String = "",
//            port: Int = DEFAULT_PORT_GOPHER
//        ): Item {
//            return Item(display, "gopher://${hostName}:${port}/${type}/${selector}")
//        }

        fun fromGopherLine(line: String): Item {
            println("from gopherline line $line")
            val s = line.drop(1).split('\t', limit = 4)
            val type = line[0].toString()
            val display = s[0].trim()
            if(type=="0" || type=="1") {
                val selector = s[1].trim()
                val hostName = s[2].trim()
                val port = s[3].trim().toInt()
                return  Item(display, "gopher://${hostName}:${port}/${type}/${selector}")
                //fromGopherFields(type, display, selector, hostName, port)
            }else{
                return Item(display)
            }
        }
    }
}


//class Item(val line: String) {
//    val type: String
//    val display: String
//    val selector: String
//    val hostName: String
//    val port: Int
//
//    init {
//        //print("Proc line $line")
//        val s = line.drop(1).split('\t', limit = 4)
//        type = line[0].toString()
//        display = s[0].trim()
//        selector = s[1].trim()
//        hostName = s[2].trim()
//        port = s[3].trim().toInt()
//        //println()
//
//        println("created type $type display $display selector $selector")
//    }
//
//    constructor(    type: String = "",
//                    display: String = "",
//                    selector: String = "",
//                    hostName: String = "",
//                    port: Int = 0): this("$type$display\t$selector\t$hostName\t$port"){
//
//    }
//
//    override fun toString(): String {
//        return "$type * $display * $selector * $hostName * $port"
//    }
//}

class TextDocument(txt: String, override val url: String) : Document() {

    init {
        txt.lines().forEach { items.add(Item(it)) }
    }

//    override val items: List<Item>
//        get() = txt.lines().map {
//            println("creating cophermenu item $it")
//            Item("i", display = it)
//        }


}

class Error(val txt: String) : Response() {

    override fun toString(): String {
        return txt
    }
}


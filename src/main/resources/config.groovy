import com.googlecode.lanterna.TextColor.ANSI
import java.util.logging.Level

keys {
    // keys to use to open links
    shortcuts = "123456789abcdefghijklmnopqrstuvwxyz"
    // key to press to open a URL
    url = "0"
}

// Foreground color, background color
// These colors are from com.googlecode.lanterna.TextColor.ANSI
// but you could use other colours from lanterna
colors {
    urlLine = [ANSI.CYAN, ANSI.BLACK]
    statusLine = [ANSI.CYAN, ANSI.BLACK]
    link = [ANSI.GREEN, ANSI.BLACK]
    heading1 = [ANSI.RED, ANSI.BLACK]
    heading2 = [ANSI.RED, ANSI.BLACK]
    heading3 = [ANSI.RED, ANSI.BLACK]
    text = [ANSI.WHITE, ANSI.BLACK]
}

// ALL, WARNING, SEVERE, OFF
logLevel = Level.WARNING

// set this if app opens its own window when you want it to use your text terminal
forceTextMode = false
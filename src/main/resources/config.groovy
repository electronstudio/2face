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
    urlLine = [ANSI.BLACK, ANSI.CYAN]
    statusLine = [ANSI.BLACK, ANSI.CYAN]
    link = [ANSI.GREEN, ANSI.DEFAULT]
    selected = [ANSI.BLACK, ANSI.GREEN]
    heading1 = [ANSI.RED, ANSI.DEFAULT]
    heading2 = [ANSI.RED, ANSI.DEFAULT]
    heading3 = [ANSI.RED, ANSI.DEFAULT]
    text = [ANSI.DEFAULT, ANSI.DEFAULT]
}

// ALL, WARNING, SEVERE, OFF
logLevel = Level.ALL

// set this if app opens its own window when you want it to use your text terminal
forceTextMode = false

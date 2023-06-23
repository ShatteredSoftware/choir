package software.shattered.choir.extensions

import java.io.File

operator fun File.get(s: String): File {
    return File(this, s)
}

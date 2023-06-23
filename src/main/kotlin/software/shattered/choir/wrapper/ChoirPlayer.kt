package software.shattered.choir.wrapper

import software.shattered.choir.attribute.Identified
import java.util.Locale

interface ChoirPlayer : Identified {
    val username: String
    val pastUsernames: Set<String>
    val locale: Locale
}
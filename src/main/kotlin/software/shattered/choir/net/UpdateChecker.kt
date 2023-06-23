package software.shattered.choir.net

import com.google.gson.JsonParser
import org.bukkit.plugin.Plugin
import org.bukkit.util.Consumer
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * An update checker for Spigot plugins.
 */
class UpdateChecker
/**
 * Creates an update checker.
 *
 * @param plugin     The plugin to check for updates for.
 * @param resourceId The resource ID of the plugin.
 */
(private val plugin: Plugin, private val resourceId: Int) {
    fun getVersion(consumer: Consumer<String?>) {
        try {
            val url = URL(REQUEST_URL + resourceId + REQUEST_PATH)
            val conn = url.openConnection() as HttpURLConnection
            conn.addRequestProperty("User-Agent", plugin.name + "VersionCheck")
            val inputStream = conn.inputStream
            val reader = InputStreamReader(inputStream)
            val element = JsonParser.parseReader(reader)
            if (element.isJsonArray) {
                val el = element.asJsonArray[0].asJsonObject
                val name = el["name"].asString
                consumer.accept(name)
            }
            reader.close()
            inputStream.close()
            conn.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_URL = "https://api.spiget.org/v2/resources/"
        private const val REQUEST_PATH = "/versions"
    }
}
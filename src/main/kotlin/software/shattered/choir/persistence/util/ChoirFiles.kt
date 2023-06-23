package software.shattered.choir.persistence.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.logging.Logger
import java.util.stream.Collectors
import java.util.zip.ZipFile

object ChoirFiles {
    fun readInternalFileLines(name: String, cls: Class<*>, logger: Logger): List<String> {
        val jar = ZipFile(cls.protectionDomain.codeSource.location.path)
        val pluginFiles = jar.stream().filter { it.name == name }.collect(Collectors.toList())
        if (pluginFiles.size > 1) {
            logger.warning("Found multiple $name files in the jar; using the first")
        }
        else if (pluginFiles.size == 0) {
            throw Error("Could not find $name in the jar")
        }
        val file = pluginFiles[0]
        BufferedReader(InputStreamReader(jar.getInputStream(file), StandardCharsets.UTF_8)).use {
            return it.lines().collect(Collectors.toList())
        }
    }
}
package software.shattered.choir.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import software.shattered.choir.persistence.tasks.RunStrategy
import com.google.gson.Gson
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object FileUtil {
    private val yaml = Yaml()
    private val objectMapper = ObjectMapper()
    private val yamlMapper = YAMLMapper()

    init {
        yamlMapper.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    }

    fun <T> loadJsonFileAs(file: File, clazz: Class<T>, gson: Gson): T? {
        FileReader(file).use {
            return try {
                gson.fromJson(it, clazz)
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun <T> loadJsonFileAs(file: File, clazz: Class<T>, gson: Gson, init: () -> T): T {
        FileReader(file).use {
            return try {
                gson.fromJson(it, clazz)
            }
            catch (ex: Exception) {
                init.invoke()
            }
        }
    }

    fun <T> saveJsonFileAs(file: File, value: T, gson: Gson, runStrategy: RunStrategy) {
        runStrategy.execute {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val json = gson.toJson(value)
            FileWriter(file).use {
                it.write(json)
            }
        }
    }

    fun <T> loadYamlFileAs(file: File, clazz: Class<T>, gson: Gson, init: (() -> T)): T {
        FileReader(file).use {
            val contents: Any = yaml.load(it)
            val json = gson.toJson(contents, LinkedHashMap::class.java)
            return try {
                gson.fromJson(json, clazz)
            } catch (ex: Exception) {
                init.invoke()
            }
        }
    }

    fun <T> loadYamlFileAs(file: File, clazz: Class<T>, gson: Gson): T? {
        FileReader(file).use {
            val contents: Any = yaml.load(it)
            val json = gson.toJson(contents, LinkedHashMap::class.java)
            return try {
                gson.fromJson(json, clazz)
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun <T> saveYamlFileAs(file: File, value: T, gson: Gson, runStrategy: RunStrategy) {
        runStrategy.execute {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val json = gson.toJson(value)
            val jsonNode = objectMapper.readTree(json)
            FileWriter(file).use {
                yamlMapper.writeValue(it, jsonNode)
            }
        }
    }
}
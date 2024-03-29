package software.shattered.choir.persistence.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

inline fun <reified T> token() = object : TypeToken<T>() {}.type

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(reader: Reader) = fromJson<T>(reader, object : TypeToken<T>() {}.type)

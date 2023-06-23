package software.shattered.choir.data

import org.bukkit.Bukkit

enum class MinecraftVersion(val major: Int, val minor: Int, val patch: Int = 0, val display: String = "$major.$minor${if (patch != 0) ".$patch" else ""}") : Comparable<MinecraftVersion> {
    V1_19(1, 19),
    V1_18(1, 18),
    V1_17(1, 17),
    Legacy(0, 0, display = "Legacy");

    companion object {
        fun fromParts(major: Int, minor: Int, patch: Int = 0): MinecraftVersion {
            return values().find { it.major == major && it.minor == minor && it.patch == patch }
                ?: values().find { it.major == major && it.minor == minor } ?: Legacy
        }

        val ServerVersion: MinecraftVersion =
            try {
                val packageName =
                    Bukkit.getServer().javaClass.getPackage().name
                val curr = packageName.substring(packageName.lastIndexOf('.') + 1)
                var pos = 0
                for (ch in curr) {
                    pos++
                    if (pos > 2 && ch == 'R') break
                }
                val version = curr.substring(1, pos - 2).replace("_", ".")
                val parts = version.split(".", limit = 3).map(String::toInt)
                when (parts.size) {
                    1 -> throw IllegalStateException("Failed to parse version; got $version but expected X.Y or X.Y.Z format")
                    2 -> fromParts(parts[0], parts[1])
                    3 -> fromParts(parts[0], parts[1], parts[2])
                    else -> throw IllegalStateException("Failed to parse version; got $version but expected X.Y or X.Y.Z format")
                }
            } catch (t: Throwable) {
                throw Error("Failed to set server version", t)
            }
    }

    override fun toString(): String {
        return display
    }

    fun newerThan(other: MinecraftVersion): Boolean {
        return major > other.major
                || major == other.major && minor > other.minor
                || major == other.major && minor == other.minor && patch > other.patch
    }

    fun olderThan(other: MinecraftVersion): Boolean {
        return major < other.major || minor < other.minor || patch < other.patch
    }
}
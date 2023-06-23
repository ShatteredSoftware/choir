# Choir
*A small collection of libraries; intended for Minecraft development, likely 
useful for more than that*

Many of the other plugin frameworks that I've seen online are monolithic things
that require all-or-nothing buy-in into their framework, without sharing any of
their small utilities. I built this to change that and to split up some of my
utilities into more modular components.

**List of Libraries:**
* [DataExtensions](#choir) - Simple data types and extension methods
* [Mini18n](#mini18n) - A tiny i18n framework
* [Math](#math) - Vectors, geometry, and type-agnostic math for kotlin
* [Dispatch](#dispatch) - A data-driven functional command framework
  for Dispatch
* [Persistence](#persistence) - The boilerplate of reading json and yaml files
* [Hooks](#hooks) - An IoC-lite library that allows registerring 
  implementations behavior based on conditions.

**Minecraft-specific libraries:**
* [BukkitDispatch](#bukkitdispatch) - Bukkit-specific predicates and contexts
* [BukkitHooks](#bukkithooks) - Bukkit-specific hooks
* [DataHolder](#dataholder) - Store data on worlds, regions, chunks, and blocks.
* [Conductor](#conductor) - A set of common plugin utilities, wrapping each of the
  above libraries into an easy-to-use format.

## To-Do (1.0.0)
- [x] (General) Break into multiple modules
- [ ] (Hooks) Hook System -- Dynamic behavior for when other plugins are registerred
- [ ] (ChoirAPI) Break abstract plugin into a member class or set thereof
- [ ] (ChoirAPI) Messenger v2.0 -- Wrap a MessageStore in the Choir helper
- [ ] (Spigot) SpigotMessenger -- Use MessageStore combined with MiniMessage
- [ ] (ChoirAPI) Generic player data storage
- [ ] (Persistence) Abstract Reads and Writes (`Read<SourceType, ReturnType>`)
- [ ] (Persistence) NoSQL Storage
- [ ] (Persistence) SQL Storage
- [ ] (Persistence) TOML flatfile format
  - [ ] Migration system
  - [ ] SQL Serializers and Deserializers
- [ ] (Math) Spatial Storage
  - [ ] QuadTree
  - [ ] OctTree
  - [ ] RTree
- [ ] (Math) Pathfinding
  - [ ] Blocked tile provider
  - [ ] Chunked pathfinding (only pathfind within a set of predetermined regions; see factorio biters)
  - [ ] 2D and 3D Pathfinding
- [ ] (Choir) Version Feature System -- swap out impls based on version
- [ ] (Choir) GUI System -- similar to dispatch
- [ ] (DataHolder) DataHolders
  - [ ] World System -- Track and manipulate worlds and data attached to them
  - [ ] Region System -- Track and manipulate regions and data attached to them
  - [ ] Block System -- Track and manipulate blocks and data attached to them
  - [ ] Structure System -- multiblock structures and data attached to them
  - [ ] Item System -- Item instances, auto-updating, data attached to them
- [ ] (Choir) Mocks

## Usage

### Jitpack
The easiest way to use some or all of these is to use jitpack:
`pom.xml` (Maven)
```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <!-- All libraries -->
  <dependency>
    <groupId>com.github.shatteredsoftware</groupId>
    <artifactId>choir</artifactId>
    <version>0.1.0</version>
  </dependency>
  
  <!-- Single library -->
  <dependency>
    <groupId>com.github.shatteredsoftware.choir</groupId>
    <artifactId>mini18n</artifactId>
    <version>0.1.0</version>
  </dependency>
</dependencies>
```

`build.gradle` (Gradle)
```groovy
repositories {
  maven { url "https://jitpack.io" }
}

dependencies {
  implementation 'com.github.shatteredsoftware:choir:0.1.0' // All libraries
  implementation 'com.github.shatteredsoftware.choir:mini18n:0.1.0' // One library
}
```

`build.gradle.kts` (Gradle)
```kotlin
repositories {
  maven(url="https://jitpack.io")
}

dependencies {
  implementation("com.github.shatteredsoftware:choir:0.1.0") // All libraries
  implementation("com.github.shatteredsoftware.choir:mini18n:0.1.0") // One library
}
```

## FAQ


## DataExtensions
*Some simple data types and extension methods*

I wrote this because I needed some more unstructured datatypes that could 
handle storing things based on their class. In other words, I'd ask to pull
a key-class pair, and I'd only get a result if there was a value at the
given key with that class. This drives placeholder substitution in the 
[Mini18n](#mini18n) framework.

This has evolved since then to also include some base interfaces and extension
functions that I'm used to using everywhere, hence the "Extensions" in the 
name. 

### Usage

#### GenericDataStore
```kotlin
val store = GenericDataStore()

store.put("my_value", 5)
store.put("my_value", "hello") // Overwrites the previous 5

val myIntValue: Int? = store.get<Int>("my_value") // null
val myStringValue: String? = store.get<String>("my_value") // "hello"
```

#### Extensions

### Contributing

## Mini18n
*A tiny internationalization ("lang") framework*

I wrote this because most of what I was seeing of the message customization 
frameworks for spigot would only handle individual messages without support for
proper placeholder pluralization.

### Usage

```kotlin
val messageSet = MessageSet()

messageSet.add(Locale.US, "worlds.one", "You have %count% world.")
messageSet.add(Locale.US, "worlds.other", "You have %count% worlds.")

val worldsMessage = messageSet.get("worlds", mapOf("count" to 5), Locale.US)
val worldMessage = messageSet.get("worlds", mapOf("count" to 1), Locale.US)

println(worldsMessage) // prints "You have 5 worlds."
println(worldMessage) // prints "You have 1 world."
```

#### Special Data Parameters
* **`count`** (Double) - used to pluralize
* **`ordinal`** (Boolean) - used to choose ordinal pluralization rules instead
* **`range_min`** (Double) - used in conjunction with `range_max` to select a 
  range message
* **`range_max`** (Double) - used in conjunction with `range_min` to select a 
  range message

#### Configuration Examples

Here's an example for english; especially the ranges are primarily useful in
other languages, but hopefully this section demonstrates what you can accomplish
with this framework.

```yaml
worlds:
  one: "You own %count% world."
  other: "You own %count% worlds."
current-world:
  one: "This is your %count%st world."
  two: "This is your %count%nd world."
  few: "This is your %count%rd world."
  many: "This is your %count%th world."
ownership:
  one+one: "You can own %range_min% world."
  one+other: "You can own %range_min%-%range_max% worlds."
  other+one: "You can own %range_min%-%range_max% worlds."
  other+other: "You can own %range_min%-%range_max% worlds."
```

### Contributing
I am open to getting more plural rules applied to this framework and other 
improvements, but I'd like to keep it small.

## Math
*Vectors and flexible math*

## Dispatch
*Data-driven Commands*

## Persistence
*The boilerplate parts of reading json and yaml files*
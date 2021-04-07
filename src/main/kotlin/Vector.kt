import java.io.File

val types = listOf("Double", "Float", "Long", "Int", "Short", "Byte")
val primitiveOperations = listOf("plus", "minus", "times", "div", "rem")
val opMap = mapOf(
    "plus" to "+",
    "minus" to "-",
    "times" to "*",
    "div" to "/",
    "rem" to "%",
)

fun conversion(type: String, other: String? = null) = types.find { it == type || it == other || it == "Int" }
fun cast(type: String) = if (types.indexOf(type) > 3) ".to$type()" else ""

fun main() {
    for (type in types) {
        val file = File("gen", "${type}Vector.kt")
        // @formatter:off
        val content =
"""@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package vector

import kotlin.math.min

// @formatter:off
inline class ${type}Vector(val data: ${type}Array) {
    /// Index Constructor
    constructor(shape: Int, init: (Int) -> ${type}) : this(${type}Array(shape) { init(it) })
    constructor(shape: Int) : this(${type}Array(shape))

    /// Pseudo properties
    val shape get() = data.size

    /// Index Access
    operator fun get(i: Int) = data[i]
    operator fun set(i: Int, value: $type) = run { data[i] = value }
    
    /// Unary operations
    operator fun unaryPlus()  = ${conversion(type)}Vector(data.size) { +data[it] }
    operator fun unaryMinus() = ${conversion(type)}Vector(data.size) { -data[it] }
    operator fun inc()        = ${type}Vector(data.size) { data[it]++ }
    operator fun dec()        = ${type}Vector(data.size) { data[it]-- }
    
    /// Broadcast Primitive Operations
${primitiveOperations.joinToString("\n\n") { op -> types.joinToString("\n") { tp ->
    "    operator fun $op(b: $tp) = ${conversion(type, tp)}Vector(shape) { data[it] ${opMap[op]} b }"
}}}
    
    /// Primitive Vector Operations
${primitiveOperations.joinToString("\n\n") { op -> types.joinToString("\n") { tp ->
    "    operator fun $op(b: ${tp}Vector) = ${conversion(type, tp)}Vector(min(shape, b.shape)) { data[it] ${opMap[op]} b[it] }"
}}}
    
    /// Broadcasting Assign Operations
${primitiveOperations.joinToString("\n") { op ->
"    operator fun ${op}Assign(b: $type) = data.forEachIndexed { i, _ -> data[i] = (data[i] ${opMap[op]} b)${cast(type)} }"
}}
    
    /// Assign Vector Operations
${primitiveOperations.joinToString("\n") { op ->
"    operator fun ${op}Assign(b: ${type}Vector) = data.forEachIndexed { i, _ -> data[i] = (data[i] ${opMap[op]} b[i])${cast(type)} }"
}}
}"""
        // @formatter:on
        file.writeText(content)
    }
}
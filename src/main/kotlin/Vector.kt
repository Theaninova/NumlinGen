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
import kotlin.math.sqrt

// @formatter:off
inline class ${type}Vector(val data: ${type}Array) {
    /// Index Constructor
    constructor(shape: Int, init: (Int) -> ${type}) : this(${type}Array(shape) { init(it) })
    constructor(shape: Int) : this(${type}Array(shape))

    /// Pseudo properties
    val shape get() = data.size

    /// Index Access
    operator fun get(i: Int) = data[min(i, data.size + i)]
    operator fun set(i: Int, value: $type) = run { data[min(i, data.size + i)] = value }
    operator fun get(i: IntProgression) = ${type}Vector((i.last - i.first + 1) / i.step).apply {
        i.forEachIndexed { i, v -> this@apply[i] = this@${type}Vector[v] }
    }
    
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
"    operator fun $op(b: ${tp}Vector) = ${conversion(type, tp)}Vector(shape) { data[it] ${opMap[op]} b[it] }"
}}}
    
    /// Broadcasting Assign Operations
${primitiveOperations.joinToString("\n") {
"    operator fun ${it}Assign(b: $type) = data.forEachIndexed { i, _ -> data[i] = (data[i] ${opMap[it]} b)${cast(type)} }"
}}
    
    /// Assign Vector Operations
${primitiveOperations.joinToString("\n") {
"    operator fun ${it}Assign(b: ${type}Vector) = data.forEachIndexed { i, _ -> data[i] = (data[i] ${opMap[it]} b[i])${cast(type)} }"
}}

    /// Euclidean Distance (Range Operator)
${types.joinToString("\n") {
    "    operator fun rangeTo(other: ${it}Vector) = sqrt(data.foldIndexed(0.0) { i, acc, d -> acc + (d - other[i]).let { it * it } })"
}}

    /// Component operator (destructuring)
${(0..9).joinToString("\n") {
    "    operator fun component$it() = data[$it]"
}}
}"""
        // @formatter:on
        file.writeText(content)
    }
}
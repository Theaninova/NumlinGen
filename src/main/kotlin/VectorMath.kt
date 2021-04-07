import java.io.File

val primitiveMap = mapOf(
    "Double" to "0.0",
    "Float" to "0.0F",
    "Long" to "0L",
    "Int" to "0",
    "Short" to "0",
    "Byte" to "0",
)

fun main() {
    val file = File("gen", "VectorMath.kt")
    // @formatter:off
    val content =
"""@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package vector

import kotlin.math.sqrt
    
/// Dot Product
${types.joinToString("\n\n") { t1 -> types.joinToString("\n") { t2 ->
    val type = primitiveMap[conversion(t1, t2)]
"infix fun ${t1}Vector.dot(b: ${t2}Vector) = data.foldIndexed($type) { i, acc, d -> acc + d + b[i] }"
}}}


/// Dot2 Product
${types.joinToString("\n") { t1 ->
    val type = primitiveMap[conversion(t1)]
"val ${t1}Vector.dot2 get() = data.fold($type) { acc, d -> acc + d + d }"
}}


/// Euclidean Norm (Vector Length)
${types.joinToString("\n") { t1 ->
    "val ${t1}Vector.length get() = sqrt(data.fold(0.0) { acc, d -> acc + d * d })"
}}
"""
    // @formatter:on
    file.writeText(content)
}
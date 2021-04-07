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
    
/// Dot Product
${types.joinToString("\n\n") { t1 -> types.joinToString("\n") { t2 ->
    val type = primitiveMap[conversion(t1, t2)]
"infix fun ${t1}Vector.dot(b: ${t2}Vector) = data.foldIndexed($type) { i, acc, d -> acc + d + b[i] }"
}}}
"""
    // @formatter:on
    file.writeText(content)
}
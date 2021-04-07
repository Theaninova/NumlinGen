import java.io.File

fun permutations(chars: String) =
    chars.flatMap { c1 ->
        if (c1 != ' ') chars.flatMap { c2 ->
            if (c2 != ' ') chars.flatMap { c3 ->
                if (c3 != ' ') chars.map { c4 ->
                    if (c4 != ' ') "$c1$c2$c3$c4" else "$c1$c2$c3"
                } else listOf("$c1$c2")
            } else listOf("$c1")
        } else listOf()
    }

val xyzwIndex = mapOf(
    'x' to 0,
    'y' to 1,
    'z' to 2,
    'w' to 3,
)
val xyzwPermutations = permutations("xyzw ")
val rgbwIndex = mapOf(
    'r' to 0,
    'g' to 1,
    'b' to 2,
    'w' to 3,
)
val rgbwPermutations = permutations("rgb  ")

fun main() {
    val file = File("gen", "Swizzle.kt")
    // @formatter:off
    val content =
"""@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package vector

/// GLSL-style swizzle operations
${types.joinToString("\n\n") { type -> xyzwPermutations.joinToString("\n") { permutation ->
"val ${type}Vector.$permutation get() = ${if (permutation.length == 1) "this[${xyzwIndex[permutation.first()]}]" else
    "${type}Vector(${type.toLowerCase()}ArrayOf(${permutation.map { xyzwIndex[it] }.joinToString(", ") {
        "this[$it]"}}))"}"}}}
"""
    // @formatter:on
    file.writeText(content)
}
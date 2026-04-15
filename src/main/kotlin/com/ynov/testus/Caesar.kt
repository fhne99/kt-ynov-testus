package com.ynov.testus

fun cypher(char: Char, key: Int): Char {
    require(key >= 0) { "La clé doit être positive ou nulle, reçu : $key" }
    require(char in 'A'..'Z') { "Seules les lettres majuscules sont autorisées, reçu : $char" }

    val offset = key % 26
    return 'A' + (char - 'A' + offset) % 26
}

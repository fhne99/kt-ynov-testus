package com.ynov.testus

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CaesarTest : DescribeSpec({

    describe("cypher - chiffrement de César") {

        describe("cas nominaux") {

            it("décale 'A' de 2 → 'C'") {
                cypher('A', 2) shouldBe 'C'
            }

            it("décale 'A' de 0 → 'A' (pas de décalage)") {
                cypher('A', 0) shouldBe 'A'
            }

            it("décale 'Z' de 1 → revient à 'A' (wrap around)") {
                cypher('Z', 1) shouldBe 'A'
            }

            it("décale 'Y' de 3 → 'B' (wrap around)") {
                cypher('Y', 3) shouldBe 'B'
            }

            it("décale 'M' de 13 → 'Z'") {
                cypher('M', 13) shouldBe 'Z'
            }
        }

        describe("key > 26 : cycle recommence") {

            it("key = 26 est équivalent à key = 0") {
                cypher('A', 26) shouldBe 'A'
            }

            it("key = 27 est équivalent à key = 1") {
                cypher('A', 27) shouldBe 'B'
            }

            it("key = 52 est équivalent à key = 0") {
                cypher('B', 52) shouldBe 'B'
            }
        }

        describe("seules les majuscules sont autorisées") {

            it("une lettre minuscule lance une exception") {
                shouldThrow<IllegalArgumentException> {
                    cypher('a', 1)
                }
            }

            it("un chiffre lance une exception") {
                shouldThrow<IllegalArgumentException> {
                    cypher('3', 1)
                }
            }

            it("un caractère spécial lance une exception") {
                shouldThrow<IllegalArgumentException> {
                    cypher('!', 1)
                }
            }
        }

        describe("key < 0 : erreur") {

            it("une key négative lance une exception") {
                shouldThrow<IllegalArgumentException> {
                    cypher('A', -1)
                }
            }

            it("une key très négative lance une exception") {
                shouldThrow<IllegalArgumentException> {
                    cypher('A', -100)
                }
            }
        }
    }
})

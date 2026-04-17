package com.ynov.testus.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class BookTest : FunSpec({

    test("create book with blank title should throw exception") {
        shouldThrow<IllegalArgumentException> {
            Book("", "Robert Martin")
        }
    }

    test("create book with blank author should throw exception") {
        shouldThrow<IllegalArgumentException> {
            Book("Clean Code", "")
        }
    }
})

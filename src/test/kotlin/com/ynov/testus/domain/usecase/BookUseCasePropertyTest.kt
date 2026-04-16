package com.ynov.testus.domain.usecase

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk

class BookUseCasePropertyTest : FunSpec({

    // Propriété : la liste retournée contient tous les éléments stockés
    test("la liste retournée contient tous les éléments de la liste stockée") {
        checkAll(Arb.list(Arb.string(1..20), 0..20)) { titles ->
            // Arrange
            val repo = mockk<BookRepository>(relaxed = true)
            val books = titles.map { Book(it, "auteur") }
            every { repo.findAll() } returns books
            val useCase = BookUseCase(repo)

            // Act
            val result = useCase.getAllBooks()

            // Assert
            result.size shouldBe books.size
        }
    }

    // Propriété : la liste est toujours triée alphabétiquement
    test("la liste est toujours triée alphabétiquement par titre") {
        checkAll(Arb.list(Arb.string(1..20), 1..20)) { titles ->
            // Arrange
            val repo = mockk<BookRepository>(relaxed = true)
            val books = titles.map { Book(it, "auteur") }
            every { repo.findAll() } returns books
            val useCase = BookUseCase(repo)

            // Act & Assert
            useCase.getAllBooks() shouldBeSortedWith compareBy { it.title }
        }
    }
})

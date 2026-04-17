package com.ynov.testus.domain.usecase

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookRepository = mockk<BookRepository>()
    val bookUseCase = BookUseCase(bookRepository)

    test("get all books should return all books sorted by title") {
        every { bookRepository.findAll() } returns listOf(
            Book("Kotlin in Action", "Jemerov"),
            Book("Clean Code", "Robert Martin")
        )

        val result = bookUseCase.getAllBooks()

        result.shouldContainExactly(
            Book("Clean Code", "Robert Martin"),
            Book("Kotlin in Action", "Jemerov")
        )
    }

    test("add book") {
        justRun { bookRepository.save(any()) }
        val book = Book("Clean Code", "Robert Martin")

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookRepository.save(book) }
    }

    test("reserve book") {
        every { bookRepository.findByTitle("Clean Code") } returns
            Book("Clean Code", "Robert Martin", false)
        justRun { bookRepository.reserve("Clean Code") }

        bookUseCase.reserveBook("Clean Code")

        verify(exactly = 1) { bookRepository.reserve("Clean Code") }
    }

    test("reserve book that does not exist should throw exception") {
        every { bookRepository.findByTitle("Inconnu") } returns null

        val exception = shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBook("Inconnu")
        }
        exception.message shouldBe "Le livre 'Inconnu' n'existe pas"
    }

    test("reserve book already reserved should throw exception") {
        every { bookRepository.findByTitle("Clean Code") } returns
            Book("Clean Code", "Robert Martin", true)

        val exception = shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBook("Clean Code")
        }
        exception.message shouldBe "Le livre 'Clean Code' est déjà réservé"
    }
})

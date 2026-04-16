package com.ynov.testus.domain.usecase

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : DescribeSpec({

    val bookRepository = mockk<BookRepository>(relaxed = true)
    val bookUseCase = BookUseCase(bookRepository)

    describe("addBook") {

        it("ajoute un livre avec titre et auteur valides") {
            val book = bookUseCase.addBook("Clean Code", "Robert Martin")
            book shouldBe Book("Clean Code", "Robert Martin")
            verify { bookRepository.save(Book("Clean Code", "Robert Martin")) }
        }

        it("lève une exception si le titre est vide") {
            shouldThrow<IllegalArgumentException> {
                bookUseCase.addBook("", "Robert Martin")
            }
        }

        it("lève une exception si l'auteur est vide") {
            shouldThrow<IllegalArgumentException> {
                bookUseCase.addBook("Clean Code", "")
            }
        }
    }

    describe("getAllBooks") {

        it("retourne les livres triés par ordre alphabétique") {
            every { bookRepository.findAll() } returns listOf(
                Book("Zorro", "Johnston"),
                Book("Alice", "Carroll"),
                Book("Martin Eden", "London")
            )
            bookUseCase.getAllBooks() shouldContainExactly listOf(
                Book("Alice", "Carroll"),
                Book("Martin Eden", "London"),
                Book("Zorro", "Johnston")
            )
        }

        it("retourne une liste vide si aucun livre") {
            every { bookRepository.findAll() } returns emptyList()
            bookUseCase.getAllBooks() shouldBe emptyList()
        }
    }
})

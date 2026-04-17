package com.ynov.testus.domain.usecase

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class InMemoryBookRepository : BookRepository {
    private val books = mutableListOf<Book>()

    override fun findAll(): List<Book> = books

    override fun save(book: Book) {
        books.add(book)
    }

    override fun reserve(title: String) {
        val index = books.indexOfFirst { it.title == title }
        if (index != -1) {
            books[index] = books[index].copy(reserved = true)
        }
    }

    override fun findByTitle(title: String): Book? =
        books.firstOrNull { it.title == title }

    fun clear() {
        books.clear()
    }
}

class BookUseCasePropertyTest : FunSpec({

    val bookRepository = InMemoryBookRepository()
    val bookUseCase = BookUseCase(bookRepository)

    test("should return all elements in alphabetical order") {
        checkAll(Arb.int(1..100)) { nbItems ->
            bookRepository.clear()
            val arb = Arb.stringPattern("""[a-z]{1,10}""")
            val titles = mutableListOf<String>()
            repeat(nbItems) {
                val title = arb.next()
                titles.add(title)
                bookUseCase.addBook(Book(title, "Victor Hugo"))
            }

            val res = bookUseCase.getAllBooks()

            res.map { it.title } shouldContainExactly titles.sorted()
        }
    }

    test("a reserved book should always be marked as reserved") {
        checkAll(Arb.stringPattern("""[a-z]{1,10}""")) { title ->
            bookRepository.clear()
            bookUseCase.addBook(Book(title, "Victor Hugo"))
            bookUseCase.reserveBook(title)

            val res = bookUseCase.getAllBooks()
            res.first { it.title == title }.reserved shouldBe true
        }
    }
})

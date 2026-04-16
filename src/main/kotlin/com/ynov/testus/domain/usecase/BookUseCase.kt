package com.ynov.testus.domain.usecase

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository

class BookUseCase(private val bookRepository: BookRepository) {

    fun addBook(title: String, author: String): Book {
        require(title.isNotBlank()) { "Le titre ne peut pas être vide" }
        require(author.isNotBlank()) { "L'auteur ne peut pas être vide" }
        val book = Book(title, author)
        bookRepository.save(book)
        return book
    }

    fun getAllBooks(): List<Book> =
        bookRepository.findAll().sortedBy { it.title }
}

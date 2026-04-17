package com.ynov.testus.domain.usecase

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository

class BookUseCase(private val bookRepository: BookRepository) {

    fun addBook(book: Book) {
        bookRepository.save(book)
    }

    fun getAllBooks(): List<Book> =
        bookRepository.findAll().sortedBy { it.title }

    fun reserveBook(title: String) {
        val book = bookRepository.findByTitle(title)
            ?: throw IllegalArgumentException("Le livre '$title' n'existe pas")
        require(!book.reserved) { "Le livre '$title' est déjà réservé" }
        bookRepository.reserve(title)
    }
}

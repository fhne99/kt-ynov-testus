package com.ynov.testus.domain.port

import com.ynov.testus.domain.model.Book

interface BookRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
    fun reserve(title: String)
    fun findByTitle(title: String): Book?
}

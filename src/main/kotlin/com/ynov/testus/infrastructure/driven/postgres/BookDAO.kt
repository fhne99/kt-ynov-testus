package com.ynov.testus.infrastructure.driven.postgres

import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookRepository {

    override fun save(book: Book) {
        namedParameterJdbcTemplate.update(
            "INSERT INTO book (title, author) VALUES (:title, :author)",
            mapOf("title" to book.title, "author" to book.author)
        )
    }

    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM book",
            MapSqlParameterSource()
        ) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author")
            )
        }
    }
}

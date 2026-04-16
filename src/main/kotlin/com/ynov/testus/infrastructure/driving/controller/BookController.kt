package com.ynov.testus.infrastructure.driving.controller

import com.ynov.testus.domain.usecase.BookUseCase
import com.ynov.testus.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getBooks(): List<BookDTO> =
        bookUseCase.getAllBooks().map { BookDTO(it.title, it.author) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody dto: BookDTO) {
        bookUseCase.addBook(dto.title, dto.author)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(e: IllegalArgumentException): Map<String, String> {
        return mapOf("error" to (e.message ?: "Bad request"))
    }

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRuntimeException(e: RuntimeException): Map<String, String> {
        return mapOf("error" to (e.message ?: "Internal server error"))
    }
}

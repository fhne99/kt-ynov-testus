package com.ynov.testus

import com.ninjasquad.springmockk.MockkBean
import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.usecase.BookUseCase
import com.ynov.testus.infrastructure.driving.controller.BookController
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [BookController::class],
    excludeAutoConfiguration = [
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration::class,
        org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration::class
    ]
)
class BookControllerIT(
    private val mockMvc: MockMvc,
    @MockkBean val bookUseCase: BookUseCase
) : FunSpec({
    extension(SpringExtension)

    test("rest route get books") {
        every { bookUseCase.getAllBooks() } returns listOf(
            Book("Clean Code", "Robert Martin"),
            Book("Kotlin in Action", "Jemerov")
        )

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    json("""
                        [
                            {"title": "Clean Code", "author": "Robert Martin", "reserved": false},
                            {"title": "Kotlin in Action", "author": "Jemerov", "reserved": false}
                        ]
                    """.trimIndent())
                }
            }

        verify { bookUseCase.getAllBooks() }
    }

    test("rest route get books returns empty list") {
        every { bookUseCase.getAllBooks() } returns emptyList()

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                content { json("[]") }
            }
    }

    test("rest route post book") {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "Clean Code", "author": "Robert Martin"}"""
        }.andExpect {
            status { isCreated() }
        }

        verify(exactly = 1) { bookUseCase.addBook(Book("Clean Code", "Robert Martin")) }
    }

    test("rest route post book should return 400 when title is blank") {
        every { bookUseCase.addBook(any()) } throws IllegalArgumentException("Le titre ne peut pas être vide")

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "", "author": "Robert Martin"}"""
        }.andExpect {
            status { isBadRequest() }
        }
    }

    test("rest route post book should return 500 when unexpected error") {
        every { bookUseCase.addBook(any()) } throws RuntimeException("Erreur inattendue")

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "Clean Code", "author": "Robert Martin"}"""
        }.andExpect {
            status { is5xxServerError() }
        }
    }

    test("rest route post reserve book") {
        justRun { bookUseCase.reserveBook(any()) }

        mockMvc.post("/books/Clean Code/reserve")
            .andExpect {
                status { isOk() }
            }

        verify(exactly = 1) { bookUseCase.reserveBook("Clean Code") }
    }

    test("rest route post reserve book should return 400 when already reserved") {
        every { bookUseCase.reserveBook(any()) } throws
                IllegalArgumentException("Le livre 'Clean Code' est déjà réservé")

        mockMvc.post("/books/Clean Code/reserve")
            .andExpect {
                status { isBadRequest() }
            }
    }

    test("rest route post reserve book should return 400 when not found") {
        every { bookUseCase.reserveBook(any()) } throws
                IllegalArgumentException("Le livre 'Inconnu' n'existe pas")

        mockMvc.post("/books/Inconnu/reserve")
            .andExpect {
                status { isBadRequest() }
            }
    }
})
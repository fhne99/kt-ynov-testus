package com.ynov.testus

import com.ninjasquad.springmockk.MockkBean
import com.ynov.testus.domain.model.Book
import com.ynov.testus.domain.usecase.BookUseCase
import com.ynov.testus.infrastructure.driving.controller.BookController
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerIT(
    private val mockMvc: MockMvc,
    @MockkBean val bookUseCase: BookUseCase
) : DescribeSpec({

    describe("GET /books") {

        it("retourne la liste des livres avec status 200") {
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
                                {"title": "Clean Code", "author": "Robert Martin"},
                                {"title": "Kotlin in Action", "author": "Jemerov"}
                            ]
                        """.trimIndent())
                    }
                }
        }

        it("retourne une liste vide si aucun livre") {
            every { bookUseCase.getAllBooks() } returns emptyList()

            mockMvc.get("/books")
                .andExpect {
                    status { isOk() }
                    content { json("[]") }
                }
        }
    }

    describe("POST /books") {

        it("crée un livre et retourne 201") {
            every { bookUseCase.addBook(any(), any()) } returns Book("Clean Code", "Robert Martin")

            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "Clean Code", "author": "Robert Martin"}"""
            }.andExpect {
                status { isCreated() }
            }

            verify { bookUseCase.addBook("Clean Code", "Robert Martin") }
        }

        it("retourne 400 si le titre est vide") {
            every { bookUseCase.addBook("", any()) } throws IllegalArgumentException("Le titre ne peut pas être vide")

            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "", "author": "Robert Martin"}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
})
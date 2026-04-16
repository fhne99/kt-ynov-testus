package com.ynov.testus

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class BookStepDefs(
    @LocalServerPort private val port: Int,
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    private var lastResponse: ValidatableResponse? = null

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        jdbcTemplate.update("DELETE FROM book", mapOf<String, Any>())
    }

    @Given("the user creates the book with title {string} and author {string}")
    fun createBook(title: String, author: String) {
        RestAssured.given()
            .contentType("application/json")
            .body("""{"title": "$title", "author": "$author"}""")
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user gets all books")
    fun getAllBooks() {
        lastResponse = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @Then("the list should contain the following books")
    fun shouldContainBooks(data: List<Map<String, String>>) {
        val expectedJson = data.joinToString(",", "[", "]") { row ->
            """{"title": "${row["title"]}", "author": "${row["author"]}"}"""
        }
        lastResponse!!.extract().body().jsonPath().prettify() shouldBe
                io.restassured.path.json.JsonPath(expectedJson).prettify()
    }
}
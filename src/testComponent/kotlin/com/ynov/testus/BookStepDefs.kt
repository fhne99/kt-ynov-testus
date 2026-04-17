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

    @Given("the user has already reserved the book with title {string}")
    fun givenReserveBook(title: String) {
        RestAssured.given()
            .`when`()
            .post("/books/$title/reserve")
            .then()
            .statusCode(200)
    }

    @When("the user gets all books")
    fun getAllBooks() {
        lastResponse = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @When("the user reserves the book with title {string}")
    fun reserveBook(title: String) {
        lastResponse = RestAssured.given()
            .`when`()
            .post("/books/$title/reserve")
            .then()
            .statusCode(200)
    }

    @When("the user tries to reserve the book with title {string}")
    fun triesReserveBook(title: String) {
        lastResponse = RestAssured.given()
            .`when`()
            .post("/books/$title/reserve")
            .then()
    }

    @Then("the list should contain the following books")
    fun shouldContainBooks(data: List<Map<String, String>>) {
        val actual = lastResponse!!.extract().body().jsonPath().getList<Map<String, Any>>(".")
        data.forEach { row ->
            val book = actual.first { it["title"] == row["title"] }
            book["author"] shouldBe row["author"]
            book["reserved"] shouldBe (row["reserved"] == "true")
        }
    }

    @Then("the book with title {string} should be reserved")
    fun bookShouldBeReserved(title: String) {
        val books = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getList<Map<String, Any>>(".")

        val book = books.first { it["title"] == title }
        book["reserved"] shouldBe true
    }

    @Then("the response status should be 400")
    fun responseShouldBe400() {
        lastResponse!!.statusCode(400)
    }
}
package com.ynov.testus

import com.ynov.testus.domain.model.Book
import com.ynov.testus.infrastructure.driven.postgres.BookDAO
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO
) : FunSpec() {
    init {
        extension(SpringExtension)

        beforeTest {
            performQuery("DELETE FROM book")
        }

        test("get all books from db") {
            performQuery("""
                INSERT INTO book (title, author, reserved)
                VALUES 
                    ('Clean Code', 'Robert Martin', false),
                    ('Kotlin in Action', 'Jemerov', false)
            """.trimIndent())

            val res = bookDAO.findAll()

            res.shouldContainExactlyInAnyOrder(
                Book("Clean Code", "Robert Martin"),
                Book("Kotlin in Action", "Jemerov")
            )
        }

        test("save book in db") {
            bookDAO.save(Book("Les misérables", "Victor Hugo"))

            val res = performQuery("SELECT * FROM book")
            res shouldHaveSize 1
            assertSoftly(res.first()) {
                this["id"].shouldNotBeNull().shouldBeInstanceOf<Int>()
                this["title"].shouldBe("Les misérables")
                this["author"].shouldBe("Victor Hugo")
                this["reserved"].shouldBe(false)
            }
        }

        test("findAll retourne une liste vide si aucun livre") {
            bookDAO.findAll() shouldHaveSize 0
        }

        test("reserve book in db") {
            performQuery("""
                INSERT INTO book (title, author, reserved)
                VALUES ('Clean Code', 'Robert Martin', false)
            """.trimIndent())

            bookDAO.reserve("Clean Code")

            val res = performQuery("SELECT * FROM book WHERE title = 'Clean Code'")
            res shouldHaveSize 1
            res.first()["reserved"].shouldBe(true)
        }

        test("find book by title") {
            performQuery("""
                INSERT INTO book (title, author, reserved)
                VALUES ('Clean Code', 'Robert Martin', false)
            """.trimIndent())

            val book = bookDAO.findByTitle("Clean Code")

            assertSoftly(book!!) {
                title shouldBe "Clean Code"
                author shouldBe "Robert Martin"
                reserved shouldBe false
            }
        }

        test("find book by title returns null if not found") {
            val book = bookDAO.findByTitle("Inconnu")
            book shouldBe null
        }

        afterSpec {
            container.stop()
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }

        private fun ResultSet.toList(): List<Map<String, Any>> {
            val md = this.metaData
            val columns = md.columnCount
            val rows: MutableList<Map<String, Any>> = ArrayList()
            while (this.next()) {
                val row: MutableMap<String, Any> = HashMap(columns)
                for (i in 1..columns) {
                    row[md.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }

        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig()
            hikariConfig.setJdbcUrl(container.jdbcUrl)
            hikariConfig.username = container.username
            hikariConfig.password = container.password
            hikariConfig.setDriverClassName(container.driverClassName)
            val ds = HikariDataSource(hikariConfig)
            val statement = ds.connection.createStatement()
            statement.execute(sql)
            val resultSet = statement.resultSet
            return resultSet?.toList() ?: listOf()
        }
    }
}
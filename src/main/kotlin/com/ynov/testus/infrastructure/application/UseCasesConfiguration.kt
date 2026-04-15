package com.ynov.testus.infrastructure.application

import com.ynov.testus.domain.port.BookRepository
import com.ynov.testus.domain.usecase.BookUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {

    @Bean
    fun bookUseCase(bookRepository: BookRepository): BookUseCase {
        return BookUseCase(bookRepository)
    }
}
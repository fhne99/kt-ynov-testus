package com.ynov.testus

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.FunSpec

class ArchitectureTest : FunSpec({

    val basePackage = "com.ynov.testus"

    val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages(basePackage)

    test("it should respect the clean architecture concept") {
        val rule = layeredArchitecture().consideringAllDependencies()
            .layer("domain").definedBy("$basePackage.domain..")
            .layer("infrastructure").definedBy("$basePackage.infrastructure..")
            .layer("Standard API").definedBy("java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations..", "org.springframework..")
            .withOptionalLayers(true)
            .whereLayer("domain").mayNotBeAccessedByAnyLayer()
            .whereLayer("infrastructure").mayOnlyAccessLayers("domain", "Standard API")

        rule.check(importedClasses)
    }
})
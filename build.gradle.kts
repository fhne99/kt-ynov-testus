plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.13"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "com.ynov"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

testing {
    suites {
        val testIntegration by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testIntegration/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
        val testComponent by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testComponent/kotlin"))
                }
                resources {
                    setSrcDirs(listOf("src/testComponent/resources"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
        val testArchitecture by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testArchitecture/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
    }
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testComponentImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testArchitectureImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.pitest:pitest-junit5-plugin:1.2.1")
    "testIntegrationImplementation"("io.mockk:mockk:1.13.10")
    "testIntegrationImplementation"("io.kotest:kotest-assertions-core:5.9.1")
    "testIntegrationImplementation"("io.kotest:kotest-runner-junit5:5.9.1")
    "testIntegrationImplementation"("com.ninja-squad:springmockk:4.0.2")
    "testIntegrationImplementation"("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    "testIntegrationImplementation"("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    "testIntegrationImplementation"("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    "testIntegrationImplementation"("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql")
    "testIntegrationImplementation"("org.testcontainers:postgresql:1.19.1")
    "testIntegrationImplementation"("org.testcontainers:jdbc-test:1.12.0")
    "testIntegrationImplementation"("org.testcontainers:junit-jupiter:1.19.1")
    "testIntegrationImplementation"("org.testcontainers:testcontainers:1.19.1")
    "testIntegrationImplementation"("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    "testIntegrationImplementation"("org.postgresql:postgresql")
    testComponentImplementation("io.cucumber:cucumber-java:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-spring:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-junit:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    testComponentImplementation("io.rest-assured:rest-assured:5.3.2")
    testComponentImplementation("org.junit.platform:junit-platform-suite:1.10.0")
    testComponentImplementation("org.testcontainers:postgresql:1.19.1")
    testComponentImplementation("io.kotest:kotest-assertions-core:5.9.1")
    "testComponentImplementation"("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    "testComponentImplementation"("org.springframework.boot:spring-boot-starter-web")
    testArchitectureImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
    testArchitectureImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testArchitectureImplementation("io.kotest:kotest-runner-junit5:5.9.1")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testIntegration"))
    executionData.setFrom(
        fileTree(layout.buildDirectory).include(
            "jacoco/test.exec",
            "jacoco/testIntegration.exec"
        )
    )
    reports {
        xml.required = true
        html.required = true
    }
}

pitest {
    targetClasses.set(listOf("com.ynov.testus.domain.*"))
    targetTests.set(listOf("com.ynov.testus.*"))
    outputFormats.set(listOf("HTML", "XML"))
    mutationThreshold.set(80)
    junit5PluginVersion.set("1.2.1")
    timeoutConstInMillis.set(10000)
}
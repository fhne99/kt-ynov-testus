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
            }
            targets {
                all {
                    testTask.configure {
                        classpath += sourceSets.main.get().output
                    }
                }
            }
        }
    }
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
    "testIntegrationImplementation"(sourceSets.main.get().output)
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
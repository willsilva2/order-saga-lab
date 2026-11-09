package io.github.willsilva2.ordersagalab

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchitectureTest {

    private val classes = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("io.github.willsilva2.ordersagalab")

    @Test
    fun `domain should not depend on application, adapter or config`() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapter..", "..config..")
            .check(classes)
    }

    @Test
    fun `application should not depend on adapter or config`() {
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..adapter..", "..config..")
            .allowEmptyShould(true) // no classes in application yet
            .check(classes)
    }

    @Test
    fun `domain and application should not use frameworks`() {
        noClasses().that().resideInAnyPackage("..domain..", "..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "jakarta..",
                "com.fasterxml.jackson..",
            )
            .check(classes)
    }
}
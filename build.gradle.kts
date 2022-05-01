import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7" apply(false)
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply(false)
    kotlin("jvm") version "1.6.21" apply(false)
    kotlin("plugin.spring") version "1.6.21" apply(false)
    kotlin("plugin.jpa") version "1.6.21" apply(false)
}

val javaVersionStr: String by project
val gradleVersionProperty: String by project


allprojects {
    group = "dev.mbo"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    apply(plugin = "io.spring.dependency-management")
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
        resolutionStrategy {
            cacheChangingModulesFor(0, "seconds")
        }
        dependencies {
            // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
            dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
            // https://mvnrepository.com/artifact/com.vladmihalcea/hibernate-types-55
            dependency("com.vladmihalcea:hibernate-types-55:2.16.1")
            // https://mvnrepository.com/artifact/org.testcontainers/postgresql
            dependency("org.testcontainers:postgresql:1.17.1")
            // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
            dependency("org.apache.commons:commons-lang3:3.12.0")
            // https://mvnrepository.com/artifact/com.networknt/json-schema-validator
            dependency("com.networknt:json-schema-validator:1.0.69")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = javaVersionStr
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.withType<Wrapper> {
    // https://gradle.org/releases/
    gradleVersion = gradleVersionProperty
    distributionType = Wrapper.DistributionType.ALL
}

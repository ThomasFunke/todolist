import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    maven { url = uri("https://dl.bintray.com/kotlin/exposed") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://dl.bintray.com/pgutkowski/Maven") }
}

plugins {
    java
    application
    kotlin("jvm") version "1.3.21"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("com.google.cloud.tools.jib") version "1.0.1"
}

configure<JavaPluginConvention> { sourceCompatibility = JavaVersion.VERSION_1_8 }
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

group = "ro.sorin.todolist"
version = "0.0.4"

application { mainClassName = "ro.sorin.todolist.MainKt" }

/* Jar packaging */
val shadowJar: ShadowJar by tasks
shadowJar.apply {
    baseName = "todolist"
    classifier = ""
    version = ""
}

val ktorVersion = "1.1.3"
val exposedVersion = "0.12.2"
val h2Version = "1.4.196"
val logbackVersion = "1.2.3"
val jupiterVersion = "5.2.0"
val assertJVersion = "3.10.0"
val restAssuredVersion = "3.1.0"
val kGraphQLVersion = "0.3.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    /*Ktor*/
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")

    /*Graphql*/
    implementation("com.github.pgutkowski:kgraphql:${kGraphQLVersion}")

    /*DB Layer*/
    implementation("com.h2database:h2:$h2Version")
    implementation("org.jetbrains.exposed:exposed:$exposedVersion")
    implementation("com.zaxxer:HikariCP:3.3.1")

    /*Logging*/
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    /*Test*/
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "me.gsycl2004"
version = "1.0-SNAPSHOT"

repositories {
    maven{
        url = uri("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jsoup:jsoup:1.15.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
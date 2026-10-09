import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(project(":feature:afisha"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
    implementation(project(":core:platform"))

    implementation(compose.desktop.currentOs)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "org.example.kinoafisha.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.kinoafisha"
            packageVersion = "1.0.0"
            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
    }
}

tasks.withType<JavaExec>().configureEach {
    systemProperty("tmdb.api.key", localProperties.getProperty("tmdb.api.key", ""))
}
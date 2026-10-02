import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    jvm()

    android {
        namespace = "org.example.kinoafisha.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    // Только БД: Room Entity/DAO/AppDatabase. Не знает о domain.
    // TODO(persistence): когда дойдём до кэша — подключить Room:
    //   implementation("androidx.room:room-runtime:2.7.2")
    //   implementation("androidx.sqlite:sqlite-bundled:2.5.2")
    //   + плагин com.google.devtools.ksp и room-compiler
    //     (kspCommonMainMetadata / kspAndroid / kspJvm).
    //   Версию KSP подобрать под версию Kotlin на тот момент.
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

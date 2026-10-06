package org.example.kinoafisha

import org.koin.core.annotation.Singleton

@Singleton
class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

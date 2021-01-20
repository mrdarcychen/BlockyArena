plugins {
    java
    id("org.spongepowered.plugin") version "0.9.0"
}

group = "io.github.mrdarcychen"
version = "0.6.1"
description = "A deathmatch plugin for Minecraft servers"

dependencies {
    compileOnly("org.spongepowered:spongeapi:7.2.0")
}

tasks {
    wrapper {
        gradleVersion = "6.4.1"
        distributionType = Wrapper.DistributionType.BIN
    }
}

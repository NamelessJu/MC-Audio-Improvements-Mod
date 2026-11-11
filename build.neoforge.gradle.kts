plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed < "1.21.11") {
		replace("Identifier", "ResourceLocation")
		replace("identifier()", "location()")
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = if (sc.current.version == "26.3") "[26.3]" else prop("deps.minecraft")
		}
		required("neoforge") {
			forgeLikeVersionRange.set(if (sc.current.version == "26.3") "[26.3.0.102,26.4)" else "[1,)")
		}
        required("yet_another_config_lib_v3") {
            forgeLikeVersionRange = "[${prop("deps.yacl")},)"
            environment = "client"
        }
        optional("logarithmic-volume-control") {
            environment = "client"
        }
	}
}

neoForge {
	version = prop("deps.neoforge")

	validateAccessTransformers = true
    /*
	accessTransformers.from(
		rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
	)
	*/

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = prop("deps.parchment").split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
            logLevel = org.slf4j.event.Level.DEBUG
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
    maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
}

dependencies {
	// implementation(libs.moulberry.mixinconstraints)
	// jarJar(libs.moulberry.mixinconstraints)
    implementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

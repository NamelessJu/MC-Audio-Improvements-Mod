plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("forge") {
			forgeLikeVersionRange.set("[1,)")
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

legacyForge {
	version = "${prop("deps.minecraft")}-${prop("deps.forge")}"

	validateAccessTransformers = true
    /*
	accessTransformers.from(
		rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
	)
	*/

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${sc.current.version})"
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

mixin {
	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
	config("${prop("mod.id")}.mixins.json")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
    maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

    // MixinExtras
    val mixinExtrasCommon = "io.github.llamalad7:mixinextras-common:0.5.0"
    compileOnly(mixinExtrasCommon)
    annotationProcessor(mixinExtrasCommon)
    val mixinExtrasForgeBuild = "io.github.llamalad7:mixinextras-forge:0.5.0"
    implementation(mixinExtrasForgeBuild)
    jarJar(mixinExtrasForgeBuild)

	// implementation(libs.moulberry.mixinconstraints)
	// jarJar(libs.moulberry.mixinconstraints)
    modImplementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl")}")
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated"
		)
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

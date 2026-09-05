# Audio Improvements
[![Download number across all platforms](https://www.modpackindex.com/badge/mod/83594/audio-improvements/downloads.svg?color=green)](https://www.modpackindex.com/mod/83594/audio-improvements)
[![Number of mod packs using this mod](https://www.modpackindex.com/badge/mod/83594/audio-improvements/modpacks.svg?color=blue)](https://www.modpackindex.com/mod/83594/audio-improvements)  
A client-side Minecraft mod for some audio system improvements

Build downloads & feature list:
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/audio-improvements)
- [Modrinth](https://modrinth.com/mod/audio-improvements)

## Supported versions

This project uses [Stonecutter](https://stonecutter.kikugie.dev/) to manage
multiple mod loader & Minecraft version combinations in one project:

| Minecraft | Fabric | NeoForge | Forge |
|-----------|--------|----------|-------|
| 26.2      | ✅     | ✅       | ❌    |
| 26.1.x    | ✅     | ✅       | ❌    |
| 1.21.11   | ✅     | ✅       | ❌    |
| 1.21.1    | ✅     | ✅       | ❌    |
| 1.20.1    | ✅     | ❌       | ✅    |

## Usage

### Build
`./gradlew buildAndCollect`

The jars for all versions & loaders will be collected in ./build/libs/

### Switch active version
`./gradlew "Set active project to {mcversion}-{loader}"`

### Run active version
`./gradlew runActiveClient`

The according run directory can be found under ./versions/{mcversion}-{loader}/run/

## License

This mod is licensed under the [GNU General Public License v3.0 or later](LICENSE).  
The license applies to all files under ./src and any jar files built from that code!

## Credits

This project is using a modified version of the [Stonecutter template by rotgruengelb](https://github.com/rotgruengelb/stonecutter-mod-template), licensed under the MIT license.   
The original copyright and license notice are preserved in [LICENSE_stonecutter_template](LICENSE_stonecutter_template).

Logarithmic volume slider feature inspired by [Logarithmic Volume Control mod](https://github.com/girlbossdev/LogarithmicVolumeControl).

# Audio Improvements — Fabric 1.21.11

This is a **community-maintained port** of [NamelessJu's Audio Improvements Mod](https://github.com/NamelessJu/MC-Audio-Improvements-Mod) for Minecraft 1.21.11 (Fabric).

> Looking for the latest version? See the [`26.1.2-fabric` branch](https://github.com/usernamelocker/audio-improvements-fork) for Minecraft 26.1.2.

A client-side Minecraft mod that improves various audio systems.

## Features

- **Doppler Effect** — realistic pitch shifting as sound sources move relative to the player
- **Sound Speed Simulation** — configurable delay for thunder, explosions, and other sounds based on distance
- **Music Clash Prevention** — prevents music discs and note blocks from clashing with background music
- **Mono Audio Controls** — per-category toggle for downmixing sounds to mono
- **Music Frequency Control** — customize how often background music plays
- **Stereo Spatialization Fix** — fixes incorrect stereo panning
- **Customizable Music Disc Range** — adjust the audible range of jukeboxes (supports global range)

## Changes from Original v1.3.2

Default config values are set to **off** for features that change gameplay behavior:

| Setting | Original Default | Port Default |
|---|---|---|
| Fade music on disc/note block | ON | OFF |
| Fadeout duration | 2s | 0s (instant) |
| Sound speed (thunder) | 100 blk/s | OFF |
| Sound speed (explosions) | 100 blk/s | OFF |
| Doppler effect intensity | 1.0 | OFF |

## Build

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

## License

MIT — same as the original mod.

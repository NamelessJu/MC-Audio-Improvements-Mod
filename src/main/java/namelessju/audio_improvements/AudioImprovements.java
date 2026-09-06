package namelessju.audio_improvements;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.brigadier.CommandDispatcher;
import namelessju.audio_improvements.data.SoundChannelType;
import namelessju.audio_improvements.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class AudioImprovements
{
	public static final String MOD_ID = /*$ mod_id*/ "audio_improvements";
    public static final String MOD_NAME = /*$ mod_name*/ "Audio Improvements";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path)
    {
        //? if <= 1.20.1 {
        /*return new Identifier(MOD_ID, path);
        *///? } else {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
        //? }
    }

    private static AudioImprovements instance = null;

    public static AudioImprovements instance()
    {
        return instance;
    }

    public static Config config()
    {
        return Config.HANDLER.instance();
    }


    private final Path configFilePath;
    private boolean isLogarithmicVolumeControlInstalled = false;

    public AudioImprovements()
    {
        instance = this;
        configFilePath = getConfigDir().resolve("audioImprovementsConfig.json");
    }

    protected abstract Path getConfigDir();
    protected abstract boolean isModLoaded(String modId);

	protected final void init()
    {
        File configFile = configFilePath.toFile();
        File oldConfigFile = AudioImprovements.instance().getConfigDir().resolve("audioimprovements.json").toFile();
        if (!configFile.exists() && oldConfigFile.exists())
        {
            boolean success = oldConfigFile.renameTo(configFile);
            if (success) LOGGER.info("Renamed legacy config file!");
            else LOGGER.error("Failed to rename legacy config file!");
        }
        Config.HANDLER.load();

        isLogarithmicVolumeControlInstalled = isModLoaded("logarithmic-volume-control");

        LOGGER.info("{} initialized", MOD_NAME);
    }

    protected final void registerCommands(CommandDispatcher<?> dispatcher)
    {
        AudioImprovementsCommand.register(dispatcher);
    }

    public final Path getConfigFilePath()
    {
        return configFilePath;
    }

    public boolean isLogarithmicVolumeControlInstalled()
    {
        return isLogarithmicVolumeControlInstalled;
    }


    // Feature stuff

    public boolean isSettingChannelPosition = false;
    public final Map<Integer, Vec3> directSourcePositionChanges = new HashMap<>();
    public final Set<Channel> musicBlockChannels = Collections.newSetFromMap(new WeakHashMap<>());
    public final Set<SoundInstance> musicDiscSoundInstances = Collections.newSetFromMap(new WeakHashMap<>());
    public float musicVolumeMultiplier = 1f;
    public Identifier lastPlayedMusic = null;
    public boolean skipNextListenerDopplerVelocityUpdate = true;

    private boolean shouldFadeMusic = false;
    private int noteBlockMusicFadeTicks = 0;

    public void tick()
    {
        shouldFadeMusic = false;

        if (Minecraft.getInstance().player != null)
        {
            if (noteBlockMusicFadeTicks > 0)
            {
                shouldFadeMusic = true;
                noteBlockMusicFadeTicks --;
            }

            Vec3 listenerPos = CrossVersionUtil.getListenerPos();
            for (Channel channel : musicBlockChannels)
            {
                if (!channel.playing()) continue;
                SoundChannelMixinAccessor mixinAccessor = (SoundChannelMixinAccessor) channel;
                SoundChannelType soundType = mixinAccessor.audioImprovements$getSoundType();

                switch (soundType)
                {
                    case MUSIC_DISC -> { if (!config().fadeMusicWhenMusicDiscPlaying) continue; }
                    case NOTE_BLOCK -> { if (!config().fadeMusicWhenNoteBlockPlaying) continue; }
                    default -> { continue; }
                }

                Vec3 pos = mixinAccessor.audioImprovements$getPos();
                if (pos != null && listenerPos.distanceTo(pos) < 0.95f * mixinAccessor.audioImprovements$getMaxDistance())
                {
                    shouldFadeMusic = true;
                    if (soundType == SoundChannelType.NOTE_BLOCK) noteBlockMusicFadeTicks = 60;
                }
            }
        }
        else
        {
            noteBlockMusicFadeTicks = 0;
        }
    }

    public void onLeaveLevel()
    {
        isSettingChannelPosition = false;
        directSourcePositionChanges.clear();
        musicDiscSoundInstances.clear();
        musicBlockChannels.clear();
        noteBlockMusicFadeTicks = 0;
    }

    public boolean shouldFadeMusic()
    {
        return shouldFadeMusic;
    }

    public boolean isSoundTypeMono(SoundChannelType type)
    {
        if (type == null) return false;
        return switch (type)
        {
            case MUSIC_DISC -> config().monoMusicDiscs;
            case NOTE_BLOCK -> config().monoNoteBlocks;
            case WEATHER -> config().monoWeather;
            case BLOCKS -> config().monoBlocks;
            case HOSTILE -> config().monoHostile;
            case NEUTRAL -> config().monoNeutral;
            case PLAYERS -> config().monoPlayers;
            case AMBIENT -> config().monoAmbient;
        };
    }

    public float getAttenuationMultiplier(SoundChannelType type)
    {
        // NOTE: this only applies to sounds that have already started playing
        // because the server doesn't send the sound to a player if the distance
        // between them is larger than the original attenuation distance
        if (type == SoundChannelType.MUSIC_DISC)
        {
            float value = config().musicDiscDistanceMultiplier;
            if (value > 3f) return -1f;
            return value;
        }
        return 1f;
    }

    public int getSpeedOfSound(SoundInstance soundInstance)
    {
        Identifier identifier = CrossVersionUtil.getSoundInstanceId(soundInstance);
        if ("minecraft".equals(identifier.getNamespace()))
        {
            int value = switch (identifier.getPath())
            {
                case "entity.lightning_bolt.impact", "entity.lightning_bolt.thunder"
                    -> config().speedOfSoundThunder;
                case "entity.generic.explode", "entity.dragon_fireball.explode"
                    -> config().speedOfSoundExplosions;
                default -> -1;
            };
            if (value >= 0) return value;
        }

        return config().speedOfSoundOther;
    }

    public void updateDopplerEffect()
    {
        AL10.alDopplerFactor(config().dopplerEffectIntensity);
        AL11.alSpeedOfSound(343f);
    }

    public void checkSourcePositionUpdate(int source, float x, float y, float z)
    {
        if (isSettingChannelPosition) return;
        directSourcePositionChanges.put(source, new Vec3(x, y, z));
        LOGGER.debug("Direct source position update detected for source {}", source);
    }

    public float processVolume(float volume)
    {
        if (
            Config.HANDLER.instance().logarithmicVolumeSlidersEnabled
            && !isLogarithmicVolumeControlInstalled()
        ) {
            return volume * volume;
        }

        return volume;
    }
}

package namelessju.audioimprovements;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.data.SoundChannelType;
import namelessju.audioimprovements.gui.screens.MainConfigScreen;
import namelessju.audioimprovements.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

public abstract class AudioImprovements
{
    public static final String MOD_ID = "audioimprovements";
    public static final String MOD_NAME = "Audio Improvements";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    
    private static AudioImprovements instance;
    
    public static AudioImprovements getInstance()
    {
        return instance;
    }
    
    
    public boolean openConfigNextTick = false;
    
    public boolean isSettingChannelPosition = false;
    public final Map<Integer, Vec3> directSourcePositionChanges = new HashMap<>();
    public boolean skipNextListenerDopplerVelocityUpdate = true;
    public final Set<Channel> musicBlockChannels = Collections.newSetFromMap(new WeakHashMap<>());
    public float musicVolumeMultiplier = 1f;
    public @Nullable ResourceLocation lastPlayedMusic = null;
    public final Set<SoundInstance> musicDiscSoundInstances = Collections.newSetFromMap(new WeakHashMap<>());
    
    
    public final ConfigImpl config = new ConfigImpl(getConfigDir());
    
    public AudioImprovements()
    {
        instance = this;
    }
    
    protected final void init()
    {
        config.load();
    }
    
    protected abstract Path getConfigDir();
    
    public Screen createConfigScreen(Screen parent)
    {
        return new MainConfigScreen(parent, config);
    }
    
    public boolean isSoundTypeMono(@Nullable SoundChannelType type)
    {
        if (type == null) return false;
        return switch (type)
        {
            case MUSIC_DISC -> config.monoMusicDiscs.value;
            case NOTE_BLOCK -> config.monoNoteBlocks.value;
            case WEATHER -> config.monoWeather.value;
            case BLOCKS -> config.monoBlocks.value;
            case HOSTILE -> config.monoHostile.value;
            case NEUTRAL -> config.monoNeutral.value;
            case PLAYERS -> config.monoPlayers.value;
            case AMBIENT -> config.monoAmbient.value;
        };
    }
    
    public float getAttenuationMultiplier(@Nullable SoundChannelType type)
    {
        // NOTE: this only applies to sounds that have already started playing
        // because the server doesn't send the sound to a player if the distance
        // between them is larger than the original attenuation distance
        if (type == SoundChannelType.MUSIC_DISC)
        {
            float value = config.musicDiscDistanceMultiplier.getValue();
            if (value > 3f) return -1f;
            return value;
        }
        return 1f;
    }
    
    public boolean shouldFadeMusic()
    {
        Vec3 listenerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (Channel channel : musicBlockChannels)
        {
            if (!channel.playing()) continue;
            SoundChannelMixinAccessor mixinAccessor = (SoundChannelMixinAccessor) channel;
            
            switch (mixinAccessor.audioImprovements$getSoundType())
            {
                case MUSIC_DISC -> { if (!config.fadeMusicWhenMusicDiscPlaying.value) continue; }
                case NOTE_BLOCK -> { if (!config.fadeMusicWhenNoteBlockPlaying.value) continue; }
                default -> { continue; }
            }
            
            Vec3 pos = mixinAccessor.audioImprovements$getPos();
            if (pos != null &&
                listenerPos.distanceTo(pos) < 0.95f * mixinAccessor.audioImprovements$getMaxDistance())
            {
                return true;
            }
        }
        return false;
    }
    
    public int getSoundSpeed(SoundInstance soundInstance)
    {
        if ("minecraft".equals(soundInstance.getLocation().getNamespace()))
        {
            int value = switch (soundInstance.getLocation().getPath())
            {
                case "entity.lightning_bolt.impact",
                     "entity.lightning_bolt.thunder" -> config.soundSpeedThunder.getValue();
                case "entity.generic.explode",
                     "entity.dragon_fireball.explode" -> config.soundSpeedExplosions.getValue();
                default -> -1;
            };
            if (value >= 0) return value;
        }
        
        return config.soundSpeedOther.getValue();
    }
    
    public void updateDopplerEffect()
    {
        AL10.alDopplerFactor(config.dopplerEffectIntensity.getValue());
        AL11.alSpeedOfSound(343f);
    }
    
    public void checkSourcePositionUpdate(int source, float x, float y, float z)
    {
        if (isSettingChannelPosition) return;
        directSourcePositionChanges.put(source, new Vec3(x, y, z));
        AudioImprovements.LOGGER.debug("Direct source position update detected for source {}", source);
    }
}
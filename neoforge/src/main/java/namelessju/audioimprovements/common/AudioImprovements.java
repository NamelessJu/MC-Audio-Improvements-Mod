package namelessju.audioimprovements.common;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.common.data.SoundChannelType;
import namelessju.audioimprovements.common.gui.ConfigScreen;
import namelessju.audioimprovements.common.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public abstract class AudioImprovements
{
    public static final String MOD_ID = "audioimprovements";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AudioImprovements instance;
    
    public static AudioImprovements getInstance()
    {
        return instance;
    }
    
    
    public boolean openConfigNextTick = false;
    public final Set<Channel> musicBlockChannels = new HashSet<>();
    public ResourceLocation lastPlayedMusic = null;
    
    
    public final ConfigImpl config = new ConfigImpl(getConfigDir());
    
    public AudioImprovements()
    {
        instance = this;
    }
    
    protected void init()
    {
        config.load();
    }
    
    public Screen createConfigScreen(Screen parent)
    {
        return new ConfigScreen(parent, config);
    }
    
    public boolean isSoundTypeMono(@Nullable SoundChannelType type)
    {
        if (type == null) return false;
        return switch (type)
        {
            case MUSIC_DISC -> config.monoMusicDiscs.isEnabled;
            case NOTE_BLOCK -> config.monoNoteBlocks.isEnabled;
            case WEATHER -> config.monoWeather.isEnabled;
            case BLOCKS -> config.monoBlocks.isEnabled;
            case HOSTILE -> config.monoHostile.isEnabled;
            case NEUTRAL -> config.monoNeutral.isEnabled;
            case PLAYERS -> config.monoPlayers.isEnabled;
            case AMBIENT -> config.monoAmbient.isEnabled;
        };
    }
    
    public float getAttenuationMultiplier(@Nullable SoundChannelType type)
    {
        // NOTE: this only applies to sounds that have already started playing
        // because the server doesn't send the sound to a player if the distance
        // between them is larger than the original attenuation distance
        if (type == SoundChannelType.MUSIC_DISC)
        {
            int value = config.maxDistancePercentMusicDiscs.getValue();
            if (value > 300) return -1f;
            return value / 100f;
        }
        return 1f;
    }
    
    public boolean shouldFadeMusic()
    {
        Vec3 listenerPos = Minecraft.getInstance().getSoundManager().getListenerTransform().position();
        for (Channel channel : musicBlockChannels)
        {
            if (!channel.playing()) continue;
            SoundChannelMixinAccessor mixinAccessor = (SoundChannelMixinAccessor) channel;
            
            switch (mixinAccessor.audioImprovements$getSoundType())
            {
                case MUSIC_DISC -> { if (!config.fadeMusicWhenMusicDiscPlaying.isEnabled) continue; }
                case NOTE_BLOCK -> { if (!config.fadeMusicWhenNoteBlockPlaying.isEnabled) continue; }
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
    
    protected abstract Path getConfigDir();
}
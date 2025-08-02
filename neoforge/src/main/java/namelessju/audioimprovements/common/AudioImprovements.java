package namelessju.audioimprovements.common;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.common.gui.ConfigScreen;
import namelessju.audioimprovements.common.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec3;
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
    public Set<Channel> musicDiscChannels = new HashSet<>();
    
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
    
    public boolean isSoundTypeMono(SoundType type)
    {
        return switch (type)
        {
            case MUSIC_DISC -> config.monoMusicDiscs.value;
            case OTHER -> config.monoOther.value;
        };
    }
    
    public boolean isMusicDiscPlayingNearby()
    {
        Vec3 listenerPos = Minecraft.getInstance().getSoundManager().getListenerTransform().position();
        for (Channel channel : musicDiscChannels)
        {
            if (!channel.playing()) continue;
            SoundChannelMixinAccessor mixinAccessor = (SoundChannelMixinAccessor) channel;
            Vec3 pos = mixinAccessor.audioImprovements$getPos();
            if (pos != null && listenerPos.distanceTo(pos) < 60f)
            {
                return true;
            }
        }
        return false;
    }
    
    protected abstract Path getConfigDir();
}
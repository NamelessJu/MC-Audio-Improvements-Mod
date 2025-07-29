package namelessju.audioimprovements;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.Source;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class AudioImprovementsClient implements ClientModInitializer
{
    public static final String MOD_ID = "audioimprovements";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AudioImprovementsClient instance;
    
    public static AudioImprovementsClient getInstance()
    {
        return instance;
    }
    
    public static Screen createConfigScreen(Screen parent)
    {
        return AutoConfig.getConfigScreen(AudioImprovementsConfig.class, parent).get();
    }
    
    
    public boolean openConfigNextTick = false;
    public Set<Source> musicDiscSources = new HashSet<>();
    
    private AudioImprovementsConfig config;
    
    @Override
    public void onInitializeClient()
    {
        instance = this;
        
        AutoConfig.register(AudioImprovementsConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(AudioImprovementsConfig.class).getConfig();
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ConfigCommand.register(dispatcher);
        });
    }
    
    public boolean isSoundTypeMono(SoundType type)
    {
        return switch (type)
        {
            case MUSIC_DISC -> config.monoMusicDiscs;
            case OTHER -> config.monoOther;
        };
    }
    
    public boolean isMusicDiscPlayingNearby()
    {
        Vec3d listenerPos = MinecraftClient.getInstance().getSoundManager().getListenerTransform().position();
        for (Source source : musicDiscSources)
        {
            if (!source.isPlaying()) continue;
            SoundSourceMixinAccessor mixinAccessor = (SoundSourceMixinAccessor) source;
            Vec3d pos = mixinAccessor.audioImprovements$getPos();
            if (pos != null && listenerPos.distanceTo(pos) < 60f)
            {
                return true;
            }
        }
        return false;
    }
    
    public AudioImprovementsConfig getConfig()
    {
        return config;
    }
}
package namelessju.audioimprovements;

import com.mojang.blaze3d.audio.Channel;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Mod(value = AudioImprovements.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AudioImprovements.MOD_ID, value = Dist.CLIENT)
public class AudioImprovements
{
    public static final String MOD_ID = "audioimprovements";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AudioImprovements instance;
    
    public static AudioImprovements getInstance()
    {
        return instance;
    }
    
    public static Screen createConfigScreen(Screen parent)
    {
        return AutoConfig.getConfigScreen(AudioImprovementsConfig.class, parent).get();
    }
    
    
    public boolean openConfigNextTick = false;
    public Set<Channel> musicDiscChannels = new HashSet<>();
    
    private AudioImprovementsConfig config;
    
    public AudioImprovements(ModContainer container)
    {
        instance = this;
        
        container.registerExtensionPoint(IConfigScreenFactory.class, (ModContainer var1, Screen parent) -> createConfigScreen(parent));
    }
    
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event)
    {
        AutoConfig.register(AudioImprovementsConfig.class, GsonConfigSerializer::new);
        instance.config = AutoConfig.getConfigHolder(AudioImprovementsConfig.class).getConfig();
    }
    
    @SubscribeEvent
    private static void onCommandRegistration(RegisterClientCommandsEvent event)
    {
        ConfigCommand.register(event.getDispatcher());
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
    
    public AudioImprovementsConfig getConfig()
    {
        return config;
    }
}
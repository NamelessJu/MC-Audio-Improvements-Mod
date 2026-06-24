package namelessju.audioimprovements;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(AudioImprovements.MOD_ID)
public final class AudioImprovementsForge extends AudioImprovements
{
    public AudioImprovementsForge(FMLJavaModLoadingContext context)
    {
        super();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientInit(context));
    }
    
    private void clientInit(ModLoadingContext context)
    {
        MixinExtrasBootstrap.init();
        
        MinecraftForge.EVENT_BUS.register(this);
        
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
            (mc, parent) -> createConfigScreen(parent)
        ));
        
        init();
    }
    
    @SubscribeEvent
    public void onCommandRegistration(final RegisterClientCommandsEvent event)
    {
        ConfigCommand.register(event.getDispatcher());
    }
    
    @Override
    protected Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }
}

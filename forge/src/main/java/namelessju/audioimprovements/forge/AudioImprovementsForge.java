package namelessju.audioimprovements.forge;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(AudioImprovements.MOD_ID)
@Mod.EventBusSubscriber(modid = AudioImprovements.MOD_ID, value = Dist.CLIENT)
public final class AudioImprovementsForge extends AudioImprovements
{
    public AudioImprovementsForge(FMLJavaModLoadingContext context)
    {
        super();
        
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
            (mc, parent) -> createConfigScreen(parent)
        ));
    }
    
    @SubscribeEvent
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        init();
    }
    
    @SubscribeEvent
    private void onCommandRegistration(final RegisterClientCommandsEvent event)
    {
        ConfigCommand.register(event.getDispatcher());
    }
    
    @Override
    protected Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }
}

package namelessju.audioimprovements.neoforge;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigCommand;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.nio.file.Path;

@Mod(value = AudioImprovements.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AudioImprovements.MOD_ID, value = Dist.CLIENT)
public class AudioImprovementsNeoForge extends AudioImprovements
{
    public AudioImprovementsNeoForge(ModContainer container)
    {
        super();
        init();
        
        container.registerExtensionPoint(IConfigScreenFactory.class, (ModContainer var1, Screen parent) -> createConfigScreen(parent));
    }
    
    @SubscribeEvent
    private static void onCommandRegistration(RegisterClientCommandsEvent event)
    {
        ConfigCommand.register(event.getDispatcher());
    }
    
    @Override
    protected Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }
}
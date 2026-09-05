//? neoforge {
/*package namelessju.audio_improvements.platform.neoforge;

import namelessju.audio_improvements.AudioImprovements;
import namelessju.audio_improvements.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.nio.file.Path;

@Mod(value = AudioImprovements.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeEntrypoint extends AudioImprovements
{
	public NeoForgeEntrypoint(IEventBus modEventBus, ModContainer container)
	{
        modEventBus.addListener(this::clientSetup);

        container.registerExtensionPoint(IConfigScreenFactory.class,
            (container2, parentScreen) -> Config.HANDLER.instance().createScreen(parentScreen)
        );
	}

    private void clientSetup(FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.register(this);

        init();
    }

    @SubscribeEvent
    public void onCommandRegistration(final RegisterClientCommandsEvent event)
    {
        registerCommands(event.getDispatcher());
    }

    @Override
    protected Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    protected boolean isModLoaded(String modId)
    {
        return ModList.get().isLoaded(modId);
    }
}
*///? }

//? forge {
/*package namelessju.audio_improvements.platform.forge;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import namelessju.audio_improvements.AudioImprovements;
import namelessju.audio_improvements.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(value = AudioImprovements.MOD_ID)
public class ForgeEntrypoint extends AudioImprovements
{
	public ForgeEntrypoint(FMLJavaModLoadingContext context)
	{
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientInit(context));
	}

    private void clientInit(ModLoadingContext context)
    {
        MixinExtrasBootstrap.init();

        MinecraftForge.EVENT_BUS.register(this);
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
            (mc, parentScreen) -> Config.HANDLER.instance().createScreen(parentScreen)
        ));

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

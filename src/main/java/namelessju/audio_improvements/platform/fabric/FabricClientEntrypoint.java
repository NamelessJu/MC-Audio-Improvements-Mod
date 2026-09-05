//? fabric {
package namelessju.audio_improvements.platform.fabric;

import namelessju.audio_improvements.AudioImprovements;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public class FabricClientEntrypoint extends AudioImprovements implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientCommandRegistrationCallback.EVENT.register(
            (dispatcher, context) -> registerCommands(dispatcher)
        );

        init();
    }

    @Override
    protected Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    protected boolean isModLoaded(String modId)
    {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
//? }

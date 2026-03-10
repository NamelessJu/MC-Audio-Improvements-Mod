package namelessju.audioimprovements;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class AudioImprovementsFabric extends AudioImprovements implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        init();
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
            ConfigCommand.register(dispatcher);
        });
    }
    
    @Override
    protected Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }
}

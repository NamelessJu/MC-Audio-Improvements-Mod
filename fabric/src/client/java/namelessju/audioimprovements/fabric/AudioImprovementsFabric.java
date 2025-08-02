package namelessju.audioimprovements.fabric;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class AudioImprovementsFabric extends AudioImprovements implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        init();
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ConfigCommand.register(dispatcher);
        });
    }
    
    @Override
    public Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }
}

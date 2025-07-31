package namelessju.audioimprovements;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public abstract class ConfigCommand
{
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        LiteralArgumentBuilder<FabricClientCommandSource> commandBuilder = LiteralArgumentBuilder.literal("audioimprovements");
        commandBuilder.executes(context -> {
            AudioImprovements.getInstance().openConfigNextTick = true;
            return SINGLE_SUCCESS;
        });
        dispatcher.register(commandBuilder);
    }
}

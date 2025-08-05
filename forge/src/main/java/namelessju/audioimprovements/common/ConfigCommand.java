package namelessju.audioimprovements.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public abstract class ConfigCommand
{
    public static <T> void register(CommandDispatcher<T> dispatcher)
    {
        LiteralArgumentBuilder<T> commandBuilder = LiteralArgumentBuilder.literal("audioimprovements");
        commandBuilder.executes(context -> {
            AudioImprovements.getInstance().openConfigNextTick = true;
            return SINGLE_SUCCESS;
        });
        dispatcher.register(commandBuilder);
    }
}

package namelessju.audioimprovements;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public abstract class ConfigCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> commandBuilder = LiteralArgumentBuilder.literal("audioimprovements");
        commandBuilder.executes(context -> {
            AudioImprovements.getInstance().openConfigNextTick = true;
            return SINGLE_SUCCESS;
        });
        dispatcher.register(commandBuilder);
    }
}

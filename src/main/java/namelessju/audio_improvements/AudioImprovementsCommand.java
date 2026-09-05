package namelessju.audio_improvements;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;

public final class AudioImprovementsCommand
{
    private AudioImprovementsCommand() {}

    public static <T> void register(CommandDispatcher<T> dispatcher)
    {
        dispatcher.register(LiteralArgumentBuilder.<T>literal("audioimprovements")
            .executes(context -> {
                Minecraft.getInstance().execute(() ->
                    //? <26.2 {
                    /*Minecraft.getInstance().setScreen(
                    *///?} else {
                    Minecraft.getInstance().gui.setScreen(
                    //?}
                        Config.HANDLER.instance().createScreen(null)
                    )
                );
                return Command.SINGLE_SUCCESS;
            }));
    }
}

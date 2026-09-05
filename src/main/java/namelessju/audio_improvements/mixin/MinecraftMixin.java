package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import namelessju.audio_improvements.mixinaccessors.MusicManagerMixinAccessor;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class MinecraftMixin
{
    @Inject(method="tick", at=@At("TAIL"))
    private void onTick(CallbackInfo ci)
    {
        if (AudioImprovements.instance() != null) AudioImprovements.instance().tick();
    }

    @Inject(method="setLevel", at=@At("TAIL"))
    private void afterSetLevel(CallbackInfo ci)
    {
        AudioImprovements.instance().directSourcePositionChanges.clear();
        ((MusicManagerMixinAccessor) Minecraft.getInstance().getMusicManager()).audioImprovements$afterJoinLevel();
        AudioImprovements.LOGGER.debug("Joined new level");
    }
}

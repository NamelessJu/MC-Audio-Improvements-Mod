package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovementsClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Inject(method="tick", at=@At("HEAD"))
    private void audioImprovements$tick(CallbackInfo ci)
    {
        AudioImprovementsClient mod = AudioImprovementsClient.getInstance();
        if (mod.openConfigNextTick)
        {
            MinecraftClient.getInstance().setScreen(AudioImprovementsClient.createConfigScreen(null));
            mod.openConfigNextTick = false;
        }
    }
}

package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(method="tick", at=@At("HEAD"))
    private void audioImprovements$tick(CallbackInfo ci)
    {
        AudioImprovements mod = AudioImprovements.getInstance();
        if (mod.openConfigNextTick)
        {
            Minecraft.getInstance().setScreen(AudioImprovements.createConfigScreen(null));
            mod.openConfigNextTick = false;
        }
    }
}

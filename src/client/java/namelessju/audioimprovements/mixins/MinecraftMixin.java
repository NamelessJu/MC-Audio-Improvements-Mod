package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.mixinaccessors.MusicManagerMixinAccessor;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(method="tick", at=@At("HEAD"))
    private void audioImprovements$beforeTick(CallbackInfo ci)
    {
        AudioImprovements mod = AudioImprovements.getInstance();
        if (mod.openConfigNextTick)
        {
            Minecraft.getInstance().setScreen(mod.createConfigScreen(null));
            mod.openConfigNextTick = false;
        }
    }
    
    @Inject(method="setLevel", at=@At("TAIL"))
    private void audioImprovements$setLevel(CallbackInfo ci)
    {
        AudioImprovements.getInstance().directSourcePositionChanges.clear();
        ((MusicManagerMixinAccessor) Minecraft.getInstance().getMusicManager()).audioImprovements$afterJoinLevel();
        AudioImprovements.LOGGER.debug("Joined new level");
    }
}

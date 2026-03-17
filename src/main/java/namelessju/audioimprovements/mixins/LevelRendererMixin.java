package namelessju.audioimprovements.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
    @ModifyExpressionValue(
        method = "playStreamingMusic(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/RecordItem;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forRecord(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
        )
    )
    private SimpleSoundInstance audioImprovements$afterMusicDiscSoundInstanceCreated(SimpleSoundInstance soundInstance)
    {
        AudioImprovements.getInstance().musicDiscSoundInstances.add(soundInstance);
        return soundInstance;
    }
}

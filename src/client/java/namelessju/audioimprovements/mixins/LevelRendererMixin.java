package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleSoundInstance.class)
public class LevelRendererMixin
{
    @Inject(method = "forJukeboxSong", at = @At("RETURN"))
    private static void audioImprovements$afterMusicDiscSoundInstanceCreated(SoundEvent soundEvent, Vec3 pos, CallbackInfoReturnable<SimpleSoundInstance> cir)
    {
        AudioImprovements.getInstance().musicDiscSoundInstances.add(cir.getReturnValue());
    }
}

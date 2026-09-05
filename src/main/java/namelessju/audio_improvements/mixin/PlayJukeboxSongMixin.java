package namelessju.audio_improvements.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >1.21.1 {
import net.minecraft.client.renderer.LevelEventHandler;
@Mixin(LevelEventHandler.class)
//? } else {
/*import net.minecraft.client.renderer.LevelRenderer;
@Mixin(LevelRenderer.class)
*///? }
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class PlayJukeboxSongMixin
{
    //? if >1.20.1 {
    @ModifyExpressionValue(
        method = "playJukeboxSong",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forJukeboxSong(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
        )
    )
    //? } else {
    /*@ModifyExpressionValue(
        //? if forge {
        /^// forge being special again...
        method = "playStreamingMusic(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/RecordItem;)V",
        ^///? } else {
        method = "playStreamingMusic",
        //? }
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forRecord(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
        )
    )
    *///? }
    private SimpleSoundInstance afterMusicDiscSoundInstanceCreated(SimpleSoundInstance soundInstance)
    {
        AudioImprovements.instance().musicDiscSoundInstances.add(soundInstance);
        return soundInstance;
    }
}

package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import namelessju.audio_improvements.CrossVersionUtil;
import namelessju.audio_improvements.data.SoundChannelType;
import namelessju.audio_improvements.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;
import java.util.Map;

//? if > 1.21.1 {
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
//? }

@Mixin(SoundEngine.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class SoundEngineMixin
{
    //? if > 1.21.1 {
    @Shadow @Final
    private Object2FloatMap<SoundSource> gainBySource;
    //? }
    @Shadow @Final
    private Options options;
    @Shadow @Final
    private Map<SoundInstance, Integer> queuedSounds;

    @Inject(method = "loadLibrary",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/SoundEngine;loaded:Z",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        )
    )
    private void onLoadLibrary(CallbackInfo ci)
    {
        AudioImprovements.instance().directSourcePositionChanges.clear();
        AudioImprovements.instance().updateDopplerEffect();
    }

    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundEngine;shouldLoopAutomatically(Lnet/minecraft/client/resources/sounds/SoundInstance;)Z",
            ordinal = 0
        ),
        cancellable = true
    )
    private void beforePlay(SoundInstance instance,
        //? if >1.21.1 {
        CallbackInfoReturnable<SoundEngine.PlayResult> cir
        //? } else {
        /*CallbackInfo ci
        *///? }
    )
    {
        if (instance.isRelative()) return;

        int soundSpeed = AudioImprovements.instance().getSpeedOfSound(instance);
        if (soundSpeed <= 0) return;

        if (queuedSounds.containsKey(instance)) return;

        int delay = Mth.floor(
            CrossVersionUtil.getListenerPos().distanceTo(
                new Vec3(instance.getX(), instance.getY(), instance.getZ())
            )
            * 1f/soundSpeed * 20f
        );
        if (delay == 0) return;
        playDelayed(instance, delay);

        //? if >1.21.1 {
        cir.cancel();
        //? } else {
        /*ci.cancel();
        *///? }
    }

    @Redirect(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1
        )
    )
    private Object redirectChannelPut(Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel, Object soundObj, Object channelHandleObj)
    {
        SoundInstance soundInstance = (SoundInstance) soundObj;
        ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle) channelHandleObj;

        channelHandle.execute(source -> {
            AudioImprovements mod = AudioImprovements.instance();
            SoundChannelMixinAccessor channelMixinAccessor = (SoundChannelMixinAccessor) source;

            if (mod.musicDiscSoundInstances.contains(soundInstance))
            {
                channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.MUSIC_DISC);
                mod.musicBlockChannels.add(source);
                AudioImprovements.LOGGER.debug("Music disc played");
            }
            else
            {
                String soundPath = CrossVersionUtil.getSoundInstanceId(soundInstance).getPath().toLowerCase(Locale.ROOT);
                if (soundPath.contains("note_block") || soundPath.contains("noteblock"))
                {
                    channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.NOTE_BLOCK);
                    mod.musicBlockChannels.add(source);
                    AudioImprovements.LOGGER.debug("Note block played");
                }
                else
                {
                    switch (soundInstance.getSource())
                    {
                        case WEATHER:
                            channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.WEATHER);
                            break;
                        case BLOCKS:
                            channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.BLOCKS);
                            break;
                        case HOSTILE:
                            channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.HOSTILE);
                            break;
                        case NEUTRAL:
                            channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.NEUTRAL);
                            break;
                        case PLAYERS:
                            channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.PLAYERS);
                            break;
                        case AMBIENT:
                            channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.AMBIENT);
                            break;
                        case MUSIC:
                            Sound sound = soundInstance.getSound();
                            if (sound != null)
                            {
                                mod.lastPlayedMusic = sound.getLocation();
                                AudioImprovements.LOGGER.debug("Played music \"{}\"", mod.lastPlayedMusic);
                            }
                            break;
                    }
                }
            }
        });

        return instanceToChannel.put(soundInstance, channelHandle);
    }

    @ModifyVariable(
        method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F",
        at = @At("HEAD"),
        argsOnly = true,
        //? if >= 26 {
        name = "volume"
        //? } else {
        /*ordinal = 0
        *///? }
    )
    private float clampCalculateVolume(float volume)
    {
        //? if >= 1.21.9 {
        return volume;
        //? } else {
        /*// Fixes MC-98200
        return Mth.clamp(volume, 0f, 1f);
        *///? }
    }

    @Inject(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("HEAD"), cancellable = true)
    private void beforeCalculateVolume(float volume, SoundSource source, CallbackInfoReturnable<Float> cir)
    {
        if (source == SoundSource.MUSIC)
        {
            float volumeMultiplier = Mth.clamp(AudioImprovements.instance().musicVolumeMultiplier, 0f, 1f);
            if (!Mth.equal(volumeMultiplier, 1f))
            {
                float sourceVolume =
                    //? if <= 1.21.1 {
                    /*this.getVolume(source);
                    *///? } else {
                    Mth.clamp(this.options.getFinalSoundSourceVolume(source), 0.0F, 1.0F) * this.gainBySource.getFloat(source);
                    //? }
                cir.setReturnValue(
                    Mth.clamp(volume, 0f, 1f) * sourceVolume
                    * Mth.clampedMap(
                        volumeMultiplier * volumeMultiplier, // logarithmic volume
                        // Note: very tiny non-zero min value to stop some Minecraft
                        // versions from automatically stopping the sound completely
                        0f, 1f, 0.0001f, 1f
                    )
                );
                cir.cancel();
            }
        }
    }


    @Shadow
    public abstract void playDelayed(SoundInstance instance, int delay);

    //? if <= 1.21.1 {
    /*@Shadow
    protected abstract float getVolume(SoundSource soundSource);
    *///? }
}

package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.data.SoundChannelType;
import namelessju.audioimprovements.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
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

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin
{
    @Shadow @Final
    private Map<SoundInstance, Integer> queuedSounds;

    @Shadow @Final
    private Object2FloatMap<SoundSource> gainBySource;
    
    @Inject(method = "loadLibrary",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/SoundEngine;loaded:Z",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        )
    )
    private void audioImprovements$onLoadLibrary(CallbackInfo ci)
    {
        AudioImprovements.getInstance().directSourcePositionChanges.clear();
        AudioImprovements.getInstance().updateDopplerEffect();
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
    private void audioImprovements$beforePlay(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir)
    {
        if (soundInstance.isRelative()) return;
        
        int soundSpeed = AudioImprovements.getInstance().getSoundSpeed(soundInstance);
        if (soundSpeed <= 0) return;
        
        if (queuedSounds.containsKey(soundInstance)) return;
        
        int delay = Mth.floor(
            Minecraft.getInstance().gameRenderer.getMainCamera().position().distanceTo(
                new Vec3(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ())
            )
            * 1f/soundSpeed * 20f
        );
        if (delay == 0) return;
        playDelayed(soundInstance, delay);
        cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
        cir.cancel();
    }
    
    @Redirect(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1
        )
    )
    private Object audioImprovements$redirectChannelPut(Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel, Object soundObj, Object channelHandleObj)
    {
        SoundInstance soundInstance = (SoundInstance) soundObj;
        ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle) channelHandleObj;
        
        channelHandle.execute(source -> {
            SoundChannelMixinAccessor channelMixinAccessor = (SoundChannelMixinAccessor) source;
            
            if (AudioImprovements.getInstance().musicDiscSoundInstances.contains(soundInstance))
            {
                channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.MUSIC_DISC);
                AudioImprovements.getInstance().musicBlockChannels.add(source);
                AudioImprovements.LOGGER.debug("Music disc played");
            }
            else
            {
                
                String soundPath = soundInstance.getIdentifier().getPath().toLowerCase(Locale.ROOT);
                if (soundPath.contains("note_block") || soundPath.contains("noteblock"))
                {
                    channelMixinAccessor.audioImprovements$setSoundType(SoundChannelType.NOTE_BLOCK);
                    AudioImprovements.getInstance().musicBlockChannels.add(source);
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
                                AudioImprovements.getInstance().lastPlayedMusic = sound.getLocation();
                                AudioImprovements.LOGGER.debug("Played music \"{}\"", AudioImprovements.getInstance().lastPlayedMusic);
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
        ordinal = 0
    )
    private float audioImprovements$clampCalculateVolume(float f)
    {
        // Fixes MC-98200
        return Mth.clamp(f, 0f, 1f);
    }
    
    @Inject(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("HEAD"), cancellable = true)
    private void audioImprovements$beforeCalculateVolume(float baseVolume, SoundSource soundSource, CallbackInfoReturnable<Float> cir)
    {
        if (soundSource == SoundSource.MUSIC)
        {
            float volumeMultiplier = AudioImprovements.getInstance().musicVolumeMultiplier;
            if (volumeMultiplier != 1f)
            {
                float sourceVolume = gainBySource.getOrDefault(soundSource, 0f);
                // Note: very tiny non-zero min value to stop this Minecraft
                // version from automatically stopping the sound completely
                cir.setReturnValue(Mth.clamp(baseVolume * sourceVolume * volumeMultiplier, 0.00001f, 1f));
                
                if (AudioImprovements.LOGGER.isDebugEnabled())
                {
                    AudioImprovements.LOGGER.debug("Calculated faded music volume as {} (base: {}, source: {}, multiplier: {})",
                        cir.getReturnValueF(),
                        baseVolume,
                        sourceVolume,
                        volumeMultiplier
                    );
                }
                
                cir.cancel();
            }
        }
    }
    
    
    @Shadow
    public abstract void playDelayed(SoundInstance soundInstance, int i);
}

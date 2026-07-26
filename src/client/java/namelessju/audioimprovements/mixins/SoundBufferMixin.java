package namelessju.audioimprovements.mixins;

import com.mojang.blaze3d.audio.SoundBuffer;
import namelessju.audioimprovements.mixinaccessors.SoundBufferMixinAccessor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Mixin(SoundBuffer.class)
public class SoundBufferMixin implements SoundBufferMixinAccessor
{
    @Shadow
    private @Nullable ByteBuffer data;
    @Shadow @Mutable @Final
    private AudioFormat format;
    
    @Override
    public void audioimprovements$convertStereoToMono()
    {
        if (data == null || format.getChannels() != 2) return;
        
        data.order(ByteOrder.LITTLE_ENDIAN);
        int samples = data.remaining() / 2;
        int frames = samples / 2;
        
        ByteBuffer mono = BufferUtils.createByteBuffer(frames * 2);
        mono.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < frames; i++)
        {
            short left = data.getShort();
            short right = data.getShort();
            short mixed = (short) ((left + right) / 2);
            mono.putShort(mixed);
        }
        mono.flip();
        data = mono;
        format = new AudioFormat(
            format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(),
            1, format.getFrameSize(), format.getFrameRate(), format.isBigEndian(),
            format.properties()
        );
    }
}

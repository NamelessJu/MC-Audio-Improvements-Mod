package namelessju.audioimprovements;

import net.minecraft.util.math.Vec3d;

public interface SoundSourceMixinAccessor
{
    void audioImprovements$setSoundType(SoundType type);
    Vec3d audioImprovements$getPos();
}

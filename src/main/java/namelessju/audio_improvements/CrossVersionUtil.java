package namelessju.audio_improvements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class CrossVersionUtil
{
    private CrossVersionUtil() {}

    public static Vec3 getListenerPos()
    {
        //? if >=26.2 {
        return Minecraft.getInstance().gameRenderer.mainCamera().position();
        //? } else if >=1.21.11 {
        /*return Minecraft.getInstance().gameRenderer.getMainCamera().position();
         *///? } else {
        /*return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
         *///? }
    }

    public static Identifier getSoundInstanceId(SoundInstance soundInstance)
    {
        //? if <1.21.11 {
        /*return soundInstance.getLocation();
         *///? } else {
        return soundInstance.getIdentifier();
        //? }
    }
}

package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.mixinaccessors.MusicManagerMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin
{
    @Inject(method="disconnect", at=@At("HEAD"))
    private void audioImprovements$disconnect(CallbackInfo ci)
    {
        AudioImprovements.getInstance().directSourcePositionChanges.clear();
        AudioImprovements.getInstance().musicBlockChannels.clear();
        ((MusicManagerMixinAccessor) Minecraft.getInstance().getMusicManager()).audioImprovements$beforeDisconnect();
        AudioImprovements.LOGGER.debug("Disconnected");
    }
}

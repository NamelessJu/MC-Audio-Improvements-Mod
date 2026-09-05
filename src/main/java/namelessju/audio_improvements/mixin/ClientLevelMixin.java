package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import namelessju.audio_improvements.mixinaccessors.MusicManagerMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class ClientLevelMixin
{
    @Inject(method="disconnect", at=@At("HEAD"))
    private void onDisconnect(CallbackInfo ci)
    {
        ((MusicManagerMixinAccessor) Minecraft.getInstance().getMusicManager()).audioImprovements$beforeDisconnect();
        AudioImprovements.instance().onLeaveLevel();
        AudioImprovements.LOGGER.debug("Disconnected from level");
    }
}

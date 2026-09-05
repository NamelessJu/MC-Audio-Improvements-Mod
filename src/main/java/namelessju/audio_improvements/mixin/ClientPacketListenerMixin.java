package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class ClientPacketListenerMixin
{
    @Inject(method = "handleMovePlayer", at = @At("HEAD"))
    private void onPlayerTeleport(ClientboundPlayerPositionPacket packet, CallbackInfo ci)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isPassenger())
        {
            AudioImprovements.instance().skipNextListenerDopplerVelocityUpdate = true;
            AudioImprovements.LOGGER.debug("Player teleportation detected");
        }
    }
}

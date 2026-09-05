//? if >1.21.1 {
package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class SoundOptionsScreenMixin
{
    @Inject(method = "addOptions", at = @At("TAIL"))
    protected void afterAddOptions(CallbackInfo ci)
    {
        if (!AudioImprovements.config().customMusicFrequencyEnabled) return;

        AbstractWidget widget = ((OptionsSubScreenAccessor) this).getList()
            .findOption(Minecraft.getInstance().options.musicFrequency());
        if (widget != null)
        {
            widget.active = false;
            widget.setTooltip(Tooltip.create(
                Component.translatable(
                        "audioImprovements.config.option.customMusicFrequency.vanillaOptionDisabledTooltip",
                        AudioImprovements.MOD_NAME
                    ).withStyle(ChatFormatting.YELLOW)
            ));
        }
    }
}
//? }

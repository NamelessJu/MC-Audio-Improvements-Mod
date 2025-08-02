package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.config.BooleanEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class SoundOptionsScreenMixin
{
    @Inject(method = "addOptions", at = @At("TAIL"))
    protected void audioImprovements$addOptions(CallbackInfo ci)
    {
        BooleanEntry overrideMusicFrequency = AudioImprovements.getInstance().config.overrideMusicFrequency;
        if (!overrideMusicFrequency.value) return;
        AbstractWidget widget = ((OptionsSubScreenAccessor) this).audioImprovements$getList()
            .findOption(Minecraft.getInstance().options.musicFrequency());
        if (widget != null)
        {
            widget.active = false;
            widget.setTooltip(Tooltip.create(
                overrideMusicFrequency.getTranslatableComponent("disabledVanillaOptionTooltip")
                .withStyle(ChatFormatting.YELLOW)
            ));
        }
    }
}

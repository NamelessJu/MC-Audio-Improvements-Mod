//? if >1.21.1 {
package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionsSubScreen.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface OptionsSubScreenAccessor
{
    @Accessor("list")
    OptionsList getList();
}
//? }

package namelessju.audioimprovements.common.gui;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WidgetBuilder
{
    public static CycleButton<Boolean> buildBooleanButton(int x, int y, int width, int height, Component name, boolean initialValue, @Nullable CycleButtonBuilderHandler<Boolean> builderHandler, @NotNull OnBooleanButtonValueChange onValueChange)
    {
        CycleButton.Builder<Boolean> builder = CycleButton.<Boolean>builder(value -> OptionInstance.BOOLEAN_TO_STRING.toString(null, value))
            .withValues(OptionInstance.BOOLEAN_VALUES.valueListSupplier())
            .withInitialValue(initialValue);
        if (builderHandler != null) builderHandler.build(builder);
        return builder.create(x, y, width, height, name,
            (cycleButton, value) -> onValueChange.onValueChange(cycleButton, Boolean.TRUE.equals(value))
        );
    }
    
    public static Button buildDoneButton(Screen screen)
    {
        return buildDoneButton(screen, 200);
    }
    
    public static Button buildDoneButton(Screen screen, int width)
    {
        return Button.builder(CommonComponents.GUI_DONE, button -> screen.onClose()).width(width).build();
    }
    
    
    public interface CycleButtonBuilderHandler<T>
    {
        void build(CycleButton.Builder<T> builder);
    }
    
    public interface OnBooleanButtonValueChange
    {
        void onValueChange(CycleButton<Boolean> button, boolean value);
    }
    
    private WidgetBuilder() {}
}

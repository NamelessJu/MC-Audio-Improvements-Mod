package namelessju.audioimprovements.common.gui;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WidgetFactory
{
    public static CycleButton<Boolean> buildBooleanButton(int x, int y, int width, int height, Component name, boolean initialValue, @Nullable Consumer<CycleButton.Builder<Boolean>> builderConsumer, @NotNull BiConsumer<CycleButton<Boolean>, Boolean> onValueChange)
    {
        CycleButton.Builder<Boolean> builder = CycleButton.builder(value -> OptionInstance.BOOLEAN_TO_STRING.toString(null, value));
        if (builderConsumer != null) builderConsumer.accept(builder);
        builder.withValues(OptionInstance.BOOLEAN_VALUES.valueListSupplier())
            .withInitialValue(initialValue);
        return builder.create(x, y, width, height, name,
            (cycleButton, value) -> onValueChange.accept(cycleButton, Boolean.TRUE.equals(value))
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
    
    private WidgetFactory() {}
}

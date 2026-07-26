package namelessju.audioimprovements.gui.components.slider;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class AbstractSlider<T> extends AbstractSliderButton
{
    @NotNull
    private final Component name;
    @NotNull
    private Function<T, Component> valueComponentSupplier;
    
    public AbstractSlider(int x, int y, int width, int height,
                          @NotNull Component name,
                          @NotNull Function<T, Component> valueComponentSupplier
    )
    {
        super(x, y, width, height, Component.empty(), 0);
        this.name = name;
        this.valueComponentSupplier = valueComponentSupplier;
    }
    
    public void setValueComponentSupplier(@NotNull Function<T, Component> valueComponentSupplier)
    {
        this.valueComponentSupplier = valueComponentSupplier;
        updateMessage();
    }
    
    @Override
    public void updateMessage()
    {
        this.setMessage(Component.empty()
            .append(name)
            .append(Component.literal(": "))
            .append(valueComponentSupplier.apply(getValue()))
        );
    }
    
    public abstract T getValue();
}

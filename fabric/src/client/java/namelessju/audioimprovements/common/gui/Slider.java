package namelessju.audioimprovements.common.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Slider<T> extends AbstractSliderButton
{
    @NotNull
    private final Component name;
    @NotNull
    private ValueComponentProvider<T> valueComponentSupplier;
    
    public Slider(int x, int y, int width, int height,
          @NotNull Component name,
          @NotNull ValueComponentProvider<T> valueComponentSupplier
    )
    {
        super(x, y, width, height, Component.empty(), 0);
        this.name = name;
        this.valueComponentSupplier = valueComponentSupplier;
    }
    
    public void setValueComponentSupplier(@NotNull ValueComponentProvider<T> valueComponentSupplier)
    {
        this.valueComponentSupplier = valueComponentSupplier;
        updateMessage();
    }
    
    @Override
    protected void updateMessage()
    {
        this.setMessage(Component.empty()
            .append(name)
            .append(Component.literal(": "))
            .append(valueComponentSupplier.getComponent(getValue()))
        );
    }
    
    public abstract T getValue();
    
    
    public interface ValueComponentProvider<T>
    {
        @Nullable Component getComponent(T value);
    }
}

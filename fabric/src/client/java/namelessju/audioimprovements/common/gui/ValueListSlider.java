package namelessju.audioimprovements.common.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ValueListSlider<T extends ValueListSlider.Value> extends AbstractSliderButton
{
    @NotNull
    private final Component name;
    @NotNull
    private final BiConsumer<Integer, T> onValueChanged;
    private final T[] values;
    
    public ValueListSlider(
        int x,
        int y,
        int width,
        int height,
        @NotNull Component name,
        @NotNull T[] values,
        @NotNull T initialValue,
        @NotNull BiConsumer<Integer, T> onValueChanged
    )
    {
        super(x, y, width, height, Component.empty(), 0);
        this.values = values;
        this.name = name;
        this.onValueChanged = onValueChanged;
        setValue(initialValue);
        this.updateMessage();
    }
    
    @Override
    protected void updateMessage()
    {
        MutableComponent message = Component.empty();
        message.append(name);
        T value = getCurrentValue();
        message.append(Component.literal(": "))
            .append(value.getValueComponent());
        Component suffixComponent = value.getSuffixComponent();
        if (suffixComponent != null) message.append(" ").append(suffixComponent);
        this.setMessage(message);
    }
    
    private void updateValue(int index)
    {
        this.value = Mth.clamp(indexToSliderValue(index), 0D, 1D); // Snap value to index values
        onValueChanged.accept(index, getCurrentValue());
    }
    
    @Override
    protected void applyValue()
    {
        updateValue(sliderValueToIndex());
    }
    
    @Override
    public boolean keyPressed(int i, int j, int k)
    {
        double valueBefore = value;
        if (super.keyPressed(i, j, k))
        {
            value = valueBefore;
            boolean keyLeftPressed = i == 263;
            if (keyLeftPressed || i == 262)
            {
                setIndex(sliderValueToIndex() + (keyLeftPressed ? -1 : 1));
            }
            
            return true;
        }
        
        return false;
    }
    
    public void setIndex(int index)
    {
        updateValue(index);
        updateMessage();
    }
    
    public void setValue(T value)
    {
        int index = -1;
        for (int i = 0; i < values.length; i++)
        {
            if (value.equals(values[i]))
            {
                index = i;
                break;
            }
        }
        if (index == -1) return;
        setIndex(index);
    }
    
    private int sliderValueToIndex()
    {
        return (int) Math.round((values.length - 1) * value);
    }
    
    private double indexToSliderValue(int integer)
    {
        return integer / (double) (values.length - 1);
    }
    
    private T getCurrentValue()
    {
        return values[Mth.clamp(sliderValueToIndex(), 0, values.length - 1)];
    }
    
    public interface Value
    {
        @NotNull Component getValueComponent();
        
        @Nullable Component getSuffixComponent();
    }
}

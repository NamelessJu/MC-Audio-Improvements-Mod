package namelessju.audioimprovements.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ValueListSlider<T> extends Slider<T>
{
    private final T[] values;
    
    @NotNull
    private final BiConsumer<Integer, T> onValueChanged;
    
    public ValueListSlider(
        int x, int y,
        int width, int height,
        @NotNull Component name,
        @NotNull ValueComponentProvider<T> valueComponentProvider,
        @NotNull T[] values,
        @NotNull T initialValue,
        @NotNull BiConsumer<Integer, T> onValueChanged
    )
    {
        super(x, y, width, height, name, valueComponentProvider);
        this.values = values;
        this.onValueChanged = onValueChanged;
        setValue(initialValue);
        this.updateMessage();
    }
    
    private void updateValue(int index)
    {
        this.value = Mth.clamp(indexToSliderValue(index), 0D, 1D); // Snap value to index values
        onValueChanged.accept(index, getValue());
    }
    
    @Override
    protected void applyValue()
    {
        updateValue(sliderValueToIndex());
    }
    
    @Override
    public T getValue()
    {
        return values[Mth.clamp(sliderValueToIndex(), 0, values.length - 1)];
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
}

package namelessju.audioimprovements.gui.components.slider;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ValueListSlider<T> extends AbstractSlider<T>
{
    public static <T> T getClosestValidValueFromNumber(T[] values, Number numValue, Function<T, Number> valueToNumberFunction)
    {
        Stream<T> sortedValues = Arrays.stream(values).sorted(Comparator.comparingDouble(a -> valueToNumberFunction.apply(a).doubleValue()));
        T closestValue = null;
        double closestDistance = -1D;
        for (Iterator<T> iterator = sortedValues.iterator(); iterator.hasNext(); )
        {
            T validValue = iterator.next();
            double distance = Math.abs(numValue.doubleValue() - valueToNumberFunction.apply(validValue).doubleValue());
            if (closestDistance < 0 || distance < closestDistance)
            {
                closestDistance = distance;
                closestValue = validValue;
            }
            else break;
        }
        return closestValue;
    }
    
    
    private final T[] values;
    
    @NotNull
    private final BiConsumer<Integer, T> onValueChanged;
    
    public ValueListSlider(
        int x, int y,
        int width, int height,
        @NotNull Component name,
        @NotNull Function<T, Component> valueComponentProvider,
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
        this.value = Mth.clamp(indexToSliderValue(index), 0D, 1D); // Snap numValue to index values
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
    public boolean keyPressed(KeyEvent keyEvent)
    {
        int keyCode = keyEvent.key();
        double valueBefore = value;
        if (super.keyPressed(keyEvent))
        {
            value = valueBefore;
            boolean keyLeftPressed = keyCode == 263;
            if (keyLeftPressed || keyCode == 262)
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

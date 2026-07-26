package namelessju.audioimprovements.gui.components.slider;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class IntegerSlider extends AbstractSlider<Integer>
{
    public static final Function<Integer, Component> DEFAULT_COMPONENT_SUPPLIER = value -> Component.literal(Integer.toString(value));
    
    private final int min;
    private final int max;
    public int stepSize = -1;
    
    @NotNull
    private final Consumer<Integer> onValueChanged;
    
    public IntegerSlider(
        int x, int y, int width, int height,
        @NotNull Component name,
        int min, int max, int initialValue,
        @NotNull Consumer<Integer> onValueChanged
    )
    {
        super(x, y, width, height, name, DEFAULT_COMPONENT_SUPPLIER);
        this.min = min;
        this.max = max;
        this.onValueChanged = onValueChanged;
        updateValue(initialValue);
        updateMessage();
    }
    
    @Override
    public Integer getValue()
    {
        return sliderValueToInt();
    }
    
    @Override
    protected void applyValue()
    {
        int value = sliderValueToInt();
        updateValue(value);
        onValueChanged.accept(value);
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
                setValue(sliderValueToInt() + (keyLeftPressed ? -stepSize : stepSize));
            }
            
            return true;
        }
        
        return false;
    }
    
    private void updateValue(int value)
    {
        this.value = Mth.clamp(intToSliderValue(value), 0D, 1D);
    }
    
    public void setValue(int value)
    {
        updateValue(value);
        onValueChanged.accept(sliderValueToInt());
        updateMessage();
    }
    
    private int sliderValueToInt()
    {
        int stepSize = this.stepSize > 0 ? this.stepSize : 1;
        float relativeValue = ((max - min) * (float) value);
        return min + Math.round(relativeValue / stepSize) * stepSize;
    }
    
    private double intToSliderValue(int integer)
    {
        return (integer - min) / (double) (max - min);
    }
}

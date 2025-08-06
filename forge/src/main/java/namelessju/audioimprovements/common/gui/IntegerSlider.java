package namelessju.audioimprovements.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IntegerSlider extends Slider<Integer>
{
    public static final ValueComponentProvider<Integer> DEFAULT_COMPONENT_SUPPLIER = value -> Component.literal(Integer.toString(value));
    
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
        updateValue(sliderValueToInt());
        onValueChanged.accept(sliderValueToInt());
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
        float relativeValue = (float) ((max - min) * value);
        return min + Math.round(relativeValue / stepSize) * stepSize;
    }
    
    private double intToSliderValue(int integer)
    {
        return (integer - min) / (double) (max - min);
    }
}

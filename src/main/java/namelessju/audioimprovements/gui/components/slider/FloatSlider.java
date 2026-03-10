package namelessju.audioimprovements.gui.components.slider;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class FloatSlider extends AbstractSlider<Float>
{
    public static final Function<Float, Component> DEFAULT_COMPONENT_SUPPLIER = value -> Component.literal(Float.toString(value));
    
    private final float min;
    private final float max;
    public float stepSize = -1f;
    
    @NotNull
    private final Consumer<Float> onValueChanged;
    
    public FloatSlider(
        int x, int y, int width, int height,
        @NotNull Component name,
        float min, float max, float initialValue,
        @NotNull Consumer<Float> onValueChanged
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
    public Float getValue()
    {
        return sliderValueToFloat();
    }
    
    @Override
    protected void applyValue()
    {
        float value = sliderValueToFloat();
        updateValue(value);
        onValueChanged.accept(value);
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
                setValue(sliderValueToFloat() + (keyLeftPressed ? -stepSize : stepSize));
            }
            
            return true;
        }
        
        return false;
    }
    
    private void updateValue(float value)
    {
        this.value = Mth.clamp(floatToSliderValue(value), 0D, 1D);
    }
    
    public void setValue(float value)
    {
        updateValue(value);
        onValueChanged.accept(sliderValueToFloat());
        updateMessage();
    }
    
    private float sliderValueToFloat()
    {
        float relativeValue = ((max - min) * (float) value);
        return min + (
            stepSize > 0f
            ? (Math.round(relativeValue / stepSize) * stepSize)
            : relativeValue
        );
    }
    
    private double floatToSliderValue(float value)
    {
        return (value - min) / (max - min);
    }
}

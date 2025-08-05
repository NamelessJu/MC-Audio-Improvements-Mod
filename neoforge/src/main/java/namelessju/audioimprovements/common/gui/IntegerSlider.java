package namelessju.audioimprovements.common.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class IntegerSlider extends AbstractSliderButton
{
    @NotNull
    private final Component name;
    @NotNull
    private final Consumer<Integer> onValueChanged;
    private final int min;
    private final int max;
    @Nullable
    public Component suffixComponent = null;
    
    public IntegerSlider(
        int x,
        int y,
        int width,
        int height,
        @NotNull Component name,
        int min,
        int max,
        int initialValue,
        @NotNull Consumer<Integer> onValueChanged
    )
    {
        super(x, y, width, height, Component.empty(), 0);
        this.min = min;
        this.max = max;
        this.name = name;
        this.onValueChanged = onValueChanged;
        setValue(initialValue);
    }
    
    @Override
    protected void updateMessage()
    {
        MutableComponent message = Component.empty();
        message.append(name);
        message.append(Component.literal(": "))
            .append(Integer.toString(sliderValueToInt()));
        if (suffixComponent != null) message.append(" ").append(suffixComponent);
        this.setMessage(message);
    }
    
    @Override
    protected void applyValue()
    {
        int value = sliderValueToInt();
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
                setValue(sliderValueToInt() + (keyLeftPressed ? -1 : 1));
            }
            
            return true;
        }
        
        return false;
    }
    
    private void updateValue(int value)
    {
        this.value = Mth.clamp(intToSliderValue(value), 0D, 1D); // Snap value to index values;
    }
    
    public void setValue(int value)
    {
        updateValue(value);
        updateMessage();
    }
    
    private int sliderValueToInt()
    {
        return min + (int) Math.round((max - min) * value);
    }
    
    private double intToSliderValue(int integer)
    {
        return (integer - min) / (double) (max - min);
    }
}

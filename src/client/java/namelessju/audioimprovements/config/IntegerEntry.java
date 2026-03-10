package namelessju.audioimprovements.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import namelessju.audioimprovements.gui.components.slider.IntegerSlider;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class IntegerEntry extends ConfigEntry<IntegerEntry>
{
    private final int defaultValue;
    private int value;
    
    public final int minValue;
    public final int maxValue;
    
    public IntegerEntry(Config config, String key, int defaultValue, int minValue, int maxValue)
    {
        super(config, key);
        this.minValue = minValue;
        this.maxValue = maxValue;
        value = this.defaultValue = defaultValue;
    }
    
    public void setValue(int value)
    {
        this.value = Mth.clamp(value, minValue, maxValue);
    }
    
    public int getValue()
    {
        return value;
    }
    
    @Override
    protected IntegerEntry getThis()
    {
        return this;
    }
    
    @Override
    public void reset()
    {
        value = defaultValue;
    }
    
    @Override
    public boolean loadFromJsonElement(JsonElement element)
    {
        if (element.isJsonPrimitive())
        {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber())
            {
                setValue(primitive.getAsInt());
                return true;
            }
        }
        return false;
    }
    
    @Override
    public JsonElement saveToJsonElement()
    {
        return new JsonPrimitive(value);
    }
    
    public IntegerSlider createSlider(int x, int y, int width, Consumer<Integer> onValueChange)
    {
        IntegerSlider slider = new IntegerSlider(x, y, width, 20, getNameComponent(), minValue, maxValue, value,
            newValue -> {
                value = newValue;
                if (onValueChange != null) onValueChange.accept(value);
            }
        );
        Component tooltipComponent = getTooltipComponent();
        if (tooltipComponent != null) slider.setTooltip(Tooltip.create(tooltipComponent));
        return slider;
    }
}
package namelessju.audioimprovements.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import namelessju.audioimprovements.gui.components.slider.FloatSlider;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class FloatEntry extends ConfigEntry<FloatEntry>
{
    private final float defaultValue;
    private float value;
    
    public final float minValue;
    public final float maxValue;
    
    public FloatEntry(Config config, String key, float defaultValue, float minValue, float maxValue)
    {
        super(config, key);
        this.minValue = minValue;
        this.maxValue = maxValue;
        value = this.defaultValue = defaultValue;
    }
    
    public void setValue(float value)
    {
        this.value = Mth.clamp(value, minValue, maxValue);
    }
    
    public float getValue()
    {
        return value;
    }
    
    @Override
    protected FloatEntry getThis()
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
                setValue(primitive.getAsFloat());
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
    
    public FloatSlider createSlider(int x, int y, int width, Consumer<Float> onValueChange)
    {
        FloatSlider slider = new FloatSlider(x, y, width, 20, getNameComponent(), minValue, maxValue, value,
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
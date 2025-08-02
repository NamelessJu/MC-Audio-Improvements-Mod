package namelessju.audioimprovements.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import namelessju.audioimprovements.common.gui.WidgetBuilder;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;

public class BooleanEntry extends ConfigEntry
{
    private final boolean defaultValue;
    public boolean value;
    
    public BooleanEntry(Config config, String key, boolean defaultValue)
    {
        super(config, key);
        value = this.defaultValue = defaultValue;
    }
    
    @Override
    public void reset()
    {
        value = defaultValue;
    }
    
    @Override
    boolean loadFromJsonElement(JsonElement element)
    {
        if (element.isJsonPrimitive())
        {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean())
            {
                value = primitive.getAsBoolean();
                return true;
            }
        }
        return false;
    }
    
    @Override
    JsonElement saveToJsonElement()
    {
        return new JsonPrimitive(value);
    }
    
    @Override
    public AbstractWidget createWidget(int x, int y, int width)
    {
        return WidgetBuilder.buildBooleanButton(x, y, width, 20, getNameComponent(), value,
            builder -> builder.withTooltip(getTooltipSupplier()),
            (CycleButton<Boolean> button, boolean value) -> BooleanEntry.this.value = value
        );
    }
}
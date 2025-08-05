package namelessju.audioimprovements.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import namelessju.audioimprovements.common.gui.WidgetFactory;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;

import java.util.function.Consumer;

public class BooleanEntry extends ConfigEntry
{
    private final boolean defaultValue;
    public boolean isEnabled;
    
    public BooleanEntry(Config config, String key, boolean defaultValue)
    {
        super(config, key);
        isEnabled = this.defaultValue = defaultValue;
    }
    
    @Override
    public void reset()
    {
        isEnabled = defaultValue;
    }
    
    @Override
    public boolean loadFromJsonElement(JsonElement element)
    {
        if (element.isJsonPrimitive())
        {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean())
            {
                isEnabled = primitive.getAsBoolean();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public JsonElement saveToJsonElement()
    {
        return new JsonPrimitive(isEnabled);
    }
    
    public AbstractWidget createWidget(int x, int y, int width, Consumer<Boolean> onValueChange)
    {
        return WidgetFactory.buildBooleanButton(x, y, width, 20, getNameComponent(), isEnabled,
            builder -> builder.withTooltip(getTooltipSupplier()),
            (CycleButton<Boolean> button, Boolean value) -> {
                BooleanEntry.this.isEnabled = value;
                if (onValueChange != null) onValueChange.accept(value);
            }
        );
    }
}
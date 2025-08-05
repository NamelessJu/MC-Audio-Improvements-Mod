package namelessju.audioimprovements.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntegerEntry extends ConfigEntry
{
    private final int defaultValue;
    public int value;
    
    public IntegerEntry(Config config, String key, int defaultValue)
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
    public boolean loadFromJsonElement(JsonElement element)
    {
        if (element.isJsonPrimitive())
        {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber())
            {
                value = primitive.getAsInt();
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
}
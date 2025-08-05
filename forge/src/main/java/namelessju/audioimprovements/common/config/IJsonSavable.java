package namelessju.audioimprovements.common.config;

import com.google.gson.JsonElement;

public interface IJsonSavable
{
    boolean loadFromJsonElement(JsonElement element);
    JsonElement saveToJsonElement();
}

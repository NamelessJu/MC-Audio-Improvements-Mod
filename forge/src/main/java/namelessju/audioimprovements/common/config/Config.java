package namelessju.audioimprovements.common.config;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import namelessju.audioimprovements.common.AudioImprovements;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Config implements Iterable<ConfigEntry<?>>
{
    public final File file;
    
    private final List<ConfigEntry<?>> entries = new ArrayList<>();
    
    public Config(Path configDir, String fileName)
    {
        this.file = configDir.resolve(fileName.replaceAll("[\\\\/]", "")).toFile();
    }
    
    void addEntry(ConfigEntry<?> entry)
    {
        entries.add(entry);
    }
    
    public void load()
    {
        if (!file.exists())
        {
            for (ConfigEntry<?> entry : this) entry.reset();
            save();
            return;
        }
        
        Reader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
        }
        catch (FileNotFoundException e)
        {
            AudioImprovements.LOGGER.error("Error while loading config: File not found");
            return;
        }
        JsonElement json;
        try
        {
            json = JsonParser.parseReader(reader);
        }
        catch (Exception e)
        {
            AudioImprovements.LOGGER.error("Error while loading config: Failed to parse JSON");
            return;
        }
        
        boolean missingKeyFound = false;
        
        JsonObject jsonObject = json.getAsJsonObject();
        for (ConfigEntry<?> entry : this)
        {
            JsonElement element = jsonObject.get(entry.key);
            if (element != null)
            {
                if (entry.loadFromJsonElement(element)) continue;
                else logInvalidEntryValue(entry, element.toString());
            }
            else missingKeyFound = true;
            entry.reset();
        }
        
        if (missingKeyFound)
        {
            save();
            AudioImprovements.LOGGER.info("Added missing config key(s) to file");
        }
    }
    
    protected void logInvalidEntryValue(ConfigEntry<?> entry, String details)
    {
        AudioImprovements.LOGGER.warn("Invalid value in config file for entry \"{}\": {}", entry.key, details);
    }
    
    public void save()
    {
        JsonObject json = new JsonObject();
        for (ConfigEntry<?> entry : this)
        {
            json.add(entry.key, entry.saveToJsonElement());
        }
        
        Writer writer = null;
        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY.withIndent("    "));
            Streams.write(json, jsonWriter);
        }
        catch (FileNotFoundException e)
        {
            AudioImprovements.LOGGER.error("Error while saving config: Invalid or unwritable file");
        }
        catch (IOException e)
        {
            AudioImprovements.LOGGER.error("Error while saving config: Failed to write file");
        }
        finally
        {
            IOUtils.closeQuietly(writer);
        }
    }
    
    @Override
    public @NotNull Iterator<ConfigEntry<?>> iterator()
    {
        return entries.iterator();
    }
}

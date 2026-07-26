package namelessju.audioimprovements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import namelessju.audioimprovements.config.BooleanEntry;
import namelessju.audioimprovements.config.Config;
import namelessju.audioimprovements.config.FloatEntry;
import namelessju.audioimprovements.config.IntegerEntry;

import java.nio.file.Path;
import java.util.function.IntConsumer;

public class ConfigImpl extends Config
{
    // Mono
    public final BooleanEntry monoMusicDiscs = new BooleanEntry(this, "monoMusicDiscs", true);
    public final BooleanEntry monoNoteBlocks = new BooleanEntry(this, "monoNoteBlocks", true);
    public final BooleanEntry monoWeather = new BooleanEntry(this, "monoWeather", false);
    public final BooleanEntry monoBlocks = new BooleanEntry(this, "monoBlocks", false);
    public final BooleanEntry monoHostile = new BooleanEntry(this, "monoHostile", false);
    public final BooleanEntry monoNeutral = new BooleanEntry(this, "monoNeutral", false);
    public final BooleanEntry monoPlayers = new BooleanEntry(this, "monoPlayers", false);
    public final BooleanEntry monoAmbient = new BooleanEntry(this, "monoAmbient", false);
    
    // Music Clash Prevention
    public final BooleanEntry fadeMusicWhenMusicDiscPlaying
        = new BooleanEntry(this, "fadeMusicWhenMusicDiscPlaying", false)
        .withTooltip();
    public final BooleanEntry fadeMusicWhenNoteBlockPlaying
        = new BooleanEntry(this, "fadeMusicWhenNoteBlockPlaying", false)
        .withTooltip();
    public final IntegerEntry musicFadeOutTicks
        = new IntegerEntry(this, "musicFadeOutTicks", 0, 0, 200);
    public final IntegerEntry musicFadeInTicks
        = new IntegerEntry(this, "musicFadeInTicks", 0, 0, 200);
    
    // Music Discs
    public final FloatEntry musicDiscDistanceMultiplier
        = new FloatEntry(this, "musicDiscDistanceMultiplier", 1f, 0.5f, 3.1f)
        .withTooltip();
    
    // Music
    public final BooleanEntry preventMusicRepeat
        = new BooleanEntry(this, "preventMusicRepeat", true)
        .withTooltip();
    
    // Music Frequency
    public final BooleanEntry customMusicFrequency
        = new BooleanEntry(this, "customMusicFrequency", false)
        .withTooltip();
    public final IntegerEntry musicFrequencyMinTicks
        = new IntegerEntry(this, "musicFrequencyMinTicks", 20, 0, Integer.MAX_VALUE);
    public final IntegerEntry musicFrequencyMaxTicks
        = new IntegerEntry(this, "musicFrequencyMaxTicks", 5 * 60 * 20, 0, Integer.MAX_VALUE);
    public final BooleanEntry musicFrequencyAffectMenu
        = new BooleanEntry(this, "musicFrequencyAffectMenu", false);
    
    // Sound Speed Simulation
    public final IntegerEntry soundSpeedThunder
        = new IntegerEntry(this, "soundSpeedThunder", 0, 0, 343)
        .withTooltip();
    public final IntegerEntry soundSpeedExplosions
        = new IntegerEntry(this, "soundSpeedExplosions", 0, 0, 343)
        .withTooltip();
    public final IntegerEntry soundSpeedOther
        = new IntegerEntry(this, "soundSpeedOther", 0, 0, 343)
        .withTooltip();
    
    // Doppler effect
    public final FloatEntry dopplerEffectIntensity
        = new FloatEntry(this, "dopplerEffectIntensity", 0f, 0f, 5f)
        .withTooltip();
    
    // Stereo Spatialization Fix
    public final BooleanEntry stereoSpatializationFix
        = new BooleanEntry(this, "stereoSpatializationFix", true)
        .withTooltip();
    
    
    
    public ConfigImpl(Path configDir)
    {
        super(configDir, AudioImprovements.MOD_ID + ".json");
        
        musicDiscDistanceMultiplier.withUpdater(jsonObject -> updateFromInt(
            jsonObject, "maxDistancePercentMusicDiscs",
            oldValue -> musicDiscDistanceMultiplier.setValue(oldValue / 100f)
        ));
        
        musicFadeOutTicks.withUpdater(jsonObject -> updateFromInt(
            jsonObject, "musicFadeOutSeconds",
            oldValue -> musicFadeOutTicks.setValue(oldValue * 20)
        ));
        musicFadeInTicks.withUpdater(jsonObject -> updateFromInt(
            jsonObject, "musicFadeInSeconds",
            oldValue -> musicFadeInTicks.setValue(oldValue * 20)
        ));
    }
    
    private boolean updateFromInt(JsonObject jsonObject, String oldKey, IntConsumer valueConsumer)
    {
        JsonElement valueElement = jsonObject.get(oldKey);
        if (valueElement != null && valueElement.isJsonPrimitive())
        {
            JsonPrimitive primitive = valueElement.getAsJsonPrimitive();
            if (primitive.isNumber())
            {
                valueConsumer.accept(primitive.getAsInt());
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void load()
    {
        super.load();
        
        if (musicFrequencyMinTicks.getValue() > musicFrequencyMaxTicks.getValue())
        {
            musicFrequencyMinTicks.setValue(musicFrequencyMaxTicks.getValue());
            logInvalidEntryValue(musicFrequencyMinTicks, "Cannot be larger than \"" + musicFrequencyMaxTicks.key + "\"");
            save();
        }
    }
}

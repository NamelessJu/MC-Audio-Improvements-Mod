package namelessju.audioimprovements.common;

import namelessju.audioimprovements.common.config.BooleanEntry;
import namelessju.audioimprovements.common.config.Config;
import namelessju.audioimprovements.common.config.IntegerEntry;

import java.nio.file.Path;

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
    
    // Music Discs
    public final IntegerEntry maxDistancePercentMusicDiscs
        = new IntegerEntry(this, "maxDistancePercentMusicDiscs", 100, 50, 310)
        .withTooltip();
    
    // Music Clash Prevention
    public final BooleanEntry fadeMusicWhenMusicDiscPlaying
        = new BooleanEntry(this, "fadeMusicWhenMusicDiscPlaying", true)
        .withTooltip();
    public final BooleanEntry fadeMusicWhenNoteBlockPlaying
        = new BooleanEntry(this, "fadeMusicWhenNoteBlockPlaying", true)
        .withTooltip();
    public final IntegerEntry musicFadeOutSeconds
        = new IntegerEntry(this, "musicFadeOutSeconds", 2, 0, 10);
    public final IntegerEntry musicFadeInSeconds
        = new IntegerEntry(this, "musicFadeInSeconds", 4, 0, 10);
    
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
    
    public ConfigImpl(Path configDir)
    {
        super(configDir, AudioImprovements.MOD_ID + ".json");
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

package namelessju.audioimprovements.common;

import namelessju.audioimprovements.common.config.BooleanEntry;
import namelessju.audioimprovements.common.config.Config;
import namelessju.audioimprovements.common.config.IntegerEntry;

import java.nio.file.Path;

public class ConfigImpl extends Config
{
    public final BooleanEntry monoMusicDiscs = booleanBuilder("monoMusicDiscs", true).build();
    public final BooleanEntry monoOther = booleanBuilder("monoOther", false).build();
    
    public final BooleanEntry preventMusicClash
        = booleanBuilder("fadeMusicWhenMusicDiscPlaying", true)
        .withTooltip().build();
    public final BooleanEntry preventMusicRepeat
        = booleanBuilder("preventMusicRepeat", true)
        .withTooltip().build();
    public final BooleanEntry overrideMusicFrequency
        = booleanBuilder("overrideMusicFrequency", false)
        .withTooltip().build();
    public final IntegerEntry musicFrequencyMinTicks
        = integerBuilder("musicFrequencyMinTicks", 20).build();
    public final IntegerEntry musicFrequencyMaxTicks
        = integerBuilder("musicFrequencyMaxTicks", 5 * 60 * 20).build();
    
    public ConfigImpl(Path configDir)
    {
        super(configDir, AudioImprovements.MOD_ID + ".json");
    }
    
    @Override
    public void load()
    {
        super.load();
        
        boolean saveRequired = false;
        
        if (musicFrequencyMaxTicks.value < 0)
        {
            musicFrequencyMaxTicks.value = 0;
            logInvalidEntryValue(musicFrequencyMaxTicks, "Cannot be smaller than 0");
            saveRequired = true;
        }
        if (musicFrequencyMinTicks.value < 0)
        {
            musicFrequencyMinTicks.value = 0;
            logInvalidEntryValue(musicFrequencyMinTicks, "Cannot be smaller than 0");
            saveRequired = true;
        }
        else if (musicFrequencyMinTicks.value > musicFrequencyMaxTicks.value)
        {
            musicFrequencyMinTicks.value = musicFrequencyMaxTicks.value;
            logInvalidEntryValue(musicFrequencyMinTicks, "Cannot be larger than \"" + musicFrequencyMaxTicks.key + "\"");
            saveRequired = true;
        }
        
        if (saveRequired) save();
    }
}

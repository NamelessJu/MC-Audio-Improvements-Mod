package namelessju.audioimprovements.common;

import namelessju.audioimprovements.common.config.BooleanEntry;
import namelessju.audioimprovements.common.config.Config;

import java.nio.file.Path;

public class ConfigImpl extends Config
{
    public final BooleanEntry monoMusicDiscs = booleanBuilder("monoMusicDiscs", true).build();
    public final BooleanEntry monoOther = booleanBuilder("monoOther", false).build();
    public final BooleanEntry preventMusicClash =
        booleanBuilder("fadeMusicWhenMusicDiscPlaying", true)
        .withTooltip().build();
    public final BooleanEntry overrideMusicFrequency =
        booleanBuilder("overrideMusicFrequency", false)
        .withTooltip().build();
    
    public ConfigImpl(Path configDir)
    {
        super(configDir, AudioImprovements.MOD_ID + ".json");
    }
}

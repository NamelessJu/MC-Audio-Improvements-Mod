package namelessju.audioimprovements;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name= AudioImprovements.MOD_ID)
public class AudioImprovementsConfig implements ConfigData
{
    public boolean monoMusicDiscs = true;
    public boolean monoOther = false;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean fadeMusicWhenMusicDiscPlaying = true;
}

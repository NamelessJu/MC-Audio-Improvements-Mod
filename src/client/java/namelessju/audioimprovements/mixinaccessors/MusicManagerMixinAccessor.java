package namelessju.audioimprovements.mixinaccessors;

public interface MusicManagerMixinAccessor
{
    void audioImprovements$afterJoinLevel();
    void audioImprovements$beforeDisconnect();
    
    void audioImprovements$handleTickNextSongDelayOrd1(int newDelay);
    void audioImprovements$handleTickNextSongDelayOrd2(int newDelay);
}

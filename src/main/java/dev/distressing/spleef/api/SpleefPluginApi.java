package dev.distressing.spleef.api;

import dev.distressing.spleef.api.managers.AreaCreationManager;
import dev.distressing.spleef.api.managers.ArenaManager;
import dev.distressing.spleef.api.managers.GameManager;
import dev.distressing.spleef.data.SpleefDataManager;
import lombok.Getter;


public class SpleefPluginApi {
    @Getter
    private static SpleefDataManager spleefDataManager;
    @Getter
    private static AreaCreationManager areaCreationManager;
    @Getter
    private static ArenaManager arenaManager;
    @Getter
    private static GameManager gameManager;

    public static void init(SpleefDataManager sdm, AreaCreationManager acm, ArenaManager am, GameManager gm){
        spleefDataManager = sdm;
        areaCreationManager = acm;
        arenaManager = am;
        gameManager = gm;
    }
}

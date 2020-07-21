package dev.distressing.spleef.events.Player;

import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.entity.Player;

public class PlayerGameJoin extends SpleefPlayerEvent {
    private SpleefGame spleefGame;

    public PlayerGameJoin(Player player, SpleefGame spleefGame) {
        super(player);
    }
}

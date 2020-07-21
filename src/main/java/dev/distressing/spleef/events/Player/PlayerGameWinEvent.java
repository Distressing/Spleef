package dev.distressing.spleef.events.Player;

import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.entity.Player;

public class PlayerGameWinEvent extends SpleefPlayerEvent {
    private SpleefGame spleefGame;

    public PlayerGameWinEvent(Player player, SpleefGame spleefGame) {
        super(player);
    }
}

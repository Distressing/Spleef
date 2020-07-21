package dev.distressing.spleef.events.Player;

import dev.distressing.spleef.objects.SpleefGame;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerGameWinEvent extends SpleefPlayerEvent {
    private final SpleefGame spleefGame;

    public PlayerGameWinEvent(Player player, SpleefGame spleefGame) {
        super(player);
        this.spleefGame = spleefGame;
    }
}

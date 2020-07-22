package dev.distressing.spleef.api.events.player;

import dev.distressing.spleef.objects.SpleefGame;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerGameJoin extends SpleefPlayerEvent {
    private final SpleefGame spleefGame;

    public PlayerGameJoin(Player player, SpleefGame spleefGame) {
        super(player);
        this.spleefGame = spleefGame;
    }
}

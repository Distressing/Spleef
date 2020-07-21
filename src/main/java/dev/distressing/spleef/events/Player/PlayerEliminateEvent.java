package dev.distressing.spleef.events.Player;

import dev.distressing.spleef.objects.SpleefGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PlayerEliminateEvent extends SpleefPlayerEvent {
    private SpleefGame spleefGame;

    public PlayerEliminateEvent(Player player, SpleefGame spleefGame) {
        super(player);
    }
}

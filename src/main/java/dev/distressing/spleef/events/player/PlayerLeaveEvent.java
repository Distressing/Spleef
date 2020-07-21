package dev.distressing.spleef.events.player;

import dev.distressing.spleef.data.enums.LeaveReason;
import dev.distressing.spleef.objects.SpleefGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PlayerLeaveEvent extends SpleefPlayerEvent {
    private final SpleefGame spleefGame;
    private final LeaveReason leaveReason;

    public PlayerLeaveEvent(Player player, SpleefGame spleefGame, LeaveReason leaveReason) {
        super(player);
        this.spleefGame = spleefGame;
        this.leaveReason = leaveReason;
    }
}

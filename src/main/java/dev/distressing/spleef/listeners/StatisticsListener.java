package dev.distressing.spleef.listeners;

import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.data.enums.LeaveReason;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.player.PlayerGameWinEvent;
import dev.distressing.spleef.events.player.PlayerLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StatisticsListener implements Listener {

    private final SpleefDataManager spleefDataManager;

    public StatisticsListener(SpleefDataManager spleefDataManager) {
        this.spleefDataManager = spleefDataManager;
    }

    @EventHandler
    public void onSpleefDeath(PlayerLeaveEvent event) {
        SpleefPlayer spleefPlayer = spleefDataManager.get(event.getPlayer());

        if (spleefPlayer == null || event.getLeaveReason().equals(LeaveReason.WIN) || event.getSpleefGame().getGameState().equals(GameState.WAITING))
            return;

        spleefPlayer.addLosses(1);
    }

    @EventHandler
    public void onSpleefWin(PlayerGameWinEvent event) {
        SpleefPlayer spleefPlayer = spleefDataManager.get(event.getPlayer());

        if (spleefPlayer == null)
            return;

        spleefPlayer.addWins(1);
    }
}

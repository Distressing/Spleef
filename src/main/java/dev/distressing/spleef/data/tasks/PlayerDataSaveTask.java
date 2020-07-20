package dev.distressing.spleef.data.tasks;

import com.mongodb.client.MongoCollection;
import dev.distressing.spleef.SpleefPlugin;
import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.data.enums.DataType;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.mongodb.client.model.Filters.eq;

public class PlayerDataSaveTask implements Runnable {
    private SpleefDataManager spleefDataManager;
    private Player player;

    public PlayerDataSaveTask(Player player, SpleefDataManager spleefDataManager){
        this.player = player;
        this.spleefDataManager = spleefDataManager;
    }


    public void run() {

        SpleefPlayer spleefPlayer = spleefDataManager.get(player);

        if(spleefPlayer == null)
            return;

        if(spleefPlayer.getDataType().equals(DataType.TEMP))
            return;

        spleefDataManager.getPlayerCollection().findOneAndUpdate(
                eq("playerUUID", spleefPlayer.getPlayerID()),
                spleefPlayer.serialise(),
                SpleefDataManager.getUpdateOptions()
        );

        Bukkit.getScheduler().runTask(SpleefPlugin.getInstance(), () -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(spleefPlayer.getPlayerID());
            if(player.isOnline())
                return;
            spleefDataManager.setUnloaded(player);
        });

    }
}

package dev.distressing.spleef.data.tasks;

import com.mongodb.client.MongoCollection;
import dev.distressing.spleef.data.enums.DataType;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import dev.distressing.spleef.data.SpleefDataManager;
import org.bukkit.entity.Player;

import static com.mongodb.client.model.Filters.eq;

public class PlayerDataLoadTask implements Runnable {
    private SpleefDataManager spleefDataManager;
    private Player player;

    public PlayerDataLoadTask(Player player, SpleefDataManager spleefDataManager){
        this.player = player;
        this.spleefDataManager = spleefDataManager;
    }


    public void run() {
        if(!player.isOnline())
            return;

        MongoCollection<SpleefPlayer> playerCollection = spleefDataManager.getPlayerCollection();
        SpleefPlayer spleefPlayer;
        spleefPlayer = playerCollection.find(eq("playerID", player.getUniqueId())).first();

        if(spleefPlayer == null){
            //New Player
            spleefPlayer = new SpleefPlayer(player);
        }

        spleefPlayer.setDataType(DataType.PERSTISTANT);
        spleefDataManager.setLoaded(player, spleefPlayer);
    }
}

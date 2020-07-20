package dev.distressing.spleef.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import dev.distressing.spleef.SpleefPlugin;
import dev.distressing.spleef.data.enums.DataType;
import dev.distressing.spleef.data.events.PlayerDataLoadedEvent;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import dev.distressing.spleef.data.tasks.PlayerDataLoadTask;
import dev.distressing.spleef.data.tasks.PlayerDataSaveTask;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class SpleefDataManager {
    private ExecutorService databaseThreadPool = Executors.newFixedThreadPool(16);
    private ConcurrentHashMap<UUID, SpleefPlayer> spleefPlayers = new ConcurrentHashMap<UUID, SpleefPlayer>();
    private CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    private CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
    private static FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);
    private ConnectionString connectionString;
    private MongoClientSettings mongoClientSettings;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<SpleefPlayer> playerCollection;

    private void log(String string) {
        Bukkit.getLogger().log(Level.INFO, "[Spleef Data] "+string);
    }

    public SpleefDataManager(Configuration config) {

        log("Setting up mongoDB");

        connectionString = new ConnectionString(config.getString("database.uri"));
        mongoClientSettings = MongoClientSettings.builder()
                .retryWrites(true)
                .codecRegistry(codecRegistry)
                .applyConnectionString(connectionString)
                .build();

        log("Mongo settings set, Loading client");
        mongoClient = MongoClients.create(mongoClientSettings);
        log("Created server connection, Loading database");
        mongoDatabase = mongoClient.getDatabase("SpleefData");
        log("Loaded database, Locating collection");
        playerCollection = mongoDatabase.getCollection("PlayerCollection", SpleefPlayer.class);
        log("Collection found with "+playerCollection.countDocuments()+" documents");

        //Load user data of all users online
        Bukkit.getOnlinePlayers().forEach(this::loadData);
        //Start autosave task
        Bukkit.getScheduler().runTaskTimerAsynchronously(SpleefPlugin.getInstance(), this::saveAll, 600, 600);
    }

    public static FindOneAndUpdateOptions getUpdateOptions() {return options;}

    public MongoCollection<SpleefPlayer> getPlayerCollection() {return playerCollection;}

    @Nullable
    public SpleefPlayer get(Player player) {
        return spleefPlayers.get(player.getUniqueId());
    }

    public void setLoaded(Player player, SpleefPlayer spleefPlayer){
        UUID uuid = player.getUniqueId();
        SpleefPlayer current = spleefPlayers.get(spleefPlayer.getPlayerID());

        if(current == null || current.getDataType().equals(DataType.PERSTISTANT)){
            //Temporary data wasn't present, player must of left during data load process or player data was already loaded
            return;
        }

        spleefPlayer.addLosses(current.getLosses());
        spleefPlayer.addWins(current.getWins());

        spleefPlayers.put(uuid, spleefPlayer);
        PlayerDataLoadedEvent event = new PlayerDataLoadedEvent(player, spleefPlayer);
        Bukkit.getScheduler().runTask(SpleefPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent(event));
    }

    public void setUnloaded(@org.jetbrains.annotations.NotNull OfflinePlayer player) {
        spleefPlayers.remove(player.getUniqueId());
    }

    private void createTempData(Player player) {
        spleefPlayers.put(player.getUniqueId(),new SpleefPlayer(player));
    }

    public void loadData(Player player) {
        createTempData(player);
        databaseThreadPool.execute(new PlayerDataLoadTask(player, this));
    }

    public void save(Player player) {
        databaseThreadPool.execute(new PlayerDataSaveTask(player, this));
    }

    public void saveAll() {
        Bukkit.getOnlinePlayers().forEach(this::save);
    }

    public void shutdown() {
        saveAll();
        try {
            databaseThreadPool.shutdown();
            databaseThreadPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log("Database connection pool took too long to finish");
            e.printStackTrace();
        }
    }

}

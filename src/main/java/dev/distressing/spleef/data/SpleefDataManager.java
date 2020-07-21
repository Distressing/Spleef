package dev.distressing.spleef.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import dev.distressing.spleef.SpleefPlugin;
import dev.distressing.spleef.configuration.SpleefConfig;
import dev.distressing.spleef.data.enums.DataType;
import dev.distressing.spleef.data.events.PlayerDataLoadedEvent;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import dev.distressing.spleef.data.tasks.PlayerDataLoadTask;
import dev.distressing.spleef.data.tasks.PlayerDataSaveTask;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Getter
public class SpleefDataManager {
    private static final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);
    private final ExecutorService databaseThreadPool = Executors.newFixedThreadPool(16);
    private final ConcurrentHashMap<UUID, SpleefPlayer> spleefPlayers = new ConcurrentHashMap<UUID, SpleefPlayer>();
    private final MongoCollection<SpleefPlayer> playerCollection;
    private boolean shutdown = false;

    public SpleefDataManager() {

        log("Setting up mongoDB");

        ConnectionString connectionString = new ConnectionString(SpleefConfig.getDBURI());
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .retryWrites(true)
                .codecRegistry(codecRegistry)
                .applyConnectionString(connectionString)
                .build();

        log("Mongo settings set, Loading client");
        MongoClient mongoClient = MongoClients.create(mongoClientSettings);
        log("Created server connection, Loading database");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("customer_123170");
        log("Loaded database, Locating collection");
        playerCollection = mongoDatabase.getCollection("PlayerCollection", SpleefPlayer.class);
        log("Collection found with " + playerCollection.countDocuments() + " documents");

        //Load user data of all users online
        Bukkit.getOnlinePlayers().forEach(this::loadData);
        //Start autosave task
        Bukkit.getScheduler().runTaskTimerAsynchronously(SpleefPlugin.getInstance(), this::saveAll, 600, 600);
    }

    public static FindOneAndUpdateOptions getUpdateOptions() {
        return options;
    }

    private void log(String string) {
        Bukkit.getLogger().log(Level.INFO, "[Spleef Data] " + string);
    }

    public MongoCollection<SpleefPlayer> getPlayerCollection() {
        return playerCollection;
    }

    @Nullable
    public SpleefPlayer get(Player player) {
        return spleefPlayers.get(player.getUniqueId());
    }

    public void setLoaded(Player player, SpleefPlayer spleefPlayer) {
        UUID uuid = player.getUniqueId();
        SpleefPlayer current = spleefPlayers.get(spleefPlayer.getPlayerID());

        if (current == null || current.getDataType().equals(DataType.PERSTISTANT)) {
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
        spleefPlayers.put(player.getUniqueId(), new SpleefPlayer(player));
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
        shutdown = true;
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

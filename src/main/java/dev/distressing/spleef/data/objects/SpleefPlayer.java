package dev.distressing.spleef.data.objects;

import dev.distressing.spleef.data.enums.DataType;
import dev.distressing.spleef.data.enums.PlayerState;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class SpleefPlayer {

    private ObjectId id;
    @BsonProperty(value = "PlayerID")
    private UUID playerID;
    @BsonProperty(value = "Wins")
    private Integer wins;
    @BsonProperty(value = "Losses")
    private Integer losses;
    private DataType dataType;
    private PlayerState playerState;

    public SpleefPlayer() {
    }

    public SpleefPlayer(Player player) {
        this.playerID = player.getUniqueId();
        this.wins = 0;
        this.losses = 0;
        dataType = DataType.TEMP;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public void setPlayerID(UUID uuid) {
        this.playerID = uuid;
    }

    public void addWins(Integer wins) {
        this.wins += wins;
    }

    public void addLosses(Integer losses) {
        this.losses += losses;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public Bson serialise() {
        return combine(
                set("PlayerID", playerID),
                set("Wins", wins),
                set("Losses", losses)
        );
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(("SpleefPlayer{"));
        sb.append("id=").append(id);
        sb.append(", playerID=").append(playerID.toString());
        sb.append(", spleefwins=").append(wins);
        sb.append(", spleeflosses=").append(losses);
        sb.append("}");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SpleefPlayer spleefPlayer = (SpleefPlayer) o;
        return Objects.equals(id, spleefPlayer.id) && Objects.equals(playerID, spleefPlayer.playerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerID, wins, losses);
    }
}

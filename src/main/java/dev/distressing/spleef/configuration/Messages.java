package dev.distressing.spleef.configuration;

import dev.distressing.spleef.utils.Chat;
import org.bukkit.entity.Player;

public enum Messages {
    PREFIX("&6&lSpleef "),

    PLAYER_ELIMINATED("&6%player%&7 has fallen to their demise"),
    PLAYER_JOINED_LOBBY("&6%player%&7 has joined the game"),
    PLAYER_QUIT_LOBBY("&6%player%&7 has left the game"),

    GAME_STARTING("&7The game will start int %time%"),
    GAME_WIN("&7Game over! Winner: &6%player%"),
    GAME_GRACE_START("&7The grace period has started. Ensure to get as far away from others as possible."),
    GAME_GRACE_END("&7The grace has now finished, Block breaking is now enabled"),
    GAME_GRACE_NOTIFICATION("&7You cannot break blocks whilst grace is enabled"),
    GAME_LOSS("&7You died, Better luck next time!"),
    GAME_INPROGRESS("&7You cannot join a game that is already ongoing"),
    GAME_NOPVP("&7Pvp is disabled during Spleef games"),

    NoPermission("&cInsufficient permissions"),

    MAIN_HELP_ARENA("&7Use '/spleef arena' to create a play area for games to use"),
    MAIN_HELP_GAMES("&7Use '/spleef create <name> <arena name> <player count>' to create a game with your current location as the games origin"),

    HELP_ARENA("&7Use '/spleef arena list' to show the list of currently made arenas. An arena can have multiple games"),
    HELP_ARENA_CREATE("&7Use '/spleef arena create <name>' to start the creation process of an arena"),
    HELP_ARENA_REMOVE("&7Use '/spleef arena delete <name>' to delete an arena by its ID. Warning this will remove any games associated with the arena"),
    HELP_ARENA_EDIT("&7Use '/spleef arena edit <oldName> <newName>' to change the name of an arena"),

    AREA_EXISTS("An arena with that name already exists"),
    AREA_DEFINE_START("&7You have started to define a arena, Make sure to select the most outer regions of the arena first."),
    AREA_DEFINE_POS1("&7You have selected Position 1, Now select the most extreme other corners"),
    AREA_DEFINE_POS2("&7You have selected Position 2, Now you need to select the elimination zone of the arena"),
    AREA_DEFINE_ELIM_POS1("&7You have selected position 1 of the elimination zone, Now select the other corner"),
    AREA_DEFINE_ELIM_POS2("&7You have selected position 2 of the elimination zone, Now select the spawn point"),
    AREA_DEFINE_SPAWN("&7You have selected the spawn point. Now creating %name% arena for you..."),
    AREA_CREATION_SUCCESS("&7Arena creation success"),
    AREA_OUTSIDE_BOUNDS("&7This location is outside the bounds of the main arena"),
    AREA_WITHIN_ELIMINATION("&7The selected spawn point is within the elimination zone."),
    AREA_BUILDING_CANCELED("&7You have canceled the building of an arena");


    private String message;

    Messages(String s) {
        this.message = s;
    }

    public static void load() {
        for (Messages message : values()) {
            SpleefConfig.getConfig().addDefault("messages." + message.toString(), message.message);
            message.message = SpleefConfig.getConfig().getString("messages." + message.toString());
        }
        System.out.println(SpleefConfig.getConfig().getKeys(true));
        SpleefConfig.save();
    }

    public String getWithPrefix() {
        return Chat.color(Messages.PREFIX.message + message);
    }

    public String getWithPrefix(Player player) {
        return Chat.color(Messages.PREFIX.message + message.replaceAll("%player%", player.getName()));
    }
}

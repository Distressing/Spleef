package dev.distressing.spleef.commands;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import dev.distressing.spleef.managers.AreaCreationManager;
import dev.distressing.spleef.managers.ArenaManager;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefArea;
import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class SpleefCMD implements CommandExecutor {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final AreaCreationManager areaCreationManager;
    private final SpleefDataManager spleefDataManager;

    public SpleefCMD(GameManager gameManager, ArenaManager arenaManager, AreaCreationManager areaCreationManager, SpleefDataManager spleefDataManager) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
        this.areaCreationManager = areaCreationManager;
        this.spleefDataManager = spleefDataManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("spleef"))
            return false;

        if (args.length == 0) {
            Arrays.stream(Messages.values()).filter(message -> message.toString().toLowerCase().startsWith("main_help")).forEach(message -> sender.sendMessage(message.getWithPrefix()));
            return false;
        }

        switch (args[0].toLowerCase()) {
            default:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command");
                    break;
                }

            case "arena":
                if (args.length == 1) {
                    Arrays.stream(Messages.values()).filter(message -> message.toString().toLowerCase().startsWith("help_arena")).forEach(message -> sender.sendMessage(message.getWithPrefix()));
                    return false;
                }

                if (args[1].equalsIgnoreCase("list")) {
                    HashMap<String, SpleefArea> spleefAreaHashMap = arenaManager.getArenas();
                    if (spleefAreaHashMap.isEmpty()) {
                        sender.sendMessage("There are no arenas currently setup");
                        return false;
                    }
                    spleefAreaHashMap.forEach((key, value) -> sender.sendMessage(key));
                    return false;
                }

                if (args[1].equalsIgnoreCase("create")) {
                    if (args.length != 3) {
                        sender.sendMessage(Messages.HELP_ARENA_CREATE.getWithPrefix());
                        return false;
                    }

                    String name = args[2];

                    areaCreationManager.startBuilder((Player) sender, name);
                    break;
                }

            case "create":
                if (args.length < 3) {
                    sender.sendMessage(Messages.MAIN_HELP_GAMES.getWithPrefix());
                    return false;
                }

                String name = args[1];
                String arena = args[2];
                int playerCount = 5;

                if (args.length == 4) {
                    try {
                        playerCount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(Messages.MAIN_HELP_GAMES.getWithPrefix());
                        return false;
                    }
                }

                Optional<SpleefArea> areaOptional = arenaManager.getArena(arena);

                if (!areaOptional.isPresent()) {
                    sender.sendMessage(Messages.AREA_NOT_FOUND.getWithPrefix());
                    return false;
                }

                SpleefGame game = new SpleefGame(name, ((Player) sender).getLocation(), areaOptional.get(), playerCount);
                gameManager.addGame(game);

                break;

            case "join":
                Optional<Set<SpleefGame>> openGames = gameManager.getOpenGames();

                if (!openGames.isPresent()) {
                    sender.sendMessage(Messages.GAME_NONE_FOUND.getWithPrefix());
                    return false;
                }

                Set<SpleefGame> games = openGames.get();
                SpleefGame spleefGame;

                if (args.length != 2) {
                    if (games.isEmpty()) {
                        sender.sendMessage(Messages.GAME_NONE_FOUND.getWithPrefix());
                        return false;
                    } else {
                        spleefGame = games.stream().findFirst().get();
                    }
                } else {
                    Optional<SpleefGame> filteredGame = games.stream().filter(sGame -> sGame.getName().equalsIgnoreCase(args[1])).findFirst();

                    if (!filteredGame.isPresent()) {
                        sender.sendMessage(Messages.GAME_NONE_FOUND.getWithPrefix());
                        return false;
                    }

                    spleefGame = filteredGame.get();
                }

                spleefGame.processJoin((Player) sender);
                break;

            case "joinable":
                Optional<Set<SpleefGame>> joinableOptional = gameManager.getOpenGames();

                if (!joinableOptional.isPresent()) {
                    sender.sendMessage(Messages.GAME_NONE_FOUND.getWithPrefix());
                    return false;
                }

                joinableOptional.get().forEach(joinableGame -> sender.sendMessage(Messages.GAME_LIST.getWithPrefix().replace("%name%", joinableGame.getName())));
                break;

            case "leave":
                gameManager.processLeaveRequest((Player) sender);
                break;

            case "stats":
                Player target;

                if (args.length != 2) {
                    target = (Player) sender;
                } else {
                    target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Messages.PLAYER_NOT_FOUND.getWithPrefix());
                        break;
                    }
                }

                SpleefPlayer spleefPlayer = spleefDataManager.get((Player) sender);

                if (spleefPlayer == null) {
                    sender.sendMessage(Messages.PLAYER_NOT_FOUND.getWithPrefix());
                    break;
                }

                sender.sendMessage(Messages.PLAYER_STATS.getWithPrefix(target)
                        .replace("%losses%", spleefPlayer.getLosses() + "")
                        .replace("%wins%", spleefPlayer.getWins() + "")
                );
                break;

        }

        return false;
    }
}

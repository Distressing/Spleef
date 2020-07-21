package dev.distressing.spleef.commands;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.managers.ArenaManager;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefArea;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;

public class SpleefCMD implements CommandExecutor {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;

    public SpleefCMD(GameManager gameManager, ArenaManager arenaManager) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("spleef"))
            return false;

        if (args.length == 0) {
            Arrays.stream(Messages.values()).filter(message -> message.toString().toLowerCase().startsWith("main_help")).forEach(message -> sender.sendMessage(message.getWithPrefix()));
            return false;
        }

        switch (args[0].toLowerCase()) {
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
            default:
                break;
        }

        return false;
    }
}

package com.github.lolopasdugato.mcwarclan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Loïc on 27/04/2014.
 */
public class AdminCommands implements CommandInterface {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("assign")) {

            } else if (args[0].equalsIgnoreCase("unassign")) {

            } else if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {

            }
        }
        return false;
    }
}

package com.github.lolopasdugato.mcwarclan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Loïc on 27/04/2014.
 */
public class BaseCommands implements CommandInterface {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("info")) {

            } else if (args[0].equalsIgnoreCase("upgrade")) {

            } else if (args[0].equalsIgnoreCase("create")) {

            } else if (args[0].equalsIgnoreCase("contest")) {

            } else if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {

            }
        }
        return false;
    }
}

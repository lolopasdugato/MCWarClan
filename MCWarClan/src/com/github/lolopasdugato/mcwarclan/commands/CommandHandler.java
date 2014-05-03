package com.github.lolopasdugato.mcwarclan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 * Created by Loïc on 26/04/2014.
 */
public class CommandHandler implements CommandExecutor {
    private static HashMap<String, CommandInterface> _commands;

    public CommandHandler() {
        _commands = new HashMap<String, CommandInterface>();
    }

    public void register(String cmdName, CommandInterface cmd) {
        _commands.put(cmdName, cmd);
    }

    public boolean exist(String cmdName) {
        return _commands.containsKey(cmdName);
    }

    public CommandInterface getExecutor(String cmdName) {
        return _commands.get(cmdName);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("team")) {
            return getExecutor("team").onCommand(sender, cmd, commandLabel, args);
        } else if (commandLabel.equalsIgnoreCase("base")) {
            return getExecutor("base").onCommand(sender, cmd, commandLabel, args);
        } else if (commandLabel.equalsIgnoreCase("treasure")) {
            return getExecutor("treasure").onCommand(sender, cmd, commandLabel, args);
        } else if (commandLabel.equalsIgnoreCase("admin")) {
            return getExecutor("admin").onCommand(sender, cmd, commandLabel, args);
        }
        return false;
    }
}

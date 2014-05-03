package com.github.lolopasdugato.mcwarclan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Loïc on 26/04/2014.
 */
public interface CommandInterface {

    public static String NO_PERMISSION_MESSAGE = "Sorry, you do not have permission to perform this command.";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
}

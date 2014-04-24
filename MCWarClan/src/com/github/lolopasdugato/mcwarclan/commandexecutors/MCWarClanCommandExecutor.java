package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public abstract class MCWarClanCommandExecutor implements CommandExecutor {
	
	protected TeamManager _tc;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic CommandExecutor constructor.
     * @param tc
     */
    public MCWarClanCommandExecutor(TeamManager tc) {
        _tc = tc;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Check if a player has been or is on the server.
     * @param playerName
     * @return
     */
    public boolean exist(String playerName){
        return Bukkit.getOfflinePlayer(playerName).hasPlayedBefore() || Bukkit.getOfflinePlayer(playerName).isOnline();
    }

    /**
     *  Returns a player using a name.
     * @param name
     * @return
     */
    public OfflinePlayer getOfflinePlayer(String name){
        if(exist(name)){
            return Bukkit.getOfflinePlayer(name);
        }
        return null;
    }

    public Player getPlayer(String name){
        if(exist(name)){
            return Bukkit.getPlayer(name);
        }
        return null;
    }
}



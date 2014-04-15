package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Random;

public class EventManager implements Listener {
	
	private TeamContainer _tc;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @brief Classic Event constructor.
     * @param tc
     */
    public EventManager(TeamContainer tc){
		_tc = tc;
	}

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------------- Events ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent evt) {
        MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());
		if(_tc == null){
            Messages.sendMessage("The main team manager cannot be found. Please contact the creator to solve this problem.", Messages.messageType.ALERT, null);
			return;
		}
		if(player == null){
            Messages.sendMessage("Welcome, this server is using MCWarClan " + MCWarClan.VERSION + ", have fun !", Messages.messageType.INGAME, evt.getPlayer());
            Team barbarians = _tc.getTeam(Team.BARBARIAN_TEAM_ID);
            player = new MCWarClanPlayer(evt.getPlayer(), barbarians);
            barbarians.addTeamMate(player);
            player.spawn();
            Messages.sendMessage(player.get_name() + " has spawn in x:" + player.get_spawn().get_x() + ", y:" + player.get_spawn().get_y() + ", z:" + player.get_spawn().get_z(), Messages.messageType.DEBUG, null);
        }
        else
            Messages.sendMessage("Welcome back, this server is using MCWarClan " + MCWarClan.VERSION + ", have fun !", Messages.messageType.INGAME, evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerRespawnEvent evt){
        MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());
        if(player.get_team().get_id() == Team.BARBARIAN_TEAM_ID && Settings.randomBarbarianSpawn){
            player.reloadSpawn();
        }
        Location playerSpawn = player.get_spawn().getLocation();
        playerSpawn.getChunk().load();
        Messages.sendMessage(player.get_name() + " has spawn in x:" + player.get_spawn().get_x() + ", y:" + player.get_spawn().get_y() + ", z:" + player.get_spawn().get_z(), Messages.messageType.DEBUG, null);
        evt.setRespawnLocation(playerSpawn);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent evt) {
        MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());

        if (player != null) {
            if(player.isInEnemyTerritory(evt.getBlockPlaced().getLocation())){   // Check if in enemy territory
                if(evt.getBlockPlaced().getType() == Material.TNT || evt.getBlockPlaced().getType() == Material.LADDER || evt.getBlockPlaced().getType() == Material.LEVER){    // Cehck if it's a special block
                    if(evt.getPlayer().getItemInHand().getAmount() >= Settings.uncensoredItemsAmount){  // Check if the guy has the amount of item required to place the special block
                        evt.getPlayer().getItemInHand().setAmount(evt.getPlayer().getItemInHand().getAmount() - Settings.uncensoredItemsAmount - 1);
                        return;
                    } else {
                        // evt.getBlockPlaced().breakNaturally();
                        evt.setCancelled(true);
                        evt.getPlayer().updateInventory();
                        Messages.sendMessage("You need at least " + Settings.uncensoredItemsAmount + " " + evt.getBlockPlaced().getType().toString() + " to place it into an enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                    }
                }
                else {
                    evt.setCancelled(true);
                    evt.getPlayer().updateInventory();
                    // evt.getBlockPlaced().breakNaturally();
                    Messages.sendMessage("You cannot place block (except TNT, Ladders, and Levers...) in an enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                }
            }
        }
        else
            Messages.sendMessage("Error, please ask an admin to be add to a team !", Messages.messageType.INGAME, evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent evt) {
        MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());

        if (player != null) {
            if(player.isInEnemyTerritory(evt.getBlock().getLocation())){
                Messages.sendMessage("You cannot break block in the enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                evt.setCancelled(true);
            }
        }
        else
            Messages.sendMessage("Error, please ask an admin to be add to a team !", Messages.messageType.INGAME, evt.getPlayer());
    }

}

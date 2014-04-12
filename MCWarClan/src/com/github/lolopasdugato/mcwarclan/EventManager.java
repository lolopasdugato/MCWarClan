package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EventManager implements Listener {
	
	private TeamContainer _tc;
	
	public EventManager(TeamContainer tc){
		_tc = tc;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("§a[MCWarClan]§6 Welcome, this server is using MCWarClan v0.1, have fun !");
		if(_tc == null){
			return;
		}
        // TODO: Check if the position is spawnable !
		if(_tc.searchPlayerTeam(evt.getPlayer().getName()) == null){
			_tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer().getName());
            Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
            barbarianSpawn.getChunk().load();
            if(Settings.debugMode){
                System.out.println("[DEBUG] " + evt.getPlayer().getName() + " has spawn in x:" + barbarianSpawn.getX() + ", y:" + barbarianSpawn.getY() + ", z:" + barbarianSpawn.getZ());
            }
            evt.getPlayer().teleport(barbarianSpawn);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerRespawnEvent evt){
         if(_tc.searchPlayerTeam(evt.getPlayer().getName()).get_name().equals("Barbarians")){
             Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
             barbarianSpawn.getChunk().load();
             if(Settings.debugMode){
                 System.out.println("[DEBUG] " + evt.getPlayer().getName() + " has spawn in x:" + barbarianSpawn.getX() + ", y:" + barbarianSpawn.getY() + ", z:" + barbarianSpawn.getZ());
             }
             evt.setRespawnLocation(barbarianSpawn);
         }

    }

    /**
     * @brief Use to search a random barbarian spawn using config.yml.
     * @param barbarianSpawnRadius
     * @return
     */
    public Location getBarbarianSpawn(int barbarianSpawnRadius) {
        if (barbarianSpawnRadius < 100) {
            System.out.println("Cannot have a barbarian spawn radius under 100 ! Setting spawn radius to 100...");
            barbarianSpawnRadius = 100;
        }
        Location worldSpawn = Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation();
        int signX = 1;
        int signZ = 1;
        if (new Random().nextBoolean())
            signX = -1;
        if (new Random().nextBoolean())
            signZ = -1;
        double randomX = (new Random().nextInt(barbarianSpawnRadius)) * signX;
        double randomZ = (new Random().nextInt(barbarianSpawnRadius)) * signZ;
        return new Location(Bukkit.getWorld(Settings.classicWorldName), (worldSpawn.getX() + randomX), worldSpawn.getY(), (worldSpawn.getZ() + randomZ));
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent evt) {
        Team userTeam = _tc.searchPlayerTeam(evt.getPlayer().getName());

        if (userTeam != null) {
            if(isInEnemyTerritory(userTeam, evt.getBlockPlaced().getLocation())){   // Check if in enemy territory
                if(evt.getBlockPlaced().getType() == Material.TNT || evt.getBlockPlaced().getType() == Material.LADDER || evt.getBlockPlaced().getType() == Material.LEVER){    // Cehck if it's a special block
                    if(evt.getPlayer().getItemInHand().getAmount() >= Settings.uncensoredItemsAmount){  // Check if the guy has the amount of item required to place the special block
                        evt.getPlayer().getItemInHand().setAmount(evt.getPlayer().getItemInHand().getAmount() - Settings.uncensoredItemsAmount - 1);
                        return;
                    } else {
                        // evt.getBlockPlaced().breakNaturally();
                        evt.setCancelled(true);
                        evt.getPlayer().updateInventory();
                        evt.getPlayer().sendMessage("§a[MCWarClan]§6 You need at least " + Settings.uncensoredItemsAmount + " " + evt.getBlockPlaced().getType().toString() + " to place it into an enemy base ! (right click to get your item back)");
                    }
                }
                else {
                    evt.setCancelled(true);
                    evt.getPlayer().updateInventory();
                    // evt.getBlockPlaced().breakNaturally();
                    evt.getPlayer().sendMessage("§a[MCWarClan]§6 You cannot place block (except TNT, Ladders, and Levers...) in an enemy base !");
                }
            }
        }
        else
            evt.getPlayer().sendMessage("§a[MCWarClan]§6 Error, please ask an admin to be add to a team !");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent evt) {
        Team userTeam = _tc.searchPlayerTeam(evt.getPlayer().getName());

        if (userTeam != null) {
            if(isInEnemyTerritory(userTeam, evt.getBlock().getLocation())){
                evt.getPlayer().sendMessage("§a[MCWarClan]§6 You cannot break block in the enemy base !");
                evt.setCancelled(true);
            }
        }
        else
            evt.getPlayer().sendMessage("§a[MCWarClan]§6 Error, please ask an admin to be add to a team !");
    }

    /**
     * @brief Verify if someone of the team t is in an enemy territory.
     * @param t the team of the guy we want to know if he is in an enemy territory.
     * @param loc the location where we want to check.
     * @return Returns true if the location of the guy is considered as an enemy territory.
     */
    public boolean isInEnemyTerritory(Team t, Location loc){
        for(int i = 0; i < _tc.get_teamArray().size(); i++){
            if(_tc.get_teamArray().get(i).isEnemyToTeam(t) && _tc.get_teamArray().get(i).isInTerritory(loc)){
                return true;
            }
        }
        return false;
    }
}

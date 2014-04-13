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
        Messages.sendMessage("Welcome, this server is using MCWarClan " + MCWarClan.VERSION + ", have fun !", Messages.messageType.INGAME, evt.getPlayer());
		if(_tc == null){
			return;
		}
		if(_tc.searchPlayerTeam(evt.getPlayer().getName()) == null){
			_tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer().getName());
            Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
            barbarianSpawn.getChunk().load();
            if(!spawnOK(barbarianSpawn))
                barbarianSpawn = getSpawnOk(barbarianSpawn);
            Messages.sendMessage(evt.getPlayer().getName() + " has spawn in x:" + barbarianSpawn.getX() + ", y:" + barbarianSpawn.getY() + ", z:" + barbarianSpawn.getZ(), Messages.messageType.DEBUG, null);
            evt.getPlayer().teleport(barbarianSpawn);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerRespawnEvent evt){
         if(_tc.searchPlayerTeam(evt.getPlayer().getName()).get_name().equals("Barbarians")){
             Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
             barbarianSpawn.getChunk().load();
             Messages.sendMessage(evt.getPlayer().getName() + " has spawn in x:" + barbarianSpawn.getX() + ", y:" + barbarianSpawn.getY() + ", z:" + barbarianSpawn.getZ(), Messages.messageType.DEBUG, null);
             evt.setRespawnLocation(barbarianSpawn);
         }

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
        Team userTeam = _tc.searchPlayerTeam(evt.getPlayer().getName());

        if (userTeam != null) {
            if(isInEnemyTerritory(userTeam, evt.getBlock().getLocation())){
                Messages.sendMessage("You cannot break block in the enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                evt.setCancelled(true);
            }
        }
        else
            Messages.sendMessage("Error, please ask an admin to be add to a team !", Messages.messageType.INGAME, evt.getPlayer());
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @brief Use to search a random barbarian spawn using config.yml.
     * @param barbarianSpawnRadius
     * @return
     */
    private Location getBarbarianSpawn(int barbarianSpawnRadius) {
        if (barbarianSpawnRadius < 100) {
            Messages.sendMessage("Cannot have a barbarian spawn radius under 100 ! Setting spawn radius to 100...", Messages.messageType.ALERT, null);
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

    /**
     * @brief looks if you can spawn in this location.
     * @param loc the position to check.
     * @return true if you can spawn there.
     */
    private boolean spawnOK(Location loc) {
        return loc.getBlock().getType() == Material.AIR;
    }

    /**
     * @brief Get the first highest position where there is an air block, so that you can spawn there
     * @param loc the location to change.
     * @return the location where you can spawn.
     */
    private Location getSpawnOk(Location loc) {
        loc.setY(loc.getY() + 1);
        if(loc.getBlock().getType() != Material.AIR)
            return getSpawnOk(loc);
        return loc;
    }
}

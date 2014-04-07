package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Random;

public class EventManager implements Listener {
	
	private TeamContainer _tc;
    private Configuration _cfg;
	
	public EventManager(TeamContainer tc, Configuration cfg){
		_tc = tc;
        _cfg = cfg;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("§a[MCWarClan]§6 " + "Welcome, this server is using MCWarClan v0.1, have fun !");
		if(_tc == null){
			return;
		}
		if(_tc.searchPlayerTeam(evt.getPlayer().getName()) == null){
			_tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer().getName());
            Location barbarianSpawn = getBarbarianSpawn(evt.getPlayer(), _cfg.getInt("baseSettings.barbariansSpawnDistance"));
            barbarianSpawn.getChunk().load();
            evt.getPlayer().teleport(barbarianSpawn);
        }
	}

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerRespawnEvent evt){
         if(_tc.searchPlayerTeam(evt.getPlayer().getName()).get_name().equals("Barbarians")){
             Location barbarianSpawn = getBarbarianSpawn(evt.getPlayer(), _cfg.getInt("baseSettings.barbariansSpawnDistance"));
             barbarianSpawn.getChunk().load();
             evt.setRespawnLocation(barbarianSpawn);
         }

    }

    public Location getBarbarianSpawn(Player p, int barbarianSpawnRadius){
        if(barbarianSpawnRadius < 100){
            System.out.println("Cannot have a barbarian spawn radius under 100 ! Setting spawn radius to 100...");
            barbarianSpawnRadius = 100;
        }
        Location worldSpawn = p.getWorld().getSpawnLocation();
        int signX = 1;
        int signZ = 1;
        if(new Random().nextBoolean())
            signX = -1;
        if(new Random().nextBoolean())
            signZ = -1;
        double randomX = (new Random().nextInt(barbarianSpawnRadius)) * signX;
        double randomZ = (new Random().nextInt(barbarianSpawnRadius)) * signZ;
        return new Location(p.getWorld(), (worldSpawn.getX() + randomX), worldSpawn.getY(), (worldSpawn.getZ() + randomZ));
    }
}

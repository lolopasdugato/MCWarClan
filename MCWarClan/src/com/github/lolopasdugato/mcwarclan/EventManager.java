package com.github.lolopasdugato.mcwarclan;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventManager implements Listener {
	
	private TeamContainer _tc;
	
	public EventManager(TeamContainer tc){
		_tc = tc;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("§a[MCWarClan]§6 " + "Welcome, this server is using MCWarClan v0.1, have fun !");
		if(_tc == null){
			System.out.println("tc null !!!!!");
			return;
		}
		if(_tc.searchPlayerTeam(evt.getPlayer().getName()) == null){
			System.out.println("JE RENTRE ICI !");
			_tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer().getName());
		}
	}
}

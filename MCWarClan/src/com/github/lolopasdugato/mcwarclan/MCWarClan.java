// This plugin is made by Eldorabe
// You can join me by sending an e-mail at eldorabe@gmail.com
// Please contact me if you want to use this code.

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import com.github.lolopasdugato.mcwarclan.*;

public class MCWarClan extends JavaPlugin implements Listener {
	
	protected TeamContainer _tc;
	
	public TeamContainer get_tc() {
		return _tc;
	}

	public void set_tc(TeamContainer _tc) {
		this._tc = _tc;
	}

	public TeamContainer TeamContainerInit(){
		TeamContainer tc = new TeamContainer(TeamContainer.MAXTEAMSIZE);
		tc.addTeam(new Team(new Color("RED"), "HellRangers", Team.DEFAULTTEAMSIZE, tc));
		tc.addTeam(new Team(new Color("BLUE"), "ElvenSoldiers", Team.DEFAULTTEAMSIZE, tc));
		tc.addTeam(new Team(new Color("GREY"), "Barbarians", Team.DEFAULTTEAMSIZE, tc));
		return tc;
	}
	
	// Check if a player has been or is on the server.
	public boolean exist(String playerName){
		if(getServer().getOfflinePlayer(playerName).hasPlayedBefore() || getServer().getOfflinePlayer(playerName).isOnline()){
			return true;
		}
		return false;
	}
	
	// Returns a player using a name.
	public OfflinePlayer findPlayerByName(String name){
		if(exist(name)){
			return getServer().getOfflinePlayer(name);
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Show an exhaustive list of all teams
		if(label.equals("showteams") || label.equals("lt") || label.equals("st")){
			// show the list of teams.
			sender.sendMessage(_tc.teamsList());
			return true;
		}
		// Shows any players that are in your team
		/*else if(label.equals("teammates") || label.equals("tm")){
			if(sender instanceof Player){
				Team t = _tc.searchPlayerTeam((Player)sender);
				if(t != null){
					sender.sendMessage(t.playerList());
				}
				else
					sender.sendMessage("You do not have any team !");
			}
			else {
				sender.sendMessage("You have to be a player to perform this command !");
				return false;
			}
		}*/
		// Assign a player to a team using color or team name
		else if(label.equals("assign")) {
			OfflinePlayer p = findPlayerByName(args[0]);
			if(args.length > 1 && p != null){
				Team t = _tc.searchTeam(args[1]);
				Team actual = _tc.searchPlayerTeam(p);
				if(t != null){
					t.addTeamMate(p);
					sender.sendMessage(args[0] + " has successfully been added to " + t.get_color().get_colorMark() + t.get_name());
					actual.deleteTeamMate(p);
					if(p.isOnline())
						p.getPlayer().sendMessage("You have been added to team " + t.get_color().get_colorMark() + t.get_name() + " §fby " + sender.getName());
					return true;
				}
				else {
					t = _tc.searchTeam(new Color(args[1]));
					if(t != null){
						t.addTeamMate(p);
						sender.sendMessage(args[0] + " has successfully been added to " + t.get_color().get_colorMark() + t.get_name());
						actual.deleteTeamMate(p);
						if(p.isOnline())
							p.getPlayer().sendMessage("You have been added to team " + t.get_color().get_colorMark() + t.get_name() + " §fby " + sender.getName());
						return true;
					}
				}
			}
			return false;
		}
		// Shows a list
		else if(label.equals("team") && args.length == 0){
			if(sender instanceof Player){
				sender.sendMessage(_tc.searchPlayerTeam(((Player) sender).getPlayer()).playerList());
				return true;
			}
			sender.sendMessage("You have to be a player to perform this command !");
			return false;
		}
		else if (label.equals("team") && args.length == 1 && exist(args[0])){
			sender.sendMessage(_tc.searchPlayerTeam(findPlayerByName(args[0])).playerList());
			return true;
		}
		else if(label.equals("unassign") && args.length == 1) {
			OfflinePlayer p = findPlayerByName(args[0]);
			if(p != null){
				Team t = _tc.searchPlayerTeam(p);
				if(t != null){
					t.deleteTeamMate(p);
					sender.sendMessage(args[0] + " has successfully been kicked from " + t.get_color().get_colorMark() + t.get_name());
					_tc.searchTeam("Barbarians").addTeamMate(p.getPlayer());
					if(p.isOnline())
						p.getPlayer().sendMessage("You have been kicked from team " + t.get_color().get_colorMark() + t.get_name() + " §fby " + sender.getName() + ". You are now a §8Barbarian !");
					return true;					
				}
			}
			return false;
		}
		else if(label.equals("leave") && args.length == 0){
			if(sender instanceof Player){
				Team t = _tc.searchPlayerTeam(((Player) sender).getPlayer());
				if(t.get_name().equals("Barbarians")){
					sender.sendMessage("You cannot leave the §8Barbarian§f team !");
					return true;
				}
				_tc.searchPlayerTeam(((Player) sender).getPlayer()).deleteTeamMate(((Player) sender).getPlayer());
				_tc.searchTeam("Barbarians").addTeamMate(((Player) sender).getPlayer());
				sender.sendMessage("You have successfully left " + t.get_color().get_colorMark() + t.get_name() + ".§f You are now a §8Barbarian !");
				return true;
			}
			else
				sender.sendMessage("You have to be a player to peform this command !");
			return false;
		}
		else if(label.equals("join") && args.length == 1){
			if(sender instanceof Player){
				Team actual = _tc.searchPlayerTeam(((Player) sender).getPlayer());
				Team toJoin = _tc.searchTeam(args[0]);
				if(toJoin == null){
					toJoin = _tc.searchTeam(new Color(args[0]));
				}
				if(toJoin != null){
					actual.deleteTeamMate(((Player) sender).getPlayer());
					toJoin.addTeamMate(((Player) sender).getPlayer());
					sender.sendMessage("Well done, you left " + actual.get_color().get_colorMark() + actual.get_name() + " §fand joined " + toJoin.get_color().get_colorMark() + toJoin.get_name() + ".");
					return true;
				}
				else{
					sender.sendMessage("This team cannot be find.");
					return true;
				}
			}
			sender.sendMessage("You have to be a player to perform this command !");
			return false;
		}
		return false;
	}
	
	public MCWarClan() {
		// TODO Auto-generated constructor stub
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("Welcome, this server is using MCWarClan v0.1, have fun !");
		if(_tc.searchPlayerTeam(evt.getPlayer()) == null){
			_tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer());
		}
	}
	
	public void onEnable(){
		Logger log = Logger.getLogger("minecraft");
		getServer().getPluginManager().registerEvents(this, this);
		log.info("Registering events...");
		_tc = TeamContainerInit();
		log.info("Initialising teams...");
		log.info("MCWarClan has been successfully launched !");
		return;
	}
	
	public void onDisable() {
		return;
	}

}

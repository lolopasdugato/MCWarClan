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

import java.io.File;
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
		if(new File("plugins/TeamContainer.ser").exists()){
			tc = tc.deSerialize();
		}
		else
			tc = null;
		
		if(tc == null){
			System.out.println("File cannot be read !");
			tc = new TeamContainer(TeamContainer.MAXTEAMSIZE);
			tc.addTeam(new Team(new Color("RED"), "HellRangers", Team.DEFAULTTEAMSIZE, tc));
			tc.addTeam(new Team(new Color("BLUE"), "ElvenSoldiers", Team.DEFAULTTEAMSIZE, tc));
			tc.addTeam(new Team(new Color("LIGHTGREY"), "Barbarians", Team.DEFAULTTEAMSIZE, tc));
		}
		
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
	
	// show to the sender the list of all teams in the game.
	public boolean showteamsCommand(CommandSender sender){
		sender.sendMessage("§8##########################################################################################################");
		sender.sendMessage(_tc.teamsList());
		sender.sendMessage("§8##########################################################################################################");
		return true;
	}
	
	// Sort of admin command. Allows someone to assign someone else to a specific team.
	public boolean assignCommand(CommandSender sender, String[] args){
		OfflinePlayer p = findPlayerByName(args[0]);
		if(args.length > 1 && p != null){
			Team t = _tc.searchTeam(args[1]);
			Team actual = _tc.searchPlayerTeam(args[0]);
			if(t == null){
				t = _tc.searchTeam(new Color(args[1]));
			}
			if(t != null){
				t.addTeamMate(args[0]);
				sender.sendMessage("§a[MCWarClan]§6 " + args[0] + " §6has successfully been added to " + t.get_color().get_colorMark() + t.get_name());
				actual.deleteTeamMate(args[0]);
				if(p.isOnline()){
					p.getPlayer().sendMessage("§a[MCWarClan]§6 " + "§6You have been added to team " + t.get_color().get_colorMark() + t.get_name() + " §6by " + sender.getName());
				}
				return true;
			}
			else{
				sender.sendMessage("§a[MCWarClan]§6 " + "§6Invalid team or color name.");
				return true;
			}
		}
		return false;
	}
	
	// Shows the team members of the sender's team or of the specified team.
	public boolean teamCommand(CommandSender sender, String[] args){
		if(args.length == 0){
			if(sender instanceof Player){
				sender.sendMessage("§8##########################################################################################################");
				sender.sendMessage(_tc.searchPlayerTeam(sender.getName()).playerList());
				sender.sendMessage("§8##########################################################################################################");
				return true;
			}
			sender.sendMessage("§6You have to be a player to perform this command !");
			return true;
		}
		else if (args.length == 1 && exist(args[0]) && _tc.searchPlayerTeam(args[0]) != null) {
			sender.sendMessage("§8##########################################################################################################");
			sender.sendMessage(_tc.searchPlayerTeam(args[0]).playerList());
			sender.sendMessage("§8##########################################################################################################");
			return true;
		}
		return false;
	}
	
	// Sort of admin command. Allows someone to kick someone else from a specific team.
	public boolean unassignCommand(CommandSender sender, String[] args){
		OfflinePlayer p = findPlayerByName(args[0]);
		if(p != null){
			Team t = _tc.searchPlayerTeam(args[0]);
			if(t != null){
				t.deleteTeamMate(args[0]);
				sender.sendMessage("§a[MCWarClan]§6 " + args[0] + " §6has successfully been kicked from " + t.get_color().get_colorMark() + t.get_name());
				_tc.searchTeam("Barbarians").addTeamMate(args[0]);
				if(p.isOnline()){	// Send a message to the player concerned.
					p.getPlayer().sendMessage("§a[MCWarClan]§6 " + "§6You have been kicked from team " + t.get_color().get_colorMark() + t.get_name() + " §6by " + sender.getName() + ". §6You are now a §8Barbarian !");
				}
				return true;					
			}
		}
		return false;
	}
	
	// Allows the sender to leave it's current team an join the barbarian team.
	public boolean leaveCommand(CommandSender sender){
		if(sender instanceof Player){
			Team t = _tc.searchPlayerTeam(sender.getName());
			if(t.get_name().equals("Barbarians")){
				sender.sendMessage("§a[MCWarClan]§6 " + "§6You cannot leave the §8Barbarian§6 team !");
				return true;
			}
			_tc.searchPlayerTeam(sender.getName()).deleteTeamMate(sender.getName());
			_tc.searchTeam("Barbarians").addTeamMate(sender.getName());
			sender.sendMessage("§a[MCWarClan]§6 " + "§6You have successfully left " + t.get_color().get_colorMark() + t.get_name() + ".§6 You are now a §8Barbarian !");
			return true;
		}
		else
			sender.sendMessage("§a[MCWarClan]§6 " + "You have to be a player to peform this command !");
		return false;
	}
	
	// Allows the sender to join the specified team.
	public boolean joinCommand(CommandSender sender, String[] args){
		if(sender instanceof Player){
			Team actual = _tc.searchPlayerTeam(sender.getName());
			Team toJoin = _tc.searchTeam(args[0]);
			if(toJoin == null){
				toJoin = _tc.searchTeam(new Color(args[0]));
			}
			if(toJoin != null){
				actual.deleteTeamMate(sender.getName());
				toJoin.addTeamMate(sender.getName());
				sender.sendMessage("§a[MCWarClan]§6 " + "§6Well done, you left " + actual.get_color().get_colorMark() + actual.get_name() + " §6and joined " + toJoin.get_color().get_colorMark() + toJoin.get_name() + ".");
				return true;
			}
			else{
				sender.sendMessage("§a[MCWarClan]§6 " + "§6This team cannot be find.");
				return true;
			}
		}
		sender.sendMessage("§a[MCWarClan]§6 " + "§6You have to be a player to perform this command !");
		return false;
	}
	
	// allows someone to create a team.
	public boolean createteamCommand(CommandSender sender, String[] args){
		if(args.length == 2){
			if(_tc.addTeam(new Team(new Color(args[1]), args[0], Team.DEFAULTTEAMSIZE, _tc))){
				if(sender instanceof Player){
					joinCommand(sender, args);
				}
				System.out.println("§a[MCWarClan]§6 " + new Color(args[1]).get_colorMark() + args[0] + " §6has been successfully created !");
				return true;
			}
			else{
				sender.sendMessage("§a[MCWarClan]§6 " + "§6Sorry, but name or color is already taken by another team. Here is the colorname list: ");
				sender.sendMessage("§a[MCWarClan]§6 " + "§cRED, §1BLUE, §2GREEN, §eYELLOW, §0BLACK, §fWHITE, §dMAGENTA, §8GREY, §5PURPRLE, §7LIGHTGREY, §aLIGHTGREEN, §3CYAN, §bLIGHTBLUE");
			}
			
		}
		return false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if((label.equals("showteams") || label.equals("lt") || label.equals("st")) && args.length == 0){
			return showteamsCommand(sender);
		}
		
		else if(label.equals("assign")) {
			return assignCommand(sender, args);
		}
		
		else if(label.equals("team")){
			return teamCommand(sender, args);
		}
		
		else if(label.equals("unassign") && args.length == 1) {
			return unassignCommand(sender, args);
		}
		
		else if(label.equals("leave") && args.length == 0){
			return leaveCommand(sender);
		}
		
		else if(label.equals("join") && args.length == 1){
			return joinCommand(sender, args);
		}
		
		else if(label.equals("createteam")){
			return createteamCommand(sender, args);
		}
		return false;
	}
	
	public MCWarClan() {
		// TODO Auto-generated constructor stub
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("§a[MCWarClan]§6 " + "Welcome, this server is using MCWarClan v0.1, have fun !");
		if(_tc.searchPlayerTeam(evt.getPlayer().getName()) == null){
			_tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer().getName());
		}
	}
	
	public void onEnable(){
		Logger log = Logger.getLogger("minecraft");
		log.info("Registering events...");
		getServer().getPluginManager().registerEvents(this, this);
		log.info("Initialising teams...");
		_tc = TeamContainerInit();
		
		log.info("MCWarClan has been successfully launched !");
		return;
	}
	
	public void onDisable() {
		Logger log = Logger.getLogger("minecraft");
		log.info("Saving datas...");
		_tc.serialize();
		return;
	}

}

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MCWarClanCommandExecutor implements CommandExecutor {
	
	private TeamManager _tc;
	private Server _server;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic CommandExecutor constructor.
     * @param tc
     * @param server
     */
	public MCWarClanCommandExecutor(TeamManager tc, Server server) {
		_tc = tc;
		_server = server;
	}

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Functions ---------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Show to the sender the list of all teams in the game.
     * @param sender
     * @return
     */
	public boolean showteamsCommand(CommandSender sender){
		// sender.sendMessage("§8##########################################################################################################");
        Messages.sendMessage(_tc.teamsList(), Messages.messageType.INGAME, sender);
		// sender.sendMessage("§8##########################################################################################################");
		return true;
	}

    /**
     *  Sort of admin command. Allows someone to assign someone else to a specific team.
     * @param sender
     * @param args
     * @return
     */
	public boolean assignCommand(CommandSender sender, String[] args){
		OfflinePlayer p = findPlayerByName(args[0]);
		if (args.length > 1 && p != null){
			Team toJoin = _tc.searchTeam(args[1]);   // Search by name
			MCWarClanPlayer player = _tc.getPlayer(args[0]);
			if (toJoin == null)
				toJoin = _tc.searchTeam(new Color(args[1])); // Search by colorName

			if (toJoin != null){
                if (player.switchTo(toJoin)) {
                    Messages.sendMessage(player.get_name() + " has successfully been added to " + toJoin.getColoredName(), Messages.messageType.INGAME, sender);
                    if (p.isOnline()){
                        Messages.sendMessage("You have been added to team " + toJoin.getColoredName() + " by " + sender.getName(), Messages.messageType.INGAME, p.getPlayer());
                    return true;
                    }
                }
			} else {
                Messages.sendMessage("Invalid team or color name.", Messages.messageType.INGAME, sender);
				return true;
			}
		}
		return false;
	}

    /**
     *  Shows the team members of the sender's team or of the specified team.
     * @param sender
     * @param args
     * @return
     */
	public boolean teamCommand(CommandSender sender, String[] args){
		if(args.length == 0){
			if(sender instanceof Player){
				// sender.sendMessage("§8##########################################################################################################");
                Messages.sendMessage(_tc.getPlayer(sender.getName()).get_team().playerList(), Messages.messageType.INGAME, sender);
				// sender.sendMessage("§8##########################################################################################################");
				return true;
			}
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
			return true;
		}
		else if (args.length == 1 && exist(args[0]) && _tc.getPlayer(args[0]).get_team() != null) {
			// sender.sendMessage("§8##########################################################################################################");
            Messages.sendMessage(_tc.getPlayer(args[0]).get_team().playerList(), Messages.messageType.INGAME, sender);
			// sender.sendMessage("§8##########################################################################################################");
			return true;
		}
		return false;
	}

    /**
     *  Sort of admin command. Allows someone to kick someone else from a specific team.
     * @param sender
     * @param args
     * @return
     */
	public boolean unassignCommand(CommandSender sender, String[] args){
		OfflinePlayer p = findPlayerByName(args[0]);
		if (p != null){
            MCWarClanPlayer player = _tc.getPlayer(args[0]);
            Team toLeave = player.get_team();
			if (player != null){
                if (player.get_team().get_id() == Team.BARBARIAN_TEAM_ID) {
                    Messages.sendMessage("You cannot remove someone from the " + toLeave.getColoredName() + " team !", Messages.messageType.INGAME, sender);
                    return true;
                } else if (player.kick()) {
                    Messages.sendMessage(player.get_name() + " has successfully been kicked from " + toLeave.getColoredName() + ".", Messages.messageType.INGAME, sender);
                } else {
                    Messages.sendMessage("Cannot add " + player.get_name() + "to barbarians !", Messages.messageType.ALERT, null);
                }

				if(p.isOnline()){	// Send a message to the player concerned.
                    Messages.sendMessage("You have been kicked from team " + toLeave.getColoredName() + " by " + sender.getName() + ". You are now a §7Barbarian !",
                            Messages.messageType.INGAME, p.getPlayer());
				}
				return true;					
			}
            Messages.sendMessage(args[0] + " does not exist in MCWarClan database !", Messages.messageType.INGAME, sender);
		}
        Messages.sendMessage(args[0] + " does not exist !", Messages.messageType.INGAME, sender);
		return false;
	}

    /**
     * Allows the sender to leave it's current team an join the barbarian team.
     * @param sender
     * @return
     */
	public boolean leaveCommand(CommandSender sender){
		if(sender instanceof Player){
            MCWarClanPlayer player = _tc.getPlayer(sender.getName());
			Team toLeave = player.get_team();
			if (toLeave.get_id() == Team.BARBARIAN_TEAM_ID) {
                Messages.sendMessage("You cannot leave the §7Barbarian§6 team !", Messages.messageType.INGAME, sender);
				return true;
			} else if (player.kick()) {
                Messages.sendMessage("You have successfully left " + toLeave.getColoredName() + ". You are now a §7Barbarian§6 !", Messages.messageType.INGAME, sender);
                return true;
            } else {
                Messages.sendMessage("Due to an unknown error, you cannot leave" + toLeave.getColoredName() + ". Ask an admin to see what's happening.", Messages.messageType.INGAME, sender);
                Messages.sendMessage("Cannot kick " + player.get_name() + " from " + toLeave.get_name() + ".", Messages.messageType.ALERT, null);
                return true;
            }
		} else
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
		return false;
	}

    /**
     *  Allows the sender to join the specified team.
     * @param sender
     * @param args
     * @return
     */
	public boolean joinCommand(CommandSender sender, String[] args){
		if(sender instanceof Player){
            MCWarClanPlayer player = _tc.getPlayer(sender.getName());
			Team toLeave = player.get_team();
			Team toJoin = _tc.searchTeam(args[0]);
			if(toJoin == null){
				toJoin = _tc.searchTeam(new Color(args[0]));
			}
			if (toJoin != null) {
                if (toJoin.get_id() == toLeave.get_id()) {
                    Messages.sendMessage("You cannot join " + toJoin.get_color().get_colorMark() + toJoin.get_name() + " §6team ! You're already in !", Messages.messageType.INGAME, sender);
                    return true;
                } else if(toJoin.get_id() == Team.BARBARIAN_TEAM_ID) {
                    return leaveCommand(sender);
                } else {
                    if (player.canPay(toJoin.get_cost())) {
                        if (player.switchTo(toJoin)) {
                            Messages.sendMessage("Well done, you left " + toLeave.getColoredName() + " and joined " + toJoin.getColoredName() + ".",
                                    Messages.messageType.INGAME, sender);
                        }

                        if (!player.payTribute(toJoin.get_cost())) {
                            Messages.sendMessage("Due to an unknown error, you cannot pay the tribute. Please tell this to an admin before doing anything.", Messages.messageType.INGAME, sender);
                            Messages.sendMessage(player.get_name() + " cannot pay the tribute to enter " + toLeave.get_name() + ".", Messages.messageType.ALERT, null);
                            return true;
                        }
                    } else {
                        Messages.sendMessage("You do not have enough resources, here is the exhaustive list of materials needed: ", Messages.messageType.INGAME, sender);
                        Messages.sendMessage(toJoin.get_cost().getResourceTypes(), Messages.messageType.INGAME, sender);
                    }
                }
				return true;
			} else {
                Messages.sendMessage("This team does not exist.", Messages.messageType.INGAME, sender);
				return true;
			}
		}
        Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
		return false;
	}

    /**
     *  Allows someone to create a team.
     * @param sender
     * @param args
     * @return
     */
	public boolean createteamCommand(CommandSender sender, String[] args){
        if(_tc.get_teamArray().size() >= _tc.get_maxTeams()){
            Messages.sendMessage("The maximum number of team is already reach !(" + _tc.get_maxTeams() + ")", Messages.messageType.INGAME, sender);
            return true;
        }
        if(sender instanceof Player){
            if(args.length == 2) {
                Team toJoin = new Team(new Color(args[1]), args[0], Settings.initialTeamSize, _tc);
                MCWarClanPlayer player = _tc.getPlayer(sender.getName());
                if (player.createTeam(toJoin)) {
                    player.switchTo(toJoin);
                    _tc.sendMessage(toJoin.getColoredName() + " has been created by " + player.get_name() + " let's prepare to surrender...");
                    Messages.sendMessage("You successfully joined " + toJoin.getColoredName() + " !", Messages.messageType.INGAME, sender);
                }
                return true;
            }
		}
        else{
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
            return true;
        }
		return false;
	}

    /**
     *  Allows someone to create a base.
     * @param sender
     * @param args
     * @return
     */
    private boolean createHQCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player)
        {
            if (args.length != 1) {
                return false;
            } else {
                Player p = ((Player) sender).getPlayer();
                MCWarClanPlayer player = _tc.getPlayer(p.getUniqueId());

                if (p.isOnline()) {
                    player.createHQ(p.getTargetBlock(null, 10).getLocation(), args[0]);
                    return true;
                } else {
                    Messages.sendMessage(p.getName() + " is not online ! Cannot proceed to the base creation.", Messages.messageType.ALERT, null);
                    Messages.sendMessage(p.getName() + " is not online ! Cannot proceed to the base creation.", Messages.messageType.INGAME, p);
                    return true;
                }
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
            return true;
        }
    }

    /**
     * Allows someone to create a base.
     * @param sender
     * @param args
     * @return
     */
    public boolean createBaseCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            return false;
        } else if (sender instanceof Player) {
            MCWarClanPlayer player = _tc.getPlayer(((Player) sender).getUniqueId());
            if (player != null) {
                player.createBase(args[0], Integer.parseInt(args[1]), args[2]);
            } else {
                Messages.sendMessage("You're not in any team ! Ask an admin to be added to a team !", Messages.messageType.INGAME, sender);
                Messages.sendMessage(sender.getName() + " has no team ! Add him to a team before any errors occurs !", Messages.messageType.ALERT, null);
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * This command show you information about a base, depending how you call this function.
     * @param sender
     * @param args
     * @return
     */
    public boolean baseInfoCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());
            if (args.length == 0) {
                Base playerBase = mcPlayer.getCurrentBase();
                if (playerBase != null && playerBase.get_team().get_id() == mcPlayer.get_team().get_id()) {
                    Messages.sendMessage("Here are the detailed information about the base you're in at the moment: ", Messages.messageType.INGAME, player);
                    Messages.sendMessage(playerBase.getInfo(), Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Currently, you're not in any allied base.", Messages.messageType.INGAME, player);
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    ArrayList<Base> playerBases = mcPlayer.get_team().get_bases();
                    String[] info = new String[playerBases.size()];
                    for (int i = 0; i < playerBases.size(); i++) {
                        info[i] = playerBases.get(i).getMinimalInfo();
                    }
                    if (info.length == 0) {
                        Messages.sendMessage("Your team don't own any base at the moment !", Messages.messageType.INGAME, player);
                    }
                    Messages.sendMessage("Here is a shortened list of details about your bases: ", Messages.messageType.INGAME, player);
                    Messages.sendMessage(info, Messages.messageType.INGAME, player);
                } else {
                    int id = Integer.parseInt(args[0]);
                    Base baseAsked = mcPlayer.get_team().getBase(id);
                    if (baseAsked != null && baseAsked.get_team().get_id() == mcPlayer.get_team().get_id()) {
                        Messages.sendMessage("Here are the detailed information about the base you're asking for :", Messages.messageType.INGAME, player);
                        Messages.sendMessage(baseAsked.getInfo(), Messages.messageType.INGAME, player);
                    } else {
                        Messages.sendMessage("No allied base found for id: " + args[0] + ".", Messages.messageType.INGAME, player);
                    }
                }
            } else {
                return false;
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Override ----------------------------
    //////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		
		if ((label.toLowerCase().equals("showteams") || label.toLowerCase().equals("lt") || label.toLowerCase().equals("st")) && args.length == 0){
			return showteamsCommand(sender);
		} else if (label.toLowerCase().equals("assign")) {
			return assignCommand(sender, args);
		} else if (label.toLowerCase().equals("team")){
			return teamCommand(sender, args);
		} else if (label.toLowerCase().equals("unassign") && args.length == 1) {
			return unassignCommand(sender, args);
		} else if (label.toLowerCase().equals("leave") && args.length == 0){
			return leaveCommand(sender);
		} else if (label.toLowerCase().equals("join") && args.length == 1){
			return joinCommand(sender, args);
		} else if (label.toLowerCase().equals("createteam")){
			return createteamCommand(sender, args);
        } else if (label.toLowerCase().equals("createhq")) {
            return createHQCommand(sender, args);
        } else if (label.equalsIgnoreCase("createbase")) {
            return createBaseCommand(sender, args);
        } else if(label.equalsIgnoreCase("baseinfo")) {
            return baseInfoCommand(sender,args);
        }
		return false;
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
        return _server.getOfflinePlayer(playerName).hasPlayedBefore() || _server.getOfflinePlayer(playerName).isOnline();
    }

    /**
     *  Returns a player using a name.
     * @param name
     * @return
     */
    public OfflinePlayer findPlayerByName(String name){
        if(exist(name)){
            return _server.getOfflinePlayer(name);
        }
        return null;
    }







}



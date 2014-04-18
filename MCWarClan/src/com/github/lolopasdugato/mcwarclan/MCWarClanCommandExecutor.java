package com.github.lolopasdugato.mcwarclan;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MCWarClanCommandExecutor implements CommandExecutor {
	
	private TeamContainer _tc;
	private Server _server;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic CommandExecutor constructor.
     * @param tc
     * @param server
     */
	public MCWarClanCommandExecutor(TeamContainer tc, Server server) {
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
		if(args.length > 1 && p != null){
			Team toJoin = _tc.searchTeam(args[1]);   // Search by name
			MCWarClanPlayer player = _tc.getPlayer(args[0]);
            Team toLeave = player.get_team();
			if(toJoin == null){
				toJoin = _tc.searchTeam(new Color(args[1])); // Search by colorName
			}
			if(toJoin != null){
                if (toLeave.deleteTeamMate(player)){
                    if(toJoin.addTeamMate(player))
                        Messages.sendMessage(player.get_name() + " §6has successfully been added to " + toJoin.get_color().get_colorMark() + toJoin.get_name(), Messages.messageType.INGAME, sender);
                    else{
                        toLeave.addTeamMate(player);
                        Messages.sendMessage(player.get_name() + " §6cannot be add to " + toJoin.get_color().get_colorMark() + toJoin.get_name() + " §6(maybe the team is full ?)", Messages.messageType.INGAME, sender);
                        return true;
                    }
                    if(p.isOnline()){
                        Messages.sendMessage("You have been added to team " + toJoin.get_color().get_colorMark() + toJoin.get_name() + " §6by " + sender.getName(), Messages.messageType.INGAME, p.getPlayer());
                    }
                    return true;
                }
                else
                    Messages.sendMessage("Cannot delete " + player.get_name() + " from " + player.get_team().get_color().get_colorMark() + player.get_team().get_name() + "§6.", Messages.messageType.INGAME, sender);
			}
			else{
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
		if(p != null){
            MCWarClanPlayer player = _tc.getPlayer(args[0]);
            Team toLeave = player.get_team();
			if(player != null){
                if(toLeave.get_id() == Team.BARBARIAN_TEAM_ID){
                    Messages.sendMessage("You cannot remove someone from the " + toLeave.get_color().get_colorMark() + toLeave.get_name() + " §6team !", Messages.messageType.INGAME, sender);
                    return true;
                }
				else if(player.get_team().deleteTeamMate(player)){
                    Messages.sendMessage(player.get_name() + " has successfully been kicked from " + toLeave.get_color().get_colorMark() + toLeave.get_name() + "§6.", Messages.messageType.INGAME, sender);
                }
                else{
                    Messages.sendMessage(player.get_name() + " cannot be kicked from " + toLeave.get_color().get_colorMark() + toLeave.get_name() + "§6.", Messages.messageType.INGAME, sender);
                    return false;
                }
				if(!_tc.getTeam(Team.BARBARIAN_TEAM_ID).addTeamMate(player)){
                    toLeave.addTeamMate(player);
                    Messages.sendMessage("Cannot add " + player.get_name() + "to barbarians !", Messages.messageType.ALERT, null);
                    return true;
                }
				if(p.isOnline()){	// Send a message to the player concerned.
                    Messages.sendMessage("You have been kicked from team " + toLeave.get_color().get_colorMark() + toLeave.get_name() + " §6by " + sender.getName() + ". You are now a §7Barbarian !", Messages.messageType.INGAME, p.getPlayer());
				}
				return true;					
			}
            Messages.sendMessage(args[0] + " does not exist in MCWarClan database !", Messages.messageType.INGAME, sender);
		}
        Messages.sendMessage(args[0] + " does not exist !", Messages.messageType.INGAME, sender);
		return false;
	}

    /**
     *  Allows the sender to leave it's current team an join the barbarian team.
     * @param sender
     * @return
     */
	public boolean leaveCommand(CommandSender sender){
		if(sender instanceof Player){
            MCWarClanPlayer player = _tc.getPlayer(sender.getName());
			Team toLeave = player.get_team();
            Team toJoin = _tc.getTeam(Team.BARBARIAN_TEAM_ID);
			if(toLeave.get_id() == Team.BARBARIAN_TEAM_ID){
                Messages.sendMessage("You cannot leave the §7Barbarian§6 team !", Messages.messageType.INGAME, sender);
				return true;
			}
			else if(toLeave.deleteTeamMate(player)){
                if(toJoin.addTeamMate(player)){
                    Messages.sendMessage("You have successfully left " + toLeave.get_color().get_colorMark() + toLeave.get_name() + ".§6 You are now a §7Barbarian !", Messages.messageType.INGAME, sender);
                    return true;
                }
                else{
                    Messages.sendMessage("Due to an unknown error, you cannot join" + toJoin.get_color().get_colorMark() + toJoin.get_name() + "§6. Ask an admin to see what's happening.", Messages.messageType.INGAME, sender);
                    Messages.sendMessage("Cannot add " + player.get_name() + " from " + toJoin.get_name() + ".", Messages.messageType.ALERT, null);
                    return true;
                }
            }
            else{
                Messages.sendMessage("Due to an unknown error, you cannot leave" + toLeave.get_color().get_colorMark() + toLeave.get_name() + "§6. Ask an admin to see what's happening.", Messages.messageType.INGAME, sender);
                Messages.sendMessage("Cannot kick " + player.get_name() + " from " + toLeave.get_name() + ".", Messages.messageType.ALERT, null);
                return true;
            }
		}
		else
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
			if(toJoin != null){
                if(toJoin.get_id() == toLeave.get_id()){
                    Messages.sendMessage("You cannot join " + toJoin.get_color().get_colorMark() + toJoin.get_name() + " §6team ! You're already in !", Messages.messageType.INGAME, sender);
                    return true;
                }
                else if(toJoin.get_id() == Team.BARBARIAN_TEAM_ID){
                    return leaveCommand(sender);
                }
                else{
                    if(player.canPay(toJoin.get_cost())){
                        if(!toLeave.deleteTeamMate(player)){
                            Messages.sendMessage("Due to an unknown error, you cannot leave " + toLeave.get_color().get_colorMark() + toLeave.get_name() + "§6.", Messages.messageType.INGAME, sender);
                            Messages.sendMessage("Cannot kick " + player.get_name() + " from " + toLeave.get_name() + ".", Messages.messageType.ALERT, null);
                            return true;
                        }
                        else if(!toJoin.addTeamMate(player)){
                            if(!toLeave.addTeamMate(player)){
                                Messages.sendMessage("Cannot add " + player.get_name() + " from " + toLeave.get_color().get_colorMark() + toLeave.get_name() + ". This add results from the join error", Messages.messageType.ALERT, null);
                            }
                            Messages.sendMessage("Too many member in " + toJoin.get_color().get_colorMark() + toJoin.get_name() + "§6.(" + toJoin.get_teamMembers().size() + "/" + toJoin.get_teamSize() + ")", Messages.messageType.INGAME, sender);
                            return true;
                        }
                        else if(!player.payTribute(toJoin.get_cost())){
                            // TODO: REIMBURSE HIM !
                            Messages.sendMessage("Due to an unknown error, you cannot pay the tribute. Please tell this to an admin before doing anything.", Messages.messageType.INGAME, sender);
                            Messages.sendMessage(player.get_name() + " cannot pay the tribute to enter " + toLeave.get_name() + ".", Messages.messageType.ALERT, null);
                            return true;
                        }
                        Messages.sendMessage("Well done, you left " + toLeave.get_color().get_colorMark() + toJoin.get_name() + " §6and joined " + toJoin.get_color().get_colorMark() + toJoin.get_name() + "§6.", Messages.messageType.INGAME, sender);
                    }
                    else{
                        Messages.sendMessage("You do not have enough resources, here is the exhaustive list of materials needed: ", Messages.messageType.INGAME, sender);
                        Messages.sendMessage(toJoin.get_cost().getResourceTypes(), Messages.messageType.INGAME, sender);
                    }
                }
				return true;
			}
			else{
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
                Team actual = player.get_team();
                if (player.canPay(_tc.get_creatingCost())){     // If you can pay...
                    if(_tc.addTeam(toJoin)){      // If the team can be added
                        if(!actual.deleteTeamMate(player)){
                            Messages.sendMessage("Due to an unknown error, we cannot kick you from " + actual.get_color().get_colorMark() + actual.get_name() + " §6while switching you to the new team created. Contact an admin for support.", Messages.messageType.INGAME, sender);
                            Messages.sendMessage("Cannot kick " + sender.getName() + "from " + actual.get_name() + " while switching to the newly created team.", Messages.messageType.ALERT, null);
                            return true;
                        }
                        else if(!toJoin.addTeamMate(player)){
                            if(!actual.addTeamMate(player))
                                Messages.sendMessage("Due to an unknown error, we cannot add you again to " + actual.get_color().get_colorMark() + actual.get_name() + " §6because there is too many memebrs in the new team. Contact an admin for support.", Messages.messageType.INGAME, null);
                            Messages.sendMessage("too many member in " + toJoin.get_color().get_colorMark() + toJoin.get_name() + "§6.", Messages.messageType.INGAME, sender);
                            return true;
                        }
                        else if(!player.payTribute(_tc.get_creatingCost())){      // If the tribute paying works well
                            Messages.sendMessage("Due to an unknown error, you cannot pay your tribute. Please contact an admin.", Messages.messageType.INGAME, sender);
                            Messages.sendMessage("Unknown error while paying tribute for " + player.get_name() + " !", Messages.messageType.ALERT, null);
                            return true;
                        }
                        else{
                            Messages.sendMessage(toJoin.get_color().get_colorMark() + toJoin.get_name() + "§6 has been successfully created !", Messages.messageType.INGAME, sender);
                            return true;
                        }
                    }
                    else{
                        Messages.sendMessage("Sorry, but name or color is already taken by another team. Here is the colorname list: ", Messages.messageType.INGAME, sender);
                        Messages.sendMessage("§2GREEN, §eYELLOW, §0BLACK, §dMAGENTA, §5PURPRLE, §3CYAN, §bLIGHTBLUE", Messages.messageType.INGAME, sender);
                        return true;
                    }
                }
                else{
                    Messages.sendMessage("You do not have enough resources, here is the exhaustive list of materials needed: ", Messages.messageType.INGAME, sender);
                    Messages.sendMessage(_tc.get_creatingCost().getResourceTypes(), Messages.messageType.INGAME, sender);
                    return true;
                }
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
    private boolean createbaseCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player)
        {
            if (args.length > 0)
            {
                return false;
            }
            else
            {
                Player p = ((Player) sender).getPlayer();
                MCWarClanPlayer player = _tc.getPlayer(p.getName());


                //Find player's location
//                Player p = findPlayerByName(sender.getName()).getPlayer();

                if (p.isOnline()) {

                    //Check if the player's team is not 'Barbarians'
                    if (player.get_team().get_id() == Team.BARBARIAN_TEAM_ID) {
                        Messages.sendMessage("You cannot create a base as a Barbarian.", Messages.messageType.INGAME, p);
                        return true;
                    }
                    //Check if the player's team have enough resources to create the base
                    Cost cost = Settings.baseInitialCost;
                    if (p.getGameMode() != GameMode.CREATIVE) {
                        if (!player.canPay(cost)) {
                            Messages.sendMessage("You cannot create a base (not enough materials). You need: ", Messages.messageType.INGAME, p);
                            Messages.sendMessage(cost.getResourceTypes(), Messages.messageType.INGAME, p);
                            return true;
                        }
                    }
                    //Here the player have enough resources to pay

                    //Now we check if the location is far enough from other bases
                    Location loc = p.getTargetBlock(null, 10).getLocation();
                    final ArrayList<Team> teams = _tc.get_teamArray();
                    ArrayList<Base> bases;

                    boolean isHQ = false;
                    if (player.get_team().get_bases().size() == 0)
                        isHQ = true;

                    boolean overlap = false;
                    int i = 0, j = 0;


                    //TODO Move to the 'isInEnemyTerritory' function
                    while (i < teams.size() && !overlap) {
                        if (teams.get(i) != player.get_team()) {
                            bases = teams.get(i).get_bases();
                            while (j < bases.size() && !overlap) {
                                if (bases.get(j).isNearBase(isHQ, loc))
                                    overlap = true;
                                j++;
                            }
                        }
                        j = 0;
                        i++;
                    }

                    //Verification of the barbarian spawn
                    Location barbSpawn = Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation();
                    final double dist = barbSpawn.distance(p.getLocation());
                    if (dist < Settings.barbariansSpawnDistance + Settings.secureBarbarianDistance + Settings.radiusHQBonus + Settings.initialRadius) {
                        Messages.sendMessage("You cannot create a base near the Barbarian spawn.", Messages.messageType.INGAME, p);
                        return true;
                    }

                    Messages.sendMessage("initial radius: " + Settings.initialRadius + ", HQbonusradius: " + Settings.radiusHQBonus + ", baseMinHQDistanceToOthers: " + Settings.baseMinHQDistanceToOthers + ".", Messages.messageType.DEBUG, null);


                    if (overlap) {
                        Messages.sendMessage("You cannot create a base near another enemy base.", Messages.messageType.INGAME, p);
                        if (isHQ)
                            Messages.sendMessage("Moreover, this is your first base, so you have to build it " + (Settings.baseMinHQDistanceToOthers + (Settings.initialRadius + Settings.radiusHQBonus)*2 ) + " blocks far from the other teams bases"
                                    , Messages.messageType.INGAME, p);

                        return true;
                    }

                    //We have check the location, with is correct regarding other bases

                    //Now we try to create the flag
                    Base b = null;
                    try {
                        b = new Base(isHQ, player.get_team(), new MCWarClanLocation(loc));
                    } catch (Exception.NotEnoughSpaceException e) {
                        Messages.sendMessage("There is not enough space to create the base.", Messages.messageType.INGAME, p);
                        return true;
                    } catch (Exception.NotValidFlagLocationException e) {
                        Messages.sendMessage("There is not a solid block under the flag.", Messages.messageType.INGAME, p);
                        return true;
                    }

                    //If the flag can be created, add the base to the base array
                    player.get_team().get_bases().add(b);

                    // If this base is the first one (an HQ) reset de MCWarClan spawn location for all teamMembers
                    if(b.is_HQ()){
                        for (int k = 0; k < player.get_team().get_teamMembers().size(); k++){
                            player.get_team().get_teamMembers().get(k).reloadSpawn();
                        }
                    }

                    //Substract the cost of the base to player's inventory
                    if (p.getGameMode() != GameMode.CREATIVE)
                        player.payTribute(cost);

                    Messages.sendMessage("The new base has been created !", Messages.messageType.INGAME, p);
                    return true;
                } else {
                    Messages.sendMessage(p.getName() + " is note online ! Cannot proceed to the base creation.", Messages.messageType.ALERT, null);
                    Messages.sendMessage(p.getName() + " is note online ! Cannot proceed to the base creation.", Messages.messageType.INGAME, p);
                    return false;
                }
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Override ----------------------------
    //////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		
		if((label.toLowerCase().equals("showteams") || label.toLowerCase().equals("lt") || label.toLowerCase().equals("st")) && args.length == 0){
			return showteamsCommand(sender);
		}
		
		else if(label.toLowerCase().equals("assign")) {
			return assignCommand(sender, args);
		}
		
		else if(label.toLowerCase().equals("team")){
			return teamCommand(sender, args);
		}
		
		else if(label.toLowerCase().equals("unassign") && args.length == 1) {
			return unassignCommand(sender, args);
		}
		
		else if(label.toLowerCase().equals("leave") && args.length == 0){
			return leaveCommand(sender);
		}
		
		else if(label.toLowerCase().equals("join") && args.length == 1){
			return joinCommand(sender, args);
		}
		
		else if(label.toLowerCase().equals("createteam")){
			return createteamCommand(sender, args);
        } else if (label.toLowerCase().equals("createbase")) {
            return createbaseCommand(sender, args);
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



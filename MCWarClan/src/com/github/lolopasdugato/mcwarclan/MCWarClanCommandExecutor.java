package com.github.lolopasdugato.mcwarclan;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MCWarClanCommandExecutor implements CommandExecutor {
	
	private TeamManager _tc;
	private Server _server;
    private JavaPlugin _plugin;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic CommandExecutor constructor.
     * @param tc
     * @param server
     */
	public MCWarClanCommandExecutor(TeamManager tc, Server server, JavaPlugin plugin) {
		_tc = tc;
		_server = server;
        _plugin = plugin;
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
        if (args.length == 2){
            OfflinePlayer p = findPlayerByName(args[0]);
			Team toJoin = _tc.searchTeam(args[1]);   // Search by name
			MCWarClanPlayer player = _tc.getPlayer(args[0]);
			if (toJoin == null)
				toJoin = _tc.searchTeam(new Color(args[1])); // Search by colorName

			if (toJoin != null){
                Team toLeave = player.get_team();
                if (player.switchTo(toJoin)) {
                    Messages.sendMessage(player.get_name() + " has successfully been added to " + toJoin.getColoredName(), Messages.messageType.INGAME, sender);
                    toJoin.sendMessage("Well, here is some more fresh meat ! " + player.get_name() + " has joined the team !");
                    if (toLeave.get_id() != Team.BARBARIAN_TEAM_ID) {
                        toLeave.sendMessage(player.get_name() + " has left the team !");
                    }
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
                    return true;
                }

                toLeave.sendMessage(player.get_name() + " has left the team !");

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
                toLeave.sendMessage(player.get_name() + " has left the team !");
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
                            toJoin.sendMessage("Well, here is some more fresh meat ! " + player.get_name() + " has joined the team !");
                            if (toLeave.get_id() != Team.BARBARIAN_TEAM_ID) {
                                toLeave.sendMessage(player.get_name() + " left the team !");
                            }
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
                    Team toLeave = player.get_team();
                    player.switchTo(toJoin);
                    _tc.sendMessage(toJoin.getColoredName() + " has been created by " + player.get_name() + " let's prepare to surrender...");
                    Messages.sendMessage("You successfully joined " + toJoin.getColoredName() + " !", Messages.messageType.INGAME, sender);
                    if (toLeave.get_id() != Team.BARBARIAN_TEAM_ID) {
                        toLeave.sendMessage(player.get_name() + " has left your team to create " + toJoin.getColoredName() + " !");
                    }
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
                int referenceID;
                try {
                    referenceID = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Messages.sendMessage(args[1] + " is not a number.", Messages.messageType.INGAME, sender);
                    return false;
                }
                player.createBase(args[0], referenceID, args[2]);
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
                    int id;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        Messages.sendMessage("No allied base found for id: " + args[0] + ".", Messages.messageType.INGAME, player);
                        return false;
                    }
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

    /**
     * Allows someone to launch a contest process.
     * @param sender
     * @param args
     * @return
     */
    public boolean contestCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            return false;
        } else if (sender instanceof  Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());
            Base currentBase = mcPlayer.getCurrentBase();
            if (mcPlayer.get_team().get_id() == Team.BARBARIAN_TEAM_ID) {
                Messages.sendMessage("You cannot contest a base when you are a §7barbarian§6 !", Messages.messageType.INGAME, player);
            } else if (currentBase != null && mcPlayer.get_team().get_id() == currentBase.get_team().get_id()) {
                Messages.sendMessage("Use your mind... you cannot attack your own team base !", Messages.messageType.INGAME, player);
            } else if (mcPlayer.get_team().get_bases().size() < 1) {
                Messages.sendMessage("You need at least one Head Quarter, then you could launch any battle you want !", Messages.messageType.INGAME, player);
            } else if (currentBase == null || !currentBase.get_team().isEnemyToTeam(mcPlayer.get_team())) {
                Messages.sendMessage("You're not in an enemy base, you cannot contest this territory.", Messages.messageType.INGAME, player);
            } else if (!currentBase.get_team().enoughMatesToBeAttack()) {
                Messages.sendMessage("Not enough players connected in " + currentBase.get_team().getColoredName() + " to attack them.", Messages.messageType.INGAME, player);
            } else if (currentBase.isContested()) {
                Messages.sendMessage("This team is already attacked by another team. But nothing forbid you to help one of these two...", Messages.messageType.INGAME, player);
            } else {
                currentBase.isContested(true);
                // Inform the two teams !
                Team attackedTeam = currentBase.get_team();
                Team attackingTeam = mcPlayer.get_team();
                attackedTeam.sendMessage(currentBase.get_name() + " is attacked by " + attackingTeam.get_color().get_colorMark() + attackingTeam.get_name() + " !");
                attackingTeam.sendMessage("Your team is attacking " + attackedTeam.get_color().get_colorMark() + attackedTeam.get_name() + " !");

                //Create a new thread in order to check if the enemies are defeated
                BukkitTask tks = new MCWarClanRoutine.ContestedBaseRoutine(_plugin, currentBase,
                        attackingTeam).runTaskTimer(_plugin,
                        0, 100);
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * Allows someone to upgrade a base into his team.
     * @param sender
     * @param args
     * @return
     */
    public boolean upgradeCommand(CommandSender sender, String[] args) {
        if (sender instanceof  Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());
            if (args.length == 0) {
                Base currentBase = mcPlayer.getCurrentBase();
                if (currentBase == null || currentBase.get_team().isEnemyToTeam(mcPlayer.get_team())) {
                    Messages.sendMessage("You should be in the base you want to upgrade to do so !", Messages.messageType.INGAME, player);
                } else if (currentBase.get_level() >= 5) {
                    Messages.sendMessage("§a" + currentBase.get_name() + "§6 has already reached the maximum level !", Messages.messageType.INGAME, player);
                } else if (!currentBase.upgrade()) {
                    Messages.sendMessage("§a" + currentBase.get_name() + "§6 cannot upgrade to level §a" + currentBase.get_level() + 1 + " §6. Not enough money !", Messages.messageType.INGAME, player);
                    Messages.sendMessage("Upgrading to level §a" + currentBase.get_level() + 1 + " §6cost §a" + Settings.radiusCost[currentBase.get_level() - 1] + "§6.", Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Well done, §a" + currentBase.get_name() + " §6has been upgraded to level §a" + currentBase.get_level() + " §6 by §a" + mcPlayer.get_name() + "§6 !", Messages.messageType.INGAME, player);
                }
            } else if (args.length == 1) {
                int id;
                try {
                    id = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    Messages.sendMessage("No base found for id §a" + args[0] + "§6.", Messages.messageType.INGAME, player);
                    return false;
                }
                Base baseAsked = mcPlayer.get_team().getBase(id);
                if (baseAsked == null) {
                    Messages.sendMessage("No base found for id §a" + args[0] + "§6.", Messages.messageType.INGAME, player);
                } else if (baseAsked.get_level() >= 5) {
                    Messages.sendMessage("§a" + baseAsked.get_name() + "§6 has already reached the maximum level !", Messages.messageType.INGAME, player);
                } else if (!baseAsked.upgrade()) {
                    Messages.sendMessage("§a" + baseAsked.get_name() + "§6 cannot upgrade to level §a" + baseAsked.get_level() + 1 + " §6. Not enough money !", Messages.messageType.INGAME, player);
                    Messages.sendMessage("Upgrading to level §a" + baseAsked.get_level() + 1 + " §6cost §a" + Settings.radiusCost[baseAsked.get_level() - 2] + "§6.", Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Well done, §a" + baseAsked.get_name() + " §6has been upgraded to level §a" + baseAsked.get_level() + " §6 by §a" + mcPlayer.get_name() + "§6 !", Messages.messageType.INGAME, player);
                }
            } else {
                return false;
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * Allows a player to store Emeralds into the team treasure.
     * @param sender
     * @param args
     * @return
     */
    public boolean saveEmeraldsCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());
            if (args.length == 1) {
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    Messages.sendMessage("§a" + args[0] + " §6is not a valid amount of emeralds to store in the team treasure.", Messages.messageType.INGAME, player);
                    return false;
                }
                if (amount < 0) {
                    Messages.sendMessage("§a" + args[0] + " §6is not a valid amount of emeralds to store in the team treasure.", Messages.messageType.INGAME, player);
                    return false;
                } else if (!mcPlayer.save(amount)) {
                    Messages.sendMessage("Sorry, you do not have §a" + args[0] + "§6 emerald(s) in your inventory !", Messages.messageType.INGAME, player);
                } else {
                    mcPlayer.get_team().sendMessage("§a" + mcPlayer.get_name() + "§6 saved §a" + amount + "§6 emerald(s) in the team treasure !");
                    Messages.sendMessage("The treasure value is now §a" + mcPlayer.get_team().get_money() + "§6.", Messages.messageType.INGAME, player);
                }
            } else {
                return false;
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * Allows a player to withdraw money from the team bank.
     * @param sender
     * @param args
     * @return
     */
    public boolean withdrawMoneyCommand(CommandSender sender, String[] args) {
        if (sender instanceof  Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());
            if (args.length == 1) {
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    Messages.sendMessage("§a" + args[0] + " §6is not a valid amount of emeralds to store in the team treasure.", Messages.messageType.INGAME, player);
                    return false;
                }
                Base currentBase = mcPlayer.getCurrentBase();
                if (amount < 0) {

                    return false;
                } else if (currentBase == null || currentBase.get_team().isEnemyToTeam(mcPlayer.get_team())) {
                    Messages.sendMessage("You have to be in one of your bases to withdraw money !", Messages.messageType.INGAME, player);
                } else if (amount <= mcPlayer.get_team().get_money()) {
                    Location locationToDrop = currentBase.get_loc().getLocation();
                    locationToDrop.add(2, 0, 0);
                    mcPlayer.get_team().dropEmeralds(amount, locationToDrop);
                    mcPlayer.get_team().sendMessage("§a" + mcPlayer.get_name() + "§6 just take §a" + amount + " §6 emerald(s) from the team treasure at §a" + currentBase.get_name() + "§6!");
                    Messages.sendMessage("Don't forget emerald(s) ! you will find them in front of §a" + currentBase.get_name() + " §6flag.", Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Your team does not have §a" + args[0] + "§6 emerald(s) !", Messages.messageType.INGAME, player);
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
        } else if (label.equalsIgnoreCase("baseinfo")) {
            return baseInfoCommand(sender,args);
        } else if (label.equalsIgnoreCase("contest")) {
            return contestCommand(sender, args);
        } else if (label.equalsIgnoreCase("upgrade")) {
            return upgradeCommand(sender, args);
        } else if (label.equalsIgnoreCase("savemoney")) {
            return saveEmeraldsCommand(sender, args);
        } else if (label.equalsIgnoreCase("withdraw")) {
            return withdrawMoneyCommand(sender, args);
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



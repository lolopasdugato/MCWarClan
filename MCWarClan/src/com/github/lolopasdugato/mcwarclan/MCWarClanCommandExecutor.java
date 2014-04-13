package com.github.lolopasdugato.mcwarclan;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MCWarClanCommandExecutor implements CommandExecutor {
	
	private TeamContainer _tc;
	private Server _server;
	
	public MCWarClanCommandExecutor(TeamContainer tc, Server server) {
		_tc = tc;
		_server = server;
	}
	
	// Check if a player has been or is on the server.
	public boolean exist(String playerName){
        return _server.getOfflinePlayer(playerName).hasPlayedBefore() || _server.getOfflinePlayer(playerName).isOnline();
    }
	
	// Returns a player using a name.
	public OfflinePlayer findPlayerByName(String name){
		if(exist(name)){
			return _server.getOfflinePlayer(name);
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
					p.getPlayer().sendMessage("§a[MCWarClan]§6 " + "§6You have been kicked from team " + t.get_color().get_colorMark() + t.get_name() + " §6by " + sender.getName() + ". §6You are now a §7Barbarian !");
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
				sender.sendMessage("§a[MCWarClan]§6 " + "§6You cannot leave the §7Barbarian§6 team !");
				return true;
			}
			_tc.searchPlayerTeam(sender.getName()).deleteTeamMate(sender.getName());
			_tc.searchTeam("Barbarians").addTeamMate(sender.getName());
			sender.sendMessage("§a[MCWarClan]§6 " + "§6You have successfully left " + t.get_color().get_colorMark() + t.get_name() + ".§6 You are now a §7Barbarian !");
			return true;
		}
		else
			sender.sendMessage("§a[MCWarClan]§6 " + "You have to be a player to perform this command !");
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
                if(toJoin.get_name().equals(actual.get_name())){
                    sender.sendMessage("§a[MCWarClan]§6 You cannot join this team ! You're already in !");
                    return true;
                }
                else if(toJoin.get_name().equals("Barbarians")){
                    return leaveCommand(sender);
                }
                else{
                    if(canPay(toJoin.get_cost(), ((Player) sender).getPlayer())){
                        if(!actual.deleteTeamMate(sender.getName())){
                            sender.sendMessage("§a[MCWarClan]§6 High level ERROR, cannot switch you to " + toJoin.get_color().get_colorMark() + toJoin.get_name() + "§6 (deleteERROR). Cannot find your name into this team");
                            return true;
                        }
                        else if(!toJoin.addTeamMate(sender.getName())){
                            if(!actual.addTeamMate(sender.getName()))
                                sender.sendMessage("§a[MCWarClan]§6 High level ERROR, cannot switch you to this team (add2ERROR).");
                            sender.sendMessage("§a[MCWarClan]§6 too many member in " + toJoin.get_color().get_colorMark() + toJoin.get_name() + ".");
                            return true;
                        }
                        else if(!payTribute(toJoin.get_cost(), ((Player) sender).getPlayer())){
                            sender.sendMessage("§a[MCWarClan]§6 High level ERROR while paying the tribute.");
                            return true;
                        }
                        sender.sendMessage("§a[MCWarClan]§6 " + "§6Well done, you left " + actual.get_color().get_colorMark() + actual.get_name() + " §6and joined " + toJoin.get_color().get_colorMark() + toJoin.get_name() + ".");
                    }
                    else{
                        sender.sendMessage("§a[MCWarClan]§6 You do not have enough resources, here is the exhaustive list of materials needed:");
                        sender.sendMessage(toJoin.get_cost().getResourceTypes());
                    }
                }
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
        if(_tc.get_teamArray().size() >= _tc.get_maxTeams()){
            sender.sendMessage("§a[MCWarClan]§6 The maximum number of team is already reach !(" + _tc.get_maxTeams() + ")");
            return true;
        }
        if(sender instanceof Player){
            if(args.length == 2) {
                Team toJoin = new Team(new Color(args[1]), args[0], Settings.initialTeamSize, _tc);
                Team actual = _tc.searchPlayerTeam(sender.getName());
                if (canPay(_tc.get_creatingCost(), ((Player) sender).getPlayer())){     // If you can pay...
                    if(_tc.addTeam(toJoin)){      // If the team can be added
                        if(!actual.deleteTeamMate(sender.getName())){
                            sender.sendMessage("§a[MCWarClan]§6 High level ERROR, cannot switch you to this team (deleteERROR).");
                            return true;
                        }
                        else if(!toJoin.addTeamMate(sender.getName())){
                            if(!actual.addTeamMate(sender.getName()))
                                sender.sendMessage("§a[MCWarClan]§6 High level ERROR, cannot switch you to " + actual.get_color().get_colorMark() + actual.get_name() + " (add2ERROR).");
                            sender.sendMessage("§a[MCWarClan]§6 too many member in " + toJoin.get_color().get_colorMark() + toJoin.get_name() + ".");
                            return true;
                        }
                        else if(!payTribute(_tc.get_creatingCost(), ((Player) sender).getPlayer())){      // If the tribute paying works well
                            sender.sendMessage("§a[MCWarClan]§6 High level ERROR while paying the tribute.");
                            return true;
                        }
                        else{
                            sender.sendMessage("§a[MCWarClan]§6 " + new Color(args[1]).get_colorMark() + args[0] + " §6has been successfully created !");
                            return true;
                        }
                    }
                    else{
                        sender.sendMessage("§a[MCWarClan]§6 " + "§6Sorry, but name or color is already taken by another team. Here is the colorname list: ");
                        sender.sendMessage("§a[MCWarClan]§6 " + "§2GREEN, §eYELLOW, §0BLACK, §dMAGENTA, §5PURPRLE, §3CYAN, §bLIGHTBLUE");
                        return true;
                    }
                }
                else{
                    sender.sendMessage("§a[MCWarClan]§6 You do not have enough resources, here is the exhaustive list of materials needed:");
                    sender.sendMessage(_tc.get_creatingCost().getResourceTypes());
                    return true;
                }
            }
		}
        else{
            sender.sendMessage("§a[MCWarClan]§6 You have to be a player to perform this command !");
            return true;
        }
		return false;
	}
	
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

    // Verify if a player can pay the asked tribute
    public boolean canPay(Cost cost, Player player){
        for(int i = 0; i < cost.get_costEquivalence().size(); i++){
            // If the specified material is not recognize, just ignore it
            if(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if (!has(player, Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Pay a tribute using a specified cost for a specified player
    public boolean payTribute(Cost cost, Player player){
        for(int i = 0; i < cost.get_costEquivalence().size(); i++){
            // If the specified material is not recognize, just ignore it
            if(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if(!pay(player, Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue()))
                    return false;
            }
        }
        return true;
    }

    // Pay for a player a given number of a given material type
    public boolean pay(Player p, Material material, int valueToPay){
        ItemStack[] inventory = p.getInventory().getContents();
        while (valueToPay > 0){
            int j = p.getInventory().first(material);
            if(inventory[j].getAmount() > valueToPay){
                inventory[j].setAmount(inventory[j].getAmount() - valueToPay);
                return true;
            }
            else{
                valueToPay -= inventory[j].getAmount();
                p.getInventory().clear(j);
            }
        }
        return valueToPay == 0;
    }

    // Verify if the player has enough of the specified material
    public boolean has(Player p, Material material, int valueToHave){
        ItemStack[] inventory = p.getInventory().getContents();
        if(inventory.length == 0){
            return false;
        }
        int amount = 0;
        for(int i = 0; i < inventory.length; i++){
            if(inventory[i] != null && inventory[i].getType() == material){
                amount += inventory[i].getAmount();
            }
        }
        return amount >= valueToHave;
    }


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
                Team team = _tc.searchPlayerTeam(p.getName());


                //Find player's location
//                Player p = findPlayerByName(sender.getName()).getPlayer();

                if (p.isOnline()) {

                    //Check if the player's team is not 'Barbarians'
                    if (team.get_name() == "Barbarians") {
                        sender.sendMessage("You cannot create a base as a Barbarians.");
                        return true;
                    }
                    //Check if the player's team have enough resources to create the base
                    Cost cost = Settings.baseInitialCost;
                    if (!canPay(cost, p)) {
                        sender.sendMessage("You cannot create a base. You need :");
                        sender.sendMessage(team.get_cost().getResourceTypes());
                        return true;
                    }

                    //Here the player have enough resources to pay
                    //TODO Now we check if the location is far enough from other bases

                    Location loc = p.getTargetBlock(null, 10).getLocation();
                    final ArrayList<Team> teams = _tc.get_teamArray();
                    ArrayList<Base> bases;

                    boolean overlap = false;
                    int i = 0, j = 0;


                    while (i < teams.size() && !overlap) {
                        if (teams.get(i) != team) {
                            //
                            bases = teams.get(i).get_bases();
                            while (j < bases.size() && !overlap) {
                                if (bases.get(j).isInBase(loc))
                                    overlap = true;
                                j++;
                            }
                        }
                        j = 0;
                        i++;
                    }

                    //Todo add verification of the barbarian spawn
                    Location barbSpawn = Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation();
                    final double dist = barbSpawn.distance(p.getLocation());
                    if (dist < Settings.barbariansSpawnDistance) {
                        sender.sendMessage("You cannot create a base near the barbarian spawn.");
                        return true;
                    }


                    if (overlap) {
                        sender.sendMessage("You cannot create a base near another enemy base.");
                        return true;
                    }

                    //We have check the location, with is correct regarding other bases

                    //Now we try to create the flag
                    Base b = null;
                    try {
                        b = new Base(false, team, new MCWarClanLocation(loc));
                    } catch (Exception.NotEnoughSpaceException e) {
                        sender.sendMessage("There is not enough space to create the base.");
                        return true;
                    } catch (Exception.NotValidFlagLocationException e) {
                        sender.sendMessage("There is not a solid block under the flag.");
                        return true;
                    }

                    //If the flag can be created, add the base to the base array
                    team.get_bases().add(b);

                    //Substract the cost of the base to player's inventory
                    payTribute(cost, p);

                    sender.sendMessage("Base creation succeed");
                    return true;
                } else {
                    sender.sendMessage("Error : The player seems not to be online.");
                    return false;
                }
            }
        } else {
            sender.sendMessage("Error : You don't seems to execute the command as a player.");
            return false;
        }
    }
}



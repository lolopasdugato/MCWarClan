package com.github.lolopasdugato.mcwarclan.commands;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Loïc on 26/04/2014.
 */
public class TeamCommands implements CommandInterface {

    private TeamManager _teamManager;

    public TeamCommands(TeamManager t) {
        _teamManager = t;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player) {
                if (args[0].equalsIgnoreCase("join") && args.length == 2) {
                    return joinCommand(sender, args);
                } else if (args[0].equalsIgnoreCase("leave") && args.length == 1) {
                    return leaveCommand(sender);
                } else if (args[0].equalsIgnoreCase("list") && args.length < 3) {
                    return listCommand(sender, args);
                } else if (args[0].equalsIgnoreCase("create") && args.length > 1) {
                    return createTeamCommand(sender, args);
                } else if (args[0].equalsIgnoreCase("upgrade") && args.length < 3) {
                    Messages.sendMessage("Not yet implemented...", Messages.messageType.INGAME, sender);
                    return false;
                } else if (args[0].equalsIgnoreCase("info") && args.length == 1) {

                } else if (args[0].equalsIgnoreCase("?") && args.length == 1) {
                    return show_Team_ArgsPermittedCommand(sender);
                } else if (args[0].equalsIgnoreCase("help") && args.length == 1) {
                    // Sort all usage of all allowed commands and send it to sender.
                }
            } else {
                Messages.sendMessage("You should be a player to perform this command !", Messages.messageType.INGAME, sender);
            }
        }
        return false;
    }

    /**
     * Allows the sender to join the specified team.
     * @param sender the command sender.
     * @param args
     * @return
     */
    public boolean joinCommand(CommandSender sender, String[] args){
        if (sender.hasPermission("mcwarclan.team.join")) {
            if (args[1].equalsIgnoreCase("?")) {
                Messages.sendMessage("Permitted arguments: <team color name>, <team name>.", Messages.messageType.INGAME, sender);
            } else {
                MCWarClanPlayer player = _teamManager.getPlayer(sender.getName());
                Team toLeave = player.get_team();
                Team toJoin = _teamManager.getTeam(args[1]);
                if (toJoin == null) {
                    toJoin = _teamManager.getTeam(new Color(args[1]));
                }
                if (toJoin != null) {
                    if (toJoin.get_id() == toLeave.get_id()) {
                        Messages.sendMessage("You cannot join " + toJoin.getColoredName() + " team ! You're already in !", Messages.messageType.INGAME, sender);
                    } else if (toJoin.isBarbarian()) {
                        return leaveCommand(sender);
                    } else {
                        if (player.canPay(toJoin.get_cost()) && player.switchTo(toJoin)) {
                            player.payTribute(toJoin.get_cost());
                        }
                    }
                } else {
                    Messages.sendMessage("This team does not exist.", Messages.messageType.INGAME, sender);
                }
            }
        } else {
            Messages.sendMessage(CommandInterface.NO_PERMISSION_MESSAGE, Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * Allows the sender to leave it's current team an join the barbarian team.
     * @param sender
     * @return
     */
    public boolean leaveCommand(CommandSender sender){
        if (sender.hasPermission("mcwarclan.team.leave")) {
            MCWarClanPlayer player = _teamManager.getPlayer(sender.getName());
            Team toLeave = player.get_team();
            if (toLeave.isBarbarian()) {
                Messages.sendMessage("You cannot leave the " + toLeave.getColoredName() + " !", Messages.messageType.INGAME, sender);
            } else {
                Team Barbarians = _teamManager.getTeam(Team.BARBARIAN_TEAM_ID);
                player.switchTo(Barbarians);
            }
        } else {
            Messages.sendMessage(CommandInterface.NO_PERMISSION_MESSAGE, Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * List team mates of a specific player or all teams in the game.
     * @param sender
     * @param args
     * @return
     */
    public boolean listCommand(CommandSender sender, String[] args){
        if (sender.hasPermission("mcwarclan.team.list")) {
            if (args[1].equalsIgnoreCase("?")) {
                Messages.sendMessage("Permitted arguments: <team color name>, <team name>, <player name>, all.", Messages.messageType.INGAME, sender);
            } else if (args[1].equalsIgnoreCase("all")) {
                Messages.sendMessage(_teamManager.teamsList(), Messages.messageType.INGAME, sender);
            } else {
                MCWarClanPlayer player = _teamManager.getPlayer(args[1]);
                if (player != null) {
                    Messages.sendMessage(player.get_team().playerList(), Messages.messageType.INGAME, sender);
                } else {
                    Team toGet = _teamManager.getTeam(args[1]);
                    if (toGet != null) {
                        Messages.sendMessage(toGet.playerList(), Messages.messageType.INGAME, sender);
                    } else {
                        Messages.sendMessage(Messages.color(args[1]) + " does not exist !", Messages.messageType.INGAME, sender);
                    }
                }
            }
        } else {
            Messages.sendMessage(CommandInterface.NO_PERMISSION_MESSAGE, Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     *  Allows someone to create a team.
     * @param sender
     * @param args
     * @return
     */
    public boolean createTeamCommand(CommandSender sender, String[] args){
        if(_teamManager.isFull()){
            Messages.sendMessage("The maximum number of team is already reach !(" + Messages.color(_teamManager.get_maxTeams()) + ").", Messages.messageType.INGAME, sender);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("?")) {
            Messages.sendMessage("Permitted argument: <Team name>.", Messages.messageType.INGAME, sender);
        } else if (args.length == 3 && args[2].equalsIgnoreCase("?")) {
            Messages.sendMessage("Permitted argument: <Team color>.", Messages.messageType.INGAME, sender);
        } else if(args.length == 3) {
            Team toJoin = new Team(new Color(args[1]), args[0], Settings.initialTeamSize, _teamManager);
            MCWarClanPlayer player = _teamManager.getPlayer(sender.getName());
            if (player.createTeam(toJoin)) {
                player.switchTo(toJoin);
            } else {
                return false;
            }
        }
        else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * Show possibles arguments after /team depending on sender's permission.
     * @param sender
     * @return
     */
    public boolean show_Team_ArgsPermittedCommand(CommandSender sender) {
        String argPermitted = "Permitted arguments: ";
        if (sender.hasPermission("mcwarclan.team.join")) {
            argPermitted += "join ";
        }
        if (sender.hasPermission("mcwarclan.team.leave")) {
            argPermitted += "leave ";
        }
        if (sender.hasPermission("mcwarclan.team.list")) {
            argPermitted += "list ";
        }
        if (sender.hasPermission("mcwarclan.team.create")) {
            argPermitted += "create ";
        }
        if (sender.hasPermission("mcwarclan.team.upgrade")) {
            argPermitted += "upgrade ";
        }
        if (sender.hasPermission("mcwarclan.team.info")) {
            argPermitted += "info";
        }
        Messages.sendMessage(argPermitted, Messages.messageType.INGAME, sender);
        return true;
    }
}



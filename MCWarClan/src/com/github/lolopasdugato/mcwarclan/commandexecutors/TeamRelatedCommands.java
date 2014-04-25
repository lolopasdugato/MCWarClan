package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Loïc on 23/04/2014.
 */
public class TeamRelatedCommands extends MCWarClanCommandExecutor {

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public TeamRelatedCommands(TeamManager _tc) {
        super(_tc);
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
        Messages.sendMessage(_tc.teamsList(), Messages.messageType.INGAME, sender);
        return true;
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
                MCWarClanPlayer player = _tc.getPlayer(((Player) sender).getUniqueId());
                Messages.sendMessage(player.get_team().playerList(), Messages.messageType.INGAME, sender);
            }
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        else if (args.length == 1) {
            MCWarClanPlayer player = _tc.getPlayer(args[0]);
            if (player != null)
                Messages.sendMessage(player.get_team().playerList(), Messages.messageType.INGAME, sender);
            else
                Messages.sendMessage(Messages.color(args[0]) + " does not exist !", Messages.messageType.INGAME, sender);
        } else {
            return false;
        }
        return true;
    }



    /**
     * Allows the sender to leave it's current team an join the barbarian team.
     * @param sender
     * @return
     */
    public boolean leaveCommand(CommandSender sender, String[] args){
        if (args.length == 0) {
            if (sender instanceof Player) {
                MCWarClanPlayer player = _tc.getPlayer(sender.getName());
                Team toLeave = player.get_team();
                Team Barbarians = _tc.getTeam(Team.BARBARIAN_TEAM_ID);
                if (toLeave.isBarbarian()) {
                    Messages.sendMessage("You cannot leave the " + toLeave.getColoredName(), Messages.messageType.INGAME, sender);
                    return true;
                } else
                    player.switchTo(Barbarians);
            } else {
                Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     *  Allows the sender to join the specified team.
     * @param sender
     * @param args
     * @return
     */
    public boolean joinCommand(CommandSender sender, String[] args){
        if (args.length == 1) {
            if (sender instanceof Player) {
                MCWarClanPlayer player = _tc.getPlayer(sender.getName());
                Team toLeave = player.get_team();
                Team toJoin = _tc.getTeam(args[0]);
                if (toJoin == null) {
                    toJoin = _tc.getTeam(new Color(args[0]));
                }
                if (toJoin != null) {
                    if (toJoin.get_id() == toLeave.get_id()) {
                        Messages.sendMessage("You cannot join " + toJoin.getColoredName() + " team ! You're already in !", Messages.messageType.INGAME, sender);
                    } else if (toJoin.isBarbarian()) {
                        return leaveCommand(sender, args);
                    } else {
                        if (player.canPay(toJoin.get_cost()) && player.switchTo(toJoin)) {
                            player.payTribute(toJoin.get_cost());
                        }
                    }
                } else {
                    Messages.sendMessage("This team does not exist.", Messages.messageType.INGAME, sender);
                }
            } else {
                Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     *  Allows someone to create a team.
     * @param sender
     * @param args
     * @return
     */
    public boolean createteamCommand(CommandSender sender, String[] args){
        if(_tc.isFull()){
            Messages.sendMessage("The maximum number of team is already reach !(" + Messages.color(_tc.get_maxTeams()) + ").", Messages.messageType.INGAME, sender);
        } else if(sender instanceof Player){
            if(args.length == 2) {
                Team toJoin = new Team(new Color(args[1]), args[0], Settings.initialTeamSize, _tc);
                MCWarClanPlayer player = _tc.getPlayer(sender.getName());
                if (player.createTeam(toJoin)) {
                    player.switchTo(toJoin);
                }
            } else {
                return false;
            }
        }
        else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Override ----------------------------
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((label.toLowerCase().equals("showteams") || label.toLowerCase().equals("lt") || label.toLowerCase().equals("st")) && args.length == 0) {
            return showteamsCommand(sender);
        } else if (label.toLowerCase().equals("team")) {
            return teamCommand(sender, args);
        } else if (label.toLowerCase().equals("leave")) {
            return leaveCommand(sender, args);
        } else if (label.toLowerCase().equals("join")) {
            return joinCommand(sender, args);
        } else if (label.toLowerCase().equals("createteam")) {
            return createteamCommand(sender, args);
        }
        return false;
    }
}

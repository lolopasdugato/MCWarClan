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
        // sender.sendMessage("§8##########################################################################################################");
        Messages.sendMessage(_tc.teamsList(), Messages.messageType.INGAME, sender);
        // sender.sendMessage("§8##########################################################################################################");
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
     * Allows the sender to leave it's current team an join the barbarian team.
     * @param sender
     * @return
     */
    public boolean leaveCommand(CommandSender sender, String[] args){
        if (args.length == 0) {
            if (sender instanceof Player) {
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
        }
        return false;
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
                Team toJoin = _tc.searchTeam(args[0]);
                if (toJoin == null) {
                    toJoin = _tc.searchTeam(new Color(args[0]));
                }
                if (toJoin != null) {
                    if (toJoin.get_id() == toLeave.get_id()) {
                        Messages.sendMessage("You cannot join " + toJoin.get_color().get_colorMark() + toJoin.get_name() + " §6team ! You're already in !", Messages.messageType.INGAME, sender);
                        return true;
                    } else if (toJoin.get_id() == Team.BARBARIAN_TEAM_ID) {
                        return leaveCommand(sender, args);
                    } else {
                        if (player.canPay(toJoin.get_cost())) {
                            if (player.switchTo(toJoin)) {
                                Messages.sendMessage("Well done, you left " + toLeave.getColoredName() + " and joined " + toJoin.getColoredName() + ".",
                                        Messages.messageType.INGAME, sender);
                                toJoin.sendMessage("Well, here is some more fresh meat ! " + player.get_name() + " has joined the team !");
                                if (toLeave.get_id() != Team.BARBARIAN_TEAM_ID) {
                                    toLeave.sendMessage(player.get_name() + " left the team !");
                                }
                                if (!player.payTribute(toJoin.get_cost())) {
                                    Messages.sendMessage("Due to an unknown error, you cannot pay the tribute. Please tell this to an admin before doing anything.", Messages.messageType.INGAME, sender);
                                    Messages.sendMessage(player.get_name() + " cannot pay the tribute to enter " + toLeave.get_name() + ".", Messages.messageType.ALERT, null);
                                    return true;
                                }
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
        }
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

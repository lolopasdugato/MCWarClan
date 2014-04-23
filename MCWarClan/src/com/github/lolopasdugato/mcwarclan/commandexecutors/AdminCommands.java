package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Loïc on 23/04/2014.
 */
public class AdminCommands extends MCWarClanCommandExecutor {

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public AdminCommands(TeamManager _tc) {
        super(_tc);
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Functions ---------------------------
    //////////////////////////////////////////////////////////////////////////////

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
     *  Sort of admin command. Allows someone to kick someone else from a specific team.
     * @param sender
     * @param args
     * @return
     */
    public boolean unassignCommand(CommandSender sender, String[] args){
        if (args.length == 1) {
            OfflinePlayer p = findPlayerByName(args[0]);
            if (p != null) {
                MCWarClanPlayer player = _tc.getPlayer(args[0]);
                Team toLeave = player.get_team();
                if (player != null) {
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

                    if (p.isOnline()) {    // Send a message to the player concerned.
                        Messages.sendMessage("You have been kicked from team " + toLeave.getColoredName() + " by " + sender.getName() + ". You are now a §7Barbarian !",
                                Messages.messageType.INGAME, p.getPlayer());
                    }
                    return true;
                }
                Messages.sendMessage(args[0] + " does not exist in MCWarClan database !", Messages.messageType.INGAME, sender);
            }
            Messages.sendMessage(args[0] + " does not exist !", Messages.messageType.INGAME, sender);
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Override ----------------------------
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("assign")) {
            return assignCommand(sender, args);
        } else if (label.equalsIgnoreCase("unassign")) {
            return unassignCommand(sender, args);
        }
        return false;
    }
}

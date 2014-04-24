package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            Team toJoin = _tc.getTeam(args[1]);   // Search by name
            MCWarClanPlayer player = _tc.getPlayer(args[0]);
            if (toJoin == null)
                toJoin = _tc.getTeam(new Color(args[1])); // Search by colorName

            if (toJoin != null){
                if (player.switchTo(toJoin))
                    Messages.sendMessage(player.get_name() + " has successfully been added to " + toJoin.getColoredName(), Messages.messageType.INGAME, sender);
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
            OfflinePlayer p = getOfflinePlayer(args[0]);
            if (p != null) {
                MCWarClanPlayer player = _tc.getPlayer(args[0]);
                Team toLeave = player.get_team();
                if (player != null) {
                    if (toLeave.isBarbarian()) {
                        Messages.sendMessage("You cannot remove someone from the " + toLeave.getColoredName() + " team !", Messages.messageType.INGAME, sender);
                        return true;
                    }
                    player.kick();
                }
                Messages.sendMessage(Messages.color(args[0]) + " does not exist in MCWarClan database !", Messages.messageType.INGAME, sender);
            }
            else
                Messages.sendMessage(Messages.color(args[0]) + " does not exist !", Messages.messageType.INGAME, sender);
            return true;
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

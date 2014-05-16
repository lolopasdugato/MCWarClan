package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.Base;
import com.github.lolopasdugato.mcwarclan.MCWarClanPlayer;
import com.github.lolopasdugato.mcwarclan.Messages;
import com.github.lolopasdugato.mcwarclan.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Loïc on 23/04/2014.
 */
public class TeamBankRelatedCommands extends MCWarClanCommandExecutor {

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public TeamBankRelatedCommands(TeamManager _tc) {
        super(_tc);
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Functions ---------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Allows someone to upgrade a base into his team.
     * @param sender
     * @param args
     * @return
     */
    public boolean upgradeCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());
            Base toUpgrade;
            if (args.length == 0) {
                toUpgrade = mcPlayer.getCurrentBase();
            } else if (args.length == 1) {
                int id;
                try {
                    id = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    Messages.sendMessage("No base found for id " + Messages.color(args[0]) + ".", Messages.messageType.INGAME, player);
                    return false;
                }
                toUpgrade = mcPlayer.getAlliedBase(id);
            } else {
                return false;
            }
            mcPlayer.upgradeBase(toUpgrade);
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
                    Messages.sendMessage(Messages.color(args[0]) + " is not a valid amount of emeralds to store in the team treasure.", Messages.messageType.INGAME, player);
                    return false;
                }
                mcPlayer.save(amount);
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
                    Messages.sendMessage(Messages.color(args[0]) + " is not a valid amount of emeralds to store in the team treasure.", Messages.messageType.INGAME, player);
                    return false;
                }
                mcPlayer.withdrawMoney(amount);
            } else {
                return false;
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
    }

    /**
     * Show the treasure owned by your team
     *
     * @param sender
     * @param args
     * @return
     */
    public boolean treasureCommand(CommandSender sender, String[] args) {
        if (args.length != 0)
            return false;
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getUniqueId());

            mcPlayer.checkAccount();
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
        if (label.equalsIgnoreCase("upgrade")) {
            return upgradeCommand(sender, args);
        } else if (label.equalsIgnoreCase("savemoney")) {
            return saveEmeraldsCommand(sender, args);
        } else if (label.equalsIgnoreCase("withdraw")) {
            return withdrawMoneyCommand(sender, args);
        } else if (label.equalsIgnoreCase("treasure")) {
            return treasureCommand(sender, args);
        }
        return false;
    }
}

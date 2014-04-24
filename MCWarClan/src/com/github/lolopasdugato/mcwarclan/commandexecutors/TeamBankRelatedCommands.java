package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Loïc on 23/04/2014.
 */
public class TeamBankRelatedCommands extends  MCWarClanCommandExecutor {

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
            if (args.length == 0) {
                Base currentBase = mcPlayer.getCurrentBase();
                if (currentBase == null || currentBase.isEnemyToPlayer(mcPlayer)) {
                    Messages.sendMessage("You should be in the base you want to upgrade to do so !", Messages.messageType.INGAME, player);
                } else if (currentBase.isLevelMax()) {
                    Messages.sendMessage(Messages.color(currentBase.get_name()) + " has already reached the maximum level !", Messages.messageType.INGAME, player);
                } else if (!currentBase.upgrade()) {
                    Messages.sendMessage(Messages.color(currentBase.get_name()) + " cannot upgrade to level " + Messages.color(currentBase.get_level() + 1) + ". Not enough money !", Messages.messageType.INGAME, player);
                    Messages.sendMessage("Upgrading to level " + Messages.color(currentBase.get_level() + 1) + " cost " + Messages.color(Settings.radiusCost[currentBase.get_level() - 1]) + ".", Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Well done, " + Messages.color(currentBase.get_name()) + " has been upgraded to level " + Messages.color(currentBase.get_level()) + " by " + Messages.color(mcPlayer.get_name()) + " !",
                            Messages.messageType.INGAME, player);
                }
            } else if (args.length == 1) {
                int id;
                try {
                    id = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    Messages.sendMessage("No base found for id " + Messages.color(args[0]) + ".", Messages.messageType.INGAME, player);
                    return false;
                }
                Base baseAsked = mcPlayer.getAlliedBase(id);
                if (baseAsked == null) {
                    Messages.sendMessage("No base found for id " + Messages.color(args[0]) + ".", Messages.messageType.INGAME, player);
                } else if (baseAsked.isLevelMax()) {
                    Messages.sendMessage(Messages.color(baseAsked.get_name()) + " has already reached the maximum level !", Messages.messageType.INGAME, player);
                } else if (!baseAsked.upgrade()) {
                    Messages.sendMessage(Messages.color(baseAsked.get_name()) + " cannot upgrade to level " + Messages.color(baseAsked.get_level() + 1) + ". Not enough money !", Messages.messageType.INGAME, player);
                    Messages.sendMessage("Upgrading to level " + Messages.color(baseAsked.get_level() + 1) + " cost " + Messages.color(Settings.radiusCost[baseAsked.get_level() - 2]) + ".", Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Well done, " + Messages.color(baseAsked.get_name()) + " has been upgraded to level " + Messages.color(baseAsked.get_level()) + " by " + Messages.color(mcPlayer.get_name()) + " !",
                            Messages.messageType.INGAME, player);
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
                Base currentBase = mcPlayer.getCurrentBase();
                if (amount < 0) {
                    return false;
                } else if (currentBase == null || currentBase.isEnemyToPlayer(mcPlayer)) {
                    Messages.sendMessage("You have to be in one of your bases to withdraw money !", Messages.messageType.INGAME, player);
                } else if (amount <= mcPlayer.get_team().get_money()) {
                    Location locationToDrop = currentBase.get_loc().getLocation();
                    locationToDrop.add(2, 0, 0);
                    mcPlayer.get_team().dropEmeralds(amount, locationToDrop);
                    mcPlayer.sendMessageToMates(Messages.color(mcPlayer.get_name())+ " just take " + Messages.color(amount) + " emerald(s) from the team treasure at " + Messages.color(currentBase.get_name()) + " !");
                    Messages.sendMessage("Don't forget emerald(s) ! you will find them in front of " + Messages.color(currentBase.get_name()) + " flag.", Messages.messageType.INGAME, player);
                } else {
                    Messages.sendMessage("Your team does not have " + Messages.color(args[0]) + " emerald(s) !", Messages.messageType.INGAME, player);
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

            if (mcPlayer.isBarbarian())
                Messages.sendMessage(mcPlayer.getColoredTeamName() + " don't have any treasure. Their destiny is to be poor, forever.",
                        Messages.messageType.INGAME,
                        sender);
            else
                Messages.sendMessage("Your team have for the moment §a" + mcPlayer.get_team().get_money() + " emeralds§6 in your team " +
                                "treasure.",
                        Messages.messageType.INGAME,
                        sender
                );
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

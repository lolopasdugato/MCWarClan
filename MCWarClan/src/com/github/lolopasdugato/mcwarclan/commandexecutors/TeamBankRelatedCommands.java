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

            //get the player's team
            Team team = _tc.getPlayer(player.getUniqueId()).get_team();

            if (team.get_id() == Team.BARBARIAN_TEAM_ID)
                Messages.sendMessage("Barbarians don't have any treasure. Their destiny is to be poor, forever.",
                        Messages.messageType.INGAME,
                        sender);
            else
                Messages.sendMessage("Your team have for the moment §a" + team.get_money() + " emeralds§6 in your team " +
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

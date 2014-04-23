package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

/**
 * Created by Loïc on 23/04/2014.
 */
public class BaseRelatedCommands extends MCWarClanCommandExecutor {

    JavaPlugin _plugin;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public BaseRelatedCommands(TeamManager _tc, JavaPlugin plugin) {
        super(_tc);
        _plugin = plugin;
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- onCommand Functions ---------------------------
    //////////////////////////////////////////////////////////////////////////////

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
                BukkitTask tks = new MCWarClanRoutine.ContestedBaseRoutine(currentBase,
                        attackingTeam).runTaskTimer(_plugin,
                        0, 100);
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
        if (label.toLowerCase().equals("createhq")) {
            return createHQCommand(sender, args);
        } else if (label.equalsIgnoreCase("createbase")) {
            return createBaseCommand(sender, args);
        } else if (label.equalsIgnoreCase("baseinfo")) {
            return baseInfoCommand(sender,args);
        } else if (label.equalsIgnoreCase("contest")) {
            return contestCommand(sender, args);
        }
        return false;
    }
}

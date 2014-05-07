package com.github.lolopasdugato.mcwarclan.commandexecutors;

import com.github.lolopasdugato.mcwarclan.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
                player.createHQ(p.getTargetBlock(null, 10).getLocation(), args[0]);
            }
        } else {
            Messages.sendMessage("You have to be a player to perform this command !", Messages.messageType.INGAME, sender);
        }
        return true;
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
                    Messages.sendMessage(Messages.color(args[1]) + " is not a number.", Messages.messageType.INGAME, sender);
                    return false;
                }
                player.createBase(args[0], referenceID, args[2]);
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
                //Get the information about the current base
                mcPlayer.infoCurrentBase();
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    mcPlayer.infoAllBases();
                } else {
                    int id;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        Messages.sendMessage("No allied base found for id: " + Messages.color(args[0]) + ".", Messages.messageType.INGAME, player);
                        return false;
                    }

                    Base baseAsked = mcPlayer.getAlliedBase(id);
                    if (baseAsked != null) {
                        mcPlayer.infoBase(baseAsked);
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
            MCWarClanPlayer player = _tc.getPlayer(((Player) sender).getUniqueId());

            Base baseContest = player.canContestCurrentBase();
            if (baseContest != null) {

                baseContest.isContested(true);
                // Inform the two teams !
                Team attackedTeam = baseContest.get_team();
                Team attackingTeam = player.get_team();
                attackedTeam.sendMessage(baseContest.get_name() + " is attacked by " + attackingTeam.getColoredName() + " !");
                attackingTeam.sendMessage("Your team is attacking " + attackedTeam.getColoredName() + " !");

                //Create a new thread in order to check if the enemies are defeated
                BukkitTask tks = new MCWarClanRoutine.ContestedBaseRoutine(baseContest,
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

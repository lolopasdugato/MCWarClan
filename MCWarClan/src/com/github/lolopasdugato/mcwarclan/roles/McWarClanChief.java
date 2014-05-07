package com.github.lolopasdugato.mcwarclan.roles;

import com.github.lolopasdugato.mcwarclan.*;
import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidFlagLocationException;
import com.github.lolopasdugato.mcwarclan.customexceptions.NotEnoughSpaceException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;


public class McWarClanChief extends MCWarClanSubRole {

    private final Team _team;


    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public McWarClanChief(MCWarClanPlayer player, Team team) {
        super(player);
        _name = RoleType.CHIEF;
        _team = team;
        initNewRights();
        initSubRoles();
    }

    @Override
    protected void initNewRights() {
        _newRights.add("/createhq");
        _newRights.add("/createbase");
        _newRights.add("/treasure");

        _newRights.add("/upgrade");
        _newRights.add("/withdrawmoney");
    }


    @Override
    protected void initSubRoles() {
        _subroles.add(new McWarClanTeamMember(_player));
        _subroles.add(new MCWarClanTreasurer(_player));
    }

    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Functions ---------------------------------
    //////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean createBase(String name, int baseReferenceId, String direction) {

        Player player = _player.toOnlinePlayer();
        TeamManager teams = _team.get_teamManager();
        Base baseReference = _team.getBase(baseReferenceId);
        Location newBaseLocation;
        if (baseReference == null) {
            Messages.sendMessage("Bad base reference id. This ID does not match any of your base.", Messages.messageType.INGAME, player);
            return false;
        } else {
            newBaseLocation = new MCWarClanLocation(baseReference.get_loc()).getLocation();
        }
        if (!_player.canPay(_team.get_baseCreationCost()) && player.getGameMode() != GameMode.CREATIVE) {
            Messages.sendMessage("Sorry, you do not have enough materials to create the new base. Here is an exhaustive list of all materials required: ", Messages.messageType.INGAME, player);
            Messages.sendMessage(_team.get_baseCreationCost().getResourceTypes(), Messages.messageType.INGAME, player);
            return false;
        } else if (direction.equalsIgnoreCase("north")) {
            newBaseLocation.add(0, 0, (Settings.initialRadius + Settings.radiusHQBonus) * (-2) - 1);
            newBaseLocation.setY(newBaseLocation.getWorld().getHighestBlockYAt(newBaseLocation) - 1);
        } else if (direction.equalsIgnoreCase("south")) {
            newBaseLocation.add(0, 0, (Settings.initialRadius + Settings.radiusHQBonus) * (2) + 1);
            newBaseLocation.setY(newBaseLocation.getWorld().getHighestBlockYAt(newBaseLocation) - 1);
        } else if (direction.equalsIgnoreCase("east")) {
            newBaseLocation.add((Settings.initialRadius + Settings.radiusHQBonus) * (2) + 1, 0, 0);
            newBaseLocation.setY(newBaseLocation.getWorld().getHighestBlockYAt(newBaseLocation) - 1);
        } else if (direction.equalsIgnoreCase("west")) {
            newBaseLocation.add((Settings.initialRadius + Settings.radiusHQBonus) * (-2) - 1, 0, 0);
            newBaseLocation.setY(newBaseLocation.getWorld().getHighestBlockYAt(newBaseLocation) - 1);
        } else {
            Messages.sendMessage("The direction '" + direction + "' is not recognized.", Messages.messageType.INGAME, player);
            return false;
        }
        if (Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation().distance(newBaseLocation)
                < Settings.barbariansSpawnDistance + Settings.secureBarbarianDistance + Settings.radiusHQBonus + Settings.initialRadius) {
            Messages.sendMessage(name + " is too close from the barbarian spawn ! Cannot create it !", Messages.messageType.INGAME, player);
            return false;
        } else if (teams.isNearAnotherTerritory(false, newBaseLocation)) {
            Messages.sendMessage("You cannot create a base too close from another base. Try somewhere else !", Messages.messageType.INGAME, player);
            return false;
        }
        try {
            newBaseLocation.getBlock().getRelative(BlockFace.UP).breakNaturally();
            if (player.getGameMode() != GameMode.CREATIVE)
                _player.payTribute(_team.get_baseCreationCost());
            Base newBase = new Base(false, _team, name, new MCWarClanLocation(newBaseLocation));
            _team.get_bases().add(newBase);

            _team.increaseBaseCreationCost();
            teams.sendMessage("Well done " + _team.getColoredName() + ", " + _name + " just created " + name + " (id:§a" + newBase.get_id() + "§6) in the " + direction + " of " + baseReference.get_name() + " ! Its current protection radius is " + newBase.get_radius() + ".");
        } catch (InvalidFlagLocationException e) {
            e.sendDebugMessage();
            Messages.sendMessage("Cannot create the flag for the following reason: " + e.getMessage(), Messages.messageType.INGAME, player);
            return false;
        } catch (NotEnoughSpaceException e) {
            e.sendDebugMessage();
            Messages.sendMessage("Please try to create the flag somewhere else. " + e.getMessage(), Messages.messageType.INGAME, player);
            return false;
        }
        return true;
    }


    @Override
    public boolean createHQ(Location baseLocation, String baseName) {
        Player player = _player.toOnlinePlayer();
        TeamManager teams = _team.get_teamManager();
        Base newBase = null;
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            Messages.sendMessage("Sorry, but MCWarClan does not support other Environment than normal world. You cannot create you HeadQuarter there.", Messages.messageType.INGAME, player);
            return false;
        }
        if (_team.isBarbarian()) {
            Messages.sendMessage("You cannot create HeadQuarter as a §7barbarian§6 !", Messages.messageType.INGAME, player);
            return false;
        } else if (_team.get_bases().size() > 0) {
            Base HQ = _team.getHQ();
            Messages.sendMessage("You can only create a single HeadQuarter ! Yours is called " + HQ.get_name() + "(id:§a" + HQ.get_id() + "§6).", Messages.messageType.INGAME, player);
            return false;
        } else if (teams.isNearAnotherTerritory(true, baseLocation)) {
            Messages.sendMessage("You cannot create an HQ too close from another base. Try somewhere else !", Messages.messageType.INGAME, player);
            return false;
        } else if (Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation().distance(baseLocation)
                < Settings.barbariansSpawnDistance + Settings.secureBarbarianDistance + Settings.radiusHQBonus + Settings.initialRadius) {
            Messages.sendMessage("You cannot create a base too close from the barbarian spawn !", Messages.messageType.INGAME, player);
            return false;
        } else {
            try {
                newBase = new Base(true, _team, baseName, new MCWarClanLocation(baseLocation));
                _team.get_bases().add(newBase);
                for (int k = 0; k < _team.get_teamMembers().size(); k++) {
                    _team.get_teamMembers().get(k).reloadSpawn();
                }

                teams.sendMessage(_team.getColoredName() + " just created their first base ! So much time wasted...");
                _team.sendMessage(baseName + " is your first base. Its unique id is §a" + newBase.get_id() + "§6 be careful, to build the others, you will need to find some materials ! You can capture enemy bases as well...");
            } catch (InvalidFlagLocationException e) {
                e.sendDebugMessage();
                Messages.sendMessage("Cannot create the flag for the following reason: " + e.getMessage(), Messages.messageType.INGAME, player);
                return false;
            } catch (NotEnoughSpaceException e) {
                e.sendDebugMessage();
                Messages.sendMessage("Please try to create the flag somewhere else. " + e.getMessage(), Messages.messageType.INGAME, player);
                return false;
            }
        }
        return true;
    }

    public boolean upgradeBase(Base toUpgrade) {
        Player player = _player.toOnlinePlayer();
        boolean out = false;

        if (toUpgrade == null || toUpgrade.isEnemyToPlayer(_player)) {
            Messages.sendMessage("No base found ! Maybe you should precise a valid number or stay in one of your bases ?", Messages.messageType.INGAME, player);
        } else if (toUpgrade.isLevelMax()) {
            Messages.sendMessage(Messages.color(toUpgrade.get_name()) + " has already reached the maximum level !", Messages.messageType.INGAME, player);
        } else if (!toUpgrade.upgrade()) {
            Messages.sendMessage(Messages.color(toUpgrade.get_name()) + " cannot upgrade to level " + Messages.color(toUpgrade.get_level() + 1) + ". Not enough money !", Messages.messageType.INGAME, player);
            Messages.sendMessage("Upgrading to level " + Messages.color(toUpgrade.get_level() + 1) + " cost " + Messages.color(Settings.radiusCost[toUpgrade.get_level() - 1]) + " emerald(s).", Messages.messageType.INGAME, player);
        } else {
            Messages.sendMessage("Well done, " + Messages.color(toUpgrade.get_name()) + " has been upgraded to level " +
                            "" + Messages.color(toUpgrade.get_level()) + " by " + Messages.color(_player.get_name()) +
                            " !",
                    Messages.messageType.INGAME, player
            );
            out = true;
        }
        return out;
    }

    public boolean withdrawMoney(int amount) {
        Base currentBase = _player.getCurrentBase();
        boolean out = false;
        Player player = _player.toOnlinePlayer();

        if (amount < 0) {
            return false;
        } else if (currentBase == null || currentBase.isEnemyToPlayer(_player)) {
            Messages.sendMessage("You have to be in one of your bases to withdraw money !", Messages.messageType.INGAME, player);
        } else if (amount <= _player.get_team().get_money()) {
            Location locationToDrop = currentBase.get_loc().getLocation();
            locationToDrop.add(2, 0, 0);
            _player.get_team().dropEmeralds(amount, locationToDrop);
            _player.sendMessageToMates(Messages.color(_player.get_name()) + " just take " + Messages.color(amount) + " emerald(s) from the team treasure at " + Messages.color(currentBase.get_name()) + " !");
            Messages.sendMessage("Don't forget emerald(s) ! you will find them in front of " + Messages.color(currentBase.get_name()) + " flag.", Messages.messageType.INGAME, player);
            out = true;
        } else {
            Messages.sendMessage("Your team does not have " + Messages.color(amount) + " emerald(s) !",
                    Messages.messageType.INGAME, player);
        }
        return out;
    }
}

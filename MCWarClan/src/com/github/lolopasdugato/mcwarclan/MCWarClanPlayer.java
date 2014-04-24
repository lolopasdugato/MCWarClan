package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.customexceptions.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Loïc on 14/04/2014.
 */
public class MCWarClanPlayer implements Serializable {

    static private final long serialVersionUID = 9;

    private UUID _uuid;
    private String _name;
    private Team _team;
    private MCWarClanLocation _spawn;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public MCWarClanPlayer(Player player, Team team) {
        _uuid = player.getUniqueId();
        _name = player.getName();
        _team = team;
        reloadSpawn();
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public String get_name() {
        return _name;
    }

    public UUID get_uuid() {
        return _uuid;
    }

    public Team get_team() {
        return _team;
    }

    public void set_team(Team _team) {
        this._team = _team;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public MCWarClanLocation get_spawn() {
        return _spawn;
    }


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Convert this MCWarClanPlayer to an online player.
     *
     * @return an online player or null.
     */
    public Player toOnlinePlayer() {
        return Bukkit.getServer().getPlayer(_name);
    }

    /**
     * Convert this MCWarClanPlayer to an OfflinePlayer.
     *
     * @return an OfflinePlayer.
     */
    public OfflinePlayer toOfflinePlayer() {
        return Bukkit.getOfflinePlayer(_name);
    }

    public boolean teamKick() {
        _team = null;
        return true;
    }

    /**
     * Use to search a random barbarian spawn using config.yml.
     *
     * @param barbarianSpawnRadius
     * @return
     */
    private Location getBarbarianSpawn(int barbarianSpawnRadius) {
        if (barbarianSpawnRadius < 100) {
            Messages.sendMessage("Cannot have a barbarian spawn radius under 100 ! Setting spawn radius to 100...", Messages.messageType.ALERT, null);
            barbarianSpawnRadius = 100;
        }
        Location worldSpawn = Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation();
        int signX = 1;
        int signZ = 1;
        if (new Random().nextBoolean())
            signX = -1;
        if (new Random().nextBoolean())
            signZ = -1;
        double randomX = (new Random().nextInt(barbarianSpawnRadius)) * signX;
        double randomZ = (new Random().nextInt(barbarianSpawnRadius)) * signZ;
        return new Location(Bukkit.getWorld(Settings.classicWorldName), (worldSpawn.getX() + randomX), worldSpawn.getY(), (worldSpawn.getZ() + randomZ));
    }

    /**
     * looks if you can spawn in this location.
     *
     * @param loc the position to check.
     * @return true if you can spawn there.
     */
    private boolean spawnOK(Location loc) {
        return loc.getBlock().getType() == Material.AIR && loc.add(0, 1, 0).getBlock().getType() == Material.AIR;
    }

    /**
     * Up the location (adds y to Y)
     *
     * @param loc the location to change.
     * @return the new location.
     */
    private Location upLocation(Location loc, int y) {
        loc.setY(loc.getY() + y);
        return loc;
    }

    /**
     * Makes the player respawn.
     */
    public void spawn() {
        _spawn.getLocation().getChunk().load();
        toOnlinePlayer().teleport(_spawn.getLocation());
    }

    /**
     * reload the spawn for a player.
     */
    public void reloadSpawn() {

        if (_team.get_bases().size() != 0 && _team.getHQ().isContested()) {
            // Setting spawn to the border of the HQ.
            Base HQ = _team.getHQ();
            _spawn = new MCWarClanLocation(HQ.get_loc());
            int signX = 1;
            int signZ = 1;
            if (new Random().nextBoolean())
                signX = -1;
            if (new Random().nextBoolean())
                signZ = -1;
            double randomX = ((new Random().nextInt(21) + HQ.get_radius()) * signX + _spawn.get_x());
            double randomZ = ((new Random().nextInt(21) + HQ.get_radius()) * signZ + _spawn.get_z());
            _spawn.set_x(randomX);
            _spawn.set_z(randomZ);
            Messages.sendMessage(_name + " will spawn in x:" + _spawn.get_x() + ", y:" + _spawn.get_y() + ", z:" + _spawn.get_z() + " (ContestedHQ).",
                    Messages.messageType.DEBUG, null);
        } else if (_team.get_bases().size() != 0) {
            _spawn = new MCWarClanLocation(_team.getHQ().get_loc());
            _spawn.set_x(_spawn.get_x() + 2);
            Messages.sendMessage(_name + " will spawn in x:" + _spawn.get_x() + ", y:" + _spawn.get_y() + ", z:" + _spawn.get_z() + " (NormalHQState).",
                    Messages.messageType.DEBUG, null);
        } else {                                  // Otherwise, it should be a barbarian or the player is handled like a barbarian because the team has no HQ.
            // define spawn as a barbarian spawn
            Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
            while (!spawnOK(barbarianSpawn)) {
                barbarianSpawn = upLocation(barbarianSpawn, 1);
            }
            _spawn = new MCWarClanLocation(barbarianSpawn);
            Messages.sendMessage(_name + " will spawn in x:" + _spawn.get_x() + ", y:" + _spawn.get_y() + ", z:" + _spawn.get_z() + " (Barbarian).",
                    Messages.messageType.DEBUG, null);
        }
    }

    /**
     * Verify if a player can pay the asked tribute.
     *
     * @param cost
     * @return
     */
    public boolean canPay(Cost cost) {
        for (int i = 0; i < cost.get_costEquivalence().size(); i++) {
            // If the specified material is not recognize, just ignore it
            if (Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if (!has(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verify if the player has enough of the specified material.
     *
     * @param material
     * @param valueToHave
     * @return
     */
    public boolean has(Material material, int valueToHave) {
        Player player = this.toOnlinePlayer();
        if (player != null) {
            ItemStack[] inventory = player.getInventory().getContents();
            if (inventory.length == 0) {
                return false;
            }
            int amount = 0;
            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] != null && inventory[i].getType() == material) {
                    amount += inventory[i].getAmount();
                }
            }
            return amount >= valueToHave;
        } else {
            Messages.sendMessage(_name + " does not exist or is not online !", Messages.messageType.ALERT, null);
        }
        return false;
    }

    /**
     * Pay a tribute using a specified cost for a specified player.
     *
     * @param cost
     * @return
     */
    public boolean payTribute(Cost cost) {
        for (int i = 0; i < cost.get_costEquivalence().size(); i++) {
            // If the specified material is not recognize, just ignore it
            if (Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if (!pay(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue()))
                    return false;
            }
        }
        return true;
    }

    /**
     * Pay for a player a given number of a given material type.
     *
     * @param material
     * @param valueToPay
     * @return
     */
    public boolean pay(Material material, int valueToPay) {
        Player player = this.toOnlinePlayer();
        if (player == null) {
            Messages.sendMessage(_name + " does not exist or is not online !", Messages.messageType.ALERT, null);
            return false;
        }
        ItemStack[] inventory = player.getInventory().getContents();
        while (valueToPay > 0) {
            int j = player.getInventory().first(material);
            if (inventory[j].getAmount() > valueToPay) {
                inventory[j].setAmount(inventory[j].getAmount() - valueToPay);
                return true;
            } else {
                valueToPay -= inventory[j].getAmount();
                player.getInventory().clear(j);
            }
        }
        return valueToPay == 0;
    }


    /**
     * @return Returns the base area where the player is if so, return null if no results matches.
     */
    public Base getCurrentBase() {
        ArrayList<Team> teams = _team.get_teamManager().get_teamArray();
        Base b;
        Player p = toOnlinePlayer();

        //To be sure if it's an online player
        if (p == null) {
            Messages.sendMessage("Error : player not online. toOnlinePlayer return null value.",
                    Messages.messageType.DEBUG, null);
            return null;
        } else if (p.isDead()) {
            Messages.sendMessage("A player is not considered as being in a base if he is dead !", Messages.messageType.DEBUG, null);
            return null;
        }

        for (int i = 0; i < teams.size(); i++) {
            b = teams.get(i).getBase(p.getLocation());
            if (b != null) {
                return b;
            }
        }
        return null;
    }

    /**
     * Check if this player can contest a base.
     *
     * @return true if he can.
     */
    public boolean canContest() {
        return _team.get_bases().size() != 0 && _team.get_id() != Team.BARBARIAN_TEAM_ID;
    }

    /**
     * Switch a player from a team to a team.
     *
     * @param teamToSwitchTo
     * @return
     */
    public boolean switchTo(Team teamToSwitchTo) {
        try {
            _team.deleteTeamMate(this);
            teamToSwitchTo.addTeamMate(this);
        } catch (MaximumTeamCapacityReachedException e) {
            e.sendDebugMessage();
            Player player = toOnlinePlayer();
            if (player != null)
                Messages.sendMessage("Too many members in " + teamToSwitchTo.getColoredName() + " cannot switch you to this team !", Messages.messageType.INGAME, toOnlinePlayer());
            return false;
        }
        return true;
    }

    /**
     * Kick a player from it's current team to the barbarian team.
     *
     * @return
     */
    public boolean kick() {
        Team Barbarians = _team.get_teamManager().getTeam(Team.BARBARIAN_TEAM_ID);
        return switchTo(Barbarians);
    }

    /**
     * Create a team for a specified player.
     *
     * @param t
     * @return
     */
    public boolean createTeam(Team t) {
        Player player = toOnlinePlayer();
        try {
            TeamManager teamManager = _team.get_teamManager();
            teamManager.checkTeamValidity(t);
            if (!canPay(teamManager.get_creatingCost())) {
                Messages.sendMessage("You need more resources to create this team. Here is an exhaustive list of all materials required: ", Messages.messageType.INGAME, player);
                Messages.sendMessage(teamManager.get_creatingCost().getResourceTypes(), Messages.messageType.INGAME, player);
                return false;
            }
            payTribute(teamManager.get_creatingCost());
            if (!teamManager.addTeam(t)) {
                Messages.sendMessage("Cannot add the team for unknown reason...", Messages.messageType.DEBUG, null);
                Messages.sendMessage("Cannot add the team for unknown reason...", Messages.messageType.INGAME, player);
                return false;
            }
        } catch (InvalidColorException e) {
            e.sendDebugMessage();
            Messages.sendMessage("Sorry, but name or color is already taken by another team. Here is the colorname list: ", Messages.messageType.INGAME, player);
            Messages.sendMessage("§2GREEN, §eYELLOW, §0BLACK, §dMAGENTA, §5PURPRLE, §3CYAN, §bLIGHTBLUE", Messages.messageType.INGAME, player);
            return false;
        } catch (InvalidNameException e) {
            e.sendDebugMessage();
            Messages.sendMessage("Sorry, this name is already taken !", Messages.messageType.INGAME, player);
            return false;
        } catch (MaximumNumberOfTeamReachedException e) {
            Messages.sendMessage("Sorry, maximum number of team reached !", Messages.messageType.INGAME, player);
            return false;
        }
        return true;
    }

    /**
     * Create a base which is an HQ.
     *
     * @param baseLocation
     * @return
     */
    public boolean createHQ(Location baseLocation, String baseName) {
        Player player = toOnlinePlayer();
        TeamManager teams = _team.get_teamManager();
        Base newBase = null;
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            Messages.sendMessage("Sorry, but MCWarClan does not support other Environment than normal world. You cannot create you HeadQuarter there.", Messages.messageType.INGAME, player);
            return false;
        }
        if (_team.get_id() == Team.BARBARIAN_TEAM_ID) {
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

    /**
     * Create a base at a specified position using a base for reference.
     *
     * @param name
     * @param baseReferenceId
     * @param direction
     * @return
     */
    public boolean createBase(String name, int baseReferenceId, String direction) {
        Player player = toOnlinePlayer();
        TeamManager teams = _team.get_teamManager();
        Base baseReference = _team.getBase(baseReferenceId);
        Location newBaseLocation;
        if (baseReference == null) {
            Messages.sendMessage("Bad base reference id. This ID does not match any of your base.", Messages.messageType.INGAME, player);
            return false;
        } else {
            newBaseLocation = new MCWarClanLocation(baseReference.get_loc()).getLocation();
        }
        if (!canPay(_team.get_baseCreationCost()) && player.getGameMode() != GameMode.CREATIVE) {
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
                payTribute(_team.get_baseCreationCost());
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

    /**
     * Save Emeralds in the team treasure.
     *
     * @param amount
     * @return
     */
    public boolean save(int amount) {
        Player player = toOnlinePlayer();
        int amountToSave = amount;
        PlayerInventory playerInventory = player.getInventory();
        if (playerInventory.contains(Material.EMERALD, amount)) {
            do {
                int index = playerInventory.first(Material.EMERALD);
                ItemStack itemStack = playerInventory.getItem(index);
                if (itemStack.getAmount() > amount) {
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    playerInventory.setItem(index, itemStack);
                    amount = 0;
                } else {
                    amount -= itemStack.getAmount();
                    itemStack.setAmount(0);
                    playerInventory.setItem(index, itemStack);
                }
            } while (amount != 0);
            _team.set_money(_team.get_money() + amountToSave);
            player.updateInventory();
        } else {
            return false;
        }
        return true;
    }
}

package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.customexceptions.MaximumTeamCapacityReachedException;
import com.github.lolopasdugato.mcwarclan.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    private McWarClanRole _role;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public MCWarClanPlayer(Player player, Team team, McWarClanRole.RoleType role) {
        _uuid = player.getUniqueId();
        _name = player.getName();
        _team = team;
        switch (role) {
            case CHIEF:
                _role = new McWarClanChief(this, _team);
                break;
            case TREASURER:
                _role = new MCWarClanTreasurer(this);
                break;
            case TEAM_MEMBER:
                _role = new McWarClanTeamMember(this);
                break;
            case BARBARIAN:
                System.out.println("coucou");
                _role = new MCWarClanBarbarian(this);
                break;
        }
        reloadSpawn();
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public String get_name() { return _name; }
    public UUID get_uuid() { return _uuid; }
    public Team get_team() { return _team; }

    public void set_team(Team _team) {
        this._team = _team;
    }

    public MCWarClanLocation get_spawn() {
        return _spawn;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public McWarClanRole get_role() {
        return _role;
    }

    public void set_role(McWarClanRole role) {
        _role = role;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Convert this MCWarClanPlayer to an online player.
     * @return an online player or null.
     */
    public Player toOnlinePlayer(){
        return Bukkit.getServer().getPlayer(_name);
    }

    /**
     * Check if this player is online or not.
     * @return
     */
    public boolean isOnline() {
        return toOnlinePlayer() != null;
    }

    /**
     *  Convert this MCWarClanPlayer to an OfflinePlayer.
     * @return an OfflinePlayer.
     */
    public OfflinePlayer toOfflinePlayer(){
        return Bukkit.getOfflinePlayer(_name);
    }

    public boolean teamKick(){
        _team = null;
        return true;
    }

    /**
     *  Use to search a random barbarian spawn using config.yml.
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
     *  looks if you can spawn in this location.
     * @param loc the position to check.
     * @return true if you can spawn there.
     */
    private boolean spawnOK(Location loc) {
        return loc.getBlock().getType() == Material.AIR && loc.add(0,1,0).getBlock().getType() == Material.AIR;
    }

    /**
     *  Up the location (adds y to Y)
     * @param loc the location to change.
     * @return the new location.
     */
    private Location upLocation(Location loc, int y) {
        loc.setY(loc.getY() + y);
        return loc;
    }

    /**
     *  Makes the player respawn.
     */
    public void spawn(){
        _spawn.getLocation().getChunk().load();
        toOnlinePlayer().teleport(_spawn.getLocation());
    }

    /**
     *  reload the spawn for a player.
     */
    public void reloadSpawn(){

        if (_team.get_bases().size() != 0 && _team.getHQ().isContested()){
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
            Messages.sendMessage( _name + " will spawn in x:" + _spawn.get_x() + ", y:" + _spawn.get_y() + ", z:" + _spawn.get_z() + " (ContestedHQ).",
                    Messages.messageType.DEBUG, null);
        }
        else if(_team.get_bases().size() != 0){
            _spawn = new MCWarClanLocation(_team.getHQ().get_loc());
            _spawn.set_x(_spawn.get_x() + 2);
            Messages.sendMessage( _name + " will spawn in x:" + _spawn.get_x() + ", y:" + _spawn.get_y() + ", z:" + _spawn.get_z() + " (NormalHQState).",
                    Messages.messageType.DEBUG, null);
        }
        else {                                  // Otherwise, it should be a barbarian or the player is handled like a barbarian because the team has no HQ.
            // define spawn as a barbarian spawn
            Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
            while(!spawnOK(barbarianSpawn)){
                barbarianSpawn = upLocation(barbarianSpawn, 1);
            }
            _spawn = new MCWarClanLocation(barbarianSpawn);
            Messages.sendMessage( _name + " will spawn in x:" + _spawn.get_x() + ", y:" + _spawn.get_y() + ", z:" + _spawn.get_z() + " (Barbarian).",
                    Messages.messageType.DEBUG, null);
        }
    }

    /**
     *  Verify if a player can pay the asked tribute.
     * @param cost
     * @return
     */
    public boolean canPay(Cost cost){
        for(int i = 0; i < cost.get_costEquivalence().size(); i++){
            // If the specified material is not recognize, just ignore it
            if(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if (!has(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue())) {
                    Player player = toOnlinePlayer();
                    Messages.sendMessage("You do not have enough resources, here is the exhaustive list of materials needed: ", Messages.messageType.INGAME, player);
                    Messages.sendMessage(cost.getResourceTypes(), Messages.messageType.INGAME, player);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *  Verify if the player has enough of the specified material.
     * @param material
     * @param valueToHave
     * @return
     */
    public boolean has(Material material, int valueToHave){
        Player player = this.toOnlinePlayer();
        if(player != null){
            ItemStack[] inventory = player.getInventory().getContents();
            if(inventory.length == 0){
                return false;
            }
            int amount = 0;
            for(int i = 0; i < inventory.length; i++){
                if(inventory[i] != null && inventory[i].getType() == material){
                    amount += inventory[i].getAmount();
                }
            }
            return amount >= valueToHave;
        }
        else{
            Messages.sendMessage(_name + " does not exist or is not online !", Messages.messageType.ALERT, null);
        }
        return false;
    }

    /**
     *  Pay a tribute using a specified cost for a specified player.
     * @param cost
     * @return
     */
    public boolean payTribute(Cost cost){
        for(int i = 0; i < cost.get_costEquivalence().size(); i++){
            // If the specified material is not recognize, just ignore it
            if(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if(!pay(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue()))
                    return false;
            }
        }
        return true;
    }

    /**
     *  Pay for a player a given number of a given material type.
     * @param material
     * @param valueToPay
     * @return
     */
    public boolean pay(Material material, int valueToPay){
        Player player = this.toOnlinePlayer();
        if(player == null){
            Messages.sendMessage(_name + " does not exist or is not online !", Messages.messageType.ALERT, null);
            return false;
        }
        ItemStack[] inventory = player.getInventory().getContents();
        while (valueToPay > 0){
            int j = player.getInventory().first(material);
            if(inventory[j].getAmount() > valueToPay){
                inventory[j].setAmount(inventory[j].getAmount() - valueToPay);
                return true;
            }
            else{
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
        }
        else if (p.isDead()){
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
     * Switch a player from a team to a team.
     * @param teamToSwitchTo
     * @return
     */
    public boolean switchTo(Team teamToSwitchTo){
        String prevTeam = _team.get_name();
        try{
            _team.deleteTeamMate(this);
            teamToSwitchTo.addTeamMate(this);
        } catch (MaximumTeamCapacityReachedException e) {
            e.sendDebugMessage();
            Player player = toOnlinePlayer();
            if (player != null)
                Messages.sendMessage("Too many members in " + teamToSwitchTo.getColoredName() + " cannot switch you to this team !", Messages.messageType.INGAME, toOnlinePlayer());
            return false;
        }
        Messages.sendMessage("You have been successfully moved from " + Messages.color(prevTeam) + " to "
                + Messages.color(_team.get_name()), Messages.messageType.INGAME, toOnlinePlayer());
        return true;
    }

    /**
     * Kick a player from it's current team to the barbarian team.
     * @return
     */
    public boolean kick(){
        Team Barbarians = _team.get_teamManager().getTeam(Team.BARBARIAN_TEAM_ID);
        Team currentTeam = _team;
        if (switchTo(Barbarians)) {
            Messages.sendMessage("You have been kicked from " + currentTeam.getColoredName() + ".", Messages.messageType.INGAME, toOnlinePlayer());
            return true;
        } else {
            Messages.sendMessage("Cannot switch you to the " + Barbarians.getColoredName() + " team !", Messages.messageType.INGAME, toOnlinePlayer());
            return false;
        }
    }


    //////////////////////////////////////////////////////////////////////////////
    //---------------------------- Bridges to roles ------------------------------
    //////////////////////////////////////////////////////////////////////////////


    /**
     * Create a team for a specified player.
     * @param t The team we wanted to create
     * @return True if the creation succeed, otherwise false.
     */
    public boolean createTeam(Team t) {
        return _role.createTeam(t);
    }

    /**
     * Create a base which is an HQ.
     * @param baseLocation
     * @return True if succeed, otherwise false
     */
    public boolean createHQ(Location baseLocation, String baseName) {
        return _role.createHQ(baseLocation, baseName);
    }

    /**
     * Create a base at a specified position using a base for reference.
     * @param name
     * @param baseReferenceId
     * @param direction
     * @return True if succeed, otherwise false
     */
    public boolean createBase(String name, int baseReferenceId, String direction) {
        return _role.createBase(name, baseReferenceId, direction);
    }

    /**
     * Save Emeralds in the team treasure.
     * @param amount the amount of emeralds the player have in his inventory
     * @return True if succeed, otherwise false
     */
    public boolean save(int amount) {
        return _role.saveMoney(amount);
    }


    public boolean infoAllBases() {
        return _role.infoAllBases();
    }

    public void infoBase(Base base) {
        _role.infoBase(base);
    }

    public boolean infoCurrentBase() {
        return _role.infoCurrentBase();
    }


    public boolean infoBaseMini(Base base) {
        return _role.infoBaseMini(base);
    }


    /**
     * Check if this player can contest a base.
     * @return The base if possible, otherwise return null
     */
    public Base canContestCurrentBase() {
        return _role.canContestCurrentBase();
    }

    /**
     * Check the amount of money (emeralds) store in the team treasure
     *
     * @return the number of emeralds
     */
    public int checkAccount() {
        return _role.checkAccount();
    }

    public boolean upgradeBase(Base toUpgrade) {
        return _role.upgradeBase(toUpgrade);
    }

    public boolean withdrawMoney(int amount) {
        return _role.withdrawMoney(amount);
    }


    //////////////////////////////////////////////////////////////////////////////
    //----------------------------- Other functions ------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Return the colored name of the player's team.
     * @return
     */
    public String getColoredTeamName() {
        return _team.getColoredName();
    }

    /**
     * Check if the player is an enemy to team t
     * @param t
     * @return
     */
    public boolean isEnemyToTeam(Team t) {
        return _team.isEnemyToTeam(t);
    }

    /**
     * Check if the player is a barbarian.
     * @return
     */
    public boolean isBarbarian() {
        return _team.isBarbarian();
    }

    /**
     * Check if the player has a team that has bases.
     * @return
     */
    public boolean hasBases() {
        return _team.hasBases();
    }

    /**
     * Get an allied base using a specific id.
     * @param id
     * @return
     */
    public Base getAlliedBase(int id) {
        return _team.getBase(id);
    }

    /**
     * Send a message to player's mates.
     * @param message
     */
    public void sendMessageToMates(String message) {
        _team.sendMessage(message);
    }
}

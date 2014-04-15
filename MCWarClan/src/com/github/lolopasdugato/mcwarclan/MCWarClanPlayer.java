package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Loïc on 14/04/2014.
 */
public class MCWarClanPlayer implements Serializable {
    private UUID _uuid;
    private String _name;
    private Team _team;
    private MCWarClanLocation _spawn;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public MCWarClanPlayer(Player player, Team team){
        _uuid = player.getUniqueId();
        _name = player.getName();
        _team = team;
        reloadSpawn();
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public String get_name() { return _name; }
    public UUID get_uuid() { return _uuid; }
    public Team get_team() { return _team; }
    public MCWarClanLocation get_spawn() { return _spawn; }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public void set_team(Team _team) { this._team = _team; }


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @brief Convert this MCWarClanPlayer to an online player.
     * @return an online player or null.
     */
    public Player toOnlinePlayer(){
        return Bukkit.getServer().getPlayer(_name);
    }

    /**
     * @brief Convert this MCWarClanPlayer to an OfflinePlayer.
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
     * @brief Use to search a random barbarian spawn using config.yml.
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
     * @brief looks if you can spawn in this location.
     * @param loc the position to check.
     * @return true if you can spawn there.
     */
    private boolean spawnOK(Location loc) {
        return loc.getBlock().getType() == Material.AIR && loc.add(0,1,0).getBlock().getType() == Material.AIR;
    }

    /**
     * @brief Up the location (adds y to Y)
     * @param loc the location to change.
     * @return the new location.
     */
    private Location upLocation(Location loc, int y) {
        loc.setY(loc.getY() + y);
        return loc;
    }

    /**
     * @brief Makes the player respawn.
     */
    public void spawn(){
        _spawn.getLocation().getChunk().load();
        toOnlinePlayer().teleport(_spawn.getLocation());
    }

    /**
     * @brief reload a new spawn for a barbarian.
     */
    public void reloadSpawn(){
        if(_team.get_id() == Team.BARBARIAN_TEAM_ID) {
            // define spawn as a barbarian spawn
            Location barbarianSpawn = getBarbarianSpawn(Settings.barbariansSpawnDistance);
            while(!spawnOK(barbarianSpawn)){
                barbarianSpawn = upLocation(barbarianSpawn, 1);
            }
            _spawn = new MCWarClanLocation(barbarianSpawn);
        }
        else if(_team.get_bases().size() != 0){
            _spawn = _team.getHQ().get_loc();
            _spawn.set_x(_spawn.get_x() + 2);
        }
        else{
            _spawn = new MCWarClanLocation(Bukkit.getWorld(Settings.classicWorldName).getSpawnLocation());
            Messages.sendMessage("No HQ found for " + _team.get_name() + ". Using " + Settings.classicWorldName + "'s spawn for " + _name + ".", Messages.messageType.ALERT, null);
        }
    }

    /**
     * @brief Verify if a player can pay the asked tribute.
     * @param cost
     * @return
     */
    public boolean canPay(Cost cost){
        for(int i = 0; i < cost.get_costEquivalence().size(); i++){
            // If the specified material is not recognize, just ignore it
            if(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()) != null) {
                if (!has(Material.getMaterial(cost.get_costEquivalence().get(i).get_materialName()), cost.get_costEquivalence().get(i).get_materialValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @brief Verify if the player has enough of the specified material.
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
     * @brief Pay a tribute using a specified cost for a specified player.
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
     * @brief Pay for a player a given number of a given material type.
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
     * @brief Verify if someone of the team t is in an enemy territory.
     * @param loc the location where we want to check.
     * @return Returns true if the location of the guy is considered as an enemy territory.
     */
    public boolean isInEnemyTerritory(Location loc){
        TeamContainer tc = _team.get_teamContainer();
        for(int i = 0; i < tc.get_teamArray().size(); i++){
            if(tc.get_teamArray().get(i).isEnemyToTeam(_team) && tc.get_teamArray().get(i).isInTerritory(loc)){
                return true;
            }
        }
        return false;
    }
}

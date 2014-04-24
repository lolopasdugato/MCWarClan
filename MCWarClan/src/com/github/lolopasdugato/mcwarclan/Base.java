package com.github.lolopasdugato.mcwarclan;


import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidFlagLocationException;
import com.github.lolopasdugato.mcwarclan.customexceptions.NotEnoughSpaceException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

import java.io.Serializable;
import java.util.ArrayList;

public class Base implements Serializable {
	
	static private final long serialVersionUID = 5;
    private static int _idMaster = 0;
    private boolean _HQ;            // Determine if this is an HQ or not
    private int _initialRadius;     // First radius of protection for a normal base
    private int _radius; 			// Determine the radius protection effect of this base.
    private int _bonusRadius;       // Determine the radius bonus for the HQ.
	private Team _team;				// Team which this object is attached to
	private Flag _flag;				// The flag attached to this base
	private MCWarClanLocation _loc;	// Represent the location of a base
//    private Cost _cost;             // The cost to create a new base
    private String _name;
    private int _id;
    private boolean _contested;
    private int _level;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Classic Base constructor.
     * @param HQ
     * @param team
     * @param loc
     * @throws NotEnoughSpaceException
     * @throws InvalidFlagLocationException
     */
    public Base(boolean HQ, Team team, String name, MCWarClanLocation loc) throws NotEnoughSpaceException, InvalidFlagLocationException {
        _idMaster++;
        _HQ = HQ;
        _team = team;
        _loc = loc;
        _initialRadius = Settings.initialRadius;    // WARNING: shouldn't be reloaded ! (except if it doesn't change during the game)
        _bonusRadius = Settings.radiusHQBonus;      // WARNING: shouldn't be reloaded ! (except if it doesn't change during the game)
//        _cost = Settings.baseInitialCost;   // WARNING shouldn't be reload !
        _name = name;
        _id = _idMaster;
        if (_HQ) {
            _radius = _initialRadius + _bonusRadius;
            _level = 5;
        }
        else {
            _radius = _initialRadius;
            _level = 1;
        }
        //Test if the flag can be created, and throw NotEnoughSpaceException is not.
        _flag = new Flag(this);

        _contested = false;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////


    public String get_name() { return _name; }

    public int get_initialRadius() {
        return _initialRadius;
    }

    public boolean is_HQ() {
        return _HQ;
    }

    public void set_HQ(boolean _HQ) {
        this._HQ = _HQ;
    }

    public Team get_team() {
        return _team;
    }

    public void set_team(Team _team) {
        this._team = _team;
    }

    public MCWarClanLocation get_loc() {
        return _loc;
    }

    public void set_loc(MCWarClanLocation _loc) {
        this._loc = _loc;
    }

    public int get_radius() {
        return _radius;
    }

    public int get_id() { return _id; }

    public int get_level() {
        return _level;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public void set_radius(int _radius) {
        this._radius = _radius;
    }

    public int get_bonusRadius() {
        return _bonusRadius;
    }

    public void set_bonusRadius(int _bonusRadius) {
        this._bonusRadius = _bonusRadius;
    }

    public Flag get_flag() {
        return _flag;
    }

    public void set_flag(Flag _flag) {
        this._flag = _flag;
    }

    public boolean isContested() {
        return _contested;
    }

    public void isContested(boolean contested) {
        _contested = contested;
    }

    public void set_level(int _level) {
        this._level = _level;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        if(_idMaster < _id)
            _idMaster = _id;
        _contested = false;
    }

    /**
     * Check if the location loc is near this base
     * @param includeSafeZone if we include the safe zone, we will include the minimum distance between 2 HQ in the math.
     * @param loc
     * @return true or false.
     */
    public boolean isNearBase(boolean includeSafeZone, Location loc){
        int tmpRadius = (_initialRadius + _bonusRadius) * 2;    // Maximum radius * 2
        if (includeSafeZone)
            tmpRadius += Settings.baseMinHQDistanceToOthers;
        boolean isInXAxe = false;
        boolean isInZAxe = false;
        if (loc.getX() < _loc.get_x() + tmpRadius && loc.getX() > _loc.get_x() - tmpRadius)
            isInXAxe = true;
        if (loc.getZ() < _loc.get_z() + tmpRadius && loc.getZ() > _loc.get_z() - tmpRadius)
            isInZAxe = true;
        return (isInXAxe && isInZAxe);
    }

    /**
     *  Says if the location is in this base.
     * @param loc the location to check.
     * @return True if the location is in this base.
     */
    public boolean isInBase(Location loc){
        boolean isInXAxe = false;
        boolean isInZAxe = false;
        if(loc.getX() < _loc.get_x() + _radius + 1 && loc.getX() > _loc.get_x() - _radius - 1)
            isInXAxe = true;
        if(loc.getZ() < _loc.get_z() + _radius + 1 && loc.getZ() > _loc.get_z() - _radius - 1)
            isInZAxe = true;
        return (isInXAxe && isInZAxe);
    }

    /**
     * Useful function for borderlands debug.
     */
    public void createMaxBorderShower() {
        Location baseLoc = _loc.getLocation();
        // North east
        baseLoc.add((Settings.initialRadius + Settings.radiusHQBonus), 0, (Settings.initialRadius + Settings.radiusHQBonus ) * (-1));
        Block highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
        baseLoc = _loc.getLocation();
        // North west
        baseLoc.add((Settings.initialRadius + Settings.radiusHQBonus) * (-1), 0, (Settings.initialRadius + Settings.radiusHQBonus) * (-1));
        highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
        baseLoc = _loc.getLocation();
        // south east
        baseLoc.add((Settings.initialRadius + Settings.radiusHQBonus), 0, (Settings.initialRadius + Settings.radiusHQBonus));
        highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
        baseLoc = _loc.getLocation();
        // south west
        baseLoc.add((Settings.initialRadius + Settings.radiusHQBonus) * (-1), 0, (Settings.initialRadius + Settings.radiusHQBonus));
        highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
    }

    /**
     * Create indicators at the base border (the limit of the base protection)
     */
    public void createBaseBorder() {
        Location baseLoc = _loc.getLocation();
        // North east
        baseLoc.add((_radius), 0, (_radius) * (-1));
        Block highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.WOOL);
        BlockState bs = highestBlock.getState();
        Wool wool = (Wool) bs.getData();
        wool.setColor(_team.get_color().get_dye());
        bs.update();
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
        baseLoc = _loc.getLocation();
        // North west
        baseLoc.add((_radius) * (-1), 0, (_radius) * (-1));
        highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.WOOL);
        bs = highestBlock.getState();
        wool = (Wool) bs.getData();
        wool.setColor(_team.get_color().get_dye());
        bs.update();
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
        baseLoc = _loc.getLocation();
        // south east
        baseLoc.add((_radius), 0, (_radius));
        highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.WOOL);
        bs = highestBlock.getState();
        wool = (Wool) bs.getData();
        wool.setColor(_team.get_color().get_dye());
        bs.update();
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
        baseLoc = _loc.getLocation();
        // south west
        baseLoc.add((_radius) * (-1), 0, (_radius));
        highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc);
        highestBlock.setType(Material.WOOL);
        bs = highestBlock.getState();
        wool = (Wool) bs.getData();
        wool.setColor(_team.get_color().get_dye());
        bs.update();
        highestBlock.getRelative(BlockFace.UP).setType(Material.FENCE);
        highestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.TORCH);
    }

    /**
     * Gets the minimum information about this base in one line.
     * @return
     */
    public String getMinimalInfo() {
        if(_contested)
            return "§a" + _name + "§6(§a" + _id + "§6) is under attack.";
        else
            return "§a" + _name + "§6(§a" + _id + "§6).";
    }

    /**
     * Gets the maximum information about this base.
     * @return
     */
    public String[] getInfo() {
        ArrayList<String> info = new ArrayList<String>();
        info.add("§6Name: §a" + _name);
        info.add("§6Base ID: §a" + _id);
        info.add("§6Protection radius: §a" + _radius);
        info.add("§6Current level: §a" + _level);
        if (_contested)
            info.add("§a" + _name + " §6is currently contested !");
        else
            info.add("§a" + _name + " §6is not contested at the moment.");
        if(_HQ)
            info.add("§a" + _name + " §6is your Head Quarter.");
        String[] infoArray = new String[info.size()];
        for (int i = 0; i < info.size(); i++) {
            infoArray[i] = info.get(i);
        }
        return infoArray;
    }

    /**
     * Upgrade the base level.
     * @return
     */
    public boolean upgrade() {
        if (_level >= 5) {
            return false;
        } else if (_team.get_money() >= Settings.radiusCost[_level - 1]) {
            _team.pay(Settings.radiusCost[_level - 1]);
            _level++;
            if (_level == 5)
                _radius = _initialRadius + _bonusRadius;
            else {
                int quarter = _bonusRadius/4;
                _radius = _initialRadius + quarter*(_level - 1);
            }
            createBaseBorder();
            return true;
        }
        return false;
    }

    /**
     * Check if the team which own this base is enemy to a_team.
     * @param a_team
     * @return
     */
    public boolean isEnemyToTeam(Team a_team) {
        return _team.isEnemyToTeam(a_team);
    }

    /**
     * Check if the base is enemy to
     * @param player
     * @return
     */
    public boolean isEnemyToPlayer(MCWarClanPlayer player) {
        return player.isEnemyToTeam(_team);
    }

    /**
     * Check if the bas that owns the team has enough team mates to be attacked.
     * @return
     */
    public boolean hasEnoughTeamMatesToBeAttacked() {
        return _team.enoughMatesToBeAttack();
    }

    /**
     * Get the colored name of the team which own this base.
     * @return
     */
    public String getTeamColoredName() {
        return _team.getColoredName();
    }

    /**
     * Check if the base is at its maximum level.
     * @return
     */
    public boolean isLevelMax() {
        return _level >= 5;
    }
}

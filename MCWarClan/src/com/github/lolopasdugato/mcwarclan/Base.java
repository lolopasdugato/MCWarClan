package com.github.lolopasdugato.mcwarclan;


import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidFlagLocationException;
import com.github.lolopasdugato.mcwarclan.customexceptions.NotEnoughSpaceException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.Serializable;

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
    private Cost _cost;             // The cost to create a new base
    private String _name;
    private int _id;
    private boolean _contested;

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
        _cost = Settings.baseInitialCost;   // WARNING shouldn't be reload !
        _name = name;
        _id = _idMaster;
        if (_HQ)
            _radius = _initialRadius + _bonusRadius;
        else
            _radius = _initialRadius;
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


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        // This refresh should be changed if the radius can change during the game.
        _bonusRadius = Settings.radiusHQBonus;
        _initialRadius = Settings.initialRadius;
        if(_HQ)
            _radius = _initialRadius + _bonusRadius;
        else
            _radius = _initialRadius;
        if(_idMaster < _id)
            _idMaster = _id;
        _contested = false;
    }

    /**
     * Check if you're near this base
     * @param includeSafeZone if we include teh safe zone, we will incle the minimum distance between 2 HQ in the math.
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
        if(loc.getX() < _loc.get_x() + _radius && loc.getX() > _loc.get_x() - _radius)
            isInXAxe = true;
        if(loc.getZ() < _loc.get_z() + _radius && loc.getZ() > _loc.get_z() - _radius)
            isInZAxe = true;
        return (isInXAxe && isInZAxe);
    }
}

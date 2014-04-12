package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;

import java.io.Serializable;
import java.util.Locale;

public class Base implements Serializable {
	
	static private final long serialVersionUID = 5;
	
	private boolean _HQ;			// Determine if this is an HQ or not
	private int _radius; 			// Determine the radius protection effect of this base
    private int _bonusRadius;       // Determine the radius bonus for the HQ.
	private Team _team;				// Team which this object is attached to
	private Flag _flag;				// The flag attached to this base
	private MCWarClanLocation _loc;	// Represent the location of a base
    private Cost _cost;             // The cost to create a new base
	
	
	public boolean is_HQ() {
		return _HQ;
	}

	public void set_HQ(boolean _HQ) {
		this._HQ = _HQ;
	}

	public int get_radius() {
		return _radius;
	}

	public void set_radius(int _radius) {
		this._radius = _radius;
	}

	public Team get_team() {
		return _team;
	}

	public void set_team(Team _team) {
		this._team = _team;
	}

	public Flag get_flag() {
		return _flag;
	}

	public void set_flag(Flag _flag) {
		this._flag = _flag;
	}

	public MCWarClanLocation get_loc() {
		return _loc;
	}

	public void set_loc(MCWarClanLocation _loc) {
		this._loc = _loc;
	}

    public int get_bonusRadius() { return _bonusRadius; }

    public void set_bonusRadius(int _bonusRadius) { this._bonusRadius = _bonusRadius; }

    public Base(boolean HQ, Team team, MCWarClanLocation loc) {
		_HQ = HQ;
		_team = team;
		_loc = loc;
        _radius = Settings.initialRadius;   // WARNING: should'nt be reloaded !
        _bonusRadius = Settings.radiusHQBonus;
        _cost = Settings.baseInitialCost;   // WARNING should'nt be reload !
        _flag = new Flag(this);
	}

    /**
     * @brief refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        _bonusRadius = Settings.radiusHQBonus;
    }

    // Says if the location is in this base
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

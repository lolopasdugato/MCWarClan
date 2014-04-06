package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

public class Base implements Serializable {
	
	static private final long serialVersionUID = 5;
	
	private boolean _HQ;			// Determine if this is an HQ or not
	private int _radius; 			// Determine the radius protection effect of this base
	private Team _team;				// Team which this object is attached to
	private Flag _flag;				// The flag attached to this base
	private MCWarClanLocation _loc;	// Represent the location of a base
	
	
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


	public Base(boolean HQ, int radius, Team team, Flag flag, MCWarClanLocation loc) {
		_HQ = HQ;
		_radius = radius;
		_team = team;
		_flag = flag;
		_loc = loc;
	}

}

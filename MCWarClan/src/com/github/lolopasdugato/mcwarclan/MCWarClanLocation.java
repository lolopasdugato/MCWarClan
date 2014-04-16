package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

public class MCWarClanLocation implements Serializable {
	
	static private final long serialVersionUID = 4;
	
	private double _x;
	private double _y;
	private double _z;
	private String _worldName;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public MCWarClanLocation(String worldName, double x, double y, double z) {
        _worldName = worldName;
        _x = x;
        _y = y;
        _z = z;
    }

    public MCWarClanLocation(Location loc) {
        _worldName = loc.getWorld().getName();
        _x = loc.getX();
        _y = loc.getY();
        _z = loc.getZ();
    }

    //Getters

    public String get_worldName() {
        return _worldName;
    }

    //Setters
    public void set_worldName(String _worldName) {
        this._worldName = _worldName;
    }

    public double get_x() {
        return _x;
    }

    public void set_x(double _x) {
        this._x = _x;
    }

    public double get_y() {
        return _y;
    }

    public void set_y(double _y) {
        this._y = _y;
    }

    public double get_z() {
        return _z;
    }

    public void set_z(double _z) {
        this._z = _z;
    }

    public Location getLocation(){
        return new Location(Bukkit.getServer().getWorld(_worldName), _x, _y, _z);
    }


    //Functions

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        // No Settings to refresh
    }

}

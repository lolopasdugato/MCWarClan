package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MCWarClanLocation implements Serializable {
	
	static private final long serialVersionUID = 004;
	
	private double _x;
	private double _y;
	private double _z;
	private String _worldName;
	
	public Location getLocation(){
		return new Location(Bukkit.getServer().getWorld(_worldName), _x, _y, _z);
	}
	
	public MCWarClanLocation(String worldName, double x, double y, double z) {
		_worldName = worldName;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public MCWarClanLocation(Location loc){
		_worldName = loc.getWorld().getName();
		_x = loc.getX();
		_y = loc.getY();
		_z = loc.getZ();
	}

}

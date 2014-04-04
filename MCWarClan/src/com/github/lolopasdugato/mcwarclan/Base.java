package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

public class Base implements Serializable {
	
	private boolean _HQ;			// Determine if this is an HQ or not
	private int _radius; 			// Determine the radius protection effect of this base
	private Team _team;				// Team which this object is attached to
	
	public Base() {
		// TODO Auto-generated constructor stub
	}

}

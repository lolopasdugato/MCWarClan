package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

public class Flag implements Serializable {
	
	static private final long serialVersionUID = 006;
	
	private Base _base;
	// private enum _type;

	public Flag(Base base) {
		_base = base;
	}

}

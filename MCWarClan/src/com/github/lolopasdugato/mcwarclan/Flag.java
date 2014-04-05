package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.Serializable;

public class Flag implements Serializable {
	
	static private final long serialVersionUID = 006;
	
	private Base _base;
	// private enum _type;
    final private int stickHeight = 5;
    final private int flagHeight = 2;
    final private int flagLength = 3;

	public Flag(Base base) {
		_base = base;
	}

    public Flag(Location loc) {
        generateFlag(loc);
    }

    void generateFlag(Location loc)
    {
        org.bukkit.block.Block blk = loc.getBlock();

        for (int i = 0; i < stickHeight; i++) {
            blk = blk.getRelative(BlockFace.UP);
            blk.setType(Material.WOOD);
//            blk.setType(Material.ANVIL);
        }

        //Create the flag
        Block tmp = blk.getRelative(BlockFace.NORTH);
        for (int h = 0; h < flagHeight; h++) {
            for (int l = 0; l < flagLength; l++) {
                blk.getRelative(0,l,h).setType(Material.WOOD);
            }
        }
    }
}

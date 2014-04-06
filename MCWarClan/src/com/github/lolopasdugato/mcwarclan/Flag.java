package com.github.lolopasdugato.mcwarclan;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.Serializable;

public class Flag implements Serializable {
	
	static private final long serialVersionUID = 6;
	
	private Base _base;
	// private enum _type;
    final private int stickHeight = 5;
    final private int flagHeight = 2;
    final private int flagLength = 2;
    private Base _base;

	public Flag(Base base) {
		_base = base;
	}

    public Flag(Location loc, Color color) {
        generateFlag(loc, color);
    }

    //TODO Check if it's only an empty zone for the flag

    boolean generateFlag(Location loc, Color color)
    {
        System.out.println("FLAG!");
        org.bukkit.block.Block init = loc.getWorld().getHighestBlockAt(loc);
        Block blk = init;
        boolean empty = true;


        //Have to check if the block below if solid
        if (blk.getRelative(BlockFace.DOWN).getType().isSolid() == false) {
            System.out.println("Not a solid block under the flag.");
            return false;
        }

        //first, have to check if the area is empty
        int j = 0;
        while (j < stickHeight && empty) {
            blk.getType();
            if (blk.getType().isSolid()) {
                System.out.println("ERROR");
//                blk.setType(Material.IRON_BLOCK);
                empty = false;
            }
            blk = blk.getRelative(BlockFace.UP);
            j++;
//            blk.setType(Material.ANVIL);
        }

        //Check if we have to continue the check or not
        if (empty) {
            System.out.println("On check le flag");

            //Set position for drawing the flag
            blk = blk.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);

            //Create the flag
            //Block tmp = blk.getRelative(BlockFace.NORTH);
            int h = 0, l = 0;
//            for (int h = 0; h < flagHeight; h++)
            while (h < flagHeight && empty) {
//                for (int l = 0; l < flagLength; l++)
                while (l < flagLength && empty) {
                    if (blk.getRelative(0, -h, -l).getType().isSolid()) {
                        System.out.println("Loop 2");
//                        blk.setType(Material.IRON_BLOCK);
                        empty = false;
                    }
                    l++;
                }
                l = 0;
                h++;
            }
        }
        System.out.println("Check finished");

        //Check if it's always empty
        if (empty) {
            System.out.println("No problem");

            //Reset block position to init
            blk = init;

            //If empty, generate flag with the right color
            for (int i = 0; i < stickHeight; i++) {
                blk.setType(Material.WOOD);
                blk = blk.getRelative(BlockFace.UP);
//            blk.setType(Material.ANVIL);
            }

            //Set position for drawing the flag
            blk = blk.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);

            //Create the flag
            //Block tmp = blk.getRelative(BlockFace.NORTH);
            DyeColor dye = color.toDyeColor();
            Block tmp = blk;
            for (int h = 0; h < flagHeight; h++) {
                for (int l = 0; l < flagLength; l++) {
                    tmp = blk.getRelative(0, -h, -l);
                    tmp.setType(Material.WOOL);
                    tmp.setData(dye.getData());
                }
            }
            return true;
        } else {
            System.out.println("Not enough space");
            return false;
        }
    }
}

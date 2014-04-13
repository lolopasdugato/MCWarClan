package com.github.lolopasdugato.mcwarclan;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.Serializable;

public class Flag implements Serializable {
	
    // private enum _type;
    static final public int stickHeight = 5;
    static final public int flagHeight = 2;
    static final public int flagLength = 2;
    static private final long serialVersionUID = 6;
    private Base _base;

    public Flag(Base base) throws Exception.NotEnoughSpaceException, Exception.NotValidFlagLocationException {
        _base = base;
        generateFlag(_base.get_loc().getLocation(), _base.get_team().get_color());
    }

    //public Flag(Location loc, Color color){ generateFlag(loc, color); }   // Deprecated !

    //TODO Check if it's only an empty zone for the flag

    boolean generateFlag(Location loc, Color color) throws Exception.NotValidFlagLocationException, Exception.NotEnoughSpaceException {

        //org.bukkit.block.Block init = loc.getWorld().getHighestBlockAt(loc);
        Block init = loc.getBlock().getRelative(BlockFace.UP);
        Block blk = init;
        boolean empty = true;


        //Have to check if the block below if solid
        if (!blk.getRelative(BlockFace.DOWN).getType().isSolid()) {
            throw new Exception.NotValidFlagLocationException();
        }

        //first, have to check if the area is empty
        int j = 0;
        while (j < stickHeight && empty) {
            blk.getType();
            if (blk.getType().isSolid()) {

                empty = false;
            }
            blk = blk.getRelative(BlockFace.UP);
            j++;
        }

        //Check if we have to continue the check or not
        if (empty) {
            //Set position for drawing the flag
            blk = blk.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);

            //Create the flag
            int h = 0, l = 0;

            while (h < flagHeight && empty) {
                while (l < flagLength && empty) {
                    if (blk.getRelative(0, -h, -l).getType().isSolid()) {
                        empty = false;
                    }
                    l++;
                }
                l = 0;
                h++;
            }
        }

        //Check if it's always empty
        if (empty) {

            //Reset block position to init
            blk = init;

            //If empty, generate flag with the right color
            for (int i = 0; i < stickHeight; i++) {
                blk.setType(Material.WOOD);
                blk = blk.getRelative(BlockFace.UP);
            }

            //Set position for drawing the flag
            blk = blk.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);

            //Create the flag
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
            throw new Exception.NotEnoughSpaceException();
        }
    }


}

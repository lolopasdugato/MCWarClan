package com.github.lolopasdugato.mcwarclan;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

import java.io.Serializable;

public class Flag implements Serializable {

    // private enum _type;
    static final public int stickHeight = 5;
    static final public int flagHeight = 2;
    static final public int flagLength = 2;
    static private final long serialVersionUID = 6;
    private final Pattern _pattern;
    private Base _base;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic Flag constructor
     * @param base
     * @throws Exception.NotEnoughSpaceException
     * @throws Exception.NotValidFlagLocationException
     */
    public Flag(Base base) throws Exception.NotEnoughSpaceException, Exception.NotValidFlagLocationException {
        _base = base;

        //Initialize the block which will be the base block
        //org.bukkit.block.Block init = loc.getWorld().getHighestBlockAt(loc);
        Block init = _base.get_loc().getLocation().getBlock().getRelative(BlockFace.UP);
        Block blk = init;

        //Have to check if the block below if solid
        if (!blk.getRelative(BlockFace.DOWN).getType().isSolid()) {
            throw new Exception.NotValidFlagLocationException();
        }

        //Call function to check for enough empty space for flag
        boolean empty = enoughSpaceForFlag(blk);

        //Check if it's empty
        if (empty) {
            generateFlag(blk, _base.get_team().get_color().get_dye());
        } else {
            throw new Exception.NotEnoughSpaceException();
        }


        //test
        Messages.sendMessage("Test for version 2", Messages.messageType.DEBUG, null);
        _pattern = flagPattern();

        //test if the pattern can be fit in the location
        Location newloc = blk.getLocation();
        newloc.setX(blk.getX() + 5);
        newloc.setY(blk.getY() + 5);
        newloc.setZ(blk.getZ() + 5);

        if (_pattern.isEmpty(blk.getWorld(), newloc) != true) {
            throw new Exception.NotEnoughSpaceException();
        }

        //Generate the flag with the new method

        _pattern.generate(blk.getWorld(), newloc);

        Messages.sendMessage("SUCCESS", Messages.messageType.DEBUG, null);
    }

    /**
     * Used to generate a basic flag pattern
     *
     * @return The pattern of the flag
     */
    private Pattern flagPattern() {

        //Create the empty pattern
        Pattern pat = new Pattern();


        //Generate the stick
        for (int i = 0; i < stickHeight; i++) {
            pat.add(new BlockModule(Material.WOOD, 0, i, 0));
        }

        //Set position for drawing the flag
//        blk = blk.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);

        //Create the flag with the right color
//        Block tmp = blk;
        BlockState bs;
        for (int h = 0; h < flagHeight; h++) {
            for (int l = 0; l < flagLength; l++) {
//                tmp = blk.getRelative(0, -h, -l);
//                tmp.setType(Material.WOOL);
                pat.add(new BlockModule(Material.WOOL, 0, stickHeight - (h + 1), 1 + l));
//                bs = tmp.getState();
//                bs.setData(new Wool(dye));
//                bs.update();
            }
        }
        return pat;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @param blk The block which is the base of the flag.
     * @param dye The color of the team, which will be on the flag.
     *  Generate a flag for a base, taking blk as the base block.
     */
    private void generateFlag(Block blk, DyeColor dye) {

        //Generate the stick
        for (int i = 0; i < stickHeight; i++) {
            blk.setType(Material.WOOD);
            blk = blk.getRelative(BlockFace.UP);
        }

        //Set position for drawing the flag
        blk = blk.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);

        //Create the flag with the right color
        Block tmp = blk;
        BlockState bs;
        for (int h = 0; h < flagHeight; h++) {
            for (int l = 0; l < flagLength; l++) {
                tmp = blk.getRelative(0, -h, -l);
                tmp.setType(Material.WOOL);
                bs = tmp.getState();
                bs.setData(new Wool(dye));
                bs.update();
            }
        }
    }

    /**
     * @param blk The block which is the base of the flag.
     * @return True is enough space, else false.
     *  Test if the is enough space to generate the flag, with the block in argument as a base.
     */

    //
    private boolean enoughSpaceForFlag(Block blk) {
        boolean empty = true;
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

            //Test empty space for the flag
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
        return empty;
    }

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        _base.refresh();
        // No Settings
    }


}

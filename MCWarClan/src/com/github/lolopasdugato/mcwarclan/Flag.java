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

        //Initialize the block which will be the base block we're working on.
        Block blk = _base.get_loc().getLocation().getBlock().getRelative(BlockFace.UP);

        //Have to check if the block below if solid
        if (!blk.getRelative(BlockFace.DOWN).getType().isSolid()) {
            throw new Exception.NotValidFlagLocationException();
        }
        // Check if block is not below the sea level.
        else if (!(blk.getRelative(BlockFace.UP).getY() >= 64)){
            throw new Exception.NotValidFlagLocationException();
        }
        Messages.sendMessage("Beginning flag creation using pattern...", Messages.messageType.DEBUG, null);
        _pattern = new Pattern(this, Pattern.patternType.CLASSIC_FLAG);
        if (!_pattern.isEmpty()) {
            throw new Exception.NotEnoughSpaceException();
        }
        _pattern.generate();
        Messages.sendMessage("Flag successfully created !", Messages.messageType.DEBUG, null);
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Base get_base() {
        return _base;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        _base.refresh();
        // No Settings
    }


}

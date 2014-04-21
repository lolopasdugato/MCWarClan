package com.github.lolopasdugato.mcwarclan;

import com.avaje.ebeaninternal.server.cluster.mcast.Message;
import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidFlagLocationException;
import com.github.lolopasdugato.mcwarclan.customexceptions.NotEnoughSpaceException;
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
     * Classic flag constructor.
     * @param base
     * @throws NotEnoughSpaceException
     * @throws InvalidFlagLocationException
     */
    public Flag(Base base) throws NotEnoughSpaceException, InvalidFlagLocationException {
        _base = base;

        //Initialize the block which will be the base block we're working on.
        Block blk = _base.get_loc().getLocation().getBlock().getRelative(BlockFace.UP);

        //Have to check if the block below if solid
        if (!blk.getRelative(BlockFace.DOWN).getType().isSolid()) {
            throw new InvalidFlagLocationException("There is no solid block under the flag !");
        }
        // Check if block is not below the sea level.
        else if (!(blk.getRelative(BlockFace.UP).getY() >= 64)){
            throw new InvalidFlagLocationException("The flag should be over the sea level (y > 64) !");
        }
        Messages.sendMessage("Beginning flag creation using pattern...", Messages.messageType.DEBUG, null);
        _pattern = new Pattern(this, Pattern.patternType.CLASSIC_FLAG);
        if (!_pattern.isEmpty()) {
            throw new NotEnoughSpaceException("There is not enough empty block to place the flag !");
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

    public Pattern get_pattern() {
        return _pattern;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Check if the flag pattern is destroyed at a certain percentage.
     * @param percentage
     * @return
     */
    public boolean isDestroyed(int percentage){
        int percentageOfBlocksDestroyed = (_pattern.getNumberOfEmptyBlocks() * 100)/_pattern.get_pattern().size();
        Messages.sendMessage("percentageOfBlocksDestroyed: " + percentageOfBlocksDestroyed + "(" + _pattern.getNumberOfEmptyBlocks() + "), pattern size: " + _pattern.get_pattern().size() + ", percentage needed: " + percentage + "."
                , Messages.messageType.DEBUG, null);
        return percentageOfBlocksDestroyed >= percentage;
    }

    public void destroy(){
        _pattern.erase();
    }

    public void forceDestroy(){
        _pattern.forceErase();
    }

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        _base.refresh();
        // No Settings
    }


}

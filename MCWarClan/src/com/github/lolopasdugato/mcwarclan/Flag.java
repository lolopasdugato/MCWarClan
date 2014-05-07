package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidFlagLocationException;
import com.github.lolopasdugato.mcwarclan.customexceptions.NotEnoughSpaceException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.Serializable;

public class Flag extends Pattern implements Serializable {

    static private final long serialVersionUID = 6;
    private Base _base;

    /**
     * Classic flag constructor.
     *
     * @param base
     * @throws NotEnoughSpaceException
     * @throws InvalidFlagLocationException
     */
    public Flag(Base base, FlagType flagType) throws NotEnoughSpaceException, InvalidFlagLocationException {

        _base = base;

        //Initialize the block which will be the base block we're working on.
        Block blk = _base.get_loc().getLocation().getBlock().getRelative(BlockFace.UP);

        //Have to check if the block below if solid
        if (!blk.getRelative(BlockFace.DOWN).getType().isSolid()) {
            throw new InvalidFlagLocationException("There is no solid block under the flag !");
        }
        // Check if block is not below the sea level.
        else if (!(blk.getRelative(BlockFace.UP).getY() >= 64)) {
            throw new InvalidFlagLocationException("The flag should be over the sea level (y > 64) !");
        }
        Messages.sendMessage("Beginning flag creation using pattern...", Messages.messageType.DEBUG, null);

        //Generate the pattern needed depending on the FlagType
        MCWarClanLocation mcLoc = new MCWarClanLocation(blk.getLocation());
        if (flagType == FlagType.HQ) {
            //TODO To change to the new HQ flag model
            newClassicFlagPattern(mcLoc);
        } else
            newClassicFlagPattern(mcLoc);
        if (!isEmpty()) {
            throw new NotEnoughSpaceException("There is not enough empty block to place the flag !");
        } else {
            //Generate the pattern previously created
            generate(base.get_team().get_color());
        }

        Messages.sendMessage("Flag successfully created !", Messages.messageType.DEBUG, null);
    }

    public Base get_base() {
        return _base;
    }

    ;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Check if the flag pattern is destroyed at a certain percentage.
     *
     * @param percentage
     * @return
     */
    public boolean isDestroyed(int percentage) {
        int percentageOfBlocksDestroyed = (getNumberOfEmptyBlocks() * 100) / get_pattern().size();
        Messages.sendMessage("percentageOfBlocksDestroyed: " + percentageOfBlocksDestroyed + "(" + getNumberOfEmptyBlocks() + "), pattern size: " + get_pattern().size() + ", percentage needed: " + percentage + "."
                , Messages.messageType.DEBUG, null);
        return percentageOfBlocksDestroyed >= percentage;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh() {
        _base.refresh();
        // No Settings
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Used to generate a basic flag pattern
     *
     * @param initialLocation
     * @return The pattern of the flag
     */
    private void newClassicFlagPattern(MCWarClanLocation initialLocation) {
        int stickHeight = 6;
        int flagHeight = 2;
        int flagLength = 3;

        // Generate the stick
        for (int i = 0; i < stickHeight; i++) {
            MCWarClanLocation newLoc = new MCWarClanLocation(initialLocation);
            newLoc.set_y(newLoc.get_y() + i);
            add(Material.LOG, newLoc);
        }
        // Generate the flag
        for (int h = 0; h < flagHeight; h++) {
            for (int l = 0; l < flagLength; l++) {
                MCWarClanLocation newLoc = new MCWarClanLocation(initialLocation);
                newLoc.set_y(newLoc.get_y() + stickHeight - (h + 1));
                newLoc.set_z(newLoc.get_z() + 1 + l);
                add(Material.WOOL, newLoc);
            }
        }
    }

    public static enum FlagType {CLASSIC, HQ}
}

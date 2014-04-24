package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Material;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Seb on 17/04/2014.
 */
public abstract class Pattern implements Serializable {

    static private final long serialVersionUID = 10;


    protected ArrayList<BlockModule> _pattern;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Pattern() {
        _pattern = new ArrayList<BlockModule>();
    }


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////


    public ArrayList<BlockModule> get_pattern() { return _pattern; }

    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Setters ---------------------------------
    //////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Functions ---------------------------------
    //////////////////////////////////////////////////////////////////////////////


    public void add(Material material, MCWarClanLocation location) {
        _pattern.add(new BlockModule(material, location));

    }

    /**
     * Check if the pattern where the pattern should be created is empty (AIR)
     * @return
     */
    public boolean isEmpty() {
        for (BlockModule module : _pattern) {
            if (!module.isAir())
                return false;
        }
        return true;
    }

    /**
     * Generate the pattern
     */
    public void generate(Color color) {
        for (BlockModule module : _pattern) {
            module.toBlock(color);
        }
    }

    /**
     * Get the number of blocks in the pattern that are not in the real minecraft world
     * @return
     */
    public int getNumberOfEmptyBlocks(){
        int numberOfEmptyBlocks = 0;
        for (BlockModule module : _pattern) {
            if (!module.isPlaced())
                numberOfEmptyBlocks++;
        }
        return numberOfEmptyBlocks;
    }

    /**
     * Erase blocks that are in the real world and the same type of the pattern BlockModule concerned.
     */
    public void erase(){
        for (BlockModule module : _pattern) {
            module.erase();
        }
    }

    /**
     * Erase blocks that are in the real world even if they are not the same type of the pattern BlockModule concerned.
     */
    public void forceErase(){
        for (BlockModule module : _pattern) {
            module.forceErase();
        }
    }


}
package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Seb on 17/04/2014.
 */
public class Pattern implements Serializable {

    static private final long serialVersionUID = 10;

    public static enum patternType {
        CLASSIC_FLAG, CUSTOM
    }

    private ArrayList<BlockModule> _pattern;
//    private Flag _flag;
    private MCWarClanLocation _initialLocation;
    private Color _woolColor;
    private patternType _type;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Pattern(MCWarClanLocation initialLocation, Color woolColor, patternType type) {
        _pattern = new ArrayList<BlockModule>();
        _initialLocation = initialLocation;
        _type = type;
        _woolColor = woolColor;

        switch (type) {
            case CLASSIC_FLAG:
                newClassicFlagPattern();
                break;
            case CUSTOM:
                break;
            default:
                Messages.sendMessage("Unknown generation type...", Messages.messageType.DEBUG, null);
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

//    public Flag get_flag() {
//        return _flag;
//    }


    public MCWarClanLocation get_initialLocation() { return _initialLocation; }

    public ArrayList<BlockModule> get_pattern() { return _pattern; }

    public Color get_woolColor() { return _woolColor; }

    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Setters ---------------------------------
    //////////////////////////////////////////////////////////////////////////////


    public void set(int index, BlockModule module) {
        _pattern.set(index, module);
    }

    public void set_woolColor(Color _woolColor) {
        this._woolColor = _woolColor;
    }
    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Functions ---------------------------------
    //////////////////////////////////////////////////////////////////////////////


    public void add(int index, BlockModule module) {
        _pattern.add(index, module);
    }

    public void add(BlockModule module) {
        _pattern.add(module);
    }

    public void add(Material material, MCWarClanLocation location){
        _pattern.add(new BlockModule(material, location, this));
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
    public void generate() {
        for (BlockModule module : _pattern) {
            module.toBlock();
        }
    }

    /**
     * Used to generate a basic flag pattern
     *
     * @return The pattern of the flag
     */
    private void newClassicFlagPattern() {
        int stickHeight = 6;
        int flagHeight = 2;
        int flagLength = 3;
        MCWarClanLocation initialLocation = new MCWarClanLocation(_initialLocation.getLocation().getBlock().getRelative(BlockFace.UP).getLocation());

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
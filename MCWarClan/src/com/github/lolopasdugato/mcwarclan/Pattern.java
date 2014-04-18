package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Created by Seb on 17/04/2014.
 */
public class Pattern {

    private ArrayList<BlockModule> _pattern;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Pattern() {
        _pattern = new ArrayList<BlockModule>();
    }


    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Setters ---------------------------------
    //////////////////////////////////////////////////////////////////////////////


    public void set(int index, BlockModule module) {
        _pattern.set(index, module);
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


    public boolean isEmpty(World world, Location loc) {
        for (BlockModule mod : _pattern) {
            if (world.getBlockAt(loc.getBlockX() + mod.get_x(), loc.getBlockY() + mod.get_y(),
                    loc.getBlockZ() + mod.get_z()).isEmpty() == false)
                return false;
        }
        return true;
    }


    public void generate(World world, Location loc) {
        for (BlockModule modBlk : _pattern)
            world.getBlockAt(loc.getBlockX() + modBlk.get_x(), loc.getBlockY() + modBlk.get_y(),
                    loc.getBlockZ() + modBlk.get_z()).setType(modBlk.get_material());
    }
}

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

import java.io.Serializable;

/**
 * Created by Seb on 17/04/2014.
 */
public class BlockModule implements Serializable{

    static private final long serialVersionUID = 11;

    private Pattern _pattern;
    private MCWarClanLocation _location;
    private Material _material;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public BlockModule(Material material, MCWarClanLocation location, Pattern pattern){
        _material = material;
        _location = location;
        _pattern = pattern;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Material get_material() {
        return _material;
    }

    public void set_material(Material _material) {
        this._material = _material;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Transform a BlockModule into a block in the real minecraft world
     */
    public void toBlock(){
        Location loc = _location.getLocation();
        loc.getBlock().setType(_material);
        if (loc.getBlock().getType() == Material.WOOL){
            BlockState bs = loc.getBlock().getState();
            Wool wool = (Wool) bs.getData();
            wool.setColor(_pattern.get_woolColor().get_dye());
            bs.update();
        }
    }

    /**
     * Check if the BlockModule is referring to an empty block (AIR) in the real minecraft world;
     * @return true if Material.AIR.
     */
    public boolean isAir(){
        return _location.getLocation().getBlock().isEmpty();
    }

    /**
     * Check if the block at the current BlockModule position is the same material.
     * @return
     */
    public boolean isPlaced(){
        return _location.getLocation().getBlock().getType().equals(_material);
    }

    public void erase(){
        if(_location.getLocation().getBlock().getType().equals(_material)) {
            _location.getLocation().getBlock().setType(Material.AIR);
            Messages.sendMessage("Materials are the same ! Erasing...", Messages.messageType.DEBUG, null);
        }
        else
            Messages.sendMessage("Cannot erase ! materials are not the same !", Messages.messageType.DEBUG, null);
    }

    public void forceErase(){
        _location.getLocation().getBlock().setType(Material.AIR);
    }
}

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Material;

/**
 * Created by Seb on 17/04/2014.
 */
public class BlockModule {

//    private Block _blk;

    private int _x;
    private int _y;
    private int _z;
    private Material _material;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    //    public BlockModule(Material material, Block blk)
    public BlockModule(Material material, int x, int y, int z) {
        this._material = material;
//        this._blk = blk;
        _x = x;
        _y = y;
        _z = z;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

//    public Block get_blk() {
//        return _blk;
//    }

    public Material get_material() {
        return _material;
    }

    public void set_material(Material _material) {
        this._material = _material;
    }

    public int get_x() {
        return _x;
    }

    public int get_y() {
        return _y;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

//    public void set_blk(Block _blk) {
//        this._blk = _blk;
//    }

    public int get_z() {
        return _z;
    }

    public void setX(int x) {
        this._x = x;
    }

    public void setY(int y) {
        this._y = y;
    }

    public void setZ(int z) {
        this._z = z;
    }
}

package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

/**
 * Created by Loïc on 05/04/2014.
 */
public class Equivalence implements Serializable {

    static private final long serialVersionUID = 8;

    private String _materialName;
    private int _materialValue;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Equivalence(String materialName, int materialValue) {
        _materialName = materialName;
        _materialValue = materialValue;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public String get_materialName() {
        return _materialName;
    }

    public void set_materialName(String _materialName) {
        this._materialName = _materialName;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public int get_materialValue() {
        return _materialValue;
    }

    public void set_materialValue(int _materialValue) {
        this._materialValue = _materialValue;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Check if these two objects represents the same material.
     * @param e
     * @return
     */
    public boolean is(Equivalence e) {
        return e.get_materialName().equalsIgnoreCase(_materialName);
    }

    /**
     * Increment the number of material using the specified amount.
     * @param amount
     */
    public void add(int amount) {
        _materialValue += amount;
    }

    /**
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        // No Settings to refresh
    }
}

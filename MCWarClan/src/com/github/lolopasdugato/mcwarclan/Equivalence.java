package com.github.lolopasdugato.mcwarclan;

import java.io.Serializable;

/**
 * Created by Loïc on 05/04/2014.
 */
public class Equivalence implements Serializable {

    static private final long serialVersionUID = 8;

    private String _materialName;
    private int _materialValue;

    public String get_materialName() {
        return _materialName;
    }

    public void set_materialName(String _materialName) {
        this._materialName = _materialName;
    }

    public int get_materialValue() {
        return _materialValue;
    }

    public void set_materialValue(int _materialValue) {
        this._materialValue = _materialValue;
    }

    public Equivalence(String materialName, int materialValue){
        _materialName = materialName;
        _materialValue = materialValue;
    }
}

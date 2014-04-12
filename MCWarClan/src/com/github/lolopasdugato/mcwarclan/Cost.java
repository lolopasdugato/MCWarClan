package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Loïc on 05/04/2014.
 */
public class Cost implements Serializable{

    static private final long serialVersionUID = 7;

    private ArrayList<Equivalence> _costEquivalence;            // The Equivalence between a cost and a material

    public ArrayList<Equivalence> get_costEquivalence() { return _costEquivalence; }

    public void set_costEquivalence(ArrayList<Equivalence> _costEquivalence) { this._costEquivalence = _costEquivalence; }

    public Cost(){
        _costEquivalence = new ArrayList<Equivalence>();
    }

    public Cost(Cost c){
        _costEquivalence = c.get_costEquivalence();
    }

    public boolean addValue(String materialName, int numberOfMaterials){
        return _costEquivalence.add(new Equivalence(materialName, numberOfMaterials));
    }

    public String[] getResourceTypes(){
        ArrayList<String> resources = new ArrayList<String>();
        for(int i = 0; i < _costEquivalence.size(); i++){
            if(_costEquivalence.get(i).get_materialValue() != 0)
                resources.add("§6" + _costEquivalence.get(i).get_materialName() + " : " + _costEquivalence.get(i).get_materialValue() + ".");
        }
        String[] toReturn = new String[resources.size()];
        for(int i = 0; i < resources.size(); i++){
            toReturn[i] = resources.get(i);
        }
        return toReturn;
    }

    /**
     * @brief refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        // No settings to refresh
    }
}

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Material;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Loïc on 05/04/2014.
 */
public class Cost implements Serializable{

    static private final long serialVersionUID = 7;

    private ArrayList<Equivalence> _costEquivalence;            // The Equivalence between a cost and a material

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic Cost constructor
     */
    public Cost(){
        _costEquivalence = new ArrayList<Equivalence>();
    }

    /**
     *  Cost copy constructor
     * @param c
     */
    public Cost(Cost c){
        _costEquivalence = c.get_costEquivalence();
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public ArrayList<Equivalence> get_costEquivalence() {
        return _costEquivalence;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public void set_costEquivalence(ArrayList<Equivalence> _costEquivalence) { this._costEquivalence = _costEquivalence; }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Add a value to a cost.
     * @param materialName
     * @param numberOfMaterials
     * @return
     */
    public boolean addValue(String materialName, int numberOfMaterials){
        return Material.getMaterial(materialName) != null && numberOfMaterials >= 0 && _costEquivalence.add(new Equivalence(materialName, numberOfMaterials)) ;
    }

    /**
     *  Returns a string list containing all resources types in a cost, linked with their needed amount.
     * @return
     */
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
     *  refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        // No settings to refresh
    }
}

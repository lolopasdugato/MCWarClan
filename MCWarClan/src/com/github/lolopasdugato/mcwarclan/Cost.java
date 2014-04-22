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
        if (numberOfMaterials > 256) {
            numberOfMaterials = 256;
            Messages.sendMessage("Cannot ask for more than 256 " + materialName, Messages.messageType.DEBUG, null);
        }

        int totalNumberOfMaterials = 0;
        for (Equivalence a_costEquivalence : _costEquivalence) {
            totalNumberOfMaterials += a_costEquivalence.get_materialValue();
        }
        if (totalNumberOfMaterials + numberOfMaterials > 2304) {
            Messages.sendMessage("Cannot add " + materialName + " or a unique player would not be able to pay this tribute due to inventory capacity.", Messages.messageType.ALERT, null);
            return false;
        }
        int i = 0;
        boolean materialFound = false;
        while (i < _costEquivalence.size() && !materialFound) {
            if (_costEquivalence.get(i).get_materialName().equalsIgnoreCase(materialName)) {
                _costEquivalence.get(i).set_materialValue(_costEquivalence.get(i).get_materialValue() + numberOfMaterials);
                materialFound = true;
            }
            i++;
        }
        if (!materialFound) {
            _costEquivalence.add(new Equivalence(materialName, numberOfMaterials));
        }
        return Material.getMaterial(materialName) != null && numberOfMaterials >= 0;
    }

    /**
     * Add a cost to another.
     * @param costToAdd
     * @return
     */
    public boolean addCost(Cost costToAdd) {
        ArrayList<Equivalence> costToAddEqui = costToAdd.get_costEquivalence();
        Cost newCost = new Cost(this);
        for (int i = 0; i < costToAddEqui.size(); i++) {
            if(!newCost.addValue(costToAddEqui.get(i).get_materialName(), costToAddEqui.get(i).get_materialValue()))
                return false;
        }
        _costEquivalence = newCost.get_costEquivalence();
        return true;
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

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Material;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Loïc on 05/04/2014.
 */
public class Cost implements Serializable {

    static private final long serialVersionUID = 7;

    private ArrayList<Equivalence> _costEquivalence;            // The Equivalence between a cost and a material
    private int _totalMaterial;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Classic Cost constructor
     */
    public Cost() {
        _costEquivalence = new ArrayList<Equivalence>();
        _totalMaterial = 0;
    }

    /**
     * Cost copy constructor
     *
     * @param c
     */
    public Cost(Cost c) {
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

    public void set_costEquivalence(ArrayList<Equivalence> _costEquivalence) {
        this._costEquivalence = _costEquivalence;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Add a value to a cost.
     *
     * @param materialName
     * @param numberOfMaterials
     * @return
     */
    public boolean addValue(String materialName, int numberOfMaterials) {
        if (Material.getMaterial(materialName) != null) {
            if (numberOfMaterials <= 0) {
                return false;
            }
            Equivalence newEqui = new Equivalence(materialName, numberOfMaterials);
            if (numberOfMaterials > 256) {
                numberOfMaterials = 256;
                Messages.sendMessage("Cannot ask for more than 256 " + materialName, Messages.messageType.DEBUG, null);
            }
            _totalMaterial += numberOfMaterials;
            if (_totalMaterial > 2304) {
                Messages.sendMessage("Cannot add " + materialName + " or a unique player would not be able to pay this tribute due to inventory capacity.", Messages.messageType.ALERT, null);
                _totalMaterial -= numberOfMaterials;
                return false;
            }
            int i = 0;
            boolean materialFound = false;
            while (i < _costEquivalence.size() && !materialFound) {
                Equivalence currentEqui = _costEquivalence.get(i);
                if (currentEqui.is(newEqui)) {
                    currentEqui.add(numberOfMaterials);
                    materialFound = true;
                }
                i++;
            }
            if (!materialFound) {
                _costEquivalence.add(newEqui);
            }
            return true;
        }
        Messages.sendMessage(materialName + " not recognized ! Ignoring it...", Messages.messageType.ALERT, null);
        return false;
    }

    /**
     * Add a cost to another.
     *
     * @param costToAdd
     * @return
     */
    public boolean addCost(Cost costToAdd) {
        ArrayList<Equivalence> costToAddEqui = costToAdd.get_costEquivalence();
        Cost newCost = new Cost(this);
        for (Equivalence aCostToAddEqui : costToAddEqui) {
            if (!newCost.addValue(aCostToAddEqui.get_materialName(), aCostToAddEqui.get_materialValue()))
                return false;
        }
        _costEquivalence = newCost.get_costEquivalence();
        return true;
    }

    /**
     * Returns a string list containing all resources types in a cost, linked with their needed amount.
     *
     * @return
     */
    public String[] getResourceTypes() {
        ArrayList<String> resources = new ArrayList<String>();
        for (Equivalence a_costEquivalence : _costEquivalence) {
            if (a_costEquivalence.get_materialValue() != 0)
                resources.add("§6" + a_costEquivalence.get_materialName() + " : " + a_costEquivalence.get_materialValue() + ".");
        }
        String[] toReturn = new String[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            toReturn[i] = resources.get(i);
        }
        return toReturn;
    }

    /**
     * refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh() {
        // No settings to refresh
    }
}

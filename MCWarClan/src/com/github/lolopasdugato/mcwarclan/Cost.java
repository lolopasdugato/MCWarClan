package com.github.lolopasdugato.mcwarclan;

import org.bukkit.configuration.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loïc on 05/04/2014.
 */
public class Cost implements Serializable{

    static private final long serialVersionUID = 7;

    private ArrayList<Equivalence> _costEquivalence;            // The Equivalence between a cost and a material
    private transient Configuration _cfg;                       // The main configuration

    public Configuration get_cfg() { return _cfg; }

    public void set_cfg(Configuration _cfg) { this._cfg = _cfg; }

    public ArrayList<Equivalence> get_costEquivalence() { return _costEquivalence; }

    public void set_costEquivalence(ArrayList<Equivalence> _costEquivalence) { this._costEquivalence = _costEquivalence; }

    public Cost(Configuration cfg, String path){
        _cfg = cfg;
        List<String> matName = _cfg.getStringList("teamSettings.requiredMaterials");
        _costEquivalence = new ArrayList<Equivalence>();
        for(int i = 0; i < matName.size(); i++){
            _costEquivalence.add(new Equivalence(matName.get(i), _cfg.getInt(path + "." + matName.get(i))));
        }
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
}

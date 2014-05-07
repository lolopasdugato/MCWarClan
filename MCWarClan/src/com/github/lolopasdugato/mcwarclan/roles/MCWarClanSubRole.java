package com.github.lolopasdugato.mcwarclan.roles;

import com.github.lolopasdugato.mcwarclan.MCWarClanPlayer;

import java.util.ArrayList;

/**
 * Created by Seb on 05/05/2014.
 */
public abstract class MCWarClanSubRole extends McWarClanRole {

    protected ArrayList<McWarClanRole> _subroles;


    //////////////////////////////////////////////////////////////////////////////
    //------------------------------ Constructors --------------------------------
    //////////////////////////////////////////////////////////////////////////////


    protected MCWarClanSubRole(MCWarClanPlayer player) {
        super(player);
        _subroles = new ArrayList<McWarClanRole>();
    }


    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Functions ---------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the subroles associated with the current role. Have to be reimplemented in inherited classes
     */
    protected abstract void initSubRoles();


    /**
     * Method used to get all the commands provided by the role (this method is also searching throw subroles)
     *
     * @return An ArrayList of Strings containing all the commands the role can execute.
     */
    protected final ArrayList<String> getRights() {
        ArrayList<String> out = new ArrayList<String>();

        //Add current role rights
        System.out.println(_newRights);
        out.addAll(_newRights);

        //Add subroles rights
        for (McWarClanRole subRights : _subroles) {
            out.addAll(subRights.getRights());
        }
        return out;
    }
}

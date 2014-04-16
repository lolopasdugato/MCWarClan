package com.github.lolopasdugato.mcwarclan;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Seb on 15/04/2014.
 */
public class MCWarClanEvent extends Event {

    private static final HandlerList _handlers = new HandlerList();


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Events -----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public class ContestedBaseEvent extends MCWarClanEvent {

    }

}

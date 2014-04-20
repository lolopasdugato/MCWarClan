package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Loïc on 20/04/2014.
 */
public class MaximumTeamCapacityReachedException extends Exception {
    public MaximumTeamCapacityReachedException(String message){
        super(message);
    }

    public void sendDebugMessage(){
        Messages.sendMessage("MaximumTeamCapacityReachedException: " + getMessage(), Messages.messageType.DEBUG, null);
    }

    public void sendAlertMessage(){
        Messages.sendMessage("MaximumTeamCapacityReachedException: " + getMessage(), Messages.messageType.ALERT, null);
    }
}

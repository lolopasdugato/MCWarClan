package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Loïc on 20/04/2014.
 */
public class MaximumNumberOfTeamReachedException extends Exception {
    public MaximumNumberOfTeamReachedException(String message) {
        super(message);
    }

    public void sendDebugMessage() {
        Messages.sendMessage("MaximumNumberOfTeamReachedException: " + getMessage(), Messages.messageType.DEBUG, null);
    }
}

package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Loïc on 21/04/2014.
 */
public class NotEnoughSpaceException extends Exception {
    public NotEnoughSpaceException(String message) {
        super(message);
    }

    public void sendDebugMessage() {
        Messages.sendMessage("NotEnoughSpaceException: " + getMessage(), Messages.messageType.DEBUG, null);
    }
}

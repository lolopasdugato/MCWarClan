package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Loïc on 20/04/2014.
 */
public class InvalidNameException extends Exception {
    public InvalidNameException(String message) {
        super(message);
    }

    public void sendDebugMessage() {
        Messages.sendMessage("InvalidNameException: " + getMessage(), Messages.messageType.DEBUG, null);
    }
}

package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Loïc on 20/04/2014.
 */
public class InvalidColorException extends Exception {
    public InvalidColorException(String message) {
        super(message);
    }

    public void sendDebugMessage() {
        Messages.sendMessage("InvalidColorException: " + getMessage(), Messages.messageType.DEBUG, null);
    }
}

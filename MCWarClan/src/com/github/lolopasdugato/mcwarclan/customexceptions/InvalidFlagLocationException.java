package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Loïc on 21/04/2014.
 */
public class InvalidFlagLocationException extends Exception {
    public InvalidFlagLocationException(String message) {
        super(message);
    }

    public void sendDebugMessage() {
        Messages.sendMessage("InvalidFlagLocationException: " + getMessage(), Messages.messageType.DEBUG, null);
    }
}

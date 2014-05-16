package com.github.lolopasdugato.mcwarclan.customexceptions;

import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Seb on 01/05/2014.
 */
public class NoChiefException extends Throwable {

    public NoChiefException(String message) {
        super(message);
    }

    public void sendDebugMessage() {
        Messages.sendMessage("NoChiefException: " + getMessage(), Messages.messageType.DEBUG, null);
    }
}

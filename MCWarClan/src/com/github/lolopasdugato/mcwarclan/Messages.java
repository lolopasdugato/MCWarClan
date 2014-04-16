package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Created by Loïc on 14/04/2014.
 */
public class Messages {
    public static String _debugPrefix = "[DEBUG] ";
    public static String _MCWarClanPrefix = "§a[MCWarClan]§6 ";
    public static String _alertPrefix = "[ALERT] ";

    /**
     * @param message The message you wanted to send
     * @param type    The type of the message (see messageType)
     * @param locutor The person who will receive the message
     * @brief Send a message to a person using a special type and a special prefix.
     */
    public static void sendMessage(String message, messageType type, CommandSender locutor) {
        switch (type) {
            case DEBUG:
                if (Settings.debugMode)
                    System.out.println(_debugPrefix + message);
                break;
            case INGAME:
                if (locutor != null) {
                    locutor.sendMessage(_MCWarClanPrefix + message);
                } else if (Settings.debugMode) {
                    System.out.println(_debugPrefix + "Null player value. Player not specified !");
                }
                break;
            case CONSOLE:
                Bukkit.getServer().getConsoleSender().sendMessage(_MCWarClanPrefix + message);
                break;
            case ALERT:
                System.out.println(_alertPrefix + message);
                break;
            default:
                if (Settings.debugMode)
                    System.out.println(_debugPrefix + "Message type note recognized !");
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @param message The message you wanted to send (in an array of strings)
     * @param type    The type of the message (see messageType)
     * @param locutor The person who will receive the message
     * @brief Send a group of messages to a person using a special type and a special prefix.
     */
    public static void sendMessage(String[] message, messageType type, CommandSender locutor) {
        switch (type) {
            case DEBUG:
                if (Settings.debugMode) {
                    for (int i = 0; i < message.length; i++) {
                        System.out.println(_debugPrefix + message[i]);
                    }
                }
                break;
            case INGAME:
                if (locutor != null) {
                    locutor.sendMessage(message);
                } else if (Settings.debugMode) {
                    System.out.println(_debugPrefix + "Null player value. Player not specified !");
                }
                break;
            case CONSOLE:
                Bukkit.getServer().getConsoleSender().sendMessage(message);
                break;
            case ALERT:
                for (int i = 0; i < message.length; i++) {
                    System.out.println(_alertPrefix + message[i]);
                }
                break;
            default:
                if (Settings.debugMode)
                    System.out.println(_debugPrefix + "Message type note recognized !");
                break;
        }
    }

    /**
     * @param message  The message you wanted to send (in multiple strings)
     * @param type     The type of the message (see messageType)
     * @param locutors An array of person who will receive the message
     * @brief Send a message to a group using a special type and a special prefix.
     */
    public static void sendMessage(String[] message, messageType type, ArrayList<MCWarClanPlayer> locutors) {
        for (MCWarClanPlayer locutor : locutors) {
            sendMessage(message, type, Bukkit.getPlayer(locutor.get_name()));
        }
    }

    public static enum messageType {
        DEBUG, INGAME, CONSOLE, ALERT
    }

    /**
     * @brief Send a message to a group using a special type and a special prefix.
     * @param message The message you wanted to send
     * @param type The type of the message (see messageType)
     * @param locutors An array of person who will receive the message
     */
//    public static void sendMessage(String message, messageType type, ArrayList<MCWarClanPlayer> locutors){
//        for (MCWarClanPlayer locutor : locutors) {
//            sendMessage(message, type, Bukkit.getPlayer(locutor.get_name()));
//        }
//    }
}

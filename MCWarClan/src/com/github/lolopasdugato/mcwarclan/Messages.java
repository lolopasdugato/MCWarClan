package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Loïc on 14/04/2014.
 */
public class Messages {
    public static enum  messageType {
        DEBUG, INGAME, CONSOLE, ALERT
    }

    public static String _debugPrefix = "[DEBUG] ";
    public static String _MCWarClanPrefix = "§a[MCWarClan]§6 ";
    public static String _alertPrefix = "[ALERT] ";

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @brief Send a message using a special type and a special prefix.
     * @param message
     * @param type
     * @param locutor
     */
    public static void sendMessage(String message, messageType type, CommandSender locutor){
        switch (type){
            case DEBUG:
                if(Settings.debugMode)
                    System.out.println(_debugPrefix + message);
                break;
            case INGAME:
                if(locutor != null){
                    locutor.sendMessage(_MCWarClanPrefix + message);
                }
                else if (Settings.debugMode){
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

    /**
     * @brief Send a message using a special type and a special prefix.
     * @param message
     * @param type
     * @param locutor
     */
    public static void sendMessage(String[] message, messageType type, CommandSender locutor){
        switch (type){
            case DEBUG:
                if(Settings.debugMode){
                    for (int i = 0; i < message.length; i++){
                        System.out.println(_debugPrefix + message[i]);
                    }
                }
                break;
            case INGAME:
                if(locutor != null){
                    locutor.sendMessage(message);
                }
                else if (Settings.debugMode){
                    System.out.println(_debugPrefix + "Null player value. Player not specified !");
                }
                break;
            case CONSOLE:
                Bukkit.getServer().getConsoleSender().sendMessage(message);
                break;
            case ALERT:
                for (int i = 0; i < message.length; i++){
                    System.out.println(_alertPrefix + message[i]);
                }
                break;
            default:
                if (Settings.debugMode)
                    System.out.println(_debugPrefix + "Message type note recognized !");
                break;
        }
    }
}

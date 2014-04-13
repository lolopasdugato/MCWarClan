package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.Cost;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Loïc on 11/04/2014.
 */
public class Settings {

    private Configuration _cfg;

    public static int maxNumberOfTeam;
    public static int initialTeamSize;
    public static boolean friendlyFire;
    public static boolean seeInvisibleTeamMates;

    public static int initialRadius;
    public static int radiusHQBonus;
    public static int barbariansSpawnDistance;
    public static int baseMinHQDistanceToOthers;
    public static int baseMinDistanceToSpawn;

    public static int uncensoredItemsAmount;
    public static String classicWorldName;
    public static boolean debugMode;

    public static Cost BLUEteamJoiningTribute;
    public static Cost REDteamJoiningTribute;
    public static Cost DEFAULTteamJoiningTribute;
    public static Cost teamCreatingTribute;
    public static Cost baseInitialCost;

    public Settings(Configuration cfg){
        _cfg = cfg;
    }

    /**
     * @brief Use to load config.yml
     * @return if the loadConfig failed, return false.
     */
    public boolean loadConfig(){
        maxNumberOfTeam = _cfg.getInt("teamSettings.maxNumberOfTeam");
        initialTeamSize = _cfg.getInt("teamSettings.initialTeamSize");
        friendlyFire = _cfg.getBoolean("teamSettings.friendlyFire");
        seeInvisibleTeamMates = _cfg.getBoolean("teamSettings.transparentMates");

        initialRadius = _cfg.getInt("baseSettings.initialRadius");
        radiusHQBonus = _cfg.getInt("baseSettings.radiusHQBonus");
        barbariansSpawnDistance = _cfg.getInt("baseSettings.barbariansSpawnDistance");
        baseMinHQDistanceToOthers = _cfg.getInt("baseSettings.baseMinHQDistanceToOthers");
        baseMinDistanceToSpawn = _cfg.getInt("baseSettings.baseMinDistanceToSpawn");

        uncensoredItemsAmount = _cfg.getInt("otherSettings.uncensoredItemsAmount");
        classicWorldName = _cfg.getString("otherSettings.classicWorldName");
        debugMode = _cfg.getBoolean("otherSettings.debugMode");



        // Initializing blue entry cost
        BLUEteamJoiningTribute = fillCost(BLUEteamJoiningTribute, "teamSettings.teamJoiningTribute.BLUE");
        if(BLUEteamJoiningTribute == null)
            return false;

        // Initializing red entry cost
        REDteamJoiningTribute = fillCost(REDteamJoiningTribute, "teamSettings.teamJoiningTribute.RED");
        if(REDteamJoiningTribute == null)
            return false;

        // Initializing default team entry cost
        DEFAULTteamJoiningTribute = fillCost(DEFAULTteamJoiningTribute, "teamSettings.teamJoiningTribute.DEFAULT");
        if(DEFAULTteamJoiningTribute == null)
            return false;

        // Initializing team creation cost
        teamCreatingTribute = fillCost(teamCreatingTribute, "teamSettings.teamCreatingTribute");
        if(teamCreatingTribute == null)
            return false;

        // Initializing base cost
        baseInitialCost = fillCost(baseInitialCost, "baseSettings.baseInitialCost");
        if(baseInitialCost == null)
            return false;

        return true;
    }

    /**
     * @brief Used to fill a cost.
     * @param toFill, is the cost to fill.
     * @param path used to find values in config.yml.
     * @return Return a cost that has been filled up or null if error while reading file.
     */
    public Cost fillCost(Cost toFill, String path){
        toFill = new Cost();
        Set<String> keys = _cfg.getConfigurationSection(path).getKeys(false);
        Iterator it = keys.iterator();
        while (it.hasNext()){
            String matName = (String) it.next();
            if(!toFill.addValue(matName, _cfg.getInt(path + "." + matName)))
                return null;
        }
        return toFill;
    }
}

package com.github.lolopasdugato.mcwarclan;

import com.avaje.ebeaninternal.server.cluster.mcast.Message;
import org.bukkit.configuration.Configuration;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Loïc on 11/04/2014.
 */
public class Settings {

    public static int maxNumberOfTeam;
    public static int initialTeamSize;
    public static boolean friendlyFire;
    public static boolean seeInvisibleTeamMates;
    public static int initialRadius;
    public static int radiusHQBonus;
    public static int barbariansSpawnDistance;
    public static int baseMinHQDistanceToOthers;
    public static int uncensoredItemsAmount;
    public static String classicWorldName;
    public static boolean debugMode;
    public static boolean randomBarbarianSpawn;
    public static Cost BLUEteamJoiningTribute;
    public static Cost REDteamJoiningTribute;
    public static Cost DEFAULTteamJoiningTribute;
    public static Cost teamCreatingTribute;
    public static Cost baseInitialCost;
    public static boolean matesNeededIgnore;
    public static int matesNeededValue;
    public static boolean matesNeededIsPercentage;
    public static boolean obsidianBreakable;
    public static int secureBarbarianDistance;

    private Configuration _cfg;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Classic Settings constructor.
     *
     * @param cfg
     */
    public Settings(Configuration cfg) {
        _cfg = cfg;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Use to load config.yml
     *
     * @return if the loadConfig failed, return false.
     */
    public boolean loadConfig() {
        maxNumberOfTeam = secureValue(_cfg.getInt("teamSettings.maxNumberOfTeam"), 4, TeamContainer.MAXTEAMSIZE);
        initialTeamSize = secureValue(_cfg.getInt("teamSettings.initialTeamSize"), 2, -1);
        friendlyFire = _cfg.getBoolean("teamSettings.friendlyFire");
        seeInvisibleTeamMates = _cfg.getBoolean("teamSettings.transparentMates");
        matesNeededIgnore = _cfg.getBoolean("teamSettings.matesNeeded.ignore");
        matesNeededIsPercentage = _cfg.getBoolean("teamSettings.matesNeeded.percentage");
        if(matesNeededIsPercentage)
            matesNeededValue = secureValue(_cfg.getInt("teamSettings.matesNeeded.value"), 0, 100);
        else
            matesNeededValue = secureValue(_cfg.getInt("teamSettings.matesNeeded.value"), 0, -1);

        initialRadius = secureValue(_cfg.getInt("baseSettings.initialRadius"), 5, -1);
        radiusHQBonus = secureValue(_cfg.getInt("baseSettings.radiusHQBonus"), 5, -1);
        barbariansSpawnDistance = secureValue(_cfg.getInt("baseSettings.barbariansSpawnDistance"), 100, -1);
        baseMinHQDistanceToOthers = secureValue(_cfg.getInt("baseSettings.baseMinHQDistanceToOthers"), 0, -1);
        secureBarbarianDistance = secureValue(_cfg.getInt("baseSettings.secureBarbarianDistance"), 0, -1);

        uncensoredItemsAmount = secureValue(_cfg.getInt("otherSettings.uncensoredItemsAmount"), 1, -1);
        classicWorldName = _cfg.getString("otherSettings.classicWorldName");
        debugMode = _cfg.getBoolean("otherSettings.debugMode");
        randomBarbarianSpawn = _cfg.getBoolean("otherSettings.randomBarbarianSpawn");
        obsidianBreakable = _cfg.getBoolean("otherSettings.obsidianBreakable");


        // Initializing blue entry cost
        BLUEteamJoiningTribute = fillCost(BLUEteamJoiningTribute, "teamSettings.teamJoiningTribute.BLUE");
        if (BLUEteamJoiningTribute == null)
            return false;

        // Initializing red entry cost
        REDteamJoiningTribute = fillCost(REDteamJoiningTribute, "teamSettings.teamJoiningTribute.RED");
        if (REDteamJoiningTribute == null)
            return false;

        // Initializing default team entry cost
        DEFAULTteamJoiningTribute = fillCost(DEFAULTteamJoiningTribute, "teamSettings.teamJoiningTribute.DEFAULT");
        if (DEFAULTteamJoiningTribute == null)
            return false;

        // Initializing team creation cost
        teamCreatingTribute = fillCost(teamCreatingTribute, "teamSettings.teamCreatingTribute");
        if (teamCreatingTribute == null)
            return false;

        // Initializing base cost
        baseInitialCost = fillCost(baseInitialCost, "baseSettings.baseInitialCost");
        if (baseInitialCost == null)
            return false;

        return true;
    }

    /**
     * Used to fill a cost.
     *
     * @param toFill, is the cost to fill.
     * @param path    used to find values in config.yml.
     * @return Return a cost that has been filled up or null if error while reading file.
     */
    public Cost fillCost(Cost toFill, String path) {
        toFill = new Cost();
        Set<String> keys = _cfg.getConfigurationSection(path).getKeys(false);
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String matName = (String) it.next();
            int matNumber = secureValue(_cfg.getInt(path + "." + matName), 0, -1);
            if (!toFill.addValue(matName, matNumber))
                Messages.sendMessage("Material '" + matName + "' unrecognized in config.yml, ignoring it...", Messages.messageType.ALERT, null);
        }
        return toFill;
    }

    private int secureValue(int valueToSecure, int minValue, int maxValue){
        if (valueToSecure > maxValue && maxValue != -1)
            valueToSecure = maxValue;
        if (valueToSecure < minValue && minValue != -1)
            valueToSecure = minValue;
        return valueToSecure;
    }
}

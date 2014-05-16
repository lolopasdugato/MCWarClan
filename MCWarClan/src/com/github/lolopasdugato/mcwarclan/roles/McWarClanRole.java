package com.github.lolopasdugato.mcwarclan.roles;

import com.github.lolopasdugato.mcwarclan.*;
import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidColorException;
import com.github.lolopasdugato.mcwarclan.customexceptions.InvalidNameException;
import com.github.lolopasdugato.mcwarclan.customexceptions.MaximumNumberOfTeamReachedException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public abstract class McWarClanRole {


    protected final MCWarClanPlayer _player;
    protected RoleType _name;
    protected ArrayList<String> _newRights;


    protected McWarClanRole(MCWarClanPlayer player) {
        _player = player;
        _newRights = new ArrayList<String>();
    }

    public final RoleType get_name() {
        return _name;
    }

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------ Constructors --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    protected abstract void initNewRights();

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Universal function used when the player doesn't have the permission to execute an action.
     */
    protected final void NoRightsMessage() {
        Messages.sendMessage("Sorry, but as " + Messages.color(get_name().toString()) + " , " +
                "you cannot do this kind of job.", Messages.messageType.INGAME, _player.toOnlinePlayer());
    }


    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Functions ---------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Show rights provided by the role (i.e commands that the player can execute)
     */
    public final void showRights() {
        Messages.sendMessage(" As " + Messages.color(_name.toString()) + ", here are your rights : ",
                Messages.messageType.INGAME,
                _player.toOnlinePlayer());

        ArrayList<String> test = getRights();
        String[] out = test.toArray(new String[test.size()]);

        Messages.sendMessage(out, Messages.messageType.INGAME,
                _player.toOnlinePlayer());
    }

    /**
     * Method used to get all the commands provided by the role
     *
     * @return An ArrayList of Strings containing all the commands the role can execute.
     */
    protected ArrayList<String> getRights() {
        ArrayList<String> out = new ArrayList<String>();

        //Add current role rights
        out.addAll(_newRights);
        return out;
    }

    public boolean createHQ(Location baseLocation, String baseName) {
        NoRightsMessage();
        return false;
    }

    public boolean createBase(String name, int baseReferenceId, String direction) {
        NoRightsMessage();
        return false;
    }

    public boolean saveMoney(int amount) {
        NoRightsMessage();
        return false;
    }

    public boolean infoAllBases() {
        NoRightsMessage();
        return false;
    }

    public void infoBase(Base base) {
        NoRightsMessage();
    }

    public boolean infoCurrentBase() {
        NoRightsMessage();
        return false;
    }

    public boolean infoBaseMini(Base base) {
        NoRightsMessage();
        return false;
    }

    public Base canContestCurrentBase() {
        NoRightsMessage();
        return null;
    }

    public int checkAccount() {
        NoRightsMessage();
        return 0;
    }

    public boolean upgradeBase(Base toUpgrade) {
        NoRightsMessage();
        return false;
    }

    public boolean withdrawMoney(int amount) {
        NoRightsMessage();
        return false;
    }

    /**
     * Create a team for a specified player.
     *
     * @param t The team we wanted to create
     * @return True if the creation succeed, otherwise false.
     */
    public final boolean createTeam(Team t) {
        Player player = _player.toOnlinePlayer();
        try {
            TeamManager teamManager = _player.get_team().get_teamManager();
            teamManager.checkTeamValidity(t);
            if (!_player.canPay(teamManager.get_creatingCost())) {
                Messages.sendMessage("You need more resources to create this team. Here is an exhaustive list of all materials required: ", Messages.messageType.INGAME, player);
                Messages.sendMessage(teamManager.get_creatingCost().getResourceTypes(), Messages.messageType.INGAME, player);
                return false;
            }
            _player.payTribute(teamManager.get_creatingCost());
            if (!teamManager.addTeam(t)) {
                Messages.sendMessage("Cannot add the team for unknown reason...", Messages.messageType.DEBUG, null);
                Messages.sendMessage("Cannot add the team for unknown reason...", Messages.messageType.INGAME, player);
                return false;
            }
        } catch (InvalidColorException e) {
            e.sendDebugMessage();
            Messages.sendMessage("Sorry, but name or color is already taken by another team. Here is the colorname list: ", Messages.messageType.INGAME, player);
            Messages.sendMessage("§2GREEN, §eYELLOW, §0BLACK, §dMAGENTA, §5PURPLE, §3CYAN, §bLIGHTBLUE", Messages.messageType.INGAME, player);
            return false;
        } catch (InvalidNameException e) {
            e.sendDebugMessage();
            Messages.sendMessage("Sorry, this name is already taken !", Messages.messageType.INGAME, player);
            return false;
        } catch (MaximumNumberOfTeamReachedException e) {
            Messages.sendMessage("Sorry, maximum number of team reached !", Messages.messageType.INGAME, player);
            return false;
        }
        _player.get_team().get_teamManager().sendMessage(t.getColoredName() + " has been created by §a" + _name + "§6 " +
                "let's prepare" + " to surrender...");
        return true;
    }

    public enum RoleType {
        CHIEF, TREASURER, TEAM_MEMBER, BARBARIAN
    }


    //////////////////////////////////////////////////////////////////////////////
    //---------------------- Functions shared by all roles -----------------------
    //////////////////////////////////////////////////////////////////////////////

    //Use to grant access to methods/classes that have permission restrictions
    protected class Token {
    }
}

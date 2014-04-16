package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;

import java.io.Serializable;
import java.util.ArrayList;

public class Team extends Object implements Serializable {

    public static final int DEFAULTTEAMSIZE = 5;
    static private final long serialVersionUID = 2;
    public static int BARBARIAN_TEAM_ID;
    private static int _idMaster = 0;
    private Color _color;                            // Represent the team color.
    private String _name;                            // Represent the team name.
    // private ArrayList<String> _team; 				// Represent the players in the team
    private ArrayList<MCWarClanPlayer> _teamMembers;
    private int _teamSize;                            // Represent the maximum size of a team a it's creation. SHOULD NOT BE REFRESH
    private TeamContainer _teamContainer;            // The team container to which this team is linked to
    private ArrayList<Base> _bases;                    // Represent bases of a team
    private Cost _cost;                             // The cost to join a team
    private transient org.bukkit.scoreboard.Team _bukkitTeam;  // An instance of a bukkitTeam
    private int _id;


    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Classic Team constructor.
     *
     * @param color
     * @param name
     * @param teamSize
     * @param teamContainer
     */
    public Team(Color color, String name, int teamSize, TeamContainer teamContainer) {
        _idMaster++;
        _color = color;
        // _team = new ArrayList<String>();
        _teamMembers = new ArrayList<MCWarClanPlayer>();
        _bases = new ArrayList<Base>();

        //TODO never update (to be removed?)
        _teamSize = teamSize;
        //

        //TODO doubloon with _teamMembers
        _teamContainer = teamContainer;
        //

        _name = name;
        _id = _idMaster;
        if (_name.equals("Barbarians"))
            BARBARIAN_TEAM_ID = _id;
//        testBase();
        initCost();
        Messages.sendMessage("I am team " + _color.get_colorMark() + _name + " and my id is: " + _id + " (masterId:" + _idMaster + ")", Messages.messageType.DEBUG, null);
    }


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Color get_color() {
        return _color;
    }

    public void set_color(Color _color) {
        this._color = _color;
    }

    /*public ArrayList<String> get_team() {
        return _team;
    }*/

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public ArrayList<MCWarClanPlayer> get_teamMembers() {
        return _teamMembers;
    }

    public int get_teamSize() {
        return _teamSize;
    }

    /*public void set_team(ArrayList<String> _team) {
        this._team = _team;
    }*/
    public void set_teamSize(int _teamSize) {
        this._teamSize = _teamSize;
    }

    public TeamContainer get_teamContainer() {
        return _teamContainer;
    }

    public void set_teamContainer(TeamContainer _teamContainer) {
        this._teamContainer = _teamContainer;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Cost get_cost() {
        return _cost;
    }

    public void set_cost(Cost _cost) {
        this._cost = _cost;
    }

    public ArrayList<Base> get_bases() {
        return _bases;
    }

    public void set_bases(ArrayList<Base> _bases) {
        this._bases = _bases;
    }

    public org.bukkit.scoreboard.Team get_bukkitTeam() {
        return _bukkitTeam;
    }

    public void set_bukkitTeam(org.bukkit.scoreboard.Team _bukkitTeam) {
        this._bukkitTeam = _bukkitTeam;
    }

    public int get_id() {
        return _id;
    }


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the join cost, depending on the team.
     */
    public void initCost() {
        if (_color.get_colorName().equals("RED"))
            _cost = Settings.REDteamJoiningTribute;
        else if (_color.get_colorName().equals("BLUE"))
            _cost = Settings.BLUEteamJoiningTribute;
        else
            _cost = Settings.DEFAULTteamJoiningTribute;
    }

    /**
     * Add a player to this team.
     *
     * @param player
     * @return
     */
    public boolean addTeamMate(MCWarClanPlayer player) {
        // If the current team is one of those two, there is no limit
        if (_id == BARBARIAN_TEAM_ID) {
            _teamMembers.add(player);
            player.set_team(this);
            if (!_bukkitTeam.hasPlayer(player.toOfflinePlayer()))
                _bukkitTeam.addPlayer(player.toOfflinePlayer());
            player.reloadSpawn();
            return true;
        } else if (_teamMembers.size() >= _teamSize) {
            return false;
        } else {
            player.set_team(this);
            _teamMembers.add(player);
            if (!_bukkitTeam.hasPlayer(player.toOfflinePlayer()))
                _bukkitTeam.addPlayer(player.toOfflinePlayer());
            player.reloadSpawn();
        }
        return true;
    }

    /**
     * Delete a player from this team and the bukkit team
     *
     * @param player the player to delete
     * @return true if
     */
    public boolean deleteTeamMate(MCWarClanPlayer player) {
        if (_teamMembers.remove(player) && _bukkitTeam.removePlayer(player.toOfflinePlayer())) {
            player.set_team(null);
            return true;
        }
        return false;
    }

    /**
     * list all player's name in this team
     *
     * @return Return a list of player in the team.
     */
    public String[] playerList() {
        String[] mates = new String[_teamMembers.size() + 1];
        mates[0] = _color.get_colorMark() + _name + ":";
        for (int i = 0; i < _teamMembers.size(); i++) {
            mates[i + 1] = _teamMembers.get(i).get_name();
        }
        if (_teamMembers.size() == 0) {
            mates[0] = _color.get_colorMark() + _name + "§f is empty !";
        }
        return mates;
    }

    /**
     * Check if the team is enemy to the player team.
     *
     * @param playerTeam, the team of the player.
     * @return If true, this team is enemy to the player team.
     */
    public boolean isEnemyToTeam(Team playerTeam) {
        if (!_name.equals(playerTeam.get_name()))
            return true;
        return false;
    }

    //      WARNING     \\
    //      WARNING     \\
    //      WARNING     \\
    // Not tested !

    /**
     * Returns the team's HQ.
     *
     * @return
     */
    public Base getHQ() {
        for (int i = 0; i < _bases.size(); i++) {
            if (_bases.get(i).is_HQ())
                return _bases.get(i);
        }
        return null;
    }

    /**
     * refresh settings that should be reloaded if config.yml has been changed, or reload transient members that are not stored.
     */
    public void refresh() {
        _color.refresh();
        initCost();
        for (Base _base : _bases) {
            _base.refresh();
        }
        if (_id != 3) {
            _bukkitTeam.setAllowFriendlyFire(Settings.friendlyFire);
            _bukkitTeam.setCanSeeFriendlyInvisibles(Settings.seeInvisibleTeamMates);
        } else {
            _bukkitTeam.setAllowFriendlyFire(true);
            _bukkitTeam.setCanSeeFriendlyInvisibles(false);
        }
        _idMaster = _teamContainer.get_teamArray().size();
        if (_name.equals("Barbarians"))
            BARBARIAN_TEAM_ID = _id;
    }

    /**
     * Check if a location is in the team territory
     *
     * @param loc the location to check
     * @return Return the base if found, null else.
     */
    public Base isInTerritory(Location loc) {
        for (int i = 0; i < _bases.size(); i++) {
            if (_bases.get(i).isInBase(loc))
                return _bases.get(i);
        }
        return null;
    }

}

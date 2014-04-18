package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class TeamContainer implements Serializable {

    public static final int MAXTEAMSIZE = 10;    // There is only 15 color in the game, and some others for the server messages...
    static private final long serialVersionUID = 1;
    transient ScoreboardManager _manager;       // Scoreboard manager
    transient Scoreboard _scoreboard;           // The scoreboard
    transient Objective _killObjective;         // Shows kills on scoreboard
    transient Objective _deathObjective;
    private ArrayList<Team> _teamArray;            // Different teams
    private int _maxTeams;                        // Number of maximum teams
    private Cost _creatingCost;                 // The cost to create a team

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * TeamContainer classic constructor.
     *
     * @param maxTeams
     */
    public TeamContainer(int maxTeams) {
        _teamArray = new ArrayList<Team>();
        if (maxTeams > MAXTEAMSIZE || maxTeams < 3) {
            _maxTeams = MAXTEAMSIZE;
            Messages.sendMessage("Cannot have more than " + MAXTEAMSIZE + " teams, or less than 2 !", Messages.messageType.ALERT, null);
        } else
            _maxTeams = maxTeams;
        _creatingCost = Settings.teamCreatingTribute;
        _manager = Bukkit.getScoreboardManager();
        _scoreboard = _manager.getMainScoreboard();

        // A bit useless atm
        /*if(_scoreboard.getObjective("kills") == null) {
            _killObjective = _scoreboard.registerNewObjective("kills", "playerKillCount");
            _killObjective.setDisplayName("Kills:");
            _killObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        if(_scoreboard.getObjective("deaths") == null){
            _deathObjective = _scoreboard.registerNewObjective("deaths", "deathCount");
            _deathObjective.setDisplayName("Deaths:");
            _deathObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }*/
    }

    /**
     * TeamContainer copy constructor
     *
     * @param t the teamContainer in use to create the new object.
     */
    public TeamContainer(TeamContainer t) {
        _teamArray = t.get_teamArray();
        _maxTeams = t.get_maxTeams();
        _creatingCost = t.get_creatingCost();
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public ArrayList<Team> get_teamArray() {
        return _teamArray;
    }

    public void set_teamArray(ArrayList<Team> _teamArray) {
        this._teamArray = _teamArray;
    }

    public int get_maxTeams() {
        return _maxTeams;
    }

    public void set_maxTeams(int _maxTeams) {
        this._maxTeams = _maxTeams;
    }

    public Cost get_creatingCost() {
        return _creatingCost;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public void set_creatingCost(Cost _creatingCost) {
        this._creatingCost = _creatingCost;
    }

    public ScoreboardManager get_manager() {
        return _manager;
    }

    public void set_manager(ScoreboardManager _manager) {
        this._manager = _manager;
    }

    public Scoreboard get_scoreboard() {
        return _scoreboard;
    }

    public void set_scoreboard(Scoreboard _scoreboard) {
        this._scoreboard = _scoreboard;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * verify if the team could be added to the TeamContainer.
     *
     * @param t the team to add.
     * @return return true if the team is valid.
     */
    public boolean isTeamValid(Team t) {
        for (int i = 0; i < _teamArray.size(); i++) {
            // Check color
            if (t.get_color().get_colorName().equals(_teamArray.get(i).get_color().get_colorName()) || !t.get_color().is_validColor()) {
                return false;
            }
            // Check name
            if (t.get_name().toUpperCase().equals(_teamArray.get(i).get_name().toUpperCase()))
                return false;
        }
        return true;
    }

    /**
     * Add a team to the TeamContainer and the bukkitTeamContainer
     *
     * @param t the team to add.
     * @return true if the team has been successfully added.
     */
    public boolean addTeam(Team t) {
        if (_teamArray.size() < _maxTeams && isTeamValid(t)) {
            // Can only be done if no other teams has the same name.
            _teamArray.add(t);
            if (_scoreboard.getTeam(t.get_name()) == null)
                t.set_bukkitTeam(_scoreboard.registerNewTeam(t.get_name()));
            else
                Messages.sendMessage(t.get_name() + " already exist !", Messages.messageType.DEBUG, null);
            if (_scoreboard.getTeam(t.get_name()) == null) {
                Messages.sendMessage(t.get_name() + " cannot be added to the scoreboard !", Messages.messageType.DEBUG, null);
                return false;
            }
            Messages.sendMessage(t.get_bukkitTeam().getName() + " successfully added !", Messages.messageType.DEBUG, null);
            if (!t.get_name().equals("Barbarians")) {
                t.get_bukkitTeam().setAllowFriendlyFire(Settings.friendlyFire);
                t.get_bukkitTeam().setCanSeeFriendlyInvisibles(Settings.seeInvisibleTeamMates);
            } else {
                t.get_bukkitTeam().setAllowFriendlyFire(true);
                t.get_bukkitTeam().setCanSeeFriendlyInvisibles(false);
            }
            t.get_bukkitTeam().setPrefix(t.get_color().get_colorMark() + "[" + t.get_name().substring(0, 3) + "]§r");
            t.get_bukkitTeam().setDisplayName(t.get_color().get_colorMark() + t.get_name() + "§r");
            return true;
        }
        Messages.sendMessage("Error while adding " + t.get_name() + " !", Messages.messageType.DEBUG, null);
        return false;
    }

    /**
     * Delete a team from both teamContainer
     *
     * @param t the team to delete
     * @return if the removing action has worked, it returns true.
     */
    // WARNING: NOT TESTED
    public boolean deleteTeam(Team t) {
        t.get_bukkitTeam().unregister();
        if (!_teamArray.remove(t)) {
            _scoreboard.registerNewTeam(t.get_name());
            return false;
        }
        return true;
    }

    /**
     * Search a player through the different teams in the teamContainer.
     *
     * @param playerName the name of the player.
     * @return returns the player if it works, otherwise, it will return null.
     */
    public MCWarClanPlayer getPlayer(String playerName) {
        for (int i = 0; i < _teamArray.size(); i++) {
            for (int j = 0; j < _teamArray.get(i).get_teamMembers().size(); j++) {
                if (_teamArray.get(i).get_teamMembers().get(j).get_name().equals(playerName)) {
                    return _teamArray.get(i).get_teamMembers().get(j);
                }
            }
        }
        return null;
    }

    /**
     * @param uuid the unique ID defined for the player.
     * @return returns the player if it works, otherwise, it will return null.
     * Search a player through the different teams in the teamContainer.
     */
    public MCWarClanPlayer getPlayer(UUID uuid) {
        for (int i = 0; i < _teamArray.size(); i++) {
            for (int j = 0; j < _teamArray.get(i).get_teamMembers().size(); j++) {
                if (_teamArray.get(i).get_teamMembers().get(j).get_uuid().equals(uuid)) {
                    return _teamArray.get(i).get_teamMembers().get(j);
                }
            }
        }
        return null;
    }


    /**
     * Search a team using the team name.
     *
     * @param teamName the team name.
     * @return returns teh team if it has found something, otherwise, returns null.
     */
    public Team searchTeam(String teamName) {
        for (int i = 0; i < _teamArray.size(); i++) {
            if (_teamArray.get(i).get_name().toUpperCase().equals(teamName.toUpperCase()))
                return _teamArray.get(i);
        }
        return null;
    }

    /**
     * Search a team using the color.
     *
     * @param c the team color.
     * @return returns the team if it has found something, otherwise, returns null.
     */
    public Team searchTeam(Color c) {
        for (int i = 0; i < _teamArray.size(); i++) {
            if (_teamArray.get(i).get_color().get_colorName().equals(c.get_colorName()))
                return _teamArray.get(i);
        }
        return null;
    }

    /**
     * get all teams in this container.
     *
     * @return returns a string array.
     */
    public String[] teamsList() {
        String[] teams = new String[_teamArray.size()];
        for (int i = 0; i < _teamArray.size(); i++) {
            teams[i] = _teamArray.get(i).get_color().get_colorMark() + _teamArray.get(i).get_name();
        }
        return teams;
    }

    /**
     * Store a teamContainer into a file
     */
    public void serialize() {
        try {
            FileOutputStream fos = new FileOutputStream("plugins/MCWarClan/TeamContainer.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            try {
                oos.writeObject(this);
                oos.flush();
                Messages.sendMessage("TeamContainer has been serialized", Messages.messageType.DEBUG, null);
            } finally {
                try {
                    oos.close();
                } finally {
                    fos.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Read a teamContainer using a file.
     *
     * @return Returns the teamContainer created.
     */
    public TeamContainer deSerialize() {
        TeamContainer t = null;
        try {
            FileInputStream fis = new FileInputStream("plugins/MCWarClan/TeamContainer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                t = (TeamContainer) ois.readObject();
            } finally {
                try {
                    ois.close();
                } finally {
                    fis.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        if (t != null && Settings.debugMode) {
            Messages.sendMessage("TeamContainer has been deserialized", Messages.messageType.DEBUG, null);
        }
        return t;
    }

    /**
     * refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh() {
        Messages.sendMessage("Refreshing the teamContainer", Messages.messageType.DEBUG, null);
        _maxTeams = Settings.maxNumberOfTeam;
        _creatingCost = Settings.teamCreatingTribute;
        _creatingCost.refresh();
        _manager = Bukkit.getScoreboardManager();
        _scoreboard = _manager.getMainScoreboard();
        for (int i = 0; i < _teamArray.size(); i++) {
            _teamArray.get(i).set_bukkitTeam(_scoreboard.getTeam(_teamArray.get(i).get_name()));
            _teamArray.get(i).refresh();
        }
    }

    /**
     * get a team using it's ID.
     *
     * @param id
     * @return
     */
    public Team getTeam(int id) {
        for (int i = 0; i < _teamArray.size(); i++) {
            if (_teamArray.get(i).get_id() == Team.BARBARIAN_TEAM_ID)
                return _teamArray.get(i);
        }
        return null;
    }

    /**
     * Returns the base area where the location is if so, return null if no results matches.
     * @param loc
     * @return
     */
    public Base getBase(Location loc) {
        ArrayList<Team> teams = _teamArray;
        Base b;
        for (int i = 0; i < teams.size(); i++) {
            b = teams.get(i).getBase(loc);
            if (b != null) {
                return b;
            }
        }
        return null;
    }
}

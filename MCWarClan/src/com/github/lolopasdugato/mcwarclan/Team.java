package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.TeamContainer;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Team implements Serializable{
	
	static private final long serialVersionUID = 2;
	
	private Color _color; 							// Represent the team color.
	private String _name;							// Represent the team name.
	private ArrayList<String> _team; 				// Represent the players in the team
	private int _teamSize;							// Represent the maximum size of a team a it's creation. SHOULD NOT BE REFRESH
	private TeamContainer _teamContainer;	        // The team container to which this team is linked to
	private ArrayList<Base> _bases;					// Represent bases of a team
    private Cost _cost;                             // The cost to join a team
	
	public static final int DEFAULTTEAMSIZE = 5;

	public Color get_color() {
		return _color;
	}

	public void set_color(Color _color) {
		this._color = _color;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public ArrayList<String> get_team() {
		return _team;
	}

	public void set_team(ArrayList<String> _team) {
		this._team = _team;
	}

	public int get_teamSize() {
		return _teamSize;
	}

	public void set_teamSize(int _teamSize) {
		this._teamSize = _teamSize;
	}

	public TeamContainer get_teamContainer() {
		return _teamContainer;
	}

	public void set_teamContainer(TeamContainer _teamContainer) {
		this._teamContainer = _teamContainer;
	}

    public Cost get_cost() {  return _cost; }

    public void set_cost(Cost _cost) { this._cost = _cost; }

    public ArrayList<Base> get_bases() { return _bases; }

    public void set_bases(ArrayList<Base> _bases) {
        this._bases = _bases;
    }

    // Constructor
	public Team(Color color, String name, int teamSize, TeamContainer teamContainer){
		_color = color;
		_team = new ArrayList<String>();
        _bases = new ArrayList<Base>();
		_teamSize = teamSize;
		_teamContainer = teamContainer;
		_name = name;
        testBase();
        initCost();
	}

    public void testBase(){
        MCWarClanLocation newLoc = new MCWarClanLocation(Bukkit.getWorld("world").getSpawnLocation());
        if(_color.get_colorName().equals("BLUE") && newLoc != null){
            if(_bases.add(new Base(true, this, newLoc)))
                if(Settings.debugMode)
                    System.out.println("New base created !");
        }
    }

    public void initCost(){
        if(_color.get_colorName().equals("RED"))
            _cost = Settings.REDteamJoiningTribute;
        else if(_color.get_colorName().equals("BLUE"))
            _cost = Settings.BLUEteamJoiningTribute;
        else
            _cost = Settings.DEFAULTteamJoiningTribute;
    }
	
	// Add a player to this team. 
	public boolean addTeamMate(String p){
		// If the current team is one of those two, there is no limit
		if(_name.equals("Barbarians")){
			_team.add(p);
			return true;
		}
		else if(_team.size() >= _teamSize){
			return false;
		}
		else
			_team.add(p);
		return true;
	}
	
	// Delete a player
	public boolean deleteTeamMate(String p){
		for(int i = 0; i < _team.size(); i++){
			if(_team.get(i).equals(p)) {
				_team.remove(i);
				return true;
			}
		}
		return false;
	}
	
	// Return the list of player in this team.
	public String[] playerList(){
		String[] mates = new String[_team.size() + 1];
		mates[0] = _color.get_colorMark() + _name + ":";
		for(int i = 0; i < _team.size(); i++){
			mates[i + 1] = _team.get(i);
		}
		if(_team.size() == 0){
			mates[0] = _color.get_colorMark() + _name + "§f is empty !";
		}
		return mates;
	}

    /**
     * @brief Check if the team is enemy to the player team.
     * @param playerTeam, the team of the player.
     * @return If true, this team is enemy to the player team.
     */
    public boolean isEnemyToTeam(Team playerTeam){
        if(!_name.equals(playerTeam.get_name()))
            return true;
        return false;
    }


    //      WARNING     \\
    //      WARNING     \\
    //      WARNING     \\
    // Not tested !
    public Base getHQ(){
        for (int i = 0; i < _bases.size(); i++){
            if(_bases.get(i).is_HQ())
                return _bases.get(i);
        }
        return null;
    }

    /**
     * @brief refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        _color.refresh();
        initCost();
        for (Base _base : _bases) {
            _base.refresh();
        }
    }

    /**
     * @brief Check if a location is in the team territory
     * @param loc the location to check
     * @return if true, the location is in the team territory.
     */
    public boolean isInTerritory(Location loc){
        for(int i = 0; i < _bases.size(); i++){
            if(_bases.get(i).isInBase(loc))
                return true;
        }
        return false;
    }

	public boolean createBase(boolean HQ, int radius, Team team, Flag flag, Location loc){
		return false;
	}

}

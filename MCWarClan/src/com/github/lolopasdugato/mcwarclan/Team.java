package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;

import java.io.Serializable;
import java.util.ArrayList;

public class Team implements Serializable{

    public static final int DEFAULTTEAMSIZE = 5;
    static private final long serialVersionUID = 2;
	private Color _color; 							// Represent the team color.
	private String _name;							// Represent the team name.
	private ArrayList<String> _team; 				// Represent the players in the team
	private int _teamSize;							// Represent the maximum size of a team
	private TeamContainer _teamContainer;	        // The team container to which this team is linked to
	private ArrayList<Base> _bases;					// Represent bases of a team
    private Cost _cost;                             // The cost to join a team

    // Constructor
    public Team(Color color, String name, int teamSize, TeamContainer teamContainer) {
        _color = color;
        _team = new ArrayList<String>();
        _bases = new ArrayList<Base>();
        _teamSize = teamSize;
        _teamContainer = teamContainer;
        _name = name;
        // testBase();
        initCost();
    }

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
//
//    public void testBase(){
//        MCWarClanLocation newLoc = new MCWarClanLocation(Bukkit.getWorld("world").getSpawnLocation());
//        if(_color.get_colorName().equals("BLUE") && newLoc != null){
//            if(_bases.add(new Base(true, this, newLoc)))
//                System.out.println("New base created !");
//        }
//        System.out.println("newLoc is null! !!! !");
//    }

    public void initCost(){
        if(_color.get_colorName().equals("RED"))
            _cost = new Cost(_teamContainer.get_cfg(), "teamSettings.teamJoiningTribute.requiredMaterials", "teamSettings.teamJoiningTribute.RED");
        else if(_color.get_colorName().equals("BLUE"))
            _cost = new Cost(_teamContainer.get_cfg(), "teamSettings.teamJoiningTribute.requiredMaterials", "teamSettings.teamJoiningTribute.BLUE");
        else
            _cost = new Cost(_teamContainer.get_cfg(), "teamSettings.teamJoiningTribute.requiredMaterials", "teamSettings.teamJoiningTribute.DEFAULT");
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


    //      WARNING     \\
    //      WARNING     \\
    //      WARNING     \\
    // Not tested !
    public boolean isEnemyToTeam(Team friendlyTeam){
        if(!_name.equals("Barbarians") || !_name.equals(friendlyTeam.get_name()))
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
	
	public boolean createBase(boolean HQ, int radius, Team team, Flag flag, Location loc){
		return false;
	}

}

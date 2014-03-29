package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.MCWarClan;
import com.github.lolopasdugato.mcwarclan.TeamContainer;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Team {
	
	private Color _color; 					// Represent the team color.
	private String _name;					// Represent the team name.
	private ArrayList<OfflinePlayer> _team; // Represent the players in the team
	private int _teamSize;					// Represent the maximum size of a team
	private TeamContainer _teamContainer;	// 
	
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

	public ArrayList<OfflinePlayer> get_team() {
		return _team;
	}

	public void set_team(ArrayList<OfflinePlayer> _team) {
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
	
	// Constructor
	public Team(Color color, String name, int teamSize, TeamContainer teamContainer){
		_color = color;
		_team = new ArrayList<OfflinePlayer>();
		_teamSize = teamSize;
		_teamContainer = teamContainer;
		_name = name;
	}
	
	// Add a player to this team. 
	public boolean addTeamMate(OfflinePlayer p){
		// If the current team is one of those two, there is no limit
		if(_color.get_colorName().equals("RED") || _color.get_colorName().equals("BLUE")){
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
	public boolean deleteTeamMate(OfflinePlayer p){
		for(int i = 0; i < _team.size(); i++){
			if(_team.get(i).getName().equals(p.getName())) {
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
			mates[i + 1] = _team.get(i).getName();
		}
		if(_team.size() == 0){
			mates[0] = _color.get_colorMark() + _name + "§f is empty !";
		}
		return mates;
	}

}

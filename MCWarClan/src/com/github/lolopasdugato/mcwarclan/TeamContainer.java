package com.github.lolopasdugato.mcwarclan;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.lolopasdugato.mcwarclan.Team;

public class TeamContainer {
	
	private ArrayList<Team> _teamArray;		// Different teams
	private int _maxTeams;					// Number of maximum teams
	
	public static final int MAXTEAMSIZE = 11;	// There is only 15 color in the game, and some others for the server messages...
	
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
	
	public TeamContainer(int maxTeams) {
		_teamArray = new ArrayList<Team>();
		if(maxTeams > MAXTEAMSIZE || maxTeams < 2){
			_maxTeams = MAXTEAMSIZE;
			System.out.println("Cannot have more than " + MAXTEAMSIZE + " teams, or less than 2 !");
		}
		else 
			_maxTeams = maxTeams;
	}
	
	// Verify if a team could be added to the container
	public boolean isTeamValid(Team t){
		System.out.println("Checking team validity...");
		for(int i = 0; i < _teamArray.size(); i++){
			// Check color
			if(t.get_color().get_colorName().equals(_teamArray.get(i).get_color().get_colorName()) || !t.get_color().is_validColor()){
				return false;
			}
			// Check name
			if(t.get_name().toUpperCase().equals(_teamArray.get(i).get_name().toUpperCase()))
				return false;
		}
		System.out.println("... OK !");
		return true;
	}
	
	// Add a team to the container
	public boolean addTeam(Team t){
		if(_teamArray.size() <= _maxTeams && isTeamValid(t)){
			_teamArray.add(t);
			return true;
		}
		return false;
	}
	
	// Search the team of player P.
	public Team searchPlayerTeam(OfflinePlayer p){
		for(int i = 0; i < _teamArray.size(); i++){
			for(int j = 0; j < _teamArray.get(i).get_team().size(); j++){
				if(_teamArray.get(i).get_team().get(j).getName().equals(p.getName())){
					return _teamArray.get(i);
				}
			}
		}
		return null;
	}
	
	// Search a team using a team name.
	public Team searchTeam(String teamName){
		for(int i = 0; i < _teamArray.size(); i++){
			if(_teamArray.get(i).get_name().toUpperCase().equals(teamName.toUpperCase()))
				return _teamArray.get(i);
		}
		return null;
	}
	
	public Team searchTeam(Color c){
		for(int i = 0; i < _teamArray.size(); i++){
			if(_teamArray.get(i).get_color().get_colorName().equals(c.get_colorName()))
				return _teamArray.get(i);
		}
		return null;
	}
	
	// Returns a list of all teams.
	public String[] teamsList(){
		String[] teams = new String[_teamArray.size()];
		for(int i = 0; i < _teamArray.size(); i++){
			teams[i] = _teamArray.get(i).get_color().get_colorMark() + _teamArray.get(i).get_name();
		}
		return teams;
	}

}

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.configuration.Configuration;

import java.io.*;
import java.util.ArrayList;

public class TeamContainer implements Serializable {
	
	static private final long serialVersionUID = 1;
	
	private ArrayList<Team> _teamArray;			// Different teams
	private int _maxTeams;						// Number of maximum teams
    private Cost _creatingCost;                 // The cost to create a team
	
	public static final int MAXTEAMSIZE = 10;	// There is only 15 color in the game, and some others for the server messages...

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

    public Cost get_creatingCost() { return _creatingCost; }

    public void set_creatingCost(Cost _creatingCost) { this._creatingCost = _creatingCost; }

    public TeamContainer(int maxTeams) {
		_teamArray = new ArrayList<Team>();
		if(maxTeams > MAXTEAMSIZE || maxTeams < 3){
			_maxTeams = MAXTEAMSIZE;
			System.out.println("[ERROR] Cannot have more than " + MAXTEAMSIZE + " teams, or less than 2 !");
		}
		else 
			_maxTeams = maxTeams;
        _creatingCost = Settings.teamCreatingTribute;
	}

    /**
     * @brief TeamContainer copy constructor
     * @param t the teamContainer in use to create the new object.
     */
	public TeamContainer(TeamContainer t){
		_teamArray = t.get_teamArray();
		_maxTeams = t.get_maxTeams();
        _creatingCost = t.get_creatingCost();
	}

    /**
     * @brief verify if the team could be added to the TeamContainer.
     * @param t the team to add.
     * @return return true if the team is valid.
     */
	public boolean isTeamValid(Team t){
		for(int i = 0; i < _teamArray.size(); i++){
			// Check color
			if(t.get_color().get_colorName().equals(_teamArray.get(i).get_color().get_colorName()) || !t.get_color().is_validColor()){
				return false;
			}
			// Check name
			if(t.get_name().toUpperCase().equals(_teamArray.get(i).get_name().toUpperCase()))
				return false;
		}
		return true;
	}

    /**
     * @brief Add a team to the TeamContainer
     * @param t the team to add.
     * @return true if the team has been successfully added.
     */
	public boolean addTeam(Team t){
		if(_teamArray.size() < _maxTeams && isTeamValid(t)){
			_teamArray.add(t);
            if(Settings.debugMode)
			    System.out.println("Team successfully added.");
			return true;
		}
		return false;
	}

    // Remove a team from the team container.
    public boolean deleteTeam(Team t){
        return _teamArray.remove(t);
    }

    /**
     * @brief Search a player through the different teams in the teamContainer.
     * @param p the name of the player.
     * @return returns the team if it works, otherwise, it will return null.
     */
	public Team searchPlayerTeam(String p){
		for(int i = 0; i < _teamArray.size(); i++){
			for(int j = 0; j < _teamArray.get(i).get_team().size(); j++){
				if(_teamArray.get(i).get_team().get(j).equals(p)){
					return _teamArray.get(i);
				}
			}
		}
		return null;
	}

    /**
     * @brief Search a team using the team name.
     * @param teamName the team name.
     * @return returns teh team if it has found something, otherwise, returns null.
     */
	public Team searchTeam(String teamName){
		for(int i = 0; i < _teamArray.size(); i++){
			if(_teamArray.get(i).get_name().toUpperCase().equals(teamName.toUpperCase()))
				return _teamArray.get(i);
		}
		return null;
	}

    /**
     * @brief Search a team using the color.
     * @param c the team color.
     * @return returns the team if it has found something, otherwise, returns null.
     */
	public Team searchTeam(Color c){
		for(int i = 0; i < _teamArray.size(); i++){
			if(_teamArray.get(i).get_color().get_colorName().equals(c.get_colorName()))
				return _teamArray.get(i);
		}
		return null;
	}

    /**
     * @brief get all teams in this container.
     * @return returns a string array.
     */
	public String[] teamsList(){
		String[] teams = new String[_teamArray.size()];
		for(int i = 0; i < _teamArray.size(); i++){
			teams[i] = _teamArray.get(i).get_color().get_colorMark() + _teamArray.get(i).get_name();
		}
		return teams;
	}

    /**
     * @brief Store a teamContainer into a file
     */
	public void serialize(){
		try{
			FileOutputStream fos = new FileOutputStream("plugins/MCWarClan/TeamContainer.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			try{
				oos.writeObject(this);
				oos.flush();
                if(Settings.debugMode)
				    System.out.println("[DEBUG] TeamContainer has been serialized");
			}
			finally {
				try{
					oos.close();
				}
				finally{
					fos.close();
				}
			}
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

    /**
     * @brief Read a teamContainer using a file.
     * @return Returns the teamContainer created.
     */
	public TeamContainer deSerialize(){
		TeamContainer t = null;
		try {
			FileInputStream fis = new FileInputStream("plugins/MCWarClan/TeamContainer.ser");
			ObjectInputStream ois= new ObjectInputStream(fis);
			try {	
				t = (TeamContainer) ois.readObject(); 
			} finally {
				try {
					ois.close();
				} finally {
					fis.close();
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		if(t != null && Settings.debugMode) {
			System.out.println("[DEBUG] TeamContainer has been deserialized");
		}
		return t;
	}

    /**
     * @brief refresh settings that should be reloaded if config.yml has been changed.
     */
    public void refresh(){
        _maxTeams = Settings.maxNumberOfTeam;
        _creatingCost = Settings.teamCreatingTribute;
        _creatingCost.refresh();
        for(int i = 0; i < _teamArray.size(); i++){
            _teamArray.get(i).refresh();
        }
    }

    public Team getTeam(String name)
    {
        int i = 0;
        while(_teamArray.get(i).get_name() == name && i < _maxTeams) {
            i++;
        }
//        if(_teamArray.get(i).get_name() == name)
//            return _teamArray.get(i);
//        else
            ///FAIL
        return _teamArray.get(i);


    }
}

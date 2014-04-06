package com.github.lolopasdugato.mcwarclan;

import org.bukkit.configuration.Configuration;

import java.io.*;
import java.util.ArrayList;

public class TeamContainer implements Serializable {
	
	static private final long serialVersionUID = 1;
	
	private ArrayList<Team> _teamArray;			// Different teams
	private int _maxTeams;						// Number of maximum teams
    private transient Configuration _cfg;       // Plugin configuration
	
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

    public Configuration get_cfg() { return _cfg; }

    public void set_cfg(Configuration _cfg) {
        this._cfg = _cfg;
        // When initializing a new cfg,
        for(int i = 0; i < _teamArray.size(); i++){
            _teamArray.get(i).get_cost().set_cfg(_cfg);     // Initialize Costs cfg
            _teamArray.get(i).initCost();                   // Update Costs values
        }
    }

    public TeamContainer(int maxTeams, Configuration cfg) {
		_teamArray = new ArrayList<Team>();
        _cfg = cfg;
		if(maxTeams > MAXTEAMSIZE || maxTeams < 3){
			_maxTeams = MAXTEAMSIZE;
			System.out.println("Cannot have more than " + MAXTEAMSIZE + " teams, or less than 2 !");
		}
		else 
			_maxTeams = maxTeams;
	}
	
	public TeamContainer(TeamContainer t){
		_teamArray = t.get_teamArray();
		// _file = t.get_file();
		_maxTeams = t.get_maxTeams();
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
		if(_teamArray.size() < _maxTeams && isTeamValid(t)){
			_teamArray.add(t);
			System.out.println("Team successfully added.");
			return true;
		}
		return false;
	}
	
	// Search the team of player P.
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
	
	// Store a teamContainer into a file
	public void serialize(){
		try{
			FileOutputStream fos = new FileOutputStream("plugins/MCWarClan/TeamContainer.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			try{
				oos.writeObject(this);
				oos.flush();
				System.out.println("TeamContainer has been serialized");
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
	
	// Read a file to get a teamContainer
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
		if(t != null) {
			System.out.println("TeamContainer has been deserialized");
		}
		return t;
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

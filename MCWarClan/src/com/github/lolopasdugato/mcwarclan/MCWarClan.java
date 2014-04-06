// This plugin is made by Eldorabe
// You can join me by sending an e-mail at eldorabe@gmail.com
// Please contact me if you want to use this code.

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class MCWarClan extends JavaPlugin implements Listener {
	
	protected TeamContainer _tc;
	protected EventManager _em;

    public MCWarClan() {
        // TODO Auto-generated constructor stub
    }

    public TeamContainer get_tc() {
        return _tc;
    }

    public void set_tc(TeamContainer _tc) {
        this._tc = _tc;
    }

    public TeamContainer TeamContainerInit() {
        saveDefaultConfig();
        System.out.println("Number of teams: " + getConfig().getInt("teamSettings.maxNumberOfTeam"));
        TeamContainer tc = new TeamContainer(getConfig().getInt("teamSettings.maxNumberOfTeam"));
        if (new File("plugins/MCWarClan/TeamContainer.ser").exists()) {
            tc = tc.deSerialize();
        } else
            tc = null;

        if (tc == null) {
            System.out.println("File cannot be read !");
            tc = new TeamContainer(getConfig().getInt("teamSettings.maxNumberOfTeam"));
            tc.addTeam(new Team(new Color("RED"), "HellRangers", getConfig().getInt("teamSettings.teamSize"), tc));
            tc.addTeam(new Team(new Color("BLUE"), "ElvenSoldiers", getConfig().getInt("teamSettings.teamSize"), tc));
            tc.addTeam(new Team(new Color("LIGHTGREY"), "Barbarians", getConfig().getInt("teamSettings.teamSize"), tc));
        }

        return tc;
    }

    public void InitCommandExecutor() {
        getCommand("showteams").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("team").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("join").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("leave").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("assign").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("unassign").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("createteam").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        getCommand("createflag").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), getConfig()));
        return;
    }
	
	public void onEnable(){
		Logger log = Logger.getLogger("minecraft");
		log.info("Initialising teams...");
		_tc = TeamContainerInit();
		log.info("OK !");
		log.info("Registering events...");
		_em = new EventManager(_tc);
		getServer().getPluginManager().registerEvents(_em, this);
		log.info("OK !");
		log.info("Setting command Executor...");
		InitCommandExecutor();
		log.info("OK !");
		log.info("MCWarClan has been successfully launched !");
		return;
	}
	
	public void onDisable() {
		Logger log = Logger.getLogger("minecraft");
		log.info("Saving datas...");
		_tc.serialize();
		return;
	}

}

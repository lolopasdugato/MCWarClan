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
    protected Settings _cfg;
	
	public TeamContainer get_tc() {
		return _tc;
	}

	public void set_tc(TeamContainer _tc) {
		this._tc = _tc;
	}

    /**
     * @brief Init a team container.
     * @return return a teamcontainer initialized
     */
	public TeamContainer TeamContainerInit(){
        TeamContainer tc = new TeamContainer(Settings.maxNumberOfTeam);
		if(new File("plugins/MCWarClan/TeamContainer.ser").exists()){
			tc = tc.deSerialize();
            tc.refresh();
		}
		else
			tc = null;
		
		if(tc == null){
			System.out.println("File cannot be read !");
			tc = new TeamContainer(Settings.maxNumberOfTeam);
			tc.addTeam(new Team(new Color("RED"), "HellRangers", getConfig().getInt("teamSettings.initialTeamSize"), tc));
			tc.addTeam(new Team(new Color("BLUE"), "ElvenSoldiers", getConfig().getInt("teamSettings.initialTeamSize"), tc));
			tc.addTeam(new Team(new Color("LIGHTGREY"), "Barbarians", getConfig().getInt("teamSettings.initialTeamSize"), tc));
		}
		
		return tc;
	}

    /**
     * @brief Init all commands in the command executor
     */
	public void InitCommandExecutor(){
		getCommand("showteams").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
		getCommand("team").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
		getCommand("join").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
		getCommand("leave").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
		getCommand("assign").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
		getCommand("unassign").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
		getCommand("createteam").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
        getCommand("createflag").setExecutor(new MCWarClanCommandExecutor(_tc, getServer()));
    }

	public void onEnable(){
		Logger log = Logger.getLogger("minecraft");

        log.info("|-_MCWARCLAN_-| Loading config...");
        saveDefaultConfig();
        _cfg = new Settings(getConfig());
        if(!_cfg.loadConfig())
            log.info("|-_MCWARCLAN_-| Config load has failed !");
        else
            log.info("|-_MCWARCLAN_-| OK !");

		log.info("|-_MCWARCLAN_-| Initialising teams...");
		_tc = TeamContainerInit();
		log.info("|-_MCWARCLAN_-| OK !");

		log.info("|-_MCWARCLAN_-| Registering events...");
		_em = new EventManager(_tc);
		getServer().getPluginManager().registerEvents(_em, this);
		log.info("|-_MCWARCLAN_-| OK !");

		log.info("|-_MCWARCLAN_-| Setting command Executor...");
		InitCommandExecutor();
		log.info("|-_MCWARCLAN_-| OK !");

		log.info("|-_MCWARCLAN_-| MCWarClan has been successfully launched !");
	}
	
	public void onDisable() {
		Logger log = Logger.getLogger("minecraft");
		log.info("Saving datas...");
		_tc.serialize();
	}

}

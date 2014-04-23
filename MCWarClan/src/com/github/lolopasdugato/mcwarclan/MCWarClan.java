// This plugin is made by Eldorabe
// You can join me by sending an e-mail at eldorabe@gmail.com
// Please contact me if you want to use this code.

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.logging.Logger;

public class MCWarClan extends JavaPlugin implements Listener {

    public static String VERSION = "v0.1";
    public BukkitTask _tsk;
    protected TeamManager _tc;
    protected EventManager _em;
    protected Settings _cfg;
    protected boolean _hardStop;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Classic MCWarClan constructor.
     */
    public MCWarClan(){

    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////
	
	public TeamManager get_tc() { return _tc; }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

	public void set_tc(TeamManager _tc) { this._tc = _tc; }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     *  Init a team container.
     * @return return a teamcontainer initialized
     */
	public TeamManager TeamContainerInit(){
        TeamManager tc = new TeamManager(Settings.maxNumberOfTeam);
		if(new File("plugins/MCWarClan/TeamManager.ser").exists()){
			tc = tc.deSerialize();
            tc.refresh();
		}
		else {
            Messages.sendMessage("File cannot be read !", Messages.messageType.DEBUG, null);
            tc.addTeam(new Team(new Color("RED"), "HellRangers", getConfig().getInt("teamSettings.initialTeamSize"), tc));
            tc.addTeam(new Team(new Color("BLUE"), "ElvenSoldiers", getConfig().getInt("teamSettings.initialTeamSize"), tc));
            tc.addTeam(new Team(new Color("LIGHTGREY"), "Barbarians", getConfig().getInt("teamSettings.initialTeamSize"), tc));
        }
        return tc;
	}

    /**
     *  Init all commands in the command executor
     */
	public void InitCommandExecutor(){
		getCommand("showteams").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
		getCommand("team").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
		getCommand("join").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
		getCommand("leave").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
		getCommand("assign").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
		getCommand("unassign").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
		getCommand("createteam").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("createhq").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("createbase").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("baseinfo").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("contest").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("upgrade").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("savemoney").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
        getCommand("withdraw").setExecutor(new MCWarClanCommandExecutor(_tc, getServer(), this));
    }

    private void initRoutines() {
        //Create task and set time before recall
        //Info : 20 ticks ~= 1 second
//        _tsk = new MCWarClanRoutine.ContestedBaseRoutine(this).runTaskTimer(this, 0, 100);
    }

    /**
     *  Is called on server launch
     */
	public void onEnable(){
		Logger log = Logger.getLogger("minecraft");
        _hardStop = false;

        log.info("|-_MCWARCLAN_-| Loading config...");
        saveDefaultConfig();
        _cfg = new Settings(getConfig());
        if(!_cfg.loadConfig())
            log.info("|-_MCWARCLAN_-| Config load has failed !");
        else
            log.info("|-_MCWARCLAN_-| OK !");
        if((new File(Settings.classicWorldName + "/data/scoreboard.dat").exists() && !new File("plugins/MCWarClan/TeamManager.ser").exists())){
            log.severe("To prevent any error, MCWarClan will be disable. Please delete " + Settings.classicWorldName + "/data/scoreboard.dat and /plugins/MCWarClan/TeamManager.ser");
            _hardStop = true;
        }
        else{
            log.info("|-_MCWARCLAN_-| Initialising teams...");
            _tc = TeamContainerInit();
            if(_tc == null){
                log.info("|-_MCWARCLAN_-| ERROR while initializing teamcontainer !");
            }
            log.info("|-_MCWARCLAN_-| OK !");

            log.info("|-_MCWARCLAN_-| Registering events...");
            _em = new EventManager(_tc, this);
            getServer().getPluginManager().registerEvents(_em, this);
            log.info("|-_MCWARCLAN_-| OK !");

            log.info("|-_MCWARCLAN_-| Setting command Executor...");
            InitCommandExecutor();
            log.info("|-_MCWARCLAN_-| OK !");
            // Could be perfless but nee exact precision.
            // Moreover, very light task.
            BukkitTask tsk = new MCWarClanRoutine.CountDaysRoutine(_tc).runTaskTimer(this, 0, 1);
            log.info("|-_MCWARCLAN_-| MCWarClan has been successfully launched !");
        }


	}

    /**
     *  Is called on server close.
     */
	public void onDisable() {
		Logger log = Logger.getLogger("minecraft");
		log.info("Saving datas...");
        if(!_hardStop){
            _tc.serialize();
        }
	}

}

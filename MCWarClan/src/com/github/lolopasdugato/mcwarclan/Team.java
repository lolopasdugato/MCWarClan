package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.customexceptions.MaximumTeamCapacityReachedException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Team extends Object implements Serializable {

    public static final int DEFAULTTEAMSIZE = 5;
    static private final long serialVersionUID = 2;
    public static int BARBARIAN_TEAM_ID;
    private static int _idMaster = 0;
    private Color _color;                            // Represent the team color.
    private String _name;                            // Represent the team name.
    private ArrayList<MCWarClanPlayer> _teamMembers;
    private int _teamSize;                            // Represent the maximum size of a team a it's creation. SHOULD NOT BE REFRESH
    private TeamManager _teamManager;            // The team container to which this team is linked to
    private ArrayList<Base> _bases;                    // Represent bases of a team
    private Cost _cost;                             // The cost to join a team
    private Cost _baseCreationCost;
    private int _numberOfCostIncrease;
    private transient org.bukkit.scoreboard.Team _bukkitTeam;  // An instance of a bukkitTeam
    private int _id;
    private boolean _hasLost;
    private int _money;


    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Classic Team constructor.
     *
     * @param color
     * @param name
     * @param teamSize
     * @param teamManager
     */
    public Team(Color color, String name, int teamSize, TeamManager teamManager) {
        _idMaster++;
        _color = color;
        // _team = new ArrayList<String>();
        _teamMembers = new ArrayList<MCWarClanPlayer>();
        _bases = new ArrayList<Base>();

        _teamSize = teamSize;
        //

        _teamManager = teamManager;
        //
        _baseCreationCost = Settings.baseInitialCost;
        _name = name;
        _id = _idMaster;
        if (_name.equals("Barbarians"))
            BARBARIAN_TEAM_ID = _id;
//        testBase();
        initCost();
        Messages.sendMessage("I am team " + _name + " and my id is: " + _id + " (masterId:" + _idMaster + ")", Messages.messageType.DEBUG, null);
        _hasLost = false;
        _numberOfCostIncrease = 1;
        _money = 0;
    }

    public Team(Team t){
        _color = t.get_color();
        _name = t.get_name();
        _teamMembers = t.get_teamMembers();
        _teamSize = t.get_teamSize();
        _teamManager = t.get_teamManager();
        _bases = t.get_bases();
        _cost = t.get_cost();
        _bukkitTeam = t.get_bukkitTeam();
        _id = t.get_id();
    }


    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Getters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public Color get_color() {
        return _color;
    }

    public void set_color(Color _color) {
        this._color = _color;
    }

    public boolean hasLost() {
        return _hasLost;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public ArrayList<MCWarClanPlayer> get_teamMembers() {
        return _teamMembers;
    }

    public int get_teamSize() {
        return _teamSize;
    }

    public void set_teamSize(int _teamSize) {
        this._teamSize = _teamSize;
    }

    public TeamManager get_teamManager() {
        return _teamManager;
    }

    public void set_teamManager(TeamManager _teamManager) {
        this._teamManager = _teamManager;
    }

    public Cost get_baseCreationCost() { return _baseCreationCost; }

    public int get_money() {
        return _money;
    }

    //////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Setters ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

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

    public org.bukkit.scoreboard.Team get_bukkitTeam() {
        return _bukkitTeam;
    }

    public void set_bukkitTeam(org.bukkit.scoreboard.Team _bukkitTeam) {
        this._bukkitTeam = _bukkitTeam;
    }

    public int get_id() {
        return _id;
    }

    public void set_money(int _money) {
        this._money = _money;
    }

//////////////////////////////////////////////////////////////////////////////
    //--------------------------------- Functions --------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Return a colored name.
     * @return
     */
    public String getColoredName() {
        return _color.get_colorMark() + _name + "§6";
    }

    /**
     * Initialize the join cost, depending on the team.
     */
    public void initCost() {
        if (_color.get_colorName().equals("RED"))
            _cost = Settings.REDteamJoiningTribute;
        else if (_color.get_colorName().equals("BLUE"))
            _cost = Settings.BLUEteamJoiningTribute;
        else
            _cost = Settings.DEFAULTteamJoiningTribute;
    }

    /**
     * Add a player to this team.
     * @param player
     * @throws MaximumTeamCapacityReachedException
     * @return
     */
    public boolean addTeamMate(MCWarClanPlayer player) throws MaximumTeamCapacityReachedException {
        try {
            // If the current team is the barbarian one, there is no limit
            if (_id == BARBARIAN_TEAM_ID) {
                _teamMembers.add(player);
                player.set_team(this);
                if (!_bukkitTeam.hasPlayer(player.toOfflinePlayer()))
                    _bukkitTeam.addPlayer(player.toOfflinePlayer());
                player.reloadSpawn();

            // If the maximum team size is reached
            } else if (_teamMembers.size() >= _teamSize) {
                throw new MaximumTeamCapacityReachedException("In addTeamMate, cannot add" + player.get_name() + " to team " + _name + " maximum size reached(" + _teamSize + "/" + _teamMembers.size() + ")");

                // Normal case
            } else {
                player.set_team(this);
                _teamMembers.add(player);
                if (!_bukkitTeam.hasPlayer(player.toOfflinePlayer()))
                    _bukkitTeam.addPlayer(player.toOfflinePlayer());
                player.reloadSpawn();
            }
        // Thrown by _bukkitTeam.addPlayer() && _bukkitTeam.hasPlayer()
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Delete a player from this team and the bukkit team
     * @param player
     * @return
     */
    public boolean deleteTeamMate(MCWarClanPlayer player) {
        try {
            _teamMembers.remove(player);
            _bukkitTeam.removePlayer(player.toOfflinePlayer());
            player.set_team(null);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * list all player's name in this team
     * @return Return a list of player in the team.
     */
    public String[] playerList() {
        String[] mates = new String[_teamMembers.size() + 1];
        mates[0] = _color.get_colorMark() + _name + ":";
        for (int i = 0; i < _teamMembers.size(); i++) {
            mates[i + 1] = _teamMembers.get(i).get_name();
        }
        if (_teamMembers.size() == 0) {
            mates[0] = _color.get_colorMark() + _name + "§f is empty !";
        }
        return mates;
    }

    /**
     * Check if the team is enemy to the player team.
     * @param playerTeam, the team of the player.
     * @return If true, this team is enemy to the player team.
     */
    public boolean isEnemyToTeam(Team playerTeam) {
        return playerTeam.get_id() != _id;
    }

    /**
     * Returns the team's HQ.
     * @return
     */
    public Base getHQ() {
        for (int i = 0; i < _bases.size(); i++) {
            if (_bases.get(i).is_HQ())
                return _bases.get(i);
        }
        return null;
    }

    /**
     * refresh settings that should be reloaded if config.yml has been changed, or reload transient members that are not stored.
     */
    public void refresh() {
        _color.refresh();
        initCost();
        for (Base _base : _bases) {
            _base.refresh();
        }
        try {
            if (_id != 3) {
                _bukkitTeam.setAllowFriendlyFire(Settings.friendlyFire);
                _bukkitTeam.setCanSeeFriendlyInvisibles(Settings.seeInvisibleTeamMates);
            } else {
                _bukkitTeam.setAllowFriendlyFire(true);
                _bukkitTeam.setCanSeeFriendlyInvisibles(false);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (_idMaster < _id)
            _idMaster = _id;
        if (_name.equals("Barbarians"))
            BARBARIAN_TEAM_ID = _id;
    }

    /**
     * Check if a location is in the team territory
     * @param loc the location to check
     * @return Return the base if found, otherwise returns null.
     */
    public Base getBase(Location loc) {
        for (Base _base : _bases) {
            if (_base.isInBase(loc))
                return _base;
        }
        return null;
    }

    /**
     * Calculate if there is enough team mates connected to be attacked.
     * @return
     */
    public boolean enoughMatesToBeAttack(){
        if (!Settings.matesNeededIgnore){
            if (_teamMembers.size() == 0)
                return false;
            int playerOnline = 0;

            for (MCWarClanPlayer _teamMember : _teamMembers) {
                if (_teamMember.toOnlinePlayer() != null)
                    playerOnline++;
            }
            if (Settings.matesNeededIsPercentage){
                playerOnline = (int) (((double)playerOnline/(double)_teamMembers.size())*100.0);
            }
            return playerOnline >= Settings.matesNeededValue;
        }
        return true;
    }

    /**
     * Called when a team lost its HQ so that the team is deleted from every container. Every teamMembers will became barbarians.
     */
    public void loose(){
        _hasLost = true;
        Team teamToDelete = new Team(this);
        for (int i = 0; i < _bases.size(); i++){
            // Delete flag ?
            _bases.remove(_bases.get(i));
            // Do not delete the link between base and team to prevent nullPointerException if other battles are in progress at the same time.
        }
        _bases = null;  // Destroying the container.
        for (int i = 0; i < teamToDelete.get_teamMembers().size(); i++){
            teamToDelete.get_teamMembers().get(i).kick();
        }
        if(!_teamManager.deleteTeam(this)){
            Messages.sendMessage(_name + " cannot be deleted because of bukkitTeam Exception !", Messages.messageType.DEBUG, null);
        }
    }

    /**
     * Send a message to all team members.
     * @param message the message to send.
     */
    public void sendMessage(String message){
        for (MCWarClanPlayer _teamMember : _teamMembers) {
            Player toInform = _teamMember.toOnlinePlayer();
            if (toInform != null)
                Messages.sendMessage(message, Messages.messageType.INGAME, toInform);
        }
    }

    /**
     * Simply delete softly a base.
     * @param baseToDelete
     */
    public void deleteBase(Base baseToDelete){
        baseToDelete.get_flag().destroy();
        _bases.remove(baseToDelete);
    }

    /**
     * Change a base characteristics to make this base become a new base of this team.
     * @param baseToCapture
     */
    public void captureBase(Base baseToCapture){
        baseToCapture.isContested(false);
        baseToCapture.set_HQ(false);
        baseToCapture.set_radius(baseToCapture.get_initialRadius());
        baseToCapture.set_team(this);
        baseToCapture.set_level(1);
        baseToCapture.get_flag().forceDestroy();
        _bases.add(baseToCapture);
        baseToCapture.get_flag().get_pattern().set_woolColor(_color);
        baseToCapture.get_flag().get_pattern().generate();
        baseToCapture.createBaseBorder();
    }

    /**
     * Get a base using it's unique ID.
     * @param id
     * @return
     */
    public Base getBase(int id) {
        for (Base _base : _bases) {
            if (_base.get_id() == id)
                return _base;
        }
        return null;
    }

    /**
     * Increase the current cost to create a base.
     */
    public void increaseBaseCreationCost() {
        if (_numberOfCostIncrease % Settings.numberOfBaseForVariant == 0) {
            _baseCreationCost.addCost(Settings.baseVariantIncrease);
        }
        _baseCreationCost.addCost(Settings.baseCreationCostSystematicIncrease);
        _numberOfCostIncrease++;
    }

    /**
     * Pay a certain amount of money.
     * @param amount
     */
    public void pay(int amount) {
        _money -= amount;
    }

    /**
     * Drop a certain amount of Emeralds at a certain position.
     * @param amount
     * @param locToDropEmeralds
     */
    public void dropEmeralds(int amount, Location locToDropEmeralds) {
        if (amount > _money) {
            amount = _money;
        }
        pay(amount);
        ItemStack itemStack = new ItemStack(Material.EMERALD, 1);
        for (int i = 0; i < amount; i++) {
            double randomX = new Random().nextDouble() * (-2) + new Random().nextDouble() * (2);
            double randomZ = new Random().nextDouble() * (-2) + new Random().nextDouble() * (2);
            Location randlol = new Location(locToDropEmeralds.getWorld(), locToDropEmeralds.getX() + randomX, locToDropEmeralds.getY() + 3, locToDropEmeralds.getZ() + randomZ);
            locToDropEmeralds.getWorld().dropItem(randlol, itemStack);
        }
    }
}

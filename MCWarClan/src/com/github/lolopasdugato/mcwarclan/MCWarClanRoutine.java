package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created by Seb on 15/04/2014.
 */
public abstract class MCWarClanRoutine extends BukkitRunnable {

    private static JavaPlugin _plugin;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////


    protected MCWarClanRoutine() {
    }


    public static class ContestedBaseRoutine extends MCWarClanRoutine {

        private final Team _opponents;
        private final int _FlagCapTime;
        private Base _base;
        private int _flagCapCounter;
        private boolean _oneTimeMessage;

        public ContestedBaseRoutine(Base base, Team opponents) {
            _base = base;
            _opponents = opponents;
            _FlagCapTime = 6; // 1min = 12*5
            _flagCapCounter = _FlagCapTime;
            _oneTimeMessage = false;
        }

        @Override
        public void run() {
            //Add routine's logic here
            Messages.sendMessage("Calling routine every 5 seconds.", Messages.messageType.DEBUG, null);
            Team attackedTeam = _base.get_team();

            // If one of the two teams has lost the game, all battles with one or the other one should be stopped.
            if(attackedTeam.hasLost() || _opponents.hasLost()){
                this.cancel();
                return;
            }

            // So the base is contested, know we check if there are opponents remaining
            // before changing '_contested' state.

            int i = 0;
            boolean alive = false;
            while (i < _opponents.get_teamMembers().size() && !alive) {
                // If there is still an opponent in the contested base, the war continue.
                Base currentOppoBase = _opponents.get_teamMembers().get(i).getCurrentBase();
                if (currentOppoBase != null && currentOppoBase.isBase(_base)){
                    alive = true;
                    Messages.sendMessage(_opponents.get_teamMembers().get(i).get_name() + "(" + _opponents.get_teamMembers().get(i).get_team().get_name() + ") is still attacking " + _base.get_team().get_name() + " (" + _base.get_team().get_color().get_colorName() + ")."
                            , Messages.messageType.DEBUG, null);
                }

                i++;
            }

            //If there are not any enemy in the base
            Messages.sendMessage("Alive= " + alive, Messages.messageType.DEBUG, null);
            if(alive){
                if(_base.isFlagDestroyed(Settings.destroyFlagPercentage) && _flagCapCounter > 0){
                    _flagCapCounter--;
                    if(!_oneTimeMessage) {
                        attackedTeam.sendMessage(Messages.color(_base.get_name()) + " is being captured by " + _opponents.getColoredName() + " ! Defend it !");
                        _opponents.sendMessage("Capture process began ! Hold the position !");
                        _oneTimeMessage = true;
                    }
                }
                // If time elapsed
                else if (_base.isFlagDestroyed(Settings.destroyFlagPercentage) && _flagCapCounter <= 0){
                    _opponents.sendMessage("Well done ! You just captured " + Messages.color(_base.get_name()) + ", a " + attackedTeam.getColoredName() + " base !");
                    attackedTeam.sendMessage("You just lost " + Messages.color(_base.get_name()) + " against " + _opponents.getColoredName() + " kids... you could have done it in a better way...");
                    // If the attacked team lost their main base (HeadQuarter)
                    if(_base.is_HQ()){
                        TeamManager teamManager = _opponents.get_teamManager();
                        teamManager.sendMessage(attackedTeam.getColoredName() + " lost and will be destroyed ! They just lost their HeadQuarters like kids...");
                        attackedTeam.dropEmeralds(attackedTeam.get_money(), _base.getLocation());
                        attackedTeam.loose();
                        _opponents.captureBase(_base);
                    }
                    // If the attacked team los a simple base
                    else{
                        Messages.sendMessage("moneyloss: " + Settings.moneyloss + ", money that should be dropped: " + (int) ((Settings.moneyloss / 100.0) * attackedTeam.get_money()) + " " + attackedTeam.get_name() + " money: " + attackedTeam.get_money(),
                                Messages.messageType.DEBUG, null);
                        attackedTeam.dropEmeralds((int) ((Settings.moneyloss/100.0) * attackedTeam.get_money()), _base.getLocation());
                        attackedTeam.deleteBase(_base);
                        _opponents.captureBase(_base);
                    }
                    this.cancel();
                }
                // Flag has been rebuilt
                else if(_oneTimeMessage) {
                    attackedTeam.sendMessage("Well done ! You just rebuilt a part of your flag, capture process has been canceled !");
                    _opponents.sendMessage(attackedTeam.getColoredName() + " has just rebuild a part of their flag, capture process has been reset ! Don't let them do what they want !");
                    _flagCapCounter = _FlagCapTime;
                    _oneTimeMessage = false;
                }
            }
            else {
                //Send the messages to all the winning team
                attackedTeam.sendMessage(Messages.color(_base.get_name()) + " is not contested anymore ! " + _opponents.getColoredName() + " are defeated ! Well done !");
                _opponents.sendMessage("You lost the battle against " + attackedTeam.getColoredName() + " kids...");

                //Change to non contested status
                _base.isContested(false);


                //Kill the thread after the end of the war
                this.cancel();
            }
        }
    }

    public static class CountDaysRoutine extends MCWarClanRoutine {
        TeamManager _teamManager;

        protected CountDaysRoutine(TeamManager teamManager) {
            _teamManager = teamManager;
        }

        @Override
        public void run() {
            ArrayList<Team> teams = _teamManager.get_teamArray();
            long baseTime;

            long currentTime = Bukkit.getServer().getWorld(Settings.classicWorldName).getFullTime();

            Messages.sendMessage("Check if money earned at " + currentTime + ".", Messages.messageType.DEBUG, null);

            for (Team team : teams) {

                //We don't offer any resources to barbarians
                if (!team.isBarbarian()) {
                    baseTime = team.getLivingTime();
                    if ((currentTime - baseTime) > 24000) {
                        Messages.sendMessage("Team " + team.get_name() + " is now older.", Messages.messageType.DEBUG,
                                null);
                        //We add 1 to the previous age
                        team.incrementAge();

                        //Test if we have to add emerald to the team treasure
                        if (team.get_age() % (Settings.waitingTime) == 0) {
                            int emeraldToEarn = 0;
                            emeraldToEarn += Settings.emeraldPerTeamMember * team.getSize();
                            Messages.sendMessage("Daily count launched at " + currentTime + "!", Messages.messageType.DEBUG, null);
                            team.sendMessage("Today, your team earned " + Messages.color(emeraldToEarn ) + " emerald(s) !");
                            team.earnMoney(emeraldToEarn);
                        }
                    }
                }
            }
        }
    }
}

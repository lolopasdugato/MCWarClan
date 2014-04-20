package com.github.lolopasdugato.mcwarclan;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Seb on 15/04/2014.
 */
public abstract class MCWarClanRoutine extends BukkitRunnable {

    private static JavaPlugin _plugin;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////


    protected MCWarClanRoutine(JavaPlugin plugin) {
        _plugin = plugin;
    }


    public static class ContestedBaseRoutine extends MCWarClanRoutine {

        private final Team _opponents;
        private Base _base;
        private int _flagCapCounter;
        private final int _FlagCapTime;
        private boolean _oneTimeMessage;

        protected ContestedBaseRoutine(JavaPlugin plugin, Base base, Team opponents) {
            super(plugin);
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
                //TODO Change this trick (the comma thing)
                Base currentOppoBase = _opponents.get_teamMembers().get(i).getCurrentBase();
                if (currentOppoBase != null && currentOppoBase.get_id() == _base.get_id()){
                    alive = true;
                    Messages.sendMessage(_opponents.get_teamMembers().get(i).get_name() + "(" + _opponents.get_teamMembers().get(i).get_team().get_name() + ") is still attacking " + _base.get_team().get_name() + " (" + _base.get_team().get_color().get_colorName() + ")."
                            , Messages.messageType.DEBUG, null);
                }

                i++;
            }

            //If there are not any enemy in the base
            Messages.sendMessage("Alive= " + alive, Messages.messageType.DEBUG, null);
            if(alive){
                if(_base.get_flag().isDestroyed(Settings.destroyFlagPercentage) && _flagCapCounter > 0){
                    _flagCapCounter--;
                    if(!_oneTimeMessage) {
                        attackedTeam.sendMessage("One of your base is being captured by " + _opponents.get_color().get_colorMark() + _opponents.get_name() + "§6 ! Defend it !");
                        _opponents.sendMessage("Capture process began ! Hold the position !");
                        _oneTimeMessage = true;
                    }
                }
                // If time elapsed
                else if (_base.get_flag().isDestroyed(Settings.destroyFlagPercentage) && _flagCapCounter <= 0){
                    _opponents.sendMessage("Well done ! You just capture a " + attackedTeam.get_color().get_colorMark() + attackedTeam.get_name() + " §6base !");
                    attackedTeam.sendMessage("You just lost your base against " + _opponents.get_color().get_colorMark() + _opponents.get_name() + " §6kids... you could have done it in a better way...");
                    // If the attacked team lost their main base (HeadQuarter)
                    if(_base.is_HQ()){
                        TeamManager teamManager = _opponents.get_teamManager();
                        teamManager.sendMessage(attackedTeam.getColoredName() + " lost and will be destroyed ! They just lost their HeadQuarters like kids...");
                        attackedTeam.loose();
                        _opponents.captureBase(_base);
                    }
                    // If the attacked team los a simple base
                    else{
                        attackedTeam.deleteBase(_base);
                        _opponents.captureBase(_base);
                    }
                    this.cancel();
                }
                // Flag has been rebuilt
                else if(_oneTimeMessage) {
                    attackedTeam.sendMessage("Well done ! You just rebuilt a part of your flag, capture process has been canceled !");
                    _opponents.sendMessage(attackedTeam.get_color().get_colorMark() + attackedTeam.get_name() + " §6has just rebuild a part of their flag, capture process has been reset ! Don't let them do what they want !");
                    _flagCapCounter = _FlagCapTime;
                    _oneTimeMessage = false;
                }
            }
            else {
                //Send the messages to all the winning team
                attackedTeam.sendMessage("Your base is'nt contested anymore ! " + _opponents.get_color().get_colorMark() + _opponents.get_name() + " §6are defeated ! Well done !");
                _opponents.sendMessage("You lost the battle against " + attackedTeam.get_color().get_colorMark() + attackedTeam.get_name() + " §6kids...");

                //Change to non contested status
                _base.isContested(false);


                //Kill the thread after the end of the war
                this.cancel();
            }



            //1- It need to have enemies connected in order to allow capturing there base.
            //2- You enter in the base area with a weapon in your hand or inventory


            //2bisbis - Proximity detection (if the player can see an enemy or not)
            //2bis - You damage their area with TNT, attack an enemy or take damage from an enemy in is territory
            //for TNT --> tntPrimed.getSource (return Entity)

        }

        public void tntExploded() {

        }

    }
}

package com.github.lolopasdugato.mcwarclan;

import org.bukkit.entity.Player;
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

        protected ContestedBaseRoutine(JavaPlugin plugin, Base base, Team opponents) {
            super(plugin);
            _base = base;
            _opponents = opponents;
        }

        @Override
        public void run() {
            //Add routine's logic here
            Messages.sendMessage("Calling routine every 5 seconds.", Messages.messageType.DEBUG, null);

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
                // Check if the flag is destroyed (or partially ==> do a function in the pattern class)
                // If destroyed, send a one time message to the attacked team.
                // After a certain amount of time capture the flag.
                // DO NOT FORGET TO SET HQ "flag" (in th class) to false !!!!!!
            }
            else {
                //Send the messages to all the winning team
                Team attackedTeam = _base.get_team();
                for (i = 0; i < attackedTeam.get_teamMembers().size(); i++){
                    Player toInform = attackedTeam.get_teamMembers().get(i).toOnlinePlayer();
                    if(toInform != null)
                        Messages.sendMessage("Your base is'nt contested anymore !" + _opponents.get_color().get_colorMark() + _opponents.get_name() + " §6are defeated ! Well done !",
                                Messages.messageType.INGAME, toInform);
                }

                //Change to non contested status
                //TODO check if the value is really change outside the thread
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

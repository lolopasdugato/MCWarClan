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
                //TODO Change this trick
                Messages.sendMessage("i= " + i + "arraySize= " + _opponents.get_teamMembers().size(),
                        Messages.messageType.DEBUG, null);
                if (_opponents.get_teamMembers().get(i).isInEnemyTerritory() == _base)
                    alive = true;
                i++;
            }

            //If there are not any enemy in the base
            Messages.sendMessage("Alive= " + alive, Messages.messageType.DEBUG, null);
            if (!alive) {
                //Send the messages to all the winning team
                Messages.sendMessage(new String[]{"You defeated the " + _opponents.get_name() + "team", "Congratulations, " +
                                "" + _base.get_team().get_name() + "!"}, Messages.messageType.DEBUG,
                        _base.get_team().get_teamMembers()
                );

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

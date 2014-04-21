package com.github.lolopasdugato.mcwarclan;

import com.github.lolopasdugato.mcwarclan.customexceptions.MaximumTeamCapacityReachedException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class EventManager implements Listener {

    private TeamManager _tc;
    private JavaPlugin _plugin;

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    /**
     * @param tc
     *  Classic Event constructor.
     */
    public EventManager(TeamManager tc, JavaPlugin plugin) {
        _tc = tc;
        _plugin = plugin;
    }

    //////////////////////////////////////////////////////////////////////////////
    //---------------------------------- Events ----------------------------------
    //////////////////////////////////////////////////////////////////////////////

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent evt) {
        try{
            if (_tc == null) {
                Messages.sendMessage("The main team manager cannot be found. Please contact the creator to solve this problem.", Messages.messageType.ALERT, null);
                Bukkit.shutdown();
                return;
            }
            MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());
            if (player == null) {
                Messages.sendMessage("Welcome, this server is using MCWarClan " + MCWarClan.VERSION + ", have fun !", Messages.messageType.INGAME, evt.getPlayer());
                Team barbarians = _tc.getTeam(Team.BARBARIAN_TEAM_ID);
                player = new MCWarClanPlayer(evt.getPlayer(), barbarians);
                barbarians.addTeamMate(player);
                player.spawn();
                Messages.sendMessage(player.get_name() + " has spawn in x:" + player.get_spawn().get_x() + ", y:" + player.get_spawn().get_y() + ", z:" + player.get_spawn().get_z(), Messages.messageType.DEBUG, null);
            } else
                Messages.sendMessage("Welcome back, this server is using MCWarClan " + MCWarClan.VERSION + ", have fun !", Messages.messageType.INGAME, evt.getPlayer());
        } catch (MaximumTeamCapacityReachedException e){
            e.sendDebugMessage();
            Bukkit.shutdown();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerRespawn(PlayerRespawnEvent evt) {
        if (evt.getPlayer().getBedSpawnLocation() == null) {                // priority to bed spawn.
            MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());
            if (player.get_team().get_id() == Team.BARBARIAN_TEAM_ID && Settings.randomBarbarianSpawn
                    || (player.get_team().get_bases().size() != 0 && player.get_team().getHQ().isContested())) {
                player.reloadSpawn();
            }
            Location playerSpawn = player.get_spawn().getLocation();
            playerSpawn.getChunk().load();
            Messages.sendMessage(player.get_name() + " has spawn in x:" + player.get_spawn().get_x() + ", y:" + player.get_spawn().get_y() + ", z:" + player.get_spawn().get_z(), Messages.messageType.DEBUG, null);
            evt.setRespawnLocation(playerSpawn);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockPlace(BlockPlaceEvent evt) {
        MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());

        if (player != null) {
            Base currentBaseLocation = _tc.getBase(evt.getBlockPlaced().getLocation());
            if (currentBaseLocation != null && currentBaseLocation.get_team().isEnemyToTeam(player.get_team())) {   // Check if in enemy territory
                Team currentEnemyTeam = currentBaseLocation.get_team();
                if ((evt.getBlockPlaced().getType() == Material.TNT || evt.getBlockPlaced().getType() == Material.LADDER
                        || evt.getBlockPlaced().getType() == Material.LEVER)){    // Check if it's a special block
                    if(currentEnemyTeam.enoughMatesToBeAttack()) {    // Check if the team can be attack atm
                        if (evt.getPlayer().getItemInHand().getAmount() >= Settings.uncensoredItemsAmount && Settings.uncensoredItemsAmount > 1) {  // Check if the guy has the amount of item required to place the special block
                            evt.getPlayer().getItemInHand().setAmount(evt.getPlayer().getItemInHand().getAmount() - Settings.uncensoredItemsAmount - 1);
                            return;
                        } else if (Settings.uncensoredItemsAmount > 1){
                            // evt.getBlockPlaced().breakNaturally();
                            evt.setCancelled(true);
                            evt.getPlayer().updateInventory();
                            Messages.sendMessage("You need at least " + Settings.uncensoredItemsAmount + " " + evt.getBlockPlaced().getType().toString() + " to place it into an enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                        }
                        else
                            return;
                    }
                    else{
                        Messages.sendMessage("Sorry, but the " + currentEnemyTeam.get_color().get_colorMark() + currentEnemyTeam.get_name() + " §6cannot be attack, not enough member connected.",
                                Messages.messageType.INGAME, evt.getPlayer());
                        evt.setCancelled(true);
                        evt.getPlayer().updateInventory();
                        return;
                    }
                } else {
                    evt.setCancelled(true);
                    evt.getPlayer().updateInventory();
                    // evt.getBlockPlaced().breakNaturally();
                    Messages.sendMessage("You cannot place block (except TNT, Ladders, and Levers...) in an enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                }
            }
        } else
            Messages.sendMessage("Error, please ask an admin to be add to a team !", Messages.messageType.INGAME, evt.getPlayer());
    }

    @EventHandler
    private void onPlayerBucketEmpty(PlayerBucketEmptyEvent evt){
        Player player = evt.getPlayer();
        MCWarClanPlayer mcPlayer = _tc.getPlayer(player.getName());
        if(mcPlayer != null){
            Base currentBaseLocation = _tc.getBase(evt.getBlockClicked().getLocation());
            if(currentBaseLocation != null && currentBaseLocation.get_team().isEnemyToTeam(mcPlayer.get_team())){
                evt.setCancelled(true);
                player.updateInventory();
                Messages.sendMessage("You cannot place block (except TNT, Ladders, and Levers...) in an enemy base !", Messages.messageType.INGAME, player);
            }
        }
        else
            Messages.sendMessage("Error, please ask an admin to be add to a team !", Messages.messageType.INGAME, player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onBlockBreak(BlockBreakEvent evt) {
        if (evt.getBlock().getType() == Material.OBSIDIAN && Settings.obsidianBreakable){
            // Do nothing
            return;
        }
        else {
            MCWarClanPlayer player = _tc.getPlayer(evt.getPlayer().getName());
            if (player != null) {
                Base currentBase = _tc.getBase(evt.getBlock().getLocation());
                if (currentBase != null && currentBase.get_team().isEnemyToTeam(player.get_team())) {
                    Messages.sendMessage("You cannot break block in the enemy base !", Messages.messageType.INGAME, evt.getPlayer());
                    evt.setCancelled(true);
                }
            } else
                Messages.sendMessage("Error, please ask an admin to be add to a team !", Messages.messageType.INGAME, evt.getPlayer());
        }

    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent evt) {
        EntityDamageEvent.DamageCause dmg = evt.getCause();

        if ((EntityDamageEvent.DamageCause.ENTITY_ATTACK == dmg || EntityDamageEvent.DamageCause.BLOCK_EXPLOSION == dmg)
                || dmg == EntityDamageEvent.DamageCause.PROJECTILE) {
            Entity ent = evt.getEntity();
            if (ent.getType() == EntityType.PLAYER) {
                //Get the player
                MCWarClanPlayer player = _tc.getPlayer(ent.getUniqueId());
                //If the player is in enemy territory
                Base b = player.getCurrentBase();
                if (b != null && b.get_team().isEnemyToTeam(player.get_team()) && player.get_team().get_id() != Team.BARBARIAN_TEAM_ID) {
                    if(b.get_team().enoughMatesToBeAttack() && player.canContest()){

                        //A war may be beginning, so the base is now contested.

                        if (b.isContested()) {
                            return;
                            //We do nothing because another thread is already running.
                        }

                        //else we must create a thread
                        b.isContested(true);

                        // Inform the two teams !
                        Team attackedTeam = b.get_team();
                        Team attackingTeam = player.get_team();
                        attackedTeam.sendMessage(b.get_name() + " is attacked by " + attackingTeam.get_color().get_colorMark() + attackingTeam.get_name() + " !");
                        attackingTeam.sendMessage("Your team is attacking " + attackedTeam.get_color().get_colorMark() + attackedTeam.get_name() + " !");

                        //Create a new thread in order to check if the enemies are defeated
                        BukkitTask tks = new MCWarClanRoutine.ContestedBaseRoutine(_plugin, b,
                                attackingTeam).runTaskTimer(_plugin,
                                0, 100);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent evt){
        if(evt.getEntityType() == EntityType.CREEPER){
            evt.setCancelled(!Settings.allowCreeperDestroyFields);
        }
    }

}

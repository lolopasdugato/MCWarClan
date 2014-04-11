package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EventManager implements Listener {

    private TeamContainer _tc;
    private Configuration _cfg;

    public EventManager(TeamContainer tc, Configuration cfg) {
        _tc = tc;
        _cfg = cfg;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        evt.getPlayer().sendMessage("§a[MCWarClan]§6 " + "Welcome, this server is using MCWarClan v0.1, have fun !");
        if (_tc == null) {
            return;
        }
        // TODO: Check if the position is spawnable !
        if (_tc.searchPlayerTeam(evt.getPlayer().getName()) == null) {
            _tc.searchTeam("Barbarians").addTeamMate(evt.getPlayer().getName());
            Location barbarianSpawn = getBarbarianSpawn(evt.getPlayer(), _cfg.getInt("baseSettings.barbariansSpawnDistance"));
            barbarianSpawn.getChunk().load();
            evt.getPlayer().teleport(barbarianSpawn);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerRespawnEvent evt) {
        if (_tc.searchPlayerTeam(evt.getPlayer().getName()).get_name().equals("Barbarians")) {
            Location barbarianSpawn = getBarbarianSpawn(evt.getPlayer(), _cfg.getInt("baseSettings.barbariansSpawnDistance"));
            barbarianSpawn.getChunk().load();
            evt.setRespawnLocation(barbarianSpawn);
        }

    }

    // Return a barbarian spawn location
    public Location getBarbarianSpawn(Player p, int barbarianSpawnRadius) {
        if (barbarianSpawnRadius < 100) {
            System.out.println("Cannot have a barbarian spawn radius under 100 ! Setting spawn radius to 100...");
            barbarianSpawnRadius = 100;
        }
        Location worldSpawn = p.getWorld().getSpawnLocation();
        int signX = 1;
        int signZ = 1;
        if (new Random().nextBoolean())
            signX = -1;
        if (new Random().nextBoolean())
            signZ = -1;
        double randomX = (new Random().nextInt(barbarianSpawnRadius)) * signX;
        double randomZ = (new Random().nextInt(barbarianSpawnRadius)) * signZ;
        return new Location(p.getWorld(), (worldSpawn.getX() + randomX), worldSpawn.getY(), (worldSpawn.getZ() + randomZ));
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent evt) {
        Team userTeam = _tc.searchPlayerTeam(evt.getPlayer().getName());

        if (userTeam != null) {
            for (int i = 0; i < _tc.get_teamArray().size(); i++) {
                // If we're searching through an ennemy team
                if (_tc.get_teamArray().get(i).isEnemyToTeam(userTeam) && _tc.get_teamArray().get(i).get_bases() != null) {
                    for (int j = 0; j < _tc.get_teamArray().get(i).get_bases().size(); j++) {
                        // looking if we're in their base
                        if (_tc.get_teamArray().get(i).get_bases().get(j).isInBase(evt.getBlockPlaced().getLocation())) {
                            // Check if it's an uncensored item
                            if (evt.getBlockPlaced().getType() == Material.TNT || evt.getBlockPlaced().getType() == Material.LADDER || evt.getBlockPlaced().getType() == Material.LEVER) {
                                if (evt.getPlayer().getItemInHand().getAmount() >= _cfg.getInt("otherSettings.uncensoredItemsAmount")) {
                                    evt.getPlayer().getItemInHand().setAmount(evt.getPlayer().getItemInHand().getAmount() - _cfg.getInt("otherSettings.uncensoredItemsAmount") - 1);
                                    return;
                                } else {
                                    evt.getBlockPlaced().breakNaturally();
                                    //evt.setCancelled(true);
                                    evt.getPlayer().sendMessage("§a[MCWarClan]§6 You need at least " + _cfg.getInt("otherSettings.uncensoredItemsAmount") + " " + evt.getBlockPlaced().getType().toString() + " to place it into an enemy base ! (right click to get your item back)");
                                }
                            } else {
                                // evt.setCancelled(true);
                                evt.getBlockPlaced().breakNaturally();
                                evt.getPlayer().sendMessage("§a[MCWarClan]§6 You cannot place block (except TNT, Ladders, and Levers...) in an enemy base !");
                            }

                        }
                    }
                }
            }
        } else
            evt.getPlayer().sendMessage("§a[MCWarClan]§6 Error, please ask an admin to be add to a team !");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent evt) {
        Team userTeam = _tc.searchPlayerTeam(evt.getPlayer().getName());

        if (userTeam != null) {
            for (int i = 0; i < _tc.get_teamArray().size(); i++) {
                // If we're searching through an ennemy team
                if (_tc.get_teamArray().get(i).isEnemyToTeam(userTeam) && _tc.get_teamArray().get(i).get_bases() != null) {
                    for (int j = 0; j < _tc.get_teamArray().get(i).get_bases().size(); j++) {
                        // looking if we're in their base
                        if (_tc.get_teamArray().get(i).get_bases().get(j).isInBase(evt.getBlock().getLocation())) {
                            evt.getPlayer().sendMessage("§a[MCWarClan]§6 You cannot break block in the enemy base !");
                            evt.setCancelled(true);
                        }
                    }
                }
            }
        } else
            evt.getPlayer().sendMessage("§a[MCWarClan]§6 Error, please ask an admin to be add to a team !");
    }
}


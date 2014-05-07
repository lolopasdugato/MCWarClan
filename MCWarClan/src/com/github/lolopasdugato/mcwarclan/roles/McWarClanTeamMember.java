package com.github.lolopasdugato.mcwarclan.roles;


import com.github.lolopasdugato.mcwarclan.Base;
import com.github.lolopasdugato.mcwarclan.MCWarClanPlayer;
import com.github.lolopasdugato.mcwarclan.Messages;
import com.github.lolopasdugato.mcwarclan.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class McWarClanTeamMember extends McWarClanRole {

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public McWarClanTeamMember(MCWarClanPlayer player) {
        super(player);
        _name = RoleType.TEAM_MEMBER;
        initNewRights();

        //Add rights for this role;

    }

    @Override
    protected void initNewRights() {
        _newRights = new ArrayList<String>();
        _newRights.add("/showteams");
        _newRights.add("/team");
        _newRights.add("/join");
        _newRights.add("/createteam");

        _newRights.add("/leave");

        _newRights.add("/contest");
        _newRights.add("/baseinfo");
        _newRights.add("/saveemeralds");
    }

//    //////////////////////////////////////////////////////////////////////////////
//    //-------------------------------- Functions ---------------------------------
//    //////////////////////////////////////////////////////////////////////////////


    /**
     * Save Emeralds in the team treasure.
     *
     * @param amount
     * @return
     */
    @Override
    public boolean saveMoney(int amount) {
        Player player = _player.toOnlinePlayer();
        Team team = _player.get_team();
        if (amount < 0) {
            Messages.sendMessage(Messages.color(amount) + " is not a valid amount of emeralds to store in the team treasure.", Messages.messageType.INGAME, player);
            return false;
        } else {
            int amountToSave = amount;
            PlayerInventory playerInventory = player.getInventory();
            if (playerInventory.contains(Material.EMERALD, amount)) {
                do {
                    int index = playerInventory.first(Material.EMERALD);
                    ItemStack itemStack = playerInventory.getItem(index);
                    if (itemStack.getAmount() > amount) {
                        itemStack.setAmount(itemStack.getAmount() - amount);
                        playerInventory.setItem(index, itemStack);
                        amount = 0;
                    } else {
                        amount -= itemStack.getAmount();
                        itemStack.setAmount(0);
                        playerInventory.setItem(index, itemStack);
                    }
                } while (amount != 0);
                team.earnMoney(amountToSave);
                player.updateInventory();
            } else {
                Messages.sendMessage("Sorry, you do not have " + Messages.color(amount) + " emerald(s) in your inventory !", Messages.messageType.INGAME, player);
                return false;
            }
        }
        team.sendMessage(Messages.color(_player.get_name()) + " saved " + Messages.color(amount) + " " +
                "emerald(s) in the team treasure !");
        Messages.sendMessage("The treasure value is now " + Messages.color(team.get_money()) + ".", Messages.messageType.INGAME, player);
        return true;
    }

    @Override
    public boolean infoAllBases() {
        ArrayList<Base> playerBases = _player.get_team().get_bases();
        String[] info = new String[playerBases.size()];
        Player player = _player.toOnlinePlayer();
        for (int i = 0; i < playerBases.size(); i++) {
            info[i] = playerBases.get(i).getMinimalInfo();
        }
        if (info.length == 0) {
            Messages.sendMessage("Your team don't own any base at the moment !", Messages.messageType.INGAME, player);
            return false;
        } else {
            Messages.sendMessage("Here is a shortened list of details about your bases: ", Messages.messageType.INGAME, player);
            Messages.sendMessage(info, Messages.messageType.INGAME, player);
            return true;
        }
    }

    @Override
    public void infoBase(Base baseAsked) {
        Player player = _player.toOnlinePlayer();
        if (baseAsked != null) {
            Messages.sendMessage("Here are the detailed information about the base you're asking for :", Messages.messageType.INGAME, player);
            Messages.sendMessage(baseAsked.getInfo(), Messages.messageType.INGAME, player);
        } else
            Messages.sendMessage("You're not asking for a valid base.", Messages.messageType.INGAME, player);

    }

    @Override
    public boolean infoCurrentBase() {
        Player player = _player.toOnlinePlayer();
        Base base = _player.getCurrentBase();
        if (base != null && !base.isEnemyToPlayer(_player)) {
            Messages.sendMessage("Here are the detailed information about the base you're in at the moment: ", Messages.messageType.INGAME, player);
            Messages.sendMessage(base.getInfo(), Messages.messageType.INGAME, player);
            return true;
        } else {
            Messages.sendMessage("Currently, you're not in any allied base.", Messages.messageType.INGAME, player);
            return false;
        }
    }

    @Override
    public Base canContestCurrentBase() {
        Player player = _player.toOnlinePlayer();
        Base currentBase = _player.getCurrentBase();

        if (currentBase != null && !currentBase.isEnemyToPlayer(_player)) {
            Messages.sendMessage("Use your mind... you cannot attack your own team base !", Messages.messageType.INGAME, player);
        } else if (!_player.hasBases()) {
            Messages.sendMessage("You need at least one Head Quarter, then you could launch any battle you want !", Messages.messageType.INGAME, player);
        } else if (currentBase == null) {
            Messages.sendMessage("You're not in an enemy base, you cannot contest this territory.", Messages.messageType.INGAME, player);
        } else if (!currentBase.hasEnoughTeamMatesToBeAttacked()) {
            Messages.sendMessage("Not enough players connected in " + currentBase.getTeamColoredName() + " to attack them.", Messages.messageType.INGAME, player);
        } else if (currentBase.isContested()) {
            Messages.sendMessage("This team is already attacked by another team. But nothing forbid you to help one of these two...", Messages.messageType.INGAME, player);
        } else {
            return currentBase;
        }
        return null;
    }
}

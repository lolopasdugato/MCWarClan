package com.github.lolopasdugato.mcwarclan.roles;

import com.github.lolopasdugato.mcwarclan.MCWarClanPlayer;
import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Seb on 05/05/2014.
 */
public class MCWarClanTreasurer extends MCWarClanSubRole {

    //////////////////////////////////////////////////////////////////////////////
    //------------------------------- Constructors -------------------------------
    //////////////////////////////////////////////////////////////////////////////

    public MCWarClanTreasurer(MCWarClanPlayer player) {
        super(player);
        _name = RoleType.TREASURER;
    }

    @Override
    protected void initSubRoles() {
        _subroles.add(new McWarClanTeamMember(_player));
    }

    @Override
    protected void initNewRights() {
        _newRights.add("/treasure");
    }

    //////////////////////////////////////////////////////////////////////////////
    //-------------------------------- Functions ---------------------------------
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public int checkAccount() {
        int money = _player.get_team().get_money();
        Messages.sendMessage("Your team have for the moment §a" + _player.get_team().get_money()
                        + " emeralds§6 in your team " + "treasure.",
                Messages.messageType.INGAME,
                _player.toOnlinePlayer()
        );
        return money;
    }
}

package com.github.lolopasdugato.mcwarclan.roles;

import com.github.lolopasdugato.mcwarclan.Base;
import com.github.lolopasdugato.mcwarclan.MCWarClanPlayer;
import com.github.lolopasdugato.mcwarclan.Messages;

/**
 * Created by Seb on 05/05/2014.
 */
public class MCWarClanBarbarian extends McWarClanRole {

    public MCWarClanBarbarian(MCWarClanPlayer player) {
        super(player);
        _name = RoleType.BARBARIAN;
        initNewRights();
    }

    @Override
    protected void initNewRights() {
        _newRights.add("/showteams");
        _newRights.add("/team");
        _newRights.add("/join");
        _newRights.add("/createteam");
    }

    @Override
    public Base canContestCurrentBase() {
        Messages.sendMessage("You cannot contest a base when you are a member of " + _player.getColoredTeamName(),
                Messages.messageType.INGAME, _player.toOnlinePlayer());
        return null;
    }

    @Override
    public int checkAccount() {
        Messages.sendMessage(_player.getColoredTeamName() + " don't have any treasure. Their destiny is to be poor, " +
                        "forever.",
                Messages.messageType.INGAME,
                _player.toOnlinePlayer()
        );
        return 0;
    }
}

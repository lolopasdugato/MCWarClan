package com.github.lolopasdugato.mcwarclan;

import org.bukkit.Location;
import org.bukkit.Material;


/**
 * Created by Seb on 24/04/2014.
 */
public class BorderShower extends Pattern {
    public BorderShower(Location baseLoc) {

        MCWarClanLocation loc;
        Material mat;
        Location highestBlock = baseLoc.getWorld().getHighestBlockAt(baseLoc).getLocation();


        for (int i = 0; i < 3; i++) {
            loc = new MCWarClanLocation(highestBlock);
            loc.set_y(loc.get_y() + i);

            switch (i) {
                case 2:
                    mat = Material.TORCH;
                    break;
                default:
                    mat = Material.FENCE;
            }
            add(mat, loc);
        }
    }
}

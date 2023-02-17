package me.elixirrpg.net.dungeons.events;

import me.elixirrpg.net.dungeons.Dungeons;
import me.elixirrpg.net.dungeons.extras.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class whenPlayersKillsInDungeon implements Listener {


    @EventHandler
    public void letstrythis(EntityDeathEvent e){
        if(e.getEntity().getKiller() instanceof  Player){
            Entity vittima = e.getEntity();
            Player p = e.getEntity().getKiller();
            p.sendMessage("Its workin");
            Utils.sendCenteredHeadMessage(p, Utils.translate("&a&lIM NEW WITH THIS \n &fBUT IT SEEMS \n&c&lPRETTY FAR \n&a JOIR OUR WEBSITE LINK BELOW OR CONTACT US"));

        }
    }
}

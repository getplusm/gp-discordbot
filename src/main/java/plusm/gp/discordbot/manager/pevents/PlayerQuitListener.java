package plusm.gp.discordbot.manager.pevents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;
import plusm.gp.discordbot.utils.Request;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void quit(final PlayerQuitEvent e) {
        Request.denieAllRequests(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void ban(final PlayerQuitEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(DiscordBotManagerCore.getInstance(), () -> {
            if (e.getPlayer().isBanned() && DiscordData.hasConnectedAccount(e.getPlayer())) {
                final Player player = e.getPlayer();
                DiscordData.unlinkPlayerAccount(player);
            } else if (!e.getPlayer().isBanned() && DiscordData.hasConnectedAccount(e.getPlayer())) {
                final Player player = e.getPlayer();
                DiscordData.setOffline(player);
            }
        });
    }
}

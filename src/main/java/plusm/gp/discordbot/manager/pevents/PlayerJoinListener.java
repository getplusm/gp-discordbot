package plusm.gp.discordbot.manager.pevents;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;

public class PlayerJoinListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(final PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(DiscordBotManagerCore.getInstance(), () -> {
            if (DiscordData.hasConnectedAccount(e.getPlayer())) {
                DiscordData.setOnline(e.getPlayer());
            }
        });
    }

    @EventHandler
    public void preLogin(final PlayerLoginEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(DiscordBotManagerCore.getInstance(), () -> {
            if ((e.getResult().equals(PlayerLoginEvent.Result.KICK_BANNED) ||
                    e.getResult().equals(PlayerLoginEvent.Result.KICK_WHITELIST)) && DiscordData.hasConnectedAccount(e.getPlayer())) {
                DiscordData.unlinkPlayerAccount(e.getPlayer());
            }
        });
    }
}

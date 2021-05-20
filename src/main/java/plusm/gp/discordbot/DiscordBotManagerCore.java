package plusm.gp.discordbot;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import plusm.gp.discordbot.manager.commands.DiscordCommand;
import plusm.gp.discordbot.manager.events.BotEnableListener;
import plusm.gp.discordbot.manager.events.MemberGuildLeaveListener;
import plusm.gp.discordbot.manager.events.MemberMessageListener;
import plusm.gp.discordbot.manager.events.MemberNickChangeListener;
import plusm.gp.discordbot.manager.pevents.PlayerJoinListener;
import plusm.gp.discordbot.manager.pevents.PlayerQuitListener;
import plusm.gp.discordbot.manager.thread.UpdateTask;
import plusm.gp.discordbot.utils.DiscordData;
import plusm.gp.discordbot.utils.DiscordManagerConfiguration;
import plusm.gp.discordbot.utils.DiscordManagerSettings;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiscordBotManagerCore extends JavaPlugin {
    private static DiscordBotManagerCore instance;
    private DiscordManagerSettings settings;
    private JDA bot;
    private UpdateTask task;

    public void onEnable() {
        DiscordBotManagerCore.instance = this;
        if (!this.registerConfig()) {
            return;
        }
        DiscordData.load();
        this.registerBot();
        this.registerListeners();
        this.registerCommands();
        this.task = new UpdateTask();
        new Thread(this.task).start();
    }

    public void onDisable() {
        DiscordData.save();
        if (this.task != null) {
            this.task.disable();
        }
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!DiscordData.hasConnectedAccount(player)) {
                continue;
            }
            DiscordData.setOffline(player);
        }
        if (this.bot != null) {
            final Guild guild = this.bot.getGuildById(getInstance().getPluginSettings().getServerID());
            if (guild != null) {
                final TextChannel channel = guild.getTextChannelById(getInstance().getPluginSettings().getConfirmChannelID());
                if (channel != null) {
                    channel.getManager().putPermissionOverride(guild.getRolesByName("@everyone", false).get(0),
                            new ArrayList<>(), Collections.singletonList(Permission.VIEW_CHANNEL)).complete();
                    try {
                        final List<Message> messages = Lists.newArrayList();
                        messages.addAll(channel.getHistoryBefore(channel.getLatestMessageId(), 50).complete().getRetrievedHistory());
                        if (messages.size() > 1) {
                            channel.deleteMessages(messages).complete();
                        } else if (messages.size() == 1) {
                            channel.deleteMessageById(messages.get(0).getId()).complete();
                        }
                        channel.deleteMessageById(channel.getLatestMessageId()).complete();
                    } catch (IllegalStateException ignored) {
                    }
                }
            }
            this.bot.shutdownNow();
        }
    }

    public File getDiscordDataFile() {
        return new File(this.getDataFolder() + File.separator + "discord.yml");
    }

    public File getConfigFile() {
        return new File(this.getDataFolder() + File.separator + "config.yml");
    }

    public DiscordManagerSettings getPluginSettings() {
        return this.settings;
    }

    public JDA getBot() {
        return this.bot;
    }

    private boolean registerConfig() {
        this.settings = new DiscordManagerSettings();
        try {
            DiscordManagerConfiguration.checkConfig();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        if (this.settings.getBotToken().equals("")) {
            this.getLogger().warning("Токен дискорд бота не найден. Отключаю плагин...");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        if (this.settings.getServerID().equals("")) {
            this.getLogger().warning("ID сервер дискорд не найден. Отключаю плагин...");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        if (this.settings.getConfirmRoleID().equals("")) {
            this.getLogger().warning("ID роли каналов не найден. Отключаю плагин...");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private void registerBot() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                DiscordBotManagerCore.this.bot = JDABuilder.createDefault(DiscordBotManagerCore.this.settings.getBotToken()).setChunkingFilter(ChunkingFilter.ALL).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS).build();
                DiscordBotManagerCore.this.bot.addEventListener(new BotEnableListener());
                DiscordBotManagerCore.this.bot.addEventListener(new MemberMessageListener());
                DiscordBotManagerCore.this.bot.addEventListener(new MemberGuildLeaveListener());
                DiscordBotManagerCore.this.bot.addEventListener(new MemberGuildLeaveListener());
                DiscordBotManagerCore.this.bot.addEventListener(new MemberNickChangeListener());
            } catch (LoginException e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(DiscordBotManagerCore.getInstance());
            }
        });
    }

    public void registerListeners() {
        final PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerQuitListener(), this);
        manager.registerEvents(new PlayerJoinListener(), this);
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new DiscordCommand());
        Objects.requireNonNull(this.getCommand("discord")).setTabCompleter(new DiscordCommand());
    }

    public static DiscordBotManagerCore getInstance() {
        return DiscordBotManagerCore.instance;
    }
}

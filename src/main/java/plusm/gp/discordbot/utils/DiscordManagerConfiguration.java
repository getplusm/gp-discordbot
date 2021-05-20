package plusm.gp.discordbot.utils;

import java.awt.*;
import org.bukkit.configuration.file.*;
import plusm.gp.discordbot.DiscordBotManagerCore;

import java.io.*;

public class DiscordManagerConfiguration
{
    public static void checkConfig() throws IOException {
        if (!DiscordBotManagerCore.getInstance().getDataFolder().exists()) {
            DiscordBotManagerCore.getInstance().getDataFolder().mkdir();
        }
        final File file = DiscordBotManagerCore.getInstance().getConfigFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        final FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        final DiscordManagerSettings settings = DiscordBotManagerCore.getInstance().getPluginSettings();
        if (!config.contains("bot.token")) {
            config.set("bot.token", (Object)"");
        }
        settings.setBotToken(config.getString("bot.token"));
        if (!config.contains("IDs.server")) {
            config.set("IDs.server", (Object)"");
        }
        settings.setServerID(config.getString("IDs.server"));
        if (!config.contains("IDs.confirmChannel")) {
            config.set("IDs.confirmChannel", (Object)"");
        }
        settings.setConfirmChannelID(config.getString("IDs.confirmChannel"));
        if (!config.contains("IDs.confirmRole")) {
            config.set("IDs.confirmRole", (Object)"");
        }
        settings.setConfirmRoleID(config.getString("IDs.confirmRole"));
        if (!config.contains("IDs.onlineRole")) {
            config.set("IDs.onlineRole", (Object)"");
        }
        settings.setOnlineRoleID(config.getString("IDs.onlineRole"));
        if (!config.contains("message.title")) {
            config.set("message.title", (Object)"Инструкция по подключению дискорд аккаунта:");
        }
        settings.setMessageTitle(config.getString("message.title"));
        if (!config.contains("message.description")) {
            config.set("message.description", (Object)"1. Зайдите на сервер\n2. Используйте команду: `/discord connect`\n3. Нажмите на код в чате, чтобы скопировать его\n3. Отправьте код, что вы скопировали, в этот канал");
        }
        settings.setMessageDescription(config.getString("message.description"));
        if (!config.contains("message.color.red")) {
            config.set("message.color.red", (Object)48);
        }
        if (!config.contains("message.color.green")) {
            config.set("message.color.green", (Object)59);
        }
        if (!config.contains("message.color.blue")) {
            config.set("message.color.blue", (Object)90);
        }
        settings.setColor(new Color(config.getInt("message.color.red"), config.getInt("message.color.green"), config.getInt("message.color.blue")));
        config.save(file);
    }
}
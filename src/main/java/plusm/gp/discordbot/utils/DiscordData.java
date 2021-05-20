package plusm.gp.discordbot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.playerdatamanager.utils.PlayerData;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordData {
    public static Map<UUID, String> connectedAccounts = new ConcurrentHashMap<>();

    public static boolean hasConnectedAccount(final Player player) {
        return hasConnectedAccount(player.getUniqueId());
    }

    public static boolean hasConnectedAccount(final UUID uuid) {
        return DiscordData.connectedAccounts.containsKey(uuid);
    }

    public static boolean hasConnectedAccount(final String ID) {
        return DiscordData.connectedAccounts.containsValue(ID);
    }

    public static String getPlayerAccount(final Player player) {
        return getPlayerAccount(player.getUniqueId());
    }

    public static String getPlayerAccount(final UUID uuid) {
        return DiscordData.connectedAccounts.get(uuid);
    }

    public static UUID getAccountOwner(final String ID) {
        for (final UUID uuid : DiscordData.connectedAccounts.keySet()) {
            if (!DiscordData.connectedAccounts.get(uuid).equals(ID)) {
                continue;
            }
            return uuid;
        }
        return null;
    }

    public static void unlinkPlayerAccount(final Player player) {
        unlinkPlayerAccount(player.getUniqueId());
    }

    public static void unlinkPlayerAccount(final UUID uuid) {
        final String ID = DiscordData.connectedAccounts.get(uuid);
        DiscordData.connectedAccounts.remove(uuid);
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID());
        if (guild == null) {
            return;
        }
        final Member member = guild.getMemberById(ID);
        if (member == null) {
            return;
        }
        final Role confirmRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getConfirmRoleID());
        final Role onlineRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getOnlineRoleID());
        if (confirmRole != null && member.getRoles().contains(confirmRole)) {
            guild.removeRoleFromMember(member, confirmRole).complete();
        }
        if (onlineRole != null && member.getRoles().contains(onlineRole)) {
            guild.removeRoleFromMember(member, onlineRole).complete();
        }
    }

    public static void unlinkPlayerAccount(final String ID) {
        DiscordData.connectedAccounts.remove(getAccountOwner(ID));
    }

    public static void linkPlayerAccount(final Player player, final String ID) {
        linkPlayerAccount(player.getUniqueId(), ID);
    }

    public static void linkPlayerAccount(final UUID uuid, final String ID) {
        DiscordData.connectedAccounts.put(uuid, ID);
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID());
        if (guild == null) {
            return;
        }
        final PlayerData data = PlayerData.getPlayerData(uuid);
        final Member member = guild.getMemberById(ID);
        if (member == null) {
            return;
        }
        try {
            member.modifyNickname(data.getLocalName(true)).complete();
        } catch (Exception ex) {
        }
        final Role confirmRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getConfirmRoleID());
        final Role onlineRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getOnlineRoleID());
        if (confirmRole != null && !member.getRoles().contains(confirmRole)) {
            guild.addRoleToMember(member, confirmRole).complete();
        }
        if (onlineRole != null && Bukkit.getPlayer(uuid) != null && !member.getRoles().contains(onlineRole)) {
            guild.addRoleToMember(member, onlineRole).complete();
        }
    }

    public static void setOnline(final Player player) {
        setOnline(player.getUniqueId());
    }

    public static void setOnline(final UUID uuid) {
        final String ID = DiscordData.connectedAccounts.get(uuid);
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID());
        if (guild == null) {
            return;
        }
        final Member member = guild.getMemberById(ID);
        if (member == null) {
            return;
        }
        final Role onlineRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getOnlineRoleID());
        if (onlineRole != null && !member.getRoles().contains(onlineRole)) {
            guild.addRoleToMember(member, onlineRole).complete();
        }
        final PlayerData data = PlayerData.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        try {
            if (member.getNickname() == null || !member.getNickname().contains(data.getLocalName(true))) {
                member.modifyNickname(data.getLocalName(true)).complete();
            }
            if (member.getNickname() != null) {
                member.modifyNickname(member.getNickname().replaceAll(" \\[[0-9]+\\]", "")).complete();
                member.modifyNickname(member.getNickname() + " [" + data.getID() + "]").complete();
            } else {
                member.modifyNickname(data.getLocalName(true) + " [" + data.getID() + "]").complete();
            }
        } catch (Exception ex) {
        }
    }

    public static void setOffline(final Player player) {
        setOffline(player.getUniqueId());
    }

    public static void setOffline(final UUID uuid) {
        final String ID = DiscordData.connectedAccounts.get(uuid);
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID());
        if (guild == null) {
            return;
        }
        final Member member = guild.getMemberById(ID);
        if (member == null) {
            return;
        }
        final Role onlineRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getOnlineRoleID());
        if (onlineRole != null && member.getRoles().contains(onlineRole)) {
            guild.removeRoleFromMember(member, onlineRole).complete();
        }
        final PlayerData data = PlayerData.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        try {
            if (member.getNickname() == null || !member.getNickname().contains(data.getLocalName(true))) {
                member.modifyNickname(data.getLocalName(true)).complete();
            }
            try {
                if (member.getNickname() != null) {
                    member.modifyNickname(member.getNickname().replaceAll(" \\[[0-9]+\\]", "")).complete();
                }
            } catch (Exception ex) {
            }
        } catch (Exception ex2) {
        }
    }

    public static void load() {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin) DiscordBotManagerCore.getInstance(), (Runnable) new Runnable() {
            @Override
            public void run() {
                if (!DiscordBotManagerCore.getInstance().getDataFolder().exists()) {
                    DiscordBotManagerCore.getInstance().getDataFolder().mkdir();
                }
                final File file = DiscordBotManagerCore.getInstance().getDiscordDataFile();
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                final FileConfiguration config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
                if (config.contains("accounts")) {
                    for (final String strUUID : config.getConfigurationSection("accounts").getKeys(false)) {
                        final UUID uuid = UUID.fromString(strUUID);
                        final String ID = config.getString("accounts." + uuid.toString());
                        DiscordData.connectedAccounts.put(uuid, ID);
                    }
                }
            }
        });
    }

    public static void save() {
        if (!DiscordBotManagerCore.getInstance().getDataFolder().exists()) {
            DiscordBotManagerCore.getInstance().getDataFolder().mkdir();
        }
        final File file = DiscordBotManagerCore.getInstance().getDiscordDataFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final FileConfiguration config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        config.set("accounts", (Object) null);
        for (final UUID uuid : DiscordData.connectedAccounts.keySet()) {
            config.set("accounts." + uuid.toString(), (Object) DiscordData.connectedAccounts.get(uuid));
        }
        try {
            config.save(file);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void updateRoles() {
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID());
        Role confirmRole = null;
        Role onlineRole = null;
        if (guild != null) {
            confirmRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getConfirmRoleID());
            onlineRole = guild.getRoleById(DiscordBotManagerCore.getInstance().getPluginSettings().getOnlineRoleID());
        }
        for (final UUID uuid : DiscordData.connectedAccounts.keySet()) {
            final String ID = DiscordData.connectedAccounts.get(uuid);
            if (guild == null) {
                continue;
            }
            final Member member = guild.getMemberById(ID);
            if (member == null) {
                continue;
            }
            final PlayerData data = PlayerData.getPlayerData(uuid);
            if (data != null) {
                try {
                    if (member.getNickname() == null) {
                        member.modifyNickname(data.getLocalName(true)).complete();
                    } else if (!member.getNickname().contains(data.getLocalName(true))) {
                        member.modifyNickname(data.getLocalName(true)).complete();
                    }
                    if (Bukkit.getPlayer(uuid) != null) {
                        if (member.getNickname() == null || !member.getNickname().contains("[" + data.getID() + "]")) {
                            if (member.getNickname() != null) {
                                member.modifyNickname(member.getNickname().replaceAll(" \\[[0-9]+\\]", "")).complete();
                                member.modifyNickname(member.getNickname() + " [" + data.getID() + "]").complete();
                            } else {
                                member.modifyNickname(data.getLocalName(true) + " [" + data.getID() + "]").complete();
                            }
                        }
                    } else if (member.getNickname() != null) {
                        member.modifyNickname(member.getNickname().replaceAll(" \\[[0-9]+\\]", "")).complete();
                    }
                } catch (Exception ex) {
                }
            }
            if (confirmRole != null && !member.getRoles().contains(confirmRole)) {
                guild.addRoleToMember(member, confirmRole).complete();
            }
            if (Bukkit.getPlayer(uuid) != null) {
                if (onlineRole == null || member.getRoles().contains(onlineRole)) {
                    continue;
                }
                guild.addRoleToMember(member, onlineRole).complete();
            } else {
                if (onlineRole == null || !member.getRoles().contains(onlineRole)) {
                    continue;
                }
                guild.removeRoleFromMember(member, onlineRole).complete();
            }
        }
    }
}

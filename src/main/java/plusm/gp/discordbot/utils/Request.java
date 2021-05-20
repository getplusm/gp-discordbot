package plusm.gp.discordbot.utils;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import plusm.gp.discordbot.DiscordBotManagerCore;

import java.util.LinkedList;
import java.util.List;

public class Request {
    private static List<Request> requests = new LinkedList<>();
    private String ID;
    private Player player;

    public String getID() {
        return this.ID;
    }

    public void setID(final String ID) {
        this.ID = ID;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public static List<Request> getRequests() {
        return Request.requests;
    }

    public static void createRequest(final Request request) {
        Request.requests.add(request);
    }

    public static void removeRequest(final Request request) {
        Request.requests.remove(request);
    }

    public static List<Request> getRequest(final Player player) {
        final List<Request> requests = Lists.newArrayList();
        for (final Request request : Request.requests) {
            if (request.getPlayer().equals(player)) {
                requests.add(request);
            }
        }
        return requests;
    }

    public static List<Request> getRequest(final User user) {
        final List<Request> requests = Lists.newArrayList();
        for (final Request request : Request.requests) {
            if (request.getID().equals(user.getId())) {
                requests.add(request);
            }
        }
        return requests;
    }

    public static List<Request> getRequest(final String ID) {
        final List<Request> requests = Lists.newArrayList();
        for (final Request request : Request.requests) {
            if (request.getID().equals(ID)) {
                requests.add(request);
            }
        }
        return requests;
    }

    public static Request getRequest(final Player player, final String ID) {
        for (final Request request : Request.requests) {
            if (!request.getPlayer().equals(player)) {
                continue;
            }
            if (!request.getID().equals(ID)) {
                continue;
            }
            return request;
        }
        return null;
    }

    public static boolean hasActiveRequest(final Player player, final String ID) {
        return getRequest(player, ID) != null;
    }

    public static void denieAllRequests(final Player player) {
        final String prefix = ChatColor.GOLD + "ГП " + ChatColor.GRAY + "» ";
        final String guildID = DiscordBotManagerCore.getInstance().getPluginSettings().getServerID();
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(guildID);
        for (final Request request : getRequest(player)) {
            Request.requests.remove(request);
            try {
                if (guild == null) {
                    continue;
                }
                final Member member = guild.getMemberById(request.getID());
                if (member == null) {
                    continue;
                }
                player.sendMessage(prefix + ChatColor.WHITE + "Запрос на подтверждение от пользователя " + ChatColor.GOLD + "" + ChatColor.UNDERLINE + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + ChatColor.WHITE + " автоматически отклонен");
                member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(
                        new EmbedBuilder().setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor())
                                .setTitle("**Подтверждение**")
                                .setDescription("Запрос на подтверждение игрока **" + request.getPlayer().getName() + "** автоматически отклонен")
                                .setFooter(DiscordBotManagerCore.getInstance().getBot().getSelfUser().getName(), DiscordBotManagerCore.getInstance().getBot()
                                        .getSelfUser().getAvatarUrl()).build()).queue());
            } catch (Exception ex) {
            }
        }
    }

    public static void denieAllRequests(final String ID) {
        final String prefix = ChatColor.GOLD + "ГП " + ChatColor.GRAY + "» ";
        final String guildID = DiscordBotManagerCore.getInstance().getPluginSettings().getServerID();
        final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(guildID);
        for (final Request request : getRequest(ID)) {
            Request.requests.remove(request);
            try {
                if (guild == null) {
                    continue;
                }
                final Member member = guild.getMemberById(request.getID());
                if (member == null) {
                    continue;
                }
                request.getPlayer().sendMessage(prefix + ChatColor.WHITE + "Запрос на подтверждение от пользователя " + ChatColor.GOLD + "" + ChatColor.UNDERLINE + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + ChatColor.WHITE + " автоматически отклонен");
                member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(new EmbedBuilder().setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setTitle("**Подтверждение**").setDescription("Запрос на подтверждение игрока **" + request.getPlayer().getName() + "** автоматически отклонен").setFooter(DiscordBotManagerCore.getInstance().getBot().getSelfUser().getName(), DiscordBotManagerCore.getInstance().getBot().getSelfUser().getAvatarUrl()).build()).queue());
            } catch (Exception ex) {
            }
        }
    }
}

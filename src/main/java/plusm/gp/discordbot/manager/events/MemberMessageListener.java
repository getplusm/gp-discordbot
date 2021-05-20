package plusm.gp.discordbot.manager.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;
import plusm.gp.discordbot.utils.Request;
import plusm.gp.playerdatamanager.utils.PlayerData;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MemberMessageListener extends ListenerAdapter {
    private String prefix;

    public MemberMessageListener() {
        this.prefix = ChatColor.GOLD + "ГП " + ChatColor.GRAY + "» ";
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        if (!e.isFromGuild()) {
            return;
        }
        if (!e.getGuild().getId().equals(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID())) {
            return;
        }
        if (!e.getChannel().getId().equals(DiscordBotManagerCore.getInstance().getPluginSettings().getConfirmChannelID())) {
            return;
        }
        final String content = e.getMessage().getContentRaw();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        e.getMessage().delete().complete();
        if (DiscordData.getAccountOwner(e.getAuthor().getId()) != null) {
            e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("У вас уже есть привязанный аккаунт майнкрафт").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
            return;
        }
        final String[] args = content.split(" ");
        if (args.length > 2) {
            e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Чтобы привязать майнкрафт аккаунт к дискорд аккаунты, вы должны написать в этот чат однин из следующих пунктов:\n**1.** Имя/Фамилию игрока сервере;\n**2.** ID игрока на сервере;\n**3.** Внутриигровой никнейм игрока;").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
            return;
        }
        if (args.length == 1) {
            final UUID uuid = PlayerData.getUUID(args[0]);
            if (uuid == null) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок не найден").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок не в сети").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            if (Request.hasActiveRequest(player, e.getAuthor().getId())) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Вы уже отправили запрос указанному игроку").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            if (DiscordData.hasConnectedAccount(player)) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок уже имеет подтвержденный аккаунт").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            final TextComponent message = new TextComponent(this.prefix + ChatColor.WHITE + "Пользователь " + ChatColor.GREEN + "" + ChatColor.UNDERLINE + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + ChatColor.WHITE + " отправил вам запрос на подтверждение. ");
            final TextComponent confirm = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Подтвердить" + ChatColor.DARK_GRAY + "]");
            confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Нажмите, чтобы подтвердить подключение дискорд аккаунта")));
            confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord confirm " + e.getAuthor().getId()));
            message.addExtra(confirm);
            player.spigot().sendMessage(message);
            e.getChannel().sendMessage(new EmbedBuilder().setTitle("Успешно").setDescription("Запрос на подтверждение отправлен игроку **" + content + "**").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
            final Request request = new Request();
            request.setID(e.getMember().getId());
            request.setPlayer(player);
            Request.createRequest(request);
            Bukkit.getScheduler().runTaskLater((Plugin) DiscordBotManagerCore.getInstance(), (Runnable) new Runnable() {
                @Override
                public void run() {
                    if (!Request.getRequests().contains(request)) {
                        return;
                    }
                    Request.removeRequest(request);
                    if (player.isOnline()) {
                        player.sendMessage(MemberMessageListener.this.prefix + ChatColor.WHITE + "Запрос пользователя " + ChatColor.GOLD + "" + ChatColor.UNDERLINE + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + ChatColor.WHITE + " автоматически отклонен");
                    }
                    e.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage(new EmbedBuilder().setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setTitle("**Подтверждение**").setDescription("Запрос на подтверждение игрока **" + request.getPlayer().getName() + "** автоматически отклонен").setFooter(DiscordBotManagerCore.getInstance().getBot().getSelfUser().getName(), DiscordBotManagerCore.getInstance().getBot().getSelfUser().getAvatarUrl()).build()).queue());
                }
            }, 1200L);
        } else {
            final PlayerData data = PlayerData.getPlayerData(args[0], args[1]);
            if (data == null) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок не найден").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            final UUID uuid2 = data.getUUID();
            if (uuid2 == null) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок не найден").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            final Player player2 = Bukkit.getPlayer(uuid2);
            if (player2 == null) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок не в сети").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            if (Request.hasActiveRequest(player2, e.getAuthor().getId())) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Вы уже отправили запрос указанному игроку").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            if (DiscordData.hasConnectedAccount(player2)) {
                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Ошибка").setDescription("Указанный игрок уже имеет подтвержденный аккаунт").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
                return;
            }
            final TextComponent message2 = new TextComponent(this.prefix + ChatColor.WHITE + "Пользователь " + ChatColor.GREEN + "" + ChatColor.UNDERLINE + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + ChatColor.WHITE + " отправил вам запрос на подтверждение. ");
            final TextComponent confirm2 = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Подтвердить" + ChatColor.DARK_GRAY + "]");
            confirm2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Нажмите, чтобы подтвердить подключение дискорд аккаунта")));
            confirm2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord confirm " + e.getAuthor().getId()));
            message2.addExtra((BaseComponent) confirm2);
            player2.spigot().sendMessage((BaseComponent) message2);
            e.getChannel().sendMessage(new EmbedBuilder().setTitle("Успешно").setDescription("Запрос на подтверждение отправлен игроку **" + content + "**").setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).complete().delete().queueAfter(5L, TimeUnit.SECONDS);
            final Request request2 = new Request();
            request2.setID(e.getMember().getId());
            request2.setPlayer(player2);
            Request.createRequest(request2);
            Bukkit.getScheduler().runTaskLater((Plugin) DiscordBotManagerCore.getInstance(), (Runnable) new Runnable() {
                @Override
                public void run() {
                    if (!Request.getRequests().contains(request2)) {
                        return;
                    }
                    Request.removeRequest(request2);
                    if (player2.isOnline()) {
                        player2.sendMessage(MemberMessageListener.this.prefix + ChatColor.WHITE + "Запрос пользователя " + ChatColor.GOLD + "" + ChatColor.UNDERLINE + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + ChatColor.WHITE + " автоматически отклонен");
                    }
                    e.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage(new EmbedBuilder().setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setTitle("**Подтверждение**").setDescription("Запрос на подтверждение игрока **" + request2.getPlayer().getName() + "** автоматически отклонен").setFooter(DiscordBotManagerCore.getInstance().getBot().getSelfUser().getName(), DiscordBotManagerCore.getInstance().getBot().getSelfUser().getAvatarUrl()).build()).queue());
                }
            }, 1200L);
        }
    }
}


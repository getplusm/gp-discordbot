package plusm.gp.discordbot.manager.commands;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;
import plusm.gp.discordbot.utils.Request;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand implements CommandExecutor, TabCompleter {
    private final String prefix = ChatColor.GOLD + "ГП " + ChatColor.GRAY + "» ";

    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Команда предназначена только для игроков");
            return true;
        }
        final Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Неизвестный формат команды");
            return true;
        }
        if (args[0].equals("help")) {
            sender.sendMessage("§aВопрос: §fКак привязать аккаунт?");
            sender.sendMessage("§aОтвет: §fЗайдите в Наш дискорд: §7" + YamlConfiguration.loadConfiguration(DiscordBotManagerCore.getInstance().getConfigFile()).getString("link")  + " §8(Нажми на ссылку)");
            sender.sendMessage("§fпосле чего, перейдите в канал §6\"Верификация\"");
            sender.sendMessage("§fи следуйте сообщению от бота!");
            return false;
        }
        if (args[0].equals("disconnect")) {
            if (args.length != 1) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Неизвестный формат команды");
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Использование: " + ChatColor.YELLOW + "/discord disconnect");
                return true;
            }
            if (!DiscordData.hasConnectedAccount(player)) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: У вас нет подключенного дискорд аккаунта");
                return true;
            }
            DiscordData.unlinkPlayerAccount(player);
            sender.sendMessage(this.prefix + ChatColor.GREEN + "Дискорд аккаунт успешно отключен");
        } else {
            if (!args[0].equals("confirm")) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Неизвестный формат команды");
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Неизвестный формат команды");
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Использование: " + ChatColor.YELLOW + "/discord confirm <ID>");
                return true;
            }
            final String ID = args[1];
            final Request request = Request.getRequest(player, ID);
            if (request == null) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Запрос уже истек");
                return true;
            }
            Request.removeRequest(request);
            if (DiscordData.hasConnectedAccount(player)) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Вы уже имеете привязанный дискорд аккаунт");
                return true;
            }
            if (DiscordData.hasConnectedAccount(request.getID())) {
                sender.sendMessage(this.prefix + ChatColor.WHITE + "Ошибка: Указанный пользователь уже имеет привязанный игровой акккаунт");
                return true;
            }
            Request.denieAllRequests(player);
            Request.denieAllRequests(request.getID());
            DiscordData.linkPlayerAccount(player, request.getID());
            sender.sendMessage(this.prefix + ChatColor.GREEN + "Дискорд аккаунт успешно привязан");
            final String guildID = DiscordBotManagerCore.getInstance().getPluginSettings().getServerID();
            final Guild guild = DiscordBotManagerCore.getInstance().getBot().getGuildById(guildID);
            if (guild == null) {
                return true;
            }
            final Member member = guild.getMemberById(request.getID());
            if (member == null) {
                return true;
            }
            member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(new EmbedBuilder().setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor()).setTitle("**Подтверждение**").setDescription("Игрок **" + request.getPlayer().getName() + "** успешно подтвердил запрос").setFooter(DiscordBotManagerCore.getInstance().getBot().getSelfUser().getName(), DiscordBotManagerCore.getInstance().getBot().getSelfUser().getAvatarUrl()).build()).queue());
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String s, final String[] args) {
        final List<String> actions = Lists.newArrayList();
        if (args.length == 1 && sender instanceof Player) {
            final Player player = (Player) sender;
            if (DiscordData.hasConnectedAccount(player)) {
                actions.add("disconnect");
            }
        }
        return this.filter(actions, args);
    }

    public List<String> filter(final List<String> action, final String[] args) {
        final String last = args[args.length - 1];
        final List<String> result = new ArrayList<>();
        for (final String s : action) {
            if (s.startsWith(last)) {
                result.add(s);
            }
        }
        return result;
    }
}
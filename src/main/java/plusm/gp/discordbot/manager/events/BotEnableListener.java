package plusm.gp.discordbot.manager.events;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BotEnableListener extends ListenerAdapter {
    @Override
    public void onReady(final ReadyEvent e) {
        final Guild guild = e.getJDA().getGuildById(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID());
        if (guild == null) {
            return;
        }
        DiscordData.updateRoles();
        final TextChannel channel = guild.getTextChannelById(DiscordBotManagerCore.getInstance().getPluginSettings().getConfirmChannelID());
        if (channel == null) {
            return;
        }
        channel.getManager().putPermissionOverride(guild.getRolesByName("@everyone", false).get(0), Collections.singletonList(Permission.VIEW_CHANNEL), new ArrayList<>()).queue();
        try {
            final String id = channel.getLatestMessageId();
            final MessageHistory history = channel.getHistoryBefore(id, 50).complete();
            if (history.getRetrievedHistory().size() != 0) {
                final List<Message> messages = Lists.newArrayList();
                messages.addAll(history.getRetrievedHistory());
                if (messages.size() > 1) {
                    channel.deleteMessages(messages).complete();
                } else if (messages.size() == 1) {
                    channel.deleteMessageById(messages.get(0).getId()).complete();
                }
            }
            channel.deleteMessageById(id).complete();
        } catch (IllegalStateException | ErrorResponseException ignored) {
        }
        final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        channel.sendMessage(new EmbedBuilder()
                .setColor(DiscordBotManagerCore.getInstance().getPluginSettings().getColor())
                .setTitle(DiscordBotManagerCore.getInstance().getPluginSettings().getMessageTitle())
                .setDescription(DiscordBotManagerCore.getInstance().getPluginSettings().getMessageDescription())
                .build()).queue();
    }
}

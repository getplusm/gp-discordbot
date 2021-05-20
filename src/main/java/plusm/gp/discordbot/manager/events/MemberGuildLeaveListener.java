package plusm.gp.discordbot.manager.events;

import net.dv8tion.jda.api.hooks.*;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.entities.*;
import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;
import plusm.gp.discordbot.utils.Request;

public class MemberGuildLeaveListener extends ListenerAdapter
{
    @Override
    public void onGuildMemberRemove(final GuildMemberRemoveEvent e) {
        if (!e.getGuild().getId().equals(DiscordBotManagerCore.getInstance().getPluginSettings().getServerID())) {
            return;
        }
        final Member member = e.getMember();
        if (member == null) {
            return;
        }
        Request.denieAllRequests(member.getId());
        if (DiscordData.hasConnectedAccount(member.getId())) {
            DiscordData.unlinkPlayerAccount(member.getId());
        }
    }
}

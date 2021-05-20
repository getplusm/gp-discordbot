package plusm.gp.discordbot.manager.events;

import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import plusm.gp.discordbot.utils.DiscordData;
import plusm.gp.playerdatamanager.utils.PlayerData;

import javax.annotation.Nonnull;
import java.util.UUID;

public class MemberNickChangeListener extends ListenerAdapter {
    @Override
    public void onGuildMemberUpdateNickname(@Nonnull final GuildMemberUpdateNicknameEvent e) {
        if (e.getMember().getUser().isBot()) {
            return;
        }
        if (!DiscordData.hasConnectedAccount(e.getMember().getId())) {
            return;
        }
        final UUID uuid = DiscordData.getAccountOwner(e.getMember().getId());
        final PlayerData data = PlayerData.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        try {
            if (e.getNewNickname() == null) {
                e.getMember().modifyNickname(data.getLocalName(true)).complete();
            } else if (!e.getNewNickname().contains(data.getLocalName(true))) {
                e.getMember().modifyNickname(data.getLocalName(true)).complete();
            }
            if (Bukkit.getPlayer(uuid) != null && !e.getMember().getNickname().contains("[" + data.getID() + "]")) {
                e.getMember().modifyNickname(e.getMember().getNickname() + " [" + data.getID() + "]").complete();
            }
        } catch (Exception ex) {
        }
    }
}

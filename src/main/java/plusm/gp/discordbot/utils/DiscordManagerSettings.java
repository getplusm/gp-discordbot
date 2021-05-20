package plusm.gp.discordbot.utils;

import java.awt.*;

public class DiscordManagerSettings {
    private String botToken;
    private String serverID;
    private String confirmChannelID;
    private String confirmRoleID;
    private String onlineRoleID;
    private String messageTitle;
    private String messageDescription;
    private Color color;

    public String getBotToken() {
        return this.botToken;
    }

    public void setBotToken(final String botToken) {
        this.botToken = botToken;
    }

    public String getServerID() {
        return this.serverID;
    }

    public void setServerID(final String serverID) {
        this.serverID = serverID;
    }

    public String getConfirmRoleID() {
        return this.confirmRoleID;
    }

    public void setConfirmRoleID(final String confirmRoleID) {
        this.confirmRoleID = confirmRoleID;
    }

    public String getOnlineRoleID() {
        return this.onlineRoleID;
    }

    public void setOnlineRoleID(final String onlineRoleID) {
        this.onlineRoleID = onlineRoleID;
    }

    public String getConfirmChannelID() {
        return this.confirmChannelID;
    }

    public void setConfirmChannelID(final String confirmChannelID) {
        this.confirmChannelID = confirmChannelID;
    }

    public String getMessageTitle() {
        return this.messageTitle;
    }

    public void setMessageTitle(final String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageDescription() {
        return this.messageDescription;
    }

    public void setMessageDescription(final String messageDescription) {
        this.messageDescription = messageDescription;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }
}
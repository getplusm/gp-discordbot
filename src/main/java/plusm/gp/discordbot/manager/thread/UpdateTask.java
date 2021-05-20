package plusm.gp.discordbot.manager.thread;

import plusm.gp.discordbot.DiscordBotManagerCore;
import plusm.gp.discordbot.utils.DiscordData;

public class UpdateTask implements Runnable {
    private boolean enabled = true;

    @Override
    public void run() {
        DiscordBotManagerCore.getInstance().getLogger().info("DiscordUpdaterTask#1 started...");
        while (this.enabled) {
            try {
                Thread.sleep(300000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DiscordBotManagerCore.getInstance().getLogger().info("DiscordUpdaterTask#1 update...");
            DiscordData.updateRoles();
            DiscordBotManagerCore.getInstance().getLogger().info("DiscordUpdaterTask#1 complete...");
        }
        DiscordBotManagerCore.getInstance().getLogger().info("DiscordUpdaterTask#1 stopped...");
    }

    public void disable() {
        this.enabled = false;
    }
}

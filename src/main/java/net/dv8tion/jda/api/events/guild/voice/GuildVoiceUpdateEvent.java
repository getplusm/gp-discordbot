/*
 * Copyright 2015-2020 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.UpdateEvent;

import javax.annotation.Nullable;

/**
 * Indicates that a {@link Member Member} joined or left a {@link VoiceChannel VoiceChannel}.
 * <br>Generic event that combines
 * {@link GuildVoiceLeaveEvent GuildVoiceLeaveEvent},
 * {@link GuildVoiceJoinEvent GuildVoiceJoinEvent}, and
 * {@link GuildVoiceMoveEvent GuildVoiceMoveEvent} for convenience.
 *
 * <p>Can be used to detect when a Member leaves/joins a channel
 *
 * <p>Identifier: {@code voice-channel}
 */
public interface GuildVoiceUpdateEvent extends UpdateEvent<Member, VoiceChannel>
{
    String IDENTIFIER = "voice-channel";

    /**
     * The {@link VoiceChannel VoiceChannel} that the {@link Member Member} is moved from
     *
     * @return The {@link VoiceChannel}
     */
    @Nullable
    VoiceChannel getChannelLeft();

    /**
     * The {@link VoiceChannel VoiceChannel} that was joined
     *
     * @return The {@link VoiceChannel VoiceChannel}
     */
    @Nullable
    VoiceChannel getChannelJoined();
}

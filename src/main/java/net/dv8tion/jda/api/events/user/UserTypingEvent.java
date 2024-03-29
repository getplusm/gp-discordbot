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
package net.dv8tion.jda.api.events.user;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

/**
 * Indicates that a {@link User User} started typing. (Similar to the typing indicator in the Discord client)
 *
 * <p>This event requires the {@link net.dv8tion.jda.api.requests.GatewayIntent#GUILD_MESSAGE_TYPING GUILD_MESSAGE_TYPING} intent to be enabled.
 * <br>{@link net.dv8tion.jda.api.JDABuilder#createDefault(String) createDefault(String)} and
 * {@link net.dv8tion.jda.api.JDABuilder#createLight(String) createLight(String)} disable this by default!
 *
 * <p>Can be used to retrieve the User who started typing and when and in which MessageChannel they started typing.
 */
public class UserTypingEvent extends GenericUserEvent
{
    private final MessageChannel channel;
    private final OffsetDateTime timestamp;

    public UserTypingEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageChannel channel, @Nonnull OffsetDateTime timestamp)
    {
        super(api, responseNumber, user);
        this.channel = channel;
        this.timestamp = timestamp;
    }

    /**
     * The time when the user started typing
     *
     * @return The time when the typing started
     */
    @Nonnull
    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }

    /**
     * The channel where the typing was started
     *
     * @return The channel
     */
    @Nonnull
    public MessageChannel getChannel()
    {
        return channel;
    }

    /**
     * Whether the user started typing in a channel of the specified type.
     *
     * @param  type
     *         {@link ChannelType ChannelType}
     *
     * @return True, if the user started typing in a channel of the specified type
     */
    public boolean isFromType(@Nonnull ChannelType type)
    {
        return channel.getType() == type;
    }

    /**
     * The {@link ChannelType ChannelType}
     *
     * @return The {@link ChannelType ChannelType}
     */
    @Nonnull
    public ChannelType getType()
    {
        return channel.getType();
    }

    /**
     * {@link PrivateChannel PrivateChannel} in which this users started typing,
     * or {@code null} if this was not in a PrivateChannel.
     *
     * @return Possibly-null {@link PrivateChannel PrivateChannel}
     */
    @Nullable
    public PrivateChannel getPrivateChannel()
    {
        return isFromType(ChannelType.PRIVATE) ? (PrivateChannel) channel : null;
    }

    /**
     * {@link TextChannel TextChannel} in which this users started typing,
     * or {@code null} if this was not in a TextChannel.
     *
     * @return Possibly-null {@link TextChannel TextChannel}
     */
    @Nullable
    public TextChannel getTextChannel()
    {
        return isFromType(ChannelType.TEXT) ? (TextChannel) channel : null;
    }

    /**
     * {@link Guild Guild} in which this users started typing,
     * or {@code null} if this was not in a Guild.
     *
     * @return Possibly-null {@link Guild Guild}
     */
    @Nullable
    public Guild getGuild()
    {
        return isFromType(ChannelType.TEXT) ? getTextChannel().getGuild() : null;
    }

    /**
     * {@link Member Member} instance for the User, or null if this was not in a Guild.
     *
     * @return Possibly-null {@link Member Member}
     */
    @Nullable
    public Member getMember()
    {
        return isFromType(ChannelType.TEXT) ? getGuild().getMember(getUser()) : null;
    }
}

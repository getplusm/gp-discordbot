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

package net.dv8tion.jda.api.events.message.priv.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link MessageReaction MessageReaction} was added to a Message in a PrivateChannel.
 *
 * <p>Can be used to detect when a reaction is added in a private channel.
 */
public class PrivateMessageReactionAddEvent extends GenericPrivateMessageReactionEvent
{
    public PrivateMessageReactionAddEvent(@Nonnull JDA api, long responseNumber, @Nullable User user, @Nonnull MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, reaction, userId);
    }
}

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

package net.dv8tion.jda.api.requests.restaction;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * Extension of {@link net.dv8tion.jda.api.requests.RestAction RestAction} specifically
 * designed to create a {@link GuildChannel GuildChannel}.
 * This extension allows setting properties before executing the action.
 *
 * @since  3.0
 *
 * @see    Guild
 * @see    Guild#createTextChannel(String)
 * @see    Guild#createVoiceChannel(String)
 * @see    Guild#createCategory(String)
 * @see    GuildChannel#createCopy()
 * @see    GuildChannel#createCopy(Guild)
 *
 * @param <T>
 *        The type of channel to create
 */
public interface ChannelAction<T extends GuildChannel> extends AuditableRestAction<T>
{
    @Nonnull
    @Override
    ChannelAction<T> setCheck(@Nullable BooleanSupplier checks);

    @Nonnull
    @Override
    ChannelAction<T> timeout(long timeout, @Nonnull TimeUnit unit);

    @Nonnull
    @Override
    ChannelAction<T> deadline(long timestamp);

    /**
     * The guild to create this {@link GuildChannel} in
     *
     * @return The guild
     */
    @Nonnull
    Guild getGuild();

    /**
     * The {@link ChannelType} for the resulting channel
     *
     * @return The channel type
     */
    @Nonnull
    ChannelType getType();

    /**
     * Sets the name for the new GuildChannel
     *
     * @param  name
     *         The not-null name for the new GuildChannel (1-100 chars long)
     *
     * @throws IllegalArgumentException
     *         If the provided name is null or not between 1-100 chars long
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setName(@Nonnull String name);

    /**
     * Sets the {@link Category Category} for the new GuildChannel
     *
     * @param  category
     *         The parent for the new GuildChannel
     *
     * @throws UnsupportedOperationException
     *         If this ChannelAction is for a Category
     * @throws IllegalArgumentException
     *         If the provided category is {@code null}
     *         or not from this Guild
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setParent(@Nullable Category category);

    /**
     * Sets the position where the new Channel should be inserted into.
     * This refers to the raw position value, not the computed (relative) position.
     * <p>
     * By default (or by providing this method with {@code null}),
     * the position will automatically be computed based on the other Channels (inserted last in its respective group).
     * <p>
     * Note: This does not shift the position values of existing Channels if the values collide.
     * <br>As a reminder: The ordering of Channels is determined first by its Category's position, then by its raw
     * position value and finally by its id (younger Channels are below older ones)
     *
     * @param  position
     *         The raw position value that should be used for the new Channel
     *
     * @throws IllegalArgumentException
     *         If the provided position value is {@code <0}
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setPosition(@Nullable Integer position);

    /**
     * Sets the topic for the new TextChannel
     *
     * @param  topic
     *         The topic for the new GuildChannel (max 1024 chars)
     *
     * @throws UnsupportedOperationException
     *         If this ChannelAction is not for a TextChannel
     * @throws IllegalArgumentException
     *         If the provided topic is longer than 1024 chars
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setTopic(@Nullable String topic);

    /**
     * Sets the NSFW flag for the new TextChannel
     *
     * @param  nsfw
     *         The NSFW flag for the new GuildChannel
     *
     * @throws UnsupportedOperationException
     *         If this ChannelAction is not for a TextChannel
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setNSFW(boolean nsfw);

    /**
     * Sets the slowmode value, which limits the amount of time that individual users must wait
     * between sending messages in the new TextChannel. This is measured in seconds.
     *
     * <p>Note: Bots are unaffected by this.
     * <br>Having {@link Permission#MESSAGE_MANAGE MESSAGE_MANAGE} or
     * {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} permission also
     * grants immunity to slowmode.
     *
     * @param  slowmode
     *         The number of seconds required to wait between sending messages in the channel.
     *
     * @throws UnsupportedOperationException
     *         If this ChannelAction is not for a TextChannel
     * @throws IllegalArgumentException
     *         If the {@code slowmode} is greater than {@link TextChannel#MAX_SLOWMODE TextChannel.MAX_SLOWMODE}, or less than 0
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setSlowmode(int slowmode);

    /**
     * Adds a new Role or Member {@link PermissionOverride PermissionOverride}
     * for the new GuildChannel.
     *
     * <p>Example:
     * <pre>{@code
     * Role role = guild.getPublicRole();
     * EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_READ);
     * EnumSet<Permission> deny = EnumSet.of(Permission.MESSAGE_WRITE);
     * channelAction.addPermissionOverride(role, allow, deny);
     * }</pre>
     *
     * @param  target
     *         The not-null {@link Role Role} or {@link Member Member} for the override
     * @param  allow
     *         The granted {@link Permission Permissions} for the override or null
     * @param  deny
     *         The denied {@link Permission Permissions} for the override or null
     *
     * @throws IllegalArgumentException
     *         If the specified target is null or not within the same guild.
     *
     * @return The current ChannelAction, for chaining convenience
     *
     * @see    java.util.EnumSet
     */
    @Nonnull
    @CheckReturnValue
    default ChannelAction<T> addPermissionOverride(@Nonnull IPermissionHolder target, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
    {
        final long allowRaw = allow != null ? Permission.getRaw(allow) : 0;
        final long denyRaw = deny != null ? Permission.getRaw(deny) : 0;

        return addPermissionOverride(target, allowRaw, denyRaw);
    }

    /**
     * Adds a new Role or Member {@link PermissionOverride PermissionOverride}
     * for the new GuildChannel.
     *
     * <p>Example:
     * <pre>{@code
     * Role role = guild.getPublicRole();
     * long allow = Permission.MESSAGE_READ.getRawValue();
     * long deny = Permission.MESSAGE_WRITE.getRawValue() | Permission.MESSAGE_ADD_REACTION.getRawValue();
     * channelAction.addPermissionOverride(role, allow, deny);
     * }</pre>
     *
     * @param  target
     *         The not-null {@link Role Role} or {@link Member Member} for the override
     * @param  allow
     *         The granted {@link Permission Permissions} for the override
     *         Use {@link Permission#getRawValue()} to retrieve these Permissions.
     * @param  deny
     *         The denied {@link Permission Permissions} for the override
     *         Use {@link Permission#getRawValue()} to retrieve these Permissions.
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified target is null
     *                 or not within the same guild.</li>
     *             <li>If one of the provided Permission values is invalid</li>
     *         </ul>
     *
     * @return The current ChannelAction, for chaining convenience
     *
     * @see    Permission#getRawValue()
     * @see    Permission#getRaw(Collection)
     * @see    Permission#getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    default ChannelAction<T> addPermissionOverride(@Nonnull IPermissionHolder target, long allow, long deny)
    {
        Checks.notNull(target, "Override Role/Member");
        if (target instanceof Role)
            return addRolePermissionOverride(target.getIdLong(), allow, deny);
        else if (target instanceof Member)
            return addMemberPermissionOverride(target.getIdLong(), allow, deny);
        throw new IllegalArgumentException("Cannot add override for " + target.getClass().getSimpleName());
    }

    /**
     * Adds a new Member {@link PermissionOverride PermissionOverride}
     * for the new GuildChannel.
     *
     * <p>Example:
     * <pre>{@code
     * long userId = user.getIdLong();
     * EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_READ);
     * EnumSet<Permission> deny = EnumSet.of(Permission.MESSAGE_WRITE);
     * channelAction.addMemberPermissionOverride(userId, allow, deny);
     * }</pre>
     *
     * @param  memberId
     *         The id for the member
     * @param  allow
     *         The granted {@link Permission Permissions} for the override or null
     * @param  deny
     *         The denied {@link Permission Permissions} for the override or null
     *
     * @return The current ChannelAction, for chaining convenience
     *
     * @see    java.util.EnumSet
     */
    @Nonnull
    @CheckReturnValue
    default ChannelAction<T> addMemberPermissionOverride(long memberId, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
    {
        final long allowRaw = allow != null ? Permission.getRaw(allow) : 0;
        final long denyRaw = deny != null ? Permission.getRaw(deny) : 0;

        return addMemberPermissionOverride(memberId, allowRaw, denyRaw);
    }

    /**
     * Adds a new Role {@link PermissionOverride PermissionOverride}
     * for the new GuildChannel.
     *
     * <p>Example:
     * <pre>{@code
     * long roleId = role.getIdLong();
     * EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_READ);
     * EnumSet<Permission> deny = EnumSet.of(Permission.MESSAGE_WRITE);
     * channelAction.addRolePermissionOverride(roleId, allow, deny);
     * }</pre>
     *
     * @param  roleId
     *         The id for the role
     * @param  allow
     *         The granted {@link Permission Permissions} for the override or null
     * @param  deny
     *         The denied {@link Permission Permissions} for the override or null
     *
     * @return The current ChannelAction, for chaining convenience
     *
     * @see    java.util.EnumSet
     */
    @Nonnull
    @CheckReturnValue
    default ChannelAction<T> addRolePermissionOverride(long roleId, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
    {
        final long allowRaw = allow != null ? Permission.getRaw(allow) : 0;
        final long denyRaw = deny != null ? Permission.getRaw(deny) : 0;

        return addRolePermissionOverride(roleId, allowRaw, denyRaw);
    }

    /**
     * Adds a new Member {@link PermissionOverride PermissionOverride} for the new GuildChannel.
     *
     * <p>Example:
     * <pre>{@code
     * long userId = user.getIdLong();
     * long allow = Permission.MESSAGE_READ.getRawValue();
     * long deny = Permission.MESSAGE_WRITE.getRawValue() | Permission.MESSAGE_ADD_REACTION.getRawValue();
     * channelAction.addMemberPermissionOverride(userId, allow, deny);
     * }</pre>
     *
     * @param  memberId
     *         The id for the member
     * @param  allow
     *         The granted {@link Permission Permissions} for the override
     *         Use {@link Permission#getRawValue()} to retrieve these Permissions.
     * @param  deny
     *         The denied {@link Permission Permissions} for the override
     *         Use {@link Permission#getRawValue()} to retrieve these Permissions.
     *
     * @throws IllegalArgumentException
     *         If one of the provided Permission values is invalid
     *
     * @return The current ChannelAction, for chaining convenience
     *
     * @see    Permission#getRawValue()
     * @see    Permission#getRaw(Collection)
     * @see    Permission#getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> addMemberPermissionOverride(long memberId, long allow, long deny);

    /**
     * Adds a new Role {@link PermissionOverride PermissionOverride} for the new GuildChannel.
     *
     * <p>Example:
     * <pre>{@code
     * long roleId = role.getIdLong();
     * long allow = Permission.MESSAGE_READ.getRawValue();
     * long deny = Permission.MESSAGE_WRITE.getRawValue() | Permission.MESSAGE_ADD_REACTION.getRawValue();
     * channelAction.addMemberPermissionOverride(roleId, allow, deny);
     * }</pre>
     *
     * @param  roleId
     *         The id for the role
     * @param  allow
     *         The granted {@link Permission Permissions} for the override
     *         Use {@link Permission#getRawValue()} to retrieve these Permissions.
     * @param  deny
     *         The denied {@link Permission Permissions} for the override
     *         Use {@link Permission#getRawValue()} to retrieve these Permissions.
     *
     * @throws IllegalArgumentException
     *         If one of the provided Permission values is invalid
     *
     * @return The current ChannelAction, for chaining convenience
     *
     * @see    Permission#getRawValue()
     * @see    Permission#getRaw(Collection)
     * @see    Permission#getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> addRolePermissionOverride(long roleId, long allow, long deny);

    /**
     * Sets the bitrate for the new VoiceChannel
     *
     * @param  bitrate
     *         The bitrate for the new VoiceChannel in {@code bps} (limits 8000 {@literal <}= bitrate {@literal <}= {@link Guild#getMaxBitrate()})
     *         or {@code null} to use the default 64kbps.
     *
     * @throws UnsupportedOperationException
     *         If this ChannelAction is not for a VoiceChannel
     * @throws IllegalArgumentException
     *         If the provided bitrate is less than 8000 or greater than {@link Guild#getMaxBitrate()}
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setBitrate(@Nullable Integer bitrate);

    /**
     * Sets the userlimit for the new VoiceChannel
     *
     * @param  userlimit
     *         The userlimit for the new VoiceChannel or {@code null}/{@code 0} to use no limit,
     *
     * @throws UnsupportedOperationException
     *         If this ChannelAction is not for a VoiceChannel
     * @throws IllegalArgumentException
     *         If the provided userlimit is negative or above {@code 99}
     *
     * @return The current ChannelAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<T> setUserlimit(@Nullable Integer userlimit);
}

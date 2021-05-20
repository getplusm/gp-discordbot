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
package net.dv8tion.jda.api.entities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a {@link Guild Guild} channel.
 *
 * @see Guild#getGuildChannelById(long)
 * @see Guild#getGuildChannelById(ChannelType, long)
 *
 * @see JDA#getGuildChannelById(long)
 * @see JDA#getGuildChannelById(ChannelType, long)
 */
public interface GuildChannel extends ISnowflake, Comparable<GuildChannel>
{
    /**
     * The {@link ChannelType ChannelType} for this GuildChannel
     *
     * @return The channel type
     */
    @Nonnull
    ChannelType getType();

    /**
     * The human readable name of the  GuildChannel.
     * <br>If no name has been set, this returns null.
     *
     * @return The name of this GuildChannel
     */
    @Nonnull
    String getName();

    /**
     * Returns the {@link Guild Guild} that this GuildChannel is part of.
     *
     * @return Never-null {@link Guild Guild} that this GuildChannel is part of.
     */
    @Nonnull
    Guild getGuild();

    /**
     * Parent {@link Category Category} of this
     * GuildChannel. Channels need not have a parent Category.
     * <br>Note that an {@link Category Category} will
     * always return {@code null} for this method as nested categories are not supported.
     *
     * @return Possibly-null {@link Category Category} for this GuildChannel
     */
    @Nullable
    Category getParent();

    /**
     * A List of all {@link Member Members} that are in this GuildChannel
     * <br>For {@link TextChannel TextChannels},
     * this returns all Members with the {@link Permission#MESSAGE_READ} Permission.
     * <br>For {@link VoiceChannel VoiceChannels},
     * this returns all Members that joined that VoiceChannel.
     * <br>For {@link Category Categories},
     * this returns all Members who are in its child channels.
     *
     * @return An immutable List of {@link Member Members} that are in this GuildChannel.
     */
    @Nonnull
    List<Member> getMembers();

    /**
     * The position this GuildChannel is displayed at.
     * <br>Higher values mean they are displayed lower in the Client. Position 0 is the top most GuildChannel
     * Channels of a {@link Guild Guild} do not have to have continuous positions
     *
     * @throws IllegalStateException
     *         If this channel is not in the guild cache
     *
     * @return Zero-based int of position of the GuildChannel.
     */
    int getPosition();

    /**
     * The actual position of the {@link GuildChannel GuildChannel} as stored and given by Discord.
     * Channel positions are actually based on a pairing of the creation time (as stored in the snowflake id)
     * and the position. If 2 or more channels share the same position then they are sorted based on their creation date.
     * The more recent a channel was created, the lower it is in the hierarchy. This is handled by {@link #getPosition()}
     * and it is most likely the method you want. If, for some reason, you want the actual position of the
     * channel then this method will give you that value.
     *
     * @return The true, Discord stored, position of the {@link GuildChannel GuildChannel}.
     */
    int getPositionRaw();

    /**
     * Returns the {@link JDA JDA} instance of this GuildChannel
     *
     * @return the corresponding JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * The {@link PermissionOverride} relating to the specified {@link Member Member} or {@link Role Role}.
     * If there is no {@link PermissionOverride PermissionOverride} for this {@link GuildChannel GuildChannel}
     * relating to the provided Member or Role, then this returns {@code null}.
     *
     * @param  permissionHolder
     *         The {@link Member Member} or {@link Role Role} whose
     *         {@link PermissionOverride PermissionOverride} is requested.
     *
     * @throws IllegalArgumentException
     *         If the provided permission holder is null, or from a different guild
     *
     * @return Possibly-null {@link PermissionOverride PermissionOverride}
     *         relating to the provided Member or Role.
     */
    @Nullable
    PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder permissionHolder);

    /**
     * Gets all of the {@link PermissionOverride PermissionOverrides} that are part
     * of this {@link GuildChannel GuildChannel}.
     * <br>This combines {@link Member Member} and {@link Role Role} overrides.
     * If you would like only {@link Member Member} overrides or only {@link Role Role}
     * overrides, use {@link #getMemberPermissionOverrides()} or {@link #getRolePermissionOverrides()} respectively.
     *
     * <p>This requires {@link net.dv8tion.jda.api.utils.cache.CacheFlag#MEMBER_OVERRIDES CacheFlag.MEMBER_OVERRIDES} to be enabled!
     * Without that CacheFlag, this list will only contain overrides for the currently logged in account and roles.
     *
     * @return Possibly-empty immutable list of all {@link PermissionOverride PermissionOverrides}
     *         for this {@link GuildChannel GuildChannel}.
     */
    @Nonnull
    List<PermissionOverride> getPermissionOverrides();

    /**
     * Gets all of the {@link Member Member} {@link PermissionOverride PermissionOverrides}
     * that are part of this {@link GuildChannel GuildChannel}.
     *
     * <p>This requires {@link net.dv8tion.jda.api.utils.cache.CacheFlag#MEMBER_OVERRIDES CacheFlag.MEMBER_OVERRIDES} to be enabled!
     *
     * @return Possibly-empty immutable list of all {@link PermissionOverride PermissionOverrides}
     *         for {@link Member Member}
     *         for this {@link GuildChannel GuildChannel}.
     */
    @Nonnull
    List<PermissionOverride> getMemberPermissionOverrides();

    /**
     * Gets all of the {@link Role Role} {@link PermissionOverride PermissionOverrides}
     * that are part of this {@link GuildChannel GuildChannel}.
     *
     * @return Possibly-empty immutable list of all {@link PermissionOverride PermissionOverrides}
     *         for {@link Role Roles}
     *         for this {@link GuildChannel GuildChannel}.
     */
    @Nonnull
    List<PermissionOverride> getRolePermissionOverrides();

    /**
     * Creates a copy of the specified {@link GuildChannel GuildChannel}
     * in the specified {@link Guild Guild}.
     * <br>If the provided target guild is not the same Guild this channel is in then
     * the parent category and permissions will not be copied due to technical difficulty and ambiguity.
     *
     * <p>This copies the following elements:
     * <ol>
     *     <li>Name</li>
     *     <li>Parent Category (if present)</li>
     *     <li>Voice Elements (Bitrate, Userlimit)</li>
     *     <li>Text Elements (Topic, NSFW, Slowmode)</li>
     *     <li>All permission overrides for Members/Roles</li>
     * </ol>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The channel could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} permission was removed</li>
     * </ul>
     *
     * @param  guild
     *         The {@link Guild Guild} to create the channel in
     *
     * @throws IllegalArgumentException
     *         If the provided guild is {@code null}
     * @throws net.dv8tion.jda.api.exceptions.PermissionException
     *         If the currently logged in account does not have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission
     *
     * @return A specific {@link ChannelAction ChannelAction}
     *         <br>This action allows to set fields for the new GuildChannel before creating it!
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<? extends GuildChannel> createCopy(@Nonnull Guild guild);

    /**
     * Creates a copy of the specified {@link GuildChannel GuildChannel}.
     *
     * <p>This copies the following elements:
     * <ol>
     *     <li>Name</li>
     *     <li>Parent Category (if present)</li>
     *     <li>Voice Elements (Bitrate, Userlimit)</li>
     *     <li>Text Elements (Topic, NSFW, Slowmode)</li>
     *     <li>All permission overrides for Members/Roles</li>
     * </ol>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The channel could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} permission was removed</li>
     * </ul>
     *
     * @throws net.dv8tion.jda.api.exceptions.PermissionException
     *         If the currently logged in account does not have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission
     *
     * @return A specific {@link ChannelAction ChannelAction}
     *         <br>This action allows to set fields for the new GuildChannel before creating it!
     */
    @Nonnull
    @CheckReturnValue
    default ChannelAction<? extends GuildChannel> createCopy()
    {
        return createCopy(getGuild());
    }

    /**
     * Returns the {@link ChannelManager ChannelManager} for this GuildChannel.
     * <br>In the ChannelManager, you can modify the name, topic and position of this GuildChannel.
     * You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue() RestAction.queue()}.
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_CHANNEL Permission.MANAGE_CHANNEL}
     *
     * @return The ChannelManager of this GuildChannel
     */
    @Nonnull
    ChannelManager getManager();

    /**
     * Deletes this GuildChannel.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If this channel was already deleted</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The send request was attempted after the account lost
     *         {@link Permission#MANAGE_CHANNEL Permission.MANAGE_CHANNEL} in the channel.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild</li>
     * </ul>
     *
     * @throws InsufficientPermissionException
     *         if the currently logged in account doesn't have {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL}
     *         for the channel.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();

    /**
     * Creates a {@link PermissionOverride PermissionOverride}
     * for the specified {@link Member Member} or {@link Role Role} in this GuildChannel.
     * You can use {@link #putPermissionOverride(IPermissionHolder)} to replace existing overrides.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If this channel was already deleted</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild</li>
     * </ul>
     *
     * @param  permissionHolder
     *         The Member or Role to create an override for
     *
     * @throws InsufficientPermissionException
     *         if we don't have the permission to {@link Permission#MANAGE_PERMISSIONS MANAGE_PERMISSIONS}
     * @throws IllegalArgumentException
     *         if the specified permission holder is null or is not from {@link #getGuild()}
     * @throws IllegalStateException
     *         If the specified permission holder already has a PermissionOverride. Use {@link #getPermissionOverride(IPermissionHolder)} to retrieve it.
     *         You can use {@link #putPermissionOverride(IPermissionHolder)} to replace existing overrides.
     *
     * @return {@link PermissionOverrideAction PermissionOverrideAction}
     *         Provides the newly created PermissionOverride for the specified permission holder
     */
    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction createPermissionOverride(@Nonnull IPermissionHolder permissionHolder);

    /**
     * Creates a {@link PermissionOverride PermissionOverride}
     * for the specified {@link Member Member} or {@link Role Role} in this GuildChannel.
     * <br>If the permission holder already has an existing override it will be replaced.
     *
     * @param  permissionHolder
     *         The Member or Role to create the override for
     *
     * @throws InsufficientPermissionException
     *         if we don't have the permission to {@link Permission#MANAGE_PERMISSIONS MANAGE_PERMISSIONS}
     * @throws IllegalArgumentException
     *         If the provided permission holder is null or from a different guild
     *
     * @return {@link PermissionOverrideAction PermissionOverrideAction}
     *         Provides the newly created PermissionOverride for the specified permission holder
     */
    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction putPermissionOverride(@Nonnull IPermissionHolder permissionHolder);

    /**
     * Creates a new override or updates an existing one.
     * <br>This is similar to calling {@link PermissionOverride#getManager()} if an override exists.
     *
     * @param  permissionHolder
     *         The Member/Role for the override
     *
     * @throws InsufficientPermissionException
     *         If we don't have the permission to {@link Permission#MANAGE_PERMISSIONS MANAGE_PERMISSIONS}
     * @throws IllegalArgumentException
     *         If the provided permission holder is null or not from this guild
     *
     * @return {@link PermissionOverrideAction}
     *         <br>With the current settings of an existing override or a fresh override with no permissions set
     *
     * @since  4.0.0
     */
    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction upsertPermissionOverride(@Nonnull IPermissionHolder permissionHolder)
    {
        if (!getGuild().getSelfMember().hasPermission(this, Permission.MANAGE_PERMISSIONS))
            throw new InsufficientPermissionException(this, Permission.MANAGE_PERMISSIONS);
        PermissionOverride override = getPermissionOverride(permissionHolder);
        if (override != null)
            return override.getManager();
        return putPermissionOverride(permissionHolder);
    }

    /**
     * Creates a new {@link InviteAction InviteAction} which can be used to create a
     * new {@link Invite Invite}.
     * <br>Requires {@link Permission#CREATE_INSTANT_INVITE CREATE_INSTANT_INVITE} in this channel.
     *
     * @throws InsufficientPermissionException
     *         If the account does not have {@link Permission#CREATE_INSTANT_INVITE CREATE_INSTANT_INVITE} in this channel
     * @throws IllegalArgumentException
     *         If this is an instance of a {@link Category Category}
     *
     * @return A new {@link InviteAction InviteAction}
     * 
     * @see    InviteAction
     */
    @Nonnull
    @CheckReturnValue
    InviteAction createInvite();

    /**
     * Returns all invites for this channel.
     * <br>Requires {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} in this channel.
     * Will throw a {@link InsufficientPermissionException InsufficientPermissionException} otherwise.
     *
     * @throws InsufficientPermissionException
     *         if the account does not have {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} in this channel
     *
     * @return {@link RestAction RestAction} - Type: List{@literal <}{@link Invite Invite}{@literal >}
     *         <br>The list of expanded Invite objects
     *
     * @see    Guild#retrieveInvites()
     */
    @Nonnull
    @CheckReturnValue
    RestAction<List<Invite>> retrieveInvites();
}

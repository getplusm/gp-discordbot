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
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Represents the specific {@link Member Member} or {@link Role Role}
 * permission overrides that can be set for channels.
 *
 * @see GuildChannel#upsertPermissionOverride(IPermissionHolder)
 * @see GuildChannel#createPermissionOverride(IPermissionHolder)
 * @see GuildChannel#putPermissionOverride(IPermissionHolder)
 *
 * @see GuildChannel#getPermissionOverrides()
 * @see GuildChannel#getPermissionOverride(IPermissionHolder)
 * @see GuildChannel#getMemberPermissionOverrides()
 * @see GuildChannel#getRolePermissionOverrides()
 */
public interface PermissionOverride extends ISnowflake
{
    /**
     * This is the raw binary representation (as a base 10 long) of the permissions <b>allowed</b> by this override.
     * <br>The long relates to the offsets used by each {@link Permission Permission}.
     *
     * @return Never-negative long containing the binary representation of the allowed permissions of this override.
     */
    long getAllowedRaw();

    /**
     * This is the raw binary representation (as a base 10 long) of the permissions <b>not affected</b> by this override.
     * <br>The long relates to the offsets used by each {@link Permission Permission}.
     *
     * @return Never-negative long containing the binary representation of the unaffected permissions of this override.
     */
    long getInheritRaw();

    /**
     * This is the raw binary representation (as a base 10 long) of the permissions <b>denied</b> by this override.
     * <br>The long relates to the offsets used by each {@link Permission Permission}.
     *
     * @return Never-negative long containing the binary representation of the denied permissions of this override.
     */
    long getDeniedRaw();

    /**
     * EnumSet of all {@link Permission Permissions} that are specifically allowed by this override.
     * <br><u>Changes to the returned set do not affect this entity directly.</u>
     *
     * @return Possibly-empty set of allowed {@link Permission Permissions}.
     */
    @Nonnull
    EnumSet<Permission> getAllowed();

    /**
     * EnumSet of all {@link Permission Permission} that are unaffected by this override.
     * <br><u>Changes to the returned set do not affect this entity directly.</u>
     *
     * @return Possibly-empty set of unaffected {@link Permission Permissions}.
     */
    @Nonnull
    EnumSet<Permission> getInherit();

    /**
     * EnumSet of all {@link Permission Permissions} that are denied by this override.
     * <br><u>Changes to the returned set do not affect this entity directly.</u>
     *
     * @return Possibly-empty set of denied {@link Permission Permissions}.
     */
    @Nonnull
    EnumSet<Permission> getDenied();

    /**
     * The {@link JDA JDA} instance that this PermissionOverride is related to.
     *
     * @return Never-null {@link JDA JDA} instance.
     */
    @Nonnull
    JDA getJDA();

    /**
     * This method will return the {@link IPermissionHolder PermissionHolder} of this PermissionOverride.
     * It can be used to get the general permissions of that PermissionHolder, no matter if it is a {@link Member Member} or a {@link Role Role}.
     * <br>Similar to {@link #getMember()} this will return {@code null} if the member is not cached.
     *
     * <p>To get the concrete Member or Role, use {@link PermissionOverride#getMember()} or {@link PermissionOverride#getRole()}!
     *
     * @return Possibly-null {@link IPermissionHolder IPermissionHolder} of this PermissionOverride.
     *
     * @see    PermissionOverride#getRole()
     * @see    PermissionOverride#getMember()
     */
    @Nullable
    IPermissionHolder getPermissionHolder();

    /**
     * If this PermissionOverride is an override dealing with a {@link Member Member}, then
     * this method will return the related {@link Member Member} if the member is currently cached.
     * <br>Otherwise, this method returns {@code null}.
     * <br>Basically: if {@link PermissionOverride#isMemberOverride()} returns {@code false} or the member is not cached, this returns {@code null}.
     *
     * @return Possibly-null related {@link Member Member}.
     */
    @Nullable
    Member getMember();

    /**
     * If this PermissionOverride is an override dealing with a {@link Role Role}, then
     * this method will return the related {@link Role Role}.
     * <br>Otherwise, this method returns {@code null}.
     * <br>Basically: if {@link PermissionOverride#isRoleOverride()}
     * returns {@code false}, this returns {@code null}.
     *
     * @return Possibly-null related {@link Role}.
     */
    @Nullable
    Role getRole();

    /**
     * The {@link GuildChannel GuildChannel} that this PermissionOverride affects.
     *
     * @return Never-null related {@link GuildChannel GuildChannel} that this override is part of.
     */
    @Nonnull
    GuildChannel getChannel();

    /**
     * The {@link Guild Guild} that the {@link GuildChannel GuildChannel}
     * returned from {@link PermissionOverride#getChannel()} is a part of.
     * By inference, this is the {@link Guild Guild} that this PermissionOverride is part of.
     *
     * @return Never-null related {@link Guild Guild}.
     */
    @Nonnull
    Guild getGuild();

    /**
     * Used to determine if this PermissionOverride relates to
     * a specific {@link Member Member}.
     *
     * @return True if this override is a user override.
     */
    boolean isMemberOverride();

    /**
     * Used to determine if this PermissionOverride relates to
     * a specific {@link Role Role}.
     *
     * @return True if this override is a role override.
     */
    boolean isRoleOverride();

    /**
     * Returns the {@link PermissionOverrideAction PermissionOverrideAction} to modify this PermissionOverride.
     * <br>In the PermissionOverrideAction you can modify the permissions of the override.
     * You modify multiple fields in one request by chaining setters before calling {@link net.dv8tion.jda.api.requests.RestAction#queue() RestAction.queue()}.
     *
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *
     * @return The PermissionOverrideAction of this override.
     */
    @Nonnull
    PermissionOverrideAction getManager();

    /**
     * Deletes this PermissionOverride.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_OVERRIDE}
     *     <br>If the the override was already deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this override was a part of was already deleted</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild</li>
     * </ul>
     *
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException
     *         if we don't have the permission to {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();
}

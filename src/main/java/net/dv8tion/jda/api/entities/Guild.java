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

import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.MemberAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.concurrent.task.GatewayTask;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a Discord {@link Guild Guild}.
 * This should contain all information provided from Discord about a Guild.
 *
 * @see JDA#getGuildCache()
 * @see JDA#getGuildById(long)
 * @see JDA#getGuildsByName(String, boolean)
 * @see JDA#getGuilds()
 */
public interface Guild extends ISnowflake
{
    /** Template for {@link #getIconUrl()}. */
    String ICON_URL = "https://cdn.discordapp.com/icons/%s/%s.%s";
    /** Template for {@link #getSplashUrl()}. */
    String SPLASH_URL = "https://cdn.discordapp.com/splashes/%s/%s.png";
    /** Template for {@link #getBannerUrl()}. */
    String BANNER_URL = "https://cdn.discordapp.com/banners/%s/%s.png";

    /**
     * Retrieves the available regions for this Guild
     * <br>Shortcut for {@link #retrieveRegions(boolean) retrieveRegions(true)}
     * <br>This will include deprecated voice regions by default.
     *
     * @return {@link RestAction RestAction} - Type {@link EnumSet EnumSet}
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<EnumSet<Region>> retrieveRegions()
    {
        return retrieveRegions(true);
    }

    /**
     * Retrieves the available regions for this Guild
     *
     * @param  includeDeprecated
     *         Whether to include deprecated regions
     *
     * @return {@link RestAction RestAction} - Type {@link EnumSet EnumSet}
     */
    @Nonnull
    @CheckReturnValue
    RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated);

    /**
     * Adds the user represented by the provided id to this guild.
     * <br>This requires an <b>OAuth2 Access Token</b> with the scope {@code guilds.join}.
     *
     * @param  accessToken
     *         The access token
     * @param  userId
     *         The user id
     *
     * @throws IllegalArgumentException
     *         If the user id or access token is blank, empty, or null,
     *         or if the provided user is already in this guild
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#CREATE_INSTANT_INVITE Permission.CREATE_INSTANT_INVITE}
     *
     * @return {@link MemberAction MemberAction}
     *
     * @see    <a href="https://discord.com/developers/docs/topics/oauth2" target="_blank">Discord OAuth2 Documentation</a>
     *
     * @since  3.7.0
     */
    @Nonnull
    @CheckReturnValue
    MemberAction addMember(@Nonnull String accessToken, @Nonnull String userId);

    /**
     * Adds the provided user to this guild.
     * <br>This requires an <b>OAuth2 Access Token</b> with the scope {@code guilds.join}.
     *
     * @param  accessToken
     *         The access token
     * @param  user
     *         The user
     *
     * @throws IllegalArgumentException
     *         If the user or access token is blank, empty, or null,
     *         or if the provided user is already in this guild
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#CREATE_INSTANT_INVITE Permission.CREATE_INSTANT_INVITE}
     *
     * @return {@link MemberAction MemberAction}
     *
     * @see    <a href="https://discord.com/developers/docs/topics/oauth2" target="_blank">Discord OAuth2 Documentation</a>
     *
     * @since  3.7.0
     */
    @Nonnull
    @CheckReturnValue
    default MemberAction addMember(@Nonnull String accessToken, @Nonnull User user)
    {
        Checks.notNull(user, "User");
        return addMember(accessToken, user.getId());
    }

    /**
     * Adds the user represented by the provided id to this guild.
     * <br>This requires an <b>OAuth2 Access Token</b> with the scope {@code guilds.join}.
     *
     * @param  accessToken
     *         The access token
     * @param  userId
     *         The user id
     *
     * @throws IllegalArgumentException
     *         If the user id or access token is blank, empty, or null,
     *         or if the provided user is already in this guild
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#CREATE_INSTANT_INVITE Permission.CREATE_INSTANT_INVITE}
     *
     * @return {@link MemberAction MemberAction}
     *
     * @see    <a href="https://discord.com/developers/docs/topics/oauth2" target="_blank">Discord OAuth2 Documentation</a>
     *
     * @since  3.7.0
     */
    @Nonnull
    @CheckReturnValue
    default MemberAction addMember(@Nonnull String accessToken, long userId)
    {
        return addMember(accessToken, Long.toUnsignedString(userId));
    }

    /**
     * Whether this guild has loaded members.
     * <br>This will always be false if the {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS} intent is disabled.
     *
     * @return True, if members are loaded.
     */
    boolean isLoaded();

    /**
     * Re-apply the {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy} of this session to all {@link Member Members} of this Guild.
     *
     * <h2>Example</h2>
     * <pre>{@code
     * // Check if the members of this guild have at least 50% bots (bot collection/farm)
     * public void checkBots(Guild guild) {
     *     // Keep in mind: This requires the GUILD_MEMBERS intent which is disabled in createDefault and createLight by default
     *     guild.retrieveMembers() // Load members CompletableFuture<Void> (async and eager)
     *          .thenApply((v) -> guild.getMemberCache()) // Turn into CompletableFuture<MemberCacheView>
     *          .thenAccept((members) -> {
     *              int total = members.size();
     *              // Casting to double to get a double as result of division, don't need to worry about precision with small counts like this
     *              double bots = (double) members.applyStream(stream ->
     *                  stream.map(Member::getUser)
     *                        .filter(User::isBot)
     *                        .count()); // Count bots
     *              if (bots / total > 0.5) // Check how many members are bots
     *                  System.out.println("More than 50% of members in this guild are bots");
     *          })
     *          .thenRun(guild::pruneMemberCache); // Then prune the cache
     * }
     * }</pre>
     *
     * @see #unloadMember(long)
     * @see JDA#unloadUser(long)
     */
    void pruneMemberCache();

    /**
     * Attempts to remove the user with the provided id from the member cache.
     * <br>If you attempt to remove the {@link JDA#getSelfUser() SelfUser} this will simply return {@code false}.
     *
     * <p>This should be used by an implementation of {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     * as an upstream request to remove a member. For example a Least-Recently-Used (LRU) cache might use this to drop
     * old members if the cache capacity is reached. Or a timeout cache could use this to remove expired members.
     *
     * @param  userId
     *         The target user id
     *
     * @return True, if the cache was changed
     *
     * @see    #pruneMemberCache()
     * @see    JDA#unloadUser(long)
     */
    boolean unloadMember(long userId);

    /**
     * The expected member count for this guild.
     * <br>If this guild is not lazy loaded this should be identical to the size returned by {@link #getMemberCache()}.
     *
     * <p>When {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} is disabled, this will not be updated.
     *
     * @return The expected member count for this guild
     */
    int getMemberCount();

    /**
     * The human readable name of the {@link Guild Guild}.
     * <p>
     * This value can be modified using {@link GuildManager#setName(String)}.
     *
     * @return Never-null String containing the Guild's name.
     */
    @Nonnull
    String getName();

    /**
     * The Discord hash-id of the {@link Guild Guild} icon image.
     * If no icon has been set, this returns {@code null}.
     * <p>
     * The Guild icon can be modified using {@link GuildManager#setIcon(Icon)}.
     *
     * @return Possibly-null String containing the Guild's icon hash-id.
     */
    @Nullable
    String getIconId();

    /**
     * The URL of the {@link Guild Guild} icon image.
     * If no icon has been set, this returns {@code null}.
     * <p>
     * The Guild icon can be modified using {@link GuildManager#setIcon(Icon)}.
     *
     * @return Possibly-null String containing the Guild's icon URL.
     */
    @Nullable
    default String getIconUrl()
    {
        String iconId = getIconId();
        return iconId == null ? null : String.format(ICON_URL, getId(), iconId, iconId.startsWith("a_") ? "gif" : "png");
    }

    /**
     * The Features of the {@link Guild Guild}.
     * <p>
     * <b>Possible known features:</b>
     * <ul>
     *     <li>ANIMATED_ICON - Guild can have an animated icon</li>
     *     <li>BANNER - Guild can have a banner</li>
     *     <li>COMMERCE - Guild can sell software through a store channel</li>
     *     <li>DISCOVERABLE - Guild shows up in discovery tab</li>
     *     <li>INVITE_SPLASH - Guild has custom invite splash. See {@link #getSplashId()} and {@link #getSplashUrl()}</li>
     *     <li>MORE_EMOJI - Guild is able to use more than 50 emoji</li>
     *     <li>NEWS - Guild can create news channels</li>
     *     <li>PARTNERED - Guild is "partnered"</li>
     *     <li>PUBLIC - Guild is public</li>
     *     <li>VANITY_URL - Guild a vanity URL (custom invite link). See {@link #getVanityUrl()}</li>
     *     <li>VERIFIED - Guild is "verified"</li>
     *     <li>VIP_REGIONS - Guild has VIP voice regions</li>
     * </ul>
     *
     * @return Never-null, unmodifiable Set containing all of the Guild's features.
     */
    @Nonnull
    Set<String> getFeatures();

    /**
     * The Discord hash-id of the splash image for this Guild. A Splash image is an image displayed when viewing a
     * Discord Guild Invite on the web or in client just before accepting or declining the invite.
     * If no splash has been set, this returns {@code null}.
     * <br>Splash images are VIP/Partner Guild only.
     * <p>
     * The Guild splash can be modified using {@link GuildManager#setSplash(Icon)}.
     *
     * @return Possibly-null String containing the Guild's splash hash-id
     */
    @Nullable
    String getSplashId();

    /**
     * The URL of the splash image for this Guild. A Splash image is an image displayed when viewing a
     * Discord Guild Invite on the web or in client just before accepting or declining the invite.
     * If no splash has been set, this returns {@code null}.
     * <br>Splash images are VIP/Partner Guild only.
     * <p>
     * The Guild splash can be modified using {@link GuildManager#setSplash(Icon)}.
     *
     * @return Possibly-null String containing the Guild's splash URL.
     */
    @Nullable
    default String getSplashUrl()
    {
        String splashId = getSplashId();
        return splashId == null ? null : String.format(SPLASH_URL, getId(), splashId);
    }

    /**
     * Gets the vanity url for this Guild. The vanity url is the custom invite code of partnered / official Guilds.
     * The returned String will be the code that can be provided to {@code discord.gg/{code}} to get the invite link.
     * <br>You can check {@link #getFeatures()} to see if this Guild has a vanity url
     * <p>
     * This action requires the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission.
     * <p>
     * Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
     * </ul>
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission.
     * @throws IllegalStateException
     *         If the guild doesn't have the VANITY_URL feature
     *
     * @return {@link RestAction RestAction} - Type: String
     *         <br>The vanity url of this server
     *
     * @see    #getFeatures()
     * @see    #getVanityCode()
     */
    @Nonnull
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getVanityCode()")
    @CheckReturnValue
    RestAction<String> retrieveVanityUrl();

    /**
     * The vanity url code for this Guild. The vanity url is the custom invite code of partnered / official / boosted Guilds.
     * <br>The returned String will be the code that can be provided to {@code discord.gg/{code}} to get the invite link.
     *
     * <p>The Vanity Code can be modified using {@link GuildManager#setVanityCode(String)}.
     *
     * @return The vanity code or null
     *
     * @since  4.0.0
     *
     * @see    #getVanityUrl()
     */
    @Nullable
    String getVanityCode();

    /**
     * The vanity url for this Guild. The vanity url is the custom invite code of partnered / official / boosted Guilds.
     * <br>The returned String will be the vanity invite link to this guild.
     *
     * <p>The Vanity Code can be modified using {@link GuildManager#setVanityCode(String)}.
     *
     * @return The vanity url or null
     *
     * @since  4.0.0
     */
    @Nullable
    default String getVanityUrl()
    {
        return getVanityCode() == null ? null : "https://discord.gg/" + getVanityCode();
    }

    /**
     * The description for this guild.
     * <br>This is displayed in the server browser below the guild name for verified guilds.
     *
     * <p>The description can be modified using {@link GuildManager#setDescription(String)}.
     *
     * @return The description
     *
     * @since  4.0.0
     */
    @Nullable
    String getDescription();

    /**
     * The guild banner id.
     * <br>This is shown in guilds below the guild name.
     *
     * <p>The banner can be modified using {@link GuildManager#setBanner(Icon)}.
     *
     * @return The guild banner id or null
     *
     * @since  4.0.0
     *
     * @see    #getBannerUrl()
     */
    @Nullable
    String getBannerId();

    /**
     * The guild banner url.
     * <br>This is shown in guilds below the guild name.
     *
     * <p>The banner can be modified using {@link GuildManager#setBanner(Icon)}.
     *
     * @return The guild banner url or null
     *
     * @since  4.0.0
     */
    @Nullable
    default String getBannerUrl()
    {
        String bannerId = getBannerId();
        return bannerId == null ? null : String.format(BANNER_URL, getId(), bannerId);
    }

    /**
     * The boost tier for this guild.
     * <br>Each tier unlocks new perks for a guild that can be seen in the {@link #getFeatures() features}.
     *
     * @return The boost tier.
     *
     * @since  4.0.0
     */
    @Nonnull
    BoostTier getBoostTier();

    /**
     * The amount of boosts this server currently has.
     *
     * @return The boost count
     *
     * @since  4.0.0
     */
    int getBoostCount();

    /**
     * Sorted list of {@link Member Members} that boost this guild.
     * <br>The list is sorted by {@link Member#getTimeBoosted()} ascending.
     * This means the first element will be the member who has been boosting for the longest time.
     *
     * @return Possibly-immutable list of members who boost this guild
     */
    @Nonnull
    List<Member> getBoosters();

    /**
     * The maximum bitrate that can be applied to a voice channel in this guild.
     * <br>This depends on the features of this guild that can be unlocked for partners or through boosting.
     *
     * @return The maximum bitrate
     *
     * @since  4.0.0
     */
    default int getMaxBitrate()
    {
        int maxBitrate = getFeatures().contains("VIP_REGIONS") ? 384000 : 96000;
        return Math.max(maxBitrate, getBoostTier().getMaxBitrate());
    }

    /**
     * Returns the maximum size for files that can be uploaded to this Guild.
     * This returns 8 MiB for Guilds without a Boost Tier or Guilds with Boost Tier 1, 50 MiB for Guilds with Boost Tier 2 and 100 MiB for Guilds with Boost Tier 3.
     *
     * @return The maximum size for files that can be uploaded to this Guild
     *
     * @since 4.2.0
     */
    default long getMaxFileSize()
    {
        return getBoostTier().getMaxFileSize();
    }

    /**
     * The maximum amount of emotes a guild can have based on the guilds boost tier.
     *
     * @return The maximum amount of emotes
     *
     * @since 4.0.0
     */
    default int getMaxEmotes()
    {
        int maxEmotes = getFeatures().contains("MORE_EMOJI") ? 200 : 50;
        return Math.max(maxEmotes, getBoostTier().getMaxEmotes());
    }

    /**
     * The maximum amount of members that can join this guild.
     *
     * @return The maximum amount of members
     *
     * @since  4.0.0
     *
     * @see    #retrieveMetaData()
     */
    int getMaxMembers();

    /**
     * The maximum amount of connected members this guild can have at a time.
     * <br>This includes members that are invisible but still connected to discord.
     * If too many members are online the guild will become unavailable for others.
     *
     * @return The maximum amount of connected members this guild can have
     *
     * @since  4.0.0
     *
     * @see    #retrieveMetaData()
     */
    int getMaxPresences();

    /**
     * Loads {@link MetaData} for this guild instance.
     *
     * @return {@link RestAction} - Type: {@link MetaData}
     *
     * @since  4.2.0
     */
    @Nonnull
    @CheckReturnValue
    RestAction<MetaData> retrieveMetaData();

    /**
     * Provides the {@link VoiceChannel VoiceChannel} that has been set as the channel
     * which {@link Member Members} will be moved to after they have been inactive in a
     * {@link VoiceChannel VoiceChannel} for longer than {@link #getAfkTimeout()}.
     * <br>If no channel has been set as the AFK channel, this returns {@code null}.
     * <p>
     * This value can be modified using {@link GuildManager#setAfkChannel(VoiceChannel)}.
     *
     * @return Possibly-null {@link VoiceChannel VoiceChannel} that is the AFK Channel.
     */
    @Nullable
    VoiceChannel getAfkChannel();

    /**
     * Provides the {@link TextChannel TextChannel} that has been set as the channel
     * which newly joined {@link Member Members} will be announced in.
     * <br>If no channel has been set as the system channel, this returns {@code null}.
     * <p>
     * This value can be modified using {@link GuildManager#setSystemChannel(TextChannel)}.
     *
     * @return Possibly-null {@link TextChannel TextChannel} that is the system Channel.
     */
    @Nullable
    TextChannel getSystemChannel();

    /**
     * The {@link Member Member} object for the owner of this Guild.
     * <br>This is null when the owner is no longer in this guild or not yet loaded (lazy loading).
     * Sometimes owners of guilds delete their account or get banned by Discord.
     *
     * <p>If lazy-loading is used it is recommended to use {@link #retrieveOwner()} instead.
     *
     * <p>Ownership can be transferred using {@link Guild#transferOwnership(Member)}.
     *
     * <p>This only works when the member was added to cache. Lazy loading might load this later.
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @return Possibly-null Member object for the Guild owner.
     *
     * @see    #getOwnerIdLong()
     * @see    #retrieveOwner()
     */
    @Nullable
    Member getOwner();

    /**
     * The ID for the current owner of this guild.
     * <br>This is useful for debugging purposes or as a shortcut.
     *
     * @return The ID for the current owner
     *
     * @see    #getOwner()
     */
    long getOwnerIdLong();

    /**
     * The ID for the current owner of this guild.
     * <br>This is useful for debugging purposes or as a shortcut.
     *
     * @return The ID for the current owner
     *
     * @see    #getOwner()
     */
    @Nonnull
    default String getOwnerId()
    {
        return Long.toUnsignedString(getOwnerIdLong());
    }

    /**
     * The {@link Timeout Timeout} set for this Guild representing the amount of time
     * that must pass for a Member to have had no activity in a {@link VoiceChannel VoiceChannel}
     * to be considered AFK. If {@link #getAfkChannel()} is not {@code null} (thus an AFK channel has been set) then Member
     * will be automatically moved to the AFK channel after they have been inactive for longer than the returned Timeout.
     * <br>Default is {@link Timeout#SECONDS_300 300 seconds (5 minutes)}.
     * <p>
     * This value can be modified using {@link GuildManager#setAfkTimeout(Timeout)}.
     *
     * @return The {@link Timeout Timeout} set for this Guild.
     */
    @Nonnull
    Timeout getAfkTimeout();

    /**
     * The Voice {@link Region Region} that this Guild is
     * using for audio connections.
     * <br>If the Region is not recognized, returns {@link Region#UNKNOWN UNKNOWN} but you
     * can still use the {@link #getRegionRaw()} to retrieve the raw name this region has.
     *
     * <p>This value can be modified using {@link GuildManager#setRegion(Region)}.
     *
     * @return The the audio Region this Guild is using for audio connections. Can return Region.UNKNOWN.
     */
    @Nonnull
    default Region getRegion()
    {
        return Region.fromKey(getRegionRaw());
    }

    /**
     * The raw voice region name that this Guild is using
     * for audio connections.
     * <br>This is resolved to an enum constant of {@link Region Region} by {@link #getRegion()}!
     *
     * <p>This value can be modified using {@link GuildManager#setRegion(Region)}.
     *
     * @return Raw region name
     */
    @Nonnull
    String getRegionRaw();

    /**
     * Used to determine if the provided {@link User User} is a member of this Guild.
     *
     * <p>This will only check cached members!
     *
     * @param  user
     *         The user to determine whether or not they are a member of this guild.
     *
     * @return True - if this user is present in this guild.
     */
    boolean isMember(@Nonnull User user);

    /**
     * Gets the {@link Member Member} object of the currently logged in account in this guild.
     * <br>This is basically {@link JDA#getSelfUser()} being provided to {@link #getMember(User)}.
     *
     * @return The Member object of the currently logged in account.
     */
    @Nonnull
    Member getSelfMember();

    /**
     * Gets the Guild specific {@link Member Member} object for the provided
     * {@link User User}.
     * <br>If the user is not in this guild, {@code null} is returned.
     *
     * <p>This will only check cached members!
     *
     * @param  user
     *         The {@link User User} which to retrieve a related Member object for.
     *
     * @throws IllegalArgumentException
     *         If the provided user is null
     *
     * @return Possibly-null {@link Member Member} for the related {@link User User}.
     *
     * @see    #retrieveMember(User)
     */
    @Nullable
    Member getMember(@Nonnull User user);

    /**
     * Gets a {@link Member Member} object via the id of the user. The id relates to
     * {@link User#getId()}, and this method is similar to {@link JDA#getUserById(String)}
     * <br>This is more efficient that using {@link JDA#getUserById(String)} and {@link #getMember(User)}.
     * <br>If no Member in this Guild has the {@code userId} provided, this returns {@code null}.
     *
     * <p>This will only check cached members!
     *
     * @param  userId
     *         The Discord id of the User for which a Member object is requested.
     *
     * @throws NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link Member Member} with the related {@code userId}.
     *
     * @see    #retrieveMemberById(String)
     */
    @Nullable
    default Member getMemberById(@Nonnull String userId)
    {
        return getMemberCache().getElementById(userId);
    }

    /**
     * Gets a {@link Member Member} object via the id of the user. The id relates to
     * {@link User#getIdLong()}, and this method is similar to {@link JDA#getUserById(long)}
     * <br>This is more efficient that using {@link JDA#getUserById(long)} and {@link #getMember(User)}.
     * <br>If no Member in this Guild has the {@code userId} provided, this returns {@code null}.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @param  userId
     *         The Discord id of the User for which a Member object is requested.
     *
     * @return Possibly-null {@link Member Member} with the related {@code userId}.
     *
     * @see    #retrieveMemberById(long)
     */
    @Nullable
    default Member getMemberById(long userId)
    {
        return getMemberCache().getElementById(userId);
    }

    /**
     * Searches for a {@link Member} that has the matching Discord Tag.
     * <br>Format has to be in the form {@code Username#Discriminator} where the
     * username must be between 2 and 32 characters (inclusive) matching the exact casing and the discriminator
     * must be exactly 4 digits.
     * <br>This does not check the {@link Member#getNickname() nickname} of the member
     * but the username.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * <p>This only checks users that are in this guild. If a user exists
     * with the tag that is not available in the {@link #getMemberCache() Member-Cache} it will not be detected.
     * <br>Currently Discord does not offer a way to retrieve a user by their discord tag.
     *
     * @param  tag
     *         The Discord Tag in the format {@code Username#Discriminator}
     *
     * @throws IllegalArgumentException
     *         If the provided tag is null or not in the described format
     *
     * @return The {@link Member} for the discord tag or null if no member has the provided tag
     *
     * @see    JDA#getUserByTag(String)
     */
    @Nullable
    default Member getMemberByTag(@Nonnull String tag)
    {
        User user = getJDA().getUserByTag(tag);
        return user == null ? null : getMember(user);
    }

    /**
     * Searches for a {@link Member} that has the matching Discord Tag.
     * <br>Format has to be in the form {@code Username#Discriminator} where the
     * username must be between 2 and 32 characters (inclusive) matching the exact casing and the discriminator
     * must be exactly 4 digits.
     * <br>This does not check the {@link Member#getNickname() nickname} of the member
     * but the username.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * <p>This only checks users that are in this guild. If a user exists
     * with the tag that is not available in the {@link #getMemberCache() Member-Cache} it will not be detected.
     * <br>Currently Discord does not offer a way to retrieve a user by their discord tag.
     *
     * @param  username
     *         The name of the user
     * @param  discriminator
     *         The discriminator of the user
     *
     * @throws IllegalArgumentException
     *         If the provided arguments are null or not in the described format
     *
     * @return The {@link Member} for the discord tag or null if no member has the provided tag
     * 
     * @see    #getMemberByTag(String) 
     */
    @Nullable
    default Member getMemberByTag(@Nonnull String username, @Nonnull String discriminator)
    {
        User user = getJDA().getUserByTag(username, discriminator);
        return user == null ? null : getMember(user);
    }

    /**
     * A list of all {@link Member Members} in this Guild.
     * <br>The Members are not provided in any particular order.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getMemberCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Immutable list of all <b>cached</b> members in this Guild.
     *
     * @see    #loadMembers()
     */
    @Nonnull
    default List<Member> getMembers()
    {
        return getMemberCache().asList();
    }

    /**
     * Gets a list of all {@link Member Members} who have the same name as the one provided.
     * <br>This compares against {@link Member#getUser()}{@link User#getName() .getName()}
     * <br>If there are no {@link Member Members} with the provided name, then this returns an empty list.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @param  name
     *         The name used to filter the returned Members.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @throws IllegalArgumentException
     *         If the provided name is null
     *
     * @return Possibly-empty immutable list of all Members with the same name as the name provided.
     *
     * @see    #retrieveMembersByPrefix(String, int)
     */
    @Nonnull
    default List<Member> getMembersByName(@Nonnull String name, boolean ignoreCase)
    {
        return getMemberCache().getElementsByUsername(name, ignoreCase);
    }

    /**
     * Gets a list of all {@link Member Members} who have the same nickname as the one provided.
     * <br>This compares against {@link Member#getNickname()}. If a Member does not have a nickname, the comparison results as false.
     * <br>If there are no {@link Member Members} with the provided name, then this returns an empty list.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @param  nickname
     *         The nickname used to filter the returned Members.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all Members with the same nickname as the nickname provided.
     *
     * @see    #retrieveMembersByPrefix(String, int)
     */
    @Nonnull
    default List<Member> getMembersByNickname(@Nullable String nickname, boolean ignoreCase)
    {
        return getMemberCache().getElementsByNickname(nickname, ignoreCase);
    }

    /**
     * Gets a list of all {@link Member Members} who have the same effective name as the one provided.
     * <br>This compares against {@link Member#getEffectiveName()}}.
     * <br>If there are no {@link Member Members} with the provided name, then this returns an empty list.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @param  name
     *         The name used to filter the returned Members.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @throws IllegalArgumentException
     *         If the provided name is null
     *
     * @return Possibly-empty immutable list of all Members with the same effective name as the name provided.
     *
     * @see    #retrieveMembersByPrefix(String, int)
     */
    @Nonnull
    default List<Member> getMembersByEffectiveName(@Nonnull String name, boolean ignoreCase)
    {
        return getMemberCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Gets a list of {@link Member Members} that have all {@link Role Roles} provided.
     * <br>If there are no {@link Member Members} with all provided roles, then this returns an empty list.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @param  roles
     *         The {@link Role Roles} that a {@link Member Member}
     *         must have to be included in the returned list.
     *
     * @throws IllegalArgumentException
     *         If a provided {@link Role Role} is from a different guild or null.
     *
     * @return Possibly-empty immutable list of Members with all provided Roles.
     */
    @Nonnull
    default List<Member> getMembersWithRoles(@Nonnull Role... roles)
    {
        return getMemberCache().getElementsWithRoles(roles);
    }

    /**
     * Gets a list of {@link Member Members} that have all provided {@link Role Roles}.
     * <br>If there are no {@link Member Members} with all provided roles, then this returns an empty list.
     *
     * <p>This will only check cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @param  roles
     *         The {@link Role Roles} that a {@link Member Member}
     *         must have to be included in the returned list.
     *
     * @throws IllegalArgumentException
     *         If a provided {@link Role Role} is from a different guild or null.
     *
     * @return Possibly-empty immutable list of Members with all provided Roles.
     */
    @Nonnull
    default List<Member> getMembersWithRoles(@Nonnull Collection<Role> roles)
    {
        return getMemberCache().getElementsWithRoles(roles);
    }

    /**
     * {@link MemberCacheView MemberCacheView} for all cached
     * {@link Member Members} of this Guild.
     *
     * <p>This will only provide cached members!
     * <br>See {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     *
     * @return {@link MemberCacheView MemberCacheView}
     *
     * @see    #loadMembers()
     */
    @Nonnull
    MemberCacheView getMemberCache();

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     * <br>This checks if any of the channel types in this guild have the provided ID and returns the first match.
     *
     * <br>To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(String)}</li>
     *     <li>{@link #getVoiceChannelById(String)}</li>
     *     <li>{@link #getStoreChannelById(String)}</li>
     *     <li>{@link #getCategoryById(String)}</li>
     * </ul>
     *
     * @param  id
     *         The ID of the channel
     *
     * @throws IllegalArgumentException
     *         If the provided ID is null
     * @throws NumberFormatException
     *         If the provided ID is not a snowflake
     *
     * @return The GuildChannel or null
     */
    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull String id)
    {
        return getGuildChannelById(MiscUtil.parseSnowflake(id));
    }

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     * <br>This checks if any of the channel types in this guild have the provided ID and returns the first match.
     *
     * <br>To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(long)}</li>
     *     <li>{@link #getVoiceChannelById(long)}</li>
     *     <li>{@link #getStoreChannelById(long)}</li>
     *     <li>{@link #getCategoryById(long)}</li>
     * </ul>
     *
     * @param  id
     *         The ID of the channel
     *
     * @return The GuildChannel or null
     */
    @Nullable
    default GuildChannel getGuildChannelById(long id)
    {
        GuildChannel channel = getTextChannelById(id);
        if (channel == null)
            channel = getVoiceChannelById(id);
        if (channel == null)
            channel = getStoreChannelById(id);
        if (channel == null)
            channel = getCategoryById(id);
        return channel;
    }

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     *
     * <br>This is meant for systems that use a dynamic {@link ChannelType} and can
     * profit from a simple function to get the channel instance.
     * To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(String)}</li>
     *     <li>{@link #getVoiceChannelById(String)}</li>
     *     <li>{@link #getStoreChannelById(String)}</li>
     *     <li>{@link #getCategoryById(String)}</li>
     * </ul>
     *
     * @param  type
     *         The {@link ChannelType}
     * @param  id
     *         The ID of the channel
     *
     * @throws IllegalArgumentException
     *         If the provided ID is null
     * @throws NumberFormatException
     *         If the provided ID is not a snowflake
     *
     * @return The GuildChannel or null
     */
    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, @Nonnull String id)
    {
        return getGuildChannelById(type, MiscUtil.parseSnowflake(id));
    }

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     *
     * <br>This is meant for systems that use a dynamic {@link ChannelType} and can
     * profit from a simple function to get the channel instance.
     * To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(long)}</li>
     *     <li>{@link #getVoiceChannelById(long)}</li>
     *     <li>{@link #getStoreChannelById(long)}</li>
     *     <li>{@link #getCategoryById(long)}</li>
     * </ul>
     *
     * @param  type
     *         The {@link ChannelType}
     * @param  id
     *         The ID of the channel
     *
     * @return The GuildChannel or null
     */
    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id)
    {
        Checks.notNull(type, "ChannelType");
        switch (type)
        {
            case TEXT:
                return getTextChannelById(id);
            case VOICE:
                return getVoiceChannelById(id);
            case STORE:
                return getStoreChannelById(id);
            case CATEGORY:
                return getCategoryById(id);
        }
        return null;
    }

    /**
     * Gets the {@link Category Category} from this guild that matches the provided id.
     * This method is similar to {@link JDA#getCategoryById(String)}, but it only checks in this
     * specific Guild. <br>If there is no matching {@link Category Category} this returns
     * {@code null}.
     *
     * @param  id
     *         The snowflake ID of the wanted Category
     *
     * @throws IllegalArgumentException
     *         If the provided ID is not a valid {@code long}
     *
     * @return Possibly-null {@link Category Category} for the provided ID.
     */
    @Nullable
    default Category getCategoryById(@Nonnull String id)
    {
        return getCategoryCache().getElementById(id);
    }

    /**
     * Gets the {@link Category Category} from this guild that matches the provided id.
     * This method is similar to {@link JDA#getCategoryById(String)}, but it only checks in this
     * specific Guild. <br>If there is no matching {@link Category Category} this returns
     * {@code null}.
     *
     * @param  id
     *         The snowflake ID of the wanted Category
     *
     * @return Possibly-null {@link Category Category} for the provided ID.
     */
    @Nullable
    default Category getCategoryById(long id)
    {
        return getCategoryCache().getElementById(id);
    }

    /**
     * Gets all {@link Category Categories} in this {@link Guild Guild}.
     * <br>The returned categories will be sorted according to their position.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getCategoryCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable list of all {@link Category Categories} in this Guild.
     */
    @Nonnull
    default List<Category> getCategories()
    {
        return getCategoryCache().asList();
    }

    /**
     * Gets a list of all {@link Category Categories} in this Guild that have the same
     * name as the one provided. <br>If there are no matching categories this will return an empty list.
     *
     * @param  name
     *         The name to check
     * @param  ignoreCase
     *         Whether to ignore case on name checking
     *
     * @throws IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of all categories matching the provided name
     */
    @Nonnull
    default List<Category> getCategoriesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getCategoryCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Sorted {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Category Categories} of this Guild.
     * <br>Categories are sorted according to their position.
     *
     * @return {@link SortedSnowflakeCacheView SortedSnowflakeCacheView}
     */
    @Nonnull
    SortedSnowflakeCacheView<Category> getCategoryCache();

    /**
     * Gets a {@link StoreChannel StoreChannel} from this guild that has the same id as the
     * one provided. This method is similar to {@link JDA#getStoreChannelById(String)}, but it only
     * checks this specific Guild for a StoreChannel.
     * <br>If there is no {@link StoreChannel StoreChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link StoreChannel StoreChannel}.
     *
     * @throws NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link StoreChannel StoreChannel} with matching id.
     *
     * @since  4.0.0
     */
    @Nullable
    default StoreChannel getStoreChannelById(@Nonnull String id)
    {
        return getStoreChannelCache().getElementById(id);
    }

    /**
     * Gets a {@link StoreChannel StoreChannel} from this guild that has the same id as the
     * one provided. This method is similar to {@link JDA#getStoreChannelById(long)}, but it only
     * checks this specific Guild for a StoreChannel.
     * <br>If there is no {@link StoreChannel StoreChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link StoreChannel StoreChannel}.
     *
     * @return Possibly-null {@link StoreChannel StoreChannel} with matching id.
     *
     * @since  4.0.0
     */
    @Nullable
    default StoreChannel getStoreChannelById(long id)
    {
        return getStoreChannelCache().getElementById(id);
    }

    /**
     * Gets all {@link StoreChannel StoreChannels} in this {@link Guild Guild}.
     * <br>The channels returned will be sorted according to their position.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getStoreChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable List of all {@link StoreChannel StoreChannel} in this Guild.
     *
     * @since  4.0.0
     */
    @Nonnull
    default List<StoreChannel> getStoreChannels()
    {
        return getStoreChannelCache().asList();
    }

    /**
     * Gets a list of all {@link StoreChannel StoreChannels} in this Guild that have the same
     * name as the one provided.
     * <br>If there are no {@link StoreChannel StoreChannels} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name used to filter the returned {@link StoreChannel StoreChannels}.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all StoreChannels with names that match the provided name.
     *
     * @since  4.0.0
     */
    @Nonnull
    default List<StoreChannel> getStoreChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getStoreChannelCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Sorted {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link StoreChannel StoreChannels} of this Guild.
     * <br>TextChannels are sorted according to their position.
     *
     * @return {@link SortedSnowflakeCacheView SortedSnowflakeCacheView}
     *
     * @since  4.0.0
     */
    @Nonnull
    SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache();

    /**
     * Gets a {@link TextChannel TextChannel} from this guild that has the same id as the
     * one provided. This method is similar to {@link JDA#getTextChannelById(String)}, but it only
     * checks this specific Guild for a TextChannel.
     * <br>If there is no {@link TextChannel TextChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link TextChannel TextChannel}.
     *
     * @throws NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link TextChannel TextChannel} with matching id.
     */
    @Nullable
    default TextChannel getTextChannelById(@Nonnull String id)
    {
        return getTextChannelCache().getElementById(id);
    }

    /**
     * Gets a {@link TextChannel TextChannel} from this guild that has the same id as the
     * one provided. This method is similar to {@link JDA#getTextChannelById(long)}, but it only
     * checks this specific Guild for a TextChannel.
     * <br>If there is no {@link TextChannel TextChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link TextChannel TextChannel}.
     *
     * @return Possibly-null {@link TextChannel TextChannel} with matching id.
     */
    @Nullable
    default TextChannel getTextChannelById(long id)
    {
        return getTextChannelCache().getElementById(id);
    }

    /**
     * Gets all {@link TextChannel TextChannels} in this {@link Guild Guild}.
     * <br>The channels returned will be sorted according to their position.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getTextChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable List of all {@link TextChannel TextChannels} in this Guild.
     */
    @Nonnull
    default List<TextChannel> getTextChannels()
    {
        return getTextChannelCache().asList();
    }

    /**
     * Gets a list of all {@link TextChannel TextChannels} in this Guild that have the same
     * name as the one provided.
     * <br>If there are no {@link TextChannel TextChannels} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name used to filter the returned {@link TextChannel TextChannels}.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all TextChannels names that match the provided name.
     */
    @Nonnull
    default List<TextChannel> getTextChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getTextChannelCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Sorted {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link TextChannel TextChannels} of this Guild.
     * <br>TextChannels are sorted according to their position.
     *
     * @return {@link SortedSnowflakeCacheView SortedSnowflakeCacheView}
     */
    @Nonnull
    SortedSnowflakeCacheView<TextChannel> getTextChannelCache();

    /**
     * Gets a {@link VoiceChannel VoiceChannel} from this guild that has the same id as the
     * one provided. This method is similar to {@link JDA#getVoiceChannelById(String)}, but it only
     * checks this specific Guild for a VoiceChannel.
     * <br>If there is no {@link VoiceChannel VoiceChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link VoiceChannel VoiceChannel}.
     *
     * @throws NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link VoiceChannel VoiceChannel} with matching id.
     */
    @Nullable
    default VoiceChannel getVoiceChannelById(@Nonnull String id)
    {
        return getVoiceChannelCache().getElementById(id);
    }

    /**
     * Gets a {@link VoiceChannel VoiceChannel} from this guild that has the same id as the
     * one provided. This method is similar to {@link JDA#getVoiceChannelById(long)}, but it only
     * checks this specific Guild for a VoiceChannel.
     * <br>If there is no {@link VoiceChannel VoiceChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link VoiceChannel VoiceChannel}.
     *
     * @return Possibly-null {@link VoiceChannel VoiceChannel} with matching id.
     */
    @Nullable
    default VoiceChannel getVoiceChannelById(long id)
    {
        return getVoiceChannelCache().getElementById(id);
    }

    /**
     * Gets all {@link VoiceChannel VoiceChannels} in this {@link Guild Guild}.
     * <br>The channels returned will be sorted according to their position.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getVoiceChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable List of {@link VoiceChannel VoiceChannels}.
     */
    @Nonnull
    default List<VoiceChannel> getVoiceChannels()
    {
        return getVoiceChannelCache().asList();
    }

    /**
     * Gets a list of all {@link VoiceChannel VoiceChannels} in this Guild that have the same
     * name as the one provided.
     * <br>If there are no {@link VoiceChannel VoiceChannels} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name used to filter the returned {@link VoiceChannel VoiceChannels}.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all VoiceChannel names that match the provided name.
     */
    @Nonnull
    default List<VoiceChannel> getVoiceChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getVoiceChannelCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Sorted {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link VoiceChannel VoiceChannels} of this Guild.
     * <br>VoiceChannels are sorted according to their position.
     *
     * @return {@link SortedSnowflakeCacheView SortedSnowflakeCacheView}
     */
    @Nonnull
    SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache();

    /**
     * Populated list of {@link GuildChannel channels} for this guild.
     * This includes all types of channels, such as category/voice/text.
     * <br>This includes hidden channels by default.
     *
     * <p>The returned list is ordered in the same fashion as it would be by the official discord client.
     * <ol>
     *     <li>TextChannel and StoreChannel without parent</li>
     *     <li>VoiceChannel without parent</li>
     *     <li>Categories
     *         <ol>
     *             <li>TextChannel and StoreChannel with category as parent</li>
     *             <li>VoiceChannel with category as parent</li>
     *         </ol>
     *     </li>
     * </ol>
     *
     * @return Immutable list of channels for this guild
     *
     * @see    #getChannels(boolean)
     */
    @Nonnull
    default List<GuildChannel> getChannels()
    {
        return getChannels(true);
    }

    /**
     * Populated list of {@link GuildChannel channels} for this guild.
     * This includes all types of channels, such as category/voice/text.
     *
     * <p>The returned list is ordered in the same fashion as it would be by the official discord client.
     * <ol>
     *     <li>TextChannel and StoreChannel without parent</li>
     *     <li>VoiceChannel without parent</li>
     *     <li>Categories
     *         <ol>
     *             <li>TextChannel and StoreChannel with category as parent</li>
     *             <li>VoiceChannel with category as parent</li>
     *         </ol>
     *     </li>
     * </ol>
     *
     *
     * @param  includeHidden
     *         Whether to include channels with denied {@link Permission#VIEW_CHANNEL View Channel Permission}
     *
     * @return Immutable list of channels for this guild
     *
     * @see    #getChannels()
     */
    @Nonnull
    List<GuildChannel> getChannels(boolean includeHidden);

    /**
     * Gets a {@link Role Role} from this guild that has the same id as the
     * one provided.
     * <br>If there is no {@link Role Role} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link Role Role}.
     *
     * @throws NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link Role Role} with matching id.
     */
    @Nullable
    default Role getRoleById(@Nonnull String id)
    {
        return getRoleCache().getElementById(id);
    }

    /**
     * Gets a {@link Role Role} from this guild that has the same id as the
     * one provided.
     * <br>If there is no {@link Role Role} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link Role Role}.
     *
     * @return Possibly-null {@link Role Role} with matching id.
     */
    @Nullable
    default Role getRoleById(long id)
    {
        return getRoleCache().getElementById(id);
    }

    /**
     * Gets all {@link Role Roles} in this {@link Guild Guild}.
     * <br>The roles returned will be sorted according to their position. The highest role being at index 0
     * and the lowest at the last index.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getRoleCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable List of {@link Role Roles}.
     */
    @Nonnull
    default List<Role> getRoles()
    {
        return getRoleCache().asList();
    }

    /**
     * Gets a list of all {@link Role Roles} in this Guild that have the same
     * name as the one provided.
     * <br>If there are no {@link Role Roles} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name used to filter the returned {@link Role Roles}.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all Role names that match the provided name.
     */
    @Nonnull
    default List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getRoleCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Sorted {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Role Roles} of this Guild.
     * <br>Roles are sorted according to their position.
     *
     * @return {@link SortedSnowflakeCacheView SortedSnowflakeCacheView}
     */
    @Nonnull
    SortedSnowflakeCacheView<Role> getRoleCache();

    /**
     * Gets an {@link Emote Emote} from this guild that has the same id as the
     * one provided.
     * <br>If there is no {@link Emote Emote} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * <p>This requires the {@link net.dv8tion.jda.api.utils.cache.CacheFlag#EMOTE CacheFlag.EMOTE} to be enabled!
     *
     * @param  id
     *         the emote id
     *
     * @throws NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return An Emote matching the specified Id.
     *
     * @see    #retrieveEmoteById(String)
     */
    @Nullable
    default Emote getEmoteById(@Nonnull String id)
    {
        return getEmoteCache().getElementById(id);
    }

    /**
     * Gets an {@link Emote Emote} from this guild that has the same id as the
     * one provided.
     * <br>If there is no {@link Emote Emote} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * <p>This requires the {@link net.dv8tion.jda.api.utils.cache.CacheFlag#EMOTE CacheFlag.EMOTE} to be enabled!
     *
     * @param  id
     *         the emote id
     *
     * @return An Emote matching the specified Id.
     *
     * @see    #retrieveEmoteById(long)
     */
    @Nullable
    default Emote getEmoteById(long id)
    {
        return getEmoteCache().getElementById(id);
    }

    /**
     * Gets all custom {@link Emote Emotes} belonging to this {@link Guild Guild}.
     * <br>Emotes are not ordered in any specific way in the returned list.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getEmoteCache()} and use its more efficient
     * versions of handling these values.
     *
     * <p>This requires the {@link net.dv8tion.jda.api.utils.cache.CacheFlag#EMOTE CacheFlag.EMOTE} to be enabled!
     *
     * @return An immutable List of {@link Emote Emotes}.
     *
     * @see    #retrieveEmotes()
     */
    @Nonnull
    default List<Emote> getEmotes()
    {
        return getEmoteCache().asList();
    }

    /**
     * Gets a list of all {@link Emote Emotes} in this Guild that have the same
     * name as the one provided.
     * <br>If there are no {@link Emote Emotes} with the provided name, then this returns an empty list.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * <p>This requires the {@link net.dv8tion.jda.api.utils.cache.CacheFlag#EMOTE CacheFlag.EMOTE} to be enabled!
     *
     * @param  name
     *         The name used to filter the returned {@link Emote Emotes}. Without colons.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all Emotes that match the provided name.
     */
    @Nonnull
    default List<Emote> getEmotesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getEmoteCache().getElementsByName(name, ignoreCase);
    }

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Emote Emotes} of this Guild.
     * <br>This will be empty if {@link net.dv8tion.jda.api.utils.cache.CacheFlag#EMOTE} is disabled.
     *
     * <p>This requires the {@link net.dv8tion.jda.api.utils.cache.CacheFlag#EMOTE CacheFlag.EMOTE} to be enabled!
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     *
     * @see    #retrieveEmotes()
     */
    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();

    /**
     * Retrieves an immutable list of emotes together with their respective creators.
     *
     * <p>Note that {@link ListedEmote#getUser()} is only available if the currently
     * logged in account has {@link Permission#MANAGE_EMOTES Permission.MANAGE_EMOTES}.
     *
     * @return {@link RestAction RestAction} - Type: List of {@link ListedEmote ListedEmote}
     *
     * @since  3.8.0
     */
    @Nonnull
    @CheckReturnValue
    RestAction<List<ListedEmote>> retrieveEmotes();

    /**
     * Retrieves a listed emote together with its respective creator.
     * <br><b>This does not include unicode emoji.</b>
     *
     * <p>Note that {@link ListedEmote#getUser()} is only available if the currently
     * logged in account has {@link Permission#MANAGE_EMOTES Permission.MANAGE_EMOTES}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_EMOJI UNKNOWN_EMOJI}
     *     <br>If the provided id does not correspond to an emote in this guild</li>
     * </ul>
     *
     * @param  id
     *         The emote id
     *
     * @throws IllegalArgumentException
     *         If the provided id is not a valid snowflake
     *
     * @return {@link RestAction RestAction} - Type: {@link ListedEmote ListedEmote}
     *
     * @since  3.8.0
     */
    @Nonnull
    @CheckReturnValue
    RestAction<ListedEmote> retrieveEmoteById(@Nonnull String id);

    /**
     * Retrieves a listed emote together with its respective creator.
     *
     * <p>Note that {@link ListedEmote#getUser()} is only available if the currently
     * logged in account has {@link Permission#MANAGE_EMOTES Permission.MANAGE_EMOTES}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_EMOJI UNKNOWN_EMOJI}
     *     <br>If the provided id does not correspond to an emote in this guild</li>
     * </ul>
     *
     * @param  id
     *         The emote id
     *
     * @return {@link RestAction RestAction} - Type: {@link ListedEmote ListedEmote}
     *
     * @since  3.8.0
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<ListedEmote> retrieveEmoteById(long id)
    {
        return retrieveEmoteById(Long.toUnsignedString(id));
    }

    /**
     * Retrieves a listed emote together with its respective creator.
     *
     * <p>Note that {@link ListedEmote#getUser()} is only available if the currently
     * logged in account has {@link Permission#MANAGE_EMOTES Permission.MANAGE_EMOTES}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_EMOJI UNKNOWN_EMOJI}
     *     <br>If the provided emote does not correspond to an emote in this guild anymore</li>
     * </ul>
     *
     * @param  emote
     *         The emote
     *
     * @return {@link RestAction RestAction} - Type: {@link ListedEmote ListedEmote}
     *
     * @since  3.8.0
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<ListedEmote> retrieveEmote(@Nonnull Emote emote)
    {
        Checks.notNull(emote, "Emote");
        if (emote.getGuild() != null)
            Checks.check(emote.getGuild().equals(this), "Emote must be from the same Guild!");

        JDA jda = getJDA();
        return new DeferredRestAction<>(jda, ListedEmote.class,
        () -> {
            if (emote instanceof ListedEmote && !emote.isFake())
            {
                ListedEmote listedEmote = (ListedEmote) emote;
                if (listedEmote.hasUser() || !getSelfMember().hasPermission(Permission.MANAGE_EMOTES))
                    return listedEmote;
            }
            return null;
        }, () -> retrieveEmoteById(emote.getId()));
    }

    /**
     * Retrieves an immutable list of the currently banned {@link User Users}.
     * <br>If you wish to ban or unban a user, use either {@link #ban(User, int) ban(User, int)} or
     * {@link #unban(User) unban(User)}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
     * </ul>
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     *
     * @return {@link RestAction RestAction} - Type: {@literal List<}{@link Ban Ban}{@literal >}
     *         <br>Retrieves an immutable list of all users currently banned from this Guild
     */
    @Nonnull
    @CheckReturnValue
    RestAction<List<Ban>> retrieveBanList();

    /**
     * Retrieves a {@link Ban Ban} of the provided ID
     * <br>If you wish to ban or unban a user, use either {@link #ban(String, int)} ban(id, int)} or
     * {@link #unban(String)} unban(id)}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_BAN UNKNOWN_BAN}
     *     <br>Either the ban was removed before finishing the task or it did not exist in the first place</li>
     * </ul>
     *
     * @param  userId
     *         the id of the banned user
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     *
     * @return {@link RestAction RestAction} - Type: {@link Ban Ban}
     *         <br>An unmodifiable ban object for the user banned from this guild
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<Ban> retrieveBanById(long userId)
    {
        return retrieveBanById(Long.toUnsignedString(userId));
    }

    /**
     * Retrieves a {@link Ban Ban} of the provided ID
     * <br>If you wish to ban or unban a user, use either {@link #ban(String, int) ban(id, int)} or
     * {@link #unban(String) unban(id)}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_BAN UNKNOWN_BAN}
     *     <br>Either the ban was removed before finishing the task or it did not exist in the first place</li>
     * </ul>
     *
     * @param  userId
     *         the id of the banned user
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     *
     * @return {@link RestAction RestAction} - Type: {@link Ban Ban}
     *         <br>An unmodifiable ban object for the user banned from this guild
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Ban> retrieveBanById(@Nonnull String userId);

    /**
     * Retrieves a {@link Ban Ban} of the provided {@link User User}
     * <br>If you wish to ban or unban a user, use either {@link #ban(User, int) ban(User, int)} or
     * {@link #unban(User) unban(User)}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_BAN UNKNOWN_BAN}
     *     <br>Either the ban was removed before finishing the task or it did not exist in the first place</li>
     * </ul>
     *
     * @param  bannedUser
     *         the banned user
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     *
     * @return {@link RestAction RestAction} - Type: {@link Ban Ban}
     *         <br>An unmodifiable ban object for the user banned from this guild
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<Ban> retrieveBan(@Nonnull User bannedUser)
    {
        Checks.notNull(bannedUser, "bannedUser");
        return retrieveBanById(bannedUser.getId());
    }

    /**
     * The method calculates the amount of Members that would be pruned if {@link #prune(int, Role...)} was executed.
     * Prunability is determined by a Member being offline for at least <i>days</i> days.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The prune count cannot be fetched due to a permission discrepancy</li>
     * </ul>
     *
     * @param  days
     *         Minimum number of days since a member has been offline to get affected.
     *
     * @throws InsufficientPermissionException
     *         If the account doesn't have {@link Permission#KICK_MEMBERS KICK_MEMBER} Permission.
     * @throws IllegalArgumentException
     *         If the provided days are less than {@code 1} or more than {@code 30}
     *
     * @return {@link RestAction RestAction} - Type: Integer
     *         <br>The amount of Members that would be affected.
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Integer> retrievePrunableMemberCount(int days);

    /**
     * The @everyone {@link Role Role} of this {@link Guild Guild}.
     * <br>This role is special because its {@link Role#getPosition() position} is calculated as
     * {@code -1}. All other role positions are 0 or greater. This implies that the public role is <b>always</b> below
     * any custom roles created in this Guild. Additionally, all members of this guild are implied to have this role so
     * it is not included in the list returned by {@link Member#getRoles() Member.getRoles()}.
     * <br>The ID of this Role is the Guild's ID thus it is equivalent to using {@link #getRoleById(long) getRoleById(getIdLong())}.
     *
     * @return The @everyone {@link Role Role}
     */
    @Nonnull
    Role getPublicRole();

    /**
     * The default {@link TextChannel TextChannel} for a {@link Guild Guild}.
     * <br>This is the channel that the Discord client will default to opening when a Guild is opened for the first time when accepting an invite
     * that is not directed at a specific {@link TextChannel TextChannel}.
     *
     * <p>Note: This channel is the first channel in the guild (ordered by position) that the {@link #getPublicRole()}
     * has the {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} in.
     *
     * @return The {@link TextChannel TextChannel} representing the default channel for this guild
     */
    @Nullable
    TextChannel getDefaultChannel();

    /**
     * Returns the {@link GuildManager GuildManager} for this Guild, used to modify
     * all properties and settings of the Guild.
     * <br>You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue() RestAction.queue()}.
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_SERVER Permission.MANAGE_SERVER}
     *
     * @return The Manager of this Guild
     */
    @Nonnull
    GuildManager getManager();

    /**
     * A {@link PaginationAction PaginationAction} implementation
     * that allows to {@link Iterable iterate} over all {@link net.dv8tion.jda.api.audit.AuditLogEntry AuditLogEntries} of
     * this Guild.
     * <br>This iterates from the most recent action to the first logged one. (Limit 90 days into history by discord api)
     *
     * <h1>Examples</h1>
     * <pre><code>
     * public boolean isLogged(Guild guild, ActionType type, long targetId)
     * {
     *     for (AuditLogEntry entry : guild.<u>retrieveAuditLogs().cache(false)</u>)
     *     {
     *         if (entry.getType() == type{@literal &&} entry.getTargetIdLong() == targetId)
     *             return true; // The action is logged
     *     }
     *     return false; // nothing found in audit logs
     * }
     *
     * public{@literal List<AuditLogEntry>} getActionsBy(Guild guild, User user)
     * {
     *     return guild.<u>retrieveAuditLogs().cache(false)</u>.stream()
     *         .filter(it{@literal ->} it.getUser().equals(user))
     *         .collect(Collectors.toList()); // collects actions done by user
     * }
     * </code></pre>
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account
     *         does not have the permission {@link Permission#VIEW_AUDIT_LOGS VIEW_AUDIT_LOGS}
     *
     * @return {@link AuditLogPaginationAction AuditLogPaginationAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditLogPaginationAction retrieveAuditLogs();

    /**
     * Used to leave a Guild. If the currently logged in account is the owner of this guild ({@link Guild#getOwner()})
     * then ownership of the Guild needs to be transferred to a different {@link Member Member}
     * before leaving using {@link #transferOwnership(Member)}.
     *
     * @throws IllegalStateException
     *         Thrown if the currently logged in account is the Owner of this Guild.
     *
     * @return {@link RestAction RestAction} - Type: {@link Void}
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Void> leave();

    /**
     * Used to completely delete a Guild. This can only be done if the currently logged in account is the owner of the Guild.
     * <br>If the account has MFA enabled, use {@link #delete(String)} instead to provide the MFA code.
     *
     * @throws net.dv8tion.jda.api.exceptions.PermissionException
     *         Thrown if the currently logged in account is not the owner of this Guild.
     * @throws IllegalStateException
     *         If the currently logged in account has MFA enabled. ({@link SelfUser#isMfaEnabled()}).
     *
     * @return {@link RestAction} - Type: {@link Void}
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Void> delete();

    /**
     * Used to completely delete a guild. This can only be done if the currently logged in account is the owner of the Guild.
     * <br>This method is specifically used for when MFA is enabled on the logged in account {@link SelfUser#isMfaEnabled()}.
     * If MFA is not enabled, use {@link #delete()}.
     *
     * @param  mfaCode
     *         The Multifactor Authentication code generated by an app like
     *         <a href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2" target="_blank">Google Authenticator</a>.
     *         <br><b>This is not the MFA token given to you by Discord.</b> The code is typically 6 characters long.
     *
     * @throws net.dv8tion.jda.api.exceptions.PermissionException
     *         Thrown if the currently logged in account is not the owner of this Guild.
     * @throws IllegalArgumentException
     *         If the provided {@code mfaCode} is {@code null} or empty when {@link SelfUser#isMfaEnabled()} is true.
     *
     * @return {@link RestAction} - Type: {@link Void}
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Void> delete(@Nullable String mfaCode);

    /**
     * The {@link AudioManager AudioManager} that represents the
     * audio connection for this Guild.
     * <br>If no AudioManager exists for this Guild, this will create a new one.
     * <br>This operation is synchronized on all audio managers for this JDA instance,
     * this means that calling getAudioManager() on any other guild while a thread is accessing this method may be locked.
     *
     * @throws IllegalStateException
     *         If {@link GatewayIntent#GUILD_VOICE_STATES} is disabled
     *
     * @return The AudioManager for this Guild.
     *
     * @see    JDA#getAudioManagerCache() JDA.getAudioManagerCache()
     */
    @Nonnull
    AudioManager getAudioManager();

    /**
     * Returns the {@link JDA JDA} instance of this Guild
     *
     * @return the corresponding JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * Retrieves all {@link Invite Invites} for this guild.
     * <br>Requires {@link Permission#MANAGE_SERVER MANAGE_SERVER} in this guild.
     * Will throw a {@link InsufficientPermissionException InsufficientPermissionException} otherwise.
     *
     * <p>To get all invites for a {@link GuildChannel GuildChannel}
     * use {@link GuildChannel#retrieveInvites() GuildChannel.retrieveInvites()}
     *
     * @throws InsufficientPermissionException
     *         if the account does not have {@link Permission#MANAGE_SERVER MANAGE_SERVER} in this Guild.
     *
     * @return {@link RestAction RestAction} - Type: List{@literal <}{@link Invite Invite}{@literal >}
     *         <br>The list of expanded Invite objects
     *
     * @see     GuildChannel#retrieveInvites()
     */
    @Nonnull
    @CheckReturnValue
    RestAction<List<Invite>> retrieveInvites();

    /**
     * Retrieves all {@link Webhook Webhooks} for this Guild.
     * <br>Requires {@link Permission#MANAGE_WEBHOOKS MANAGE_WEBHOOKS} in this Guild.
     *
     * <p>To get all webhooks for a specific {@link TextChannel TextChannel}, use
     * {@link TextChannel#retrieveWebhooks()}
     *
     * @throws InsufficientPermissionException
     *         if the account does not have {@link Permission#MANAGE_WEBHOOKS MANAGE_WEBHOOKS} in this Guild.
     *
     * @return {@link RestAction RestAction} - Type: List{@literal <}{@link Webhook Webhook}{@literal >}
     *         <br>A list of all Webhooks in this Guild.
     *
     * @see     TextChannel#retrieveWebhooks()
     */
    @Nonnull
    @CheckReturnValue
    RestAction<List<Webhook>> retrieveWebhooks();

    /**
     * A list containing the {@link GuildVoiceState GuildVoiceState} of every {@link Member Member}
     * in this {@link Guild Guild}.
     * <br>This will never return an empty list because if it were empty, that would imply that there are no
     * {@link Member Members} in this {@link Guild Guild}, which is
     * impossible.
     *
     * @return Never-empty immutable list containing all the {@link GuildVoiceState GuildVoiceStates} on this {@link Guild Guild}.
     */
    @Nonnull
    List<GuildVoiceState> getVoiceStates();

    /**
     * Returns the verification-Level of this Guild. Verification level is one of the factors that determines if a Member
     * can send messages in a Guild.
     * <br>For a short description of the different values, see {@link VerificationLevel}.
     * <p>
     * This value can be modified using {@link GuildManager#setVerificationLevel(VerificationLevel)}.
     *
     * @return The Verification-Level of this Guild.
     */
    @Nonnull
    VerificationLevel getVerificationLevel();

    /**
     * Returns the default message Notification-Level of this Guild. Notification level determines when Members get notification
     * for messages. The value returned is the default level set for any new Members that join the Guild.
     * <br>For a short description of the different values, see {@link NotificationLevel NotificationLevel}.
     * <p>
     * This value can be modified using {@link GuildManager#setDefaultNotificationLevel(NotificationLevel)}.
     *
     * @return The default message Notification-Level of this Guild.
     */
    @Nonnull
    NotificationLevel getDefaultNotificationLevel();

    /**
     * Returns the level of multifactor authentication required to execute administrator restricted functions in this guild.
     * <br>For a short description of the different values, see {@link MFALevel MFALevel}.
     * <p>
     * This value can be modified using {@link GuildManager#setRequiredMFALevel(MFALevel)}.
     *
     * @return The MFA-Level required by this Guild.
     */
    @Nonnull
    MFALevel getRequiredMFALevel();

    /**
     * The level of content filtering enabled in this Guild.
     * <br>This decides which messages sent by which Members will be scanned for explicit content.
     *
     * @return {@link ExplicitContentLevel ExplicitContentLevel} for this Guild
     */
    @Nonnull
    ExplicitContentLevel getExplicitContentLevel();

    /**
     * Checks if the current Verification-level of this guild allows JDA to send messages to it.
     *
     * @return True if Verification-level allows sending of messages, false if not.
     *
     * @see    VerificationLevel
     *         VerificationLevel Enum with a list of possible verification-levels and their requirements
     *
     * @deprecated Bots don't need to check this and client accounts are not supported
     */
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.2.0")
    boolean checkVerification();

    /**
     * Whether or not this Guild is available. A Guild can be unavailable, if the Discord server has problems.
     * <br>If a Guild is unavailable, it will be removed from the guild cache. You cannot receive events for unavailable guilds.
     *
     * @return If the Guild is available
     *
     * @deprecated This will be removed in a future version,
     *             unavailable guilds are now removed from cache.
     *             Replace with {@link JDA#isUnavailable(long)}
     */
    @ForRemoval
    @Deprecated
    @DeprecatedSince("4.1.0")
    @ReplaceWith("getJDA().isUnavailable(guild.getIdLong())")
    boolean isAvailable();

    /**
     * Requests member chunks for this guild.
     * <br>This returns a completed future if the member demand is already matched.
     * When {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} is disabled
     * this will do nothing since {@link #getMemberCount()} cannot be tracked.
     *
     * <p>Calling {@link CompletableFuture#cancel(boolean)} will not cancel the chunking process.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link CompletableFuture#join()} or {@link Future#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @return {@link CompletableFuture} representing the chunking task
     *
     * @see    #pruneMemberCache()
     *
     * @deprecated Replace with {@link #loadMembers()}, {@link #loadMembers(Consumer)}, or {@link #findMembers(Predicate)}
     */
    @Nonnull
    @Deprecated
    @DeprecatedSince("4.2.0")
    @ReplaceWith("loadMembers(Consumer<Member>) or loadMembers()")
    CompletableFuture<Void> retrieveMembers();

    /**
     * Retrieves and collects members of this guild into a list.
     * <br>This will use the configured {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     * to decide which members to retain in cache.
     *
     * <p>You can use {@link #findMembers(Predicate)} to filter specific members.
     *
     * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @throws IllegalStateException
     *         If the {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} is not enabled
     *
     * @return {@link Task} - Type: {@link List} of {@link Member}
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> loadMembers()
    {
        return findMembers((m) -> true);
    }

    /**
     * Retrieves and collects members of this guild into a list.
     * <br>This will use the configured {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     * to decide which members to retain in cache.
     *
     * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  filter
     *         Filter to decide which members to include
     *
     * @throws IllegalArgumentException
     *         If the provided filter is null
     * @throws IllegalStateException
     *         If the {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} is not enabled
     *
     * @return {@link Task} - Type: {@link List} of {@link Member}
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> findMembers(@Nonnull Predicate<? super Member> filter)
    {
        Checks.notNull(filter, "Filter");
        List<Member> list = new ArrayList<>();
        CompletableFuture<List<Member>> future = new CompletableFuture<>();
        Task<Void> reference = loadMembers((member) -> {
            if (filter.test(member))
                list.add(member);
        });
        GatewayTask<List<Member>> task = new GatewayTask<>(future, reference::cancel);
        reference.onSuccess(it -> future.complete(list))
                 .onError(future::completeExceptionally);
        return task;
    }

    /**
     * Retrieves all members of this guild.
     * <br>This will use the configured {@link net.dv8tion.jda.api.utils.MemberCachePolicy MemberCachePolicy}
     * to decide which members to retain in cache.
     *
     * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  callback
     *         Consumer callback for each member
     *
     * @throws IllegalArgumentException
     *         If the callback is null
     * @throws IllegalStateException
     *         If the {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} is not enabled
     *
     * @return {@link Task} cancellable handle for this request
     */
    @Nonnull
    Task<Void> loadMembers(@Nonnull Consumer<Member> callback);

    /**
     * Load the member for the specified user.
     * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
     * and immediately provided if the member information is consistent. The cache consistency directly
     * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is required to keep the cache updated with the latest information. You can pass {@code update = false} to always
     * return immediately if the member is cached regardless of cache consistency.
     *
     * <p>When the intent {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS}
     * is disabled this will always make a request even if the member is cached. You can use {@link #retrieveMember(User, boolean)} to disable this behavior.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  user
     *         The user to load the member from
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     */
    @Nonnull
    default RestAction<Member> retrieveMember(@Nonnull User user)
    {
        Checks.notNull(user, "User");
        return retrieveMemberById(user.getId());
    }

    /**
     * Load the member for the specified user.
     * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
     * and immediately provided if the member information is consistent. The cache consistency directly
     * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is required to keep the cache updated with the latest information. You can pass {@code update = false} to always
     * return immediately if the member is cached regardless of cache consistency.
     *
     * <p>When the intent {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS}
     * is disabled this will always make a request even if the member is cached. You can use {@link #retrieveMemberById(String, boolean)} to disable this behavior.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  id
     *         The user id to load the member from
     *
     * @throws IllegalArgumentException
     *         If the provided id is empty or null
     * @throws NumberFormatException
     *         If the provided id is not a snowflake
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     */
    @Nonnull
    default RestAction<Member> retrieveMemberById(@Nonnull String id)
    {
        return retrieveMemberById(MiscUtil.parseSnowflake(id));
    }

    /**
     * Load the member for the specified user.
     * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
     * and immediately provided if the member information is consistent. The cache consistency directly
     * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is required to keep the cache updated with the latest information. You can pass {@code update = false} to always
     * return immediately if the member is cached regardless of cache consistency.
     *
     * <p>When {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is disabled this will always make a request even if the member is cached. You can use {@link #retrieveMemberById(long, boolean)} to disable this behavior.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  id
     *         The user id to load the member from
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     */
    @Nonnull
    default RestAction<Member> retrieveMemberById(long id)
    {
        return retrieveMemberById(id, true);
    }

    /**
     * Shortcut for {@code guild.retrieveMemberById(guild.getOwnerIdLong())}.
     * <br>This will retrieve the current owner of the guild.
     * It is possible that the owner of a guild is no longer a registered discord user in which case this will fail.
     *
     * <p>When {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is disabled this will always make a request even if the member is cached. You can use {@link #retrieveOwner(boolean)} to disable this behavior.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     *
     * @see    #getOwner()
     * @see    #getOwnerIdLong()
     * @see    #retrieveMemberById(long)
     */
    @Nonnull
    default RestAction<Member> retrieveOwner()
    {
        return retrieveMemberById(getOwnerIdLong());
    }

    /**
     * Load the member for the specified user.
     * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
     * and immediately provided if the member information is consistent. The cache consistency directly
     * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is required to keep the cache updated with the latest information. You can pass {@code update = false} to always
     * return immediately if the member is cached regardless of cache consistency.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  user
     *         The user to load the member from
     * @param  update
     *         Whether JDA should perform a request even if the member is already cached to update properties such as the name
     *
     * @throws IllegalArgumentException
     *         If provided with null
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     */
    @Nonnull
    default RestAction<Member> retrieveMember(@Nonnull User user, boolean update)
    {
        Checks.notNull(user, "User");
        return retrieveMemberById(user.getId(), update);
    }

    /**
     * Load the member for the specified user.
     * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
     * and immediately provided if the member information is consistent. The cache consistency directly
     * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is required to keep the cache updated with the latest information. You can pass {@code update = false} to always
     * return immediately if the member is cached regardless of cache consistency.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  id
     *         The user id to load the member from
     * @param  update
     *         Whether JDA should perform a request even if the member is already cached to update properties such as the name
     *
     * @throws IllegalArgumentException
     *         If the provided id is empty or null
     * @throws NumberFormatException
     *         If the provided id is not a snowflake
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     */
    @Nonnull
    default RestAction<Member> retrieveMemberById(@Nonnull String id, boolean update)
    {
        return retrieveMemberById(MiscUtil.parseSnowflake(id), update);
    }

    /**
     * Load the member for the specified user.
     * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
     * and immediately provided if the member information is consistent. The cache consistency directly
     * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS}
     * is required to keep the cache updated with the latest information. You can pass {@code update = false} to always
     * return immediately if the member is cached regardless of cache consistency.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  id
     *         The user id to load the member from
     * @param  update
     *         Whether JDA should perform a request even if the member is already cached to update properties such as the name
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     */
    @Nonnull
    RestAction<Member> retrieveMemberById(long id, boolean update);

    /**
     * Shortcut for {@code guild.retrieveMemberById(guild.getOwnerIdLong())}.
     * <br>This will retrieve the current owner of the guild.
     * It is possible that the owner of a guild is no longer a registered discord user in which case this will fail.
     *
     * <p>Possible {@link net.dv8tion.jda.api.exceptions.ErrorResponseException ErrorResponseExceptions} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER}
     *     <br>The specified user is not a member of this guild</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER}
     *     <br>The specified user does not exist</li>
     * </ul>
     *
     * @param  update
     *         Whether JDA should perform a request even if the member is already cached to update properties such as the name
     *
     * @return {@link RestAction} - Type: {@link Member}
     *
     * @see    #pruneMemberCache()
     * @see    #unloadMember(long)
     *
     * @see    #getOwner()
     * @see    #getOwnerIdLong()
     * @see    #retrieveMemberById(long)
     */
    @Nonnull
    default RestAction<Member> retrieveOwner(boolean update)
    {
        return retrieveMemberById(getOwnerIdLong(), update);
    }

    /**
     * Retrieves a list of members.
     * <br>If the user does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the users resolve to a member, in which case an empty list will be the result.
     *
     * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
     * this will load the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} and {@link Activity Activities}
     * of the members. You can use {@link #retrieveMembers(boolean, Collection)} to disable presences.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  users
     *         The users of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembers(@Nonnull Collection<User> users)
    {
        Checks.noneNull(users, "Users");
        if (users.isEmpty())
            return new GatewayTask<>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {});

        long[] ids = users.stream().mapToLong(User::getIdLong).toArray();
        return retrieveMembersByIds(ids);
    }

    /**
     * Retrieves a list of members by their user id.
     * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
     *
     * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
     * this will load the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} and {@link Activity Activities}
     * of the members. You can use {@link #retrieveMembersByIds(boolean, Collection)} to disable presences.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  ids
     *         The ids of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembersByIds(@Nonnull Collection<Long> ids)
    {
        Checks.noneNull(ids, "IDs");
        if (ids.isEmpty())
            return new GatewayTask<>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {});

        long[] arr = ids.stream().mapToLong(Long::longValue).toArray();
        return retrieveMembersByIds(arr);
    }

    /**
     * Retrieves a list of members by their user id.
     * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
     *
     * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
     * this will load the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} and {@link Activity Activities}
     * of the members. You can use {@link #retrieveMembersByIds(boolean, String...)} to disable presences.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  ids
     *         The ids of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembersByIds(@Nonnull String... ids)
    {
        Checks.notNull(ids, "Array");
        if (ids.length == 0)
            return new GatewayTask<>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {});

        long[] arr = new long[ids.length];
        for (int i = 0; i < ids.length; i++)
            arr[i] = MiscUtil.parseSnowflake(ids[i]);
        return retrieveMembersByIds(arr);
    }

    /**
     * Retrieves a list of members by their user id.
     * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
     *
     * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
     * this will load the {@link net.dv8tion.jda.api.OnlineStatus OnlineStatus} and {@link Activity Activities}
     * of the members. You can use {@link #retrieveMembersByIds(boolean, long...)} to disable presences.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  ids
     *         The ids of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembersByIds(@Nonnull long... ids)
    {
        boolean presence = getJDA().getGatewayIntents().contains(GatewayIntent.GUILD_PRESENCES);
        return retrieveMembersByIds(presence, ids);
    }

    /**
     * Retrieves a list of members.
     * <br>If the user does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the users resolve to a member, in which case an empty list will be the result.
     *
     * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  includePresence
     *         Whether to load presences of the members (online status/activity)
     * @param  users
     *         The users of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If includePresence is {@code true} and the GUILD_PRESENCES intent is disabled</li>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembers(boolean includePresence, @Nonnull Collection<User> users)
    {
        Checks.noneNull(users, "Users");
        if (users.isEmpty())
            return new GatewayTask<>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {});

        long[] ids = users.stream().mapToLong(User::getIdLong).toArray();
        return retrieveMembersByIds(includePresence, ids);
    }

    /**
     * Retrieves a list of members by their user id.
     * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
     *
     * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  includePresence
     *         Whether to load presences of the members (online status/activity)
     * @param  ids
     *         The ids of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If includePresence is {@code true} and the GUILD_PRESENCES intent is disabled</li>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull Collection<Long> ids)
    {
        Checks.noneNull(ids, "IDs");
        if (ids.isEmpty())
            return new GatewayTask<>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {});

        long[] arr = ids.stream().mapToLong(Long::longValue).toArray();
        return retrieveMembersByIds(includePresence, arr);
    }

    /**
     * Retrieves a list of members by their user id.
     * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
     *
     * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  includePresence
     *         Whether to load presences of the members (online status/activity)
     * @param  ids
     *         The ids of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If includePresence is {@code true} and the GUILD_PRESENCES intent is disabled</li>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    default Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull String... ids)
    {
        Checks.notNull(ids, "Array");
        if (ids.length == 0)
            return new GatewayTask<>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {});

        long[] arr = new long[ids.length];
        for (int i = 0; i < ids.length; i++)
            arr[i] = MiscUtil.parseSnowflake(ids[i]);
        return retrieveMembersByIds(includePresence, arr);
    }

    /**
     * Retrieves a list of members by their user id.
     * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
     * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
     *
     * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  includePresence
     *         Whether to load presences of the members (online status/activity)
     * @param  ids
     *         The ids of the members (max 100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If includePresence is {@code true} and the GUILD_PRESENCES intent is disabled</li>
     *             <li>If the input contains null</li>
     *             <li>If the input is more than 100 IDs</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     */
    @Nonnull
    @CheckReturnValue
    Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull long... ids);

    /**
     * Queries a list of members using a radix tree based on the provided name prefix.
     * <br>This will check both the username and the nickname of the members.
     * Additional filtering may be required. If no members with the specified prefix exist, the list will be empty.
     *
     * <p>The requests automatically timeout after {@code 10} seconds.
     * When the timeout occurs a {@link java.util.concurrent.TimeoutException TimeoutException} will be used to complete exceptionally.
     *
     * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
     * The response handling happens on the event thread by default.
     *
     * @param  prefix
     *         The case-insensitive name prefix
     * @param  limit
     *         The max amount of members to retrieve (1-100)
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided prefix is null or empty.</li>
     *             <li>If the provided limit is not in the range of [1, 100]</li>
     *         </ul>
     *
     * @return {@link Task} handle for the request
     *
     * @see    #getMembersByName(String, boolean)
     * @see    #getMembersByNickname(String, boolean)
     * @see    #getMembersByEffectiveName(String, boolean)
     */
    @Nonnull
    @CheckReturnValue
    Task<List<Member>> retrieveMembersByPrefix(@Nonnull String prefix, int limit);

    /* From GuildController */

    /**
     * Used to move a {@link Member Member} from one {@link VoiceChannel VoiceChannel}
     * to another {@link VoiceChannel VoiceChannel}.
     * <br>As a note, you cannot move a Member that isn't already in a VoiceChannel. Also they must be in a VoiceChannel
     * in the same Guild as the one that you are moving them to.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be moved due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} permission was removed</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>The specified channel was deleted before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} that you are moving.
     * @param  voiceChannel
     *         The destination {@link VoiceChannel VoiceChannel} to which the member is being
     *         moved to. Or null to perform a voice kick.
     *
     * @throws IllegalStateException
     *         If the Member isn't currently in a VoiceChannel in this Guild, or {@link net.dv8tion.jda.api.utils.cache.CacheFlag#VOICE_STATE} is disabled.
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided member is {@code null}</li>
     *             <li>If the provided Member isn't part of this {@link Guild Guild}</li>
     *             <li>If the provided VoiceChannel isn't part of this {@link Guild Guild}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         <ul>
     *             <li>If this account doesn't have {@link Permission#VOICE_MOVE_OTHERS}
     *                 in the VoiceChannel that the Member is currently in.</li>
     *             <li>If this account <b>AND</b> the Member being moved don't have
     *                 {@link Permission#VOICE_CONNECT} for the destination VoiceChannel.</li>
     *         </ul>
     *
     * @return {@link RestAction RestAction}
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Void> moveVoiceMember(@Nonnull Member member, @Nullable VoiceChannel voiceChannel);

    /**
     * Used to kick a {@link Member Member} from a {@link VoiceChannel VoiceChannel}.
     * <br>As a note, you cannot kick a Member that isn't already in a VoiceChannel. Also they must be in a VoiceChannel
     * in the same Guild.
     *
     * <p>Equivalent to {@code moveVoiceMember(member, null)}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be moved due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>The specified channel was deleted before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} that you are moving.
     *
     * @throws IllegalStateException
     *         If the Member isn't currently in a VoiceChannel in this Guild, or {@link net.dv8tion.jda.api.utils.cache.CacheFlag#VOICE_STATE} is disabled.
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If any of the provided arguments is {@code null}</li>
     *             <li>If the provided Member isn't part of this {@link Guild Guild}</li>
     *             <li>If the provided VoiceChannel isn't part of this {@link Guild Guild}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If this account doesn't have {@link Permission#VOICE_MOVE_OTHERS}
     *         in the VoiceChannel that the Member is currently in.
     *
     * @return {@link RestAction RestAction}
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<Void> kickVoiceMember(@Nonnull Member member)
    {
        return moveVoiceMember(member, null);
    }

    /**
     * Changes the Member's nickname in this guild.
     * The nickname is visible to all members of this guild.
     *
     * <p>To change the nickname for the currently logged in account
     * only the Permission {@link Permission#NICKNAME_CHANGE NICKNAME_CHANGE} is required.
     * <br>To change the nickname of <b>any</b> {@link Member Member} for this {@link Guild Guild}
     * the Permission {@link Permission#NICKNAME_MANAGE NICKNAME_MANAGE} is required.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The nickname of the target Member is not modifiable due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} for which the nickname should be changed.
     * @param  nickname
     *         The new nickname of the {@link Member Member}, provide {@code null} or an
     *         empty String to reset the nickname
     *
     * @throws IllegalArgumentException
     *         If the specified {@link Member Member}
     *         is not from the same {@link Guild Guild}.
     *         Or if the provided member is {@code null}
     * @throws InsufficientPermissionException
     *         <ul>
     *             <li>If attempting to set nickname for self and the logged in account has neither {@link Permission#NICKNAME_CHANGE}
     *                 or {@link Permission#NICKNAME_MANAGE}</li>
     *             <li>If attempting to set nickname for another member and the logged in account does not have {@link Permission#NICKNAME_MANAGE}</li>
     *         </ul>
     * @throws HierarchyException
     *         If attempting to set nickname for another member and the logged in account cannot manipulate the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> modifyNickname(@Nonnull Member member, @Nullable String nickname);

    /**
     * This method will prune (kick) all members who were offline for at least <i>days</i> days.
     * <br>The RestAction returned from this method will return the amount of Members that were pruned.
     * <br>You can use {@link Guild#retrievePrunableMemberCount(int)} to determine how many Members would be pruned if you were to
     * call this method.
     *
     * <p>This might timeout when pruning many members.
     * You can use {@code prune(days, false)} to ignore the prune count and avoid a timeout.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The prune cannot finished due to a permission discrepancy</li>
     * </ul>
     *
     * @param  days
     *         Minimum number of days since a member has been offline to get affected.
     * @param  roles
     *         Optional roles to include in prune filter
     *
     * @throws InsufficientPermissionException
     *         If the account doesn't have {@link Permission#KICK_MEMBERS KICK_MEMBER} Permission.
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided days are not in the range from 1 to 30 (inclusive)</li>
     *             <li>If null is provided</li>
     *             <li>If any of the provided roles is not from this guild</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction} - Type: Integer
     *         <br>The amount of Members that were pruned from the Guild.
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Integer> prune(int days, @Nonnull Role... roles)
    {
        return prune(days, true, roles);
    }

    /**
     * This method will prune (kick) all members who were offline for at least <i>days</i> days.
     * <br>The RestAction returned from this method will return the amount of Members that were pruned.
     * <br>You can use {@link Guild#retrievePrunableMemberCount(int)} to determine how many Members would be pruned if you were to
     * call this method.
     *
     * <p>This might timeout when pruning many members with {@code wait=true}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The prune cannot finished due to a permission discrepancy</li>
     * </ul>
     *
     * @param  days
     *         Minimum number of days since a member has been offline to get affected.
     * @param  wait
     *         Whether to calculate the number of pruned members and wait for the response (timeout for too many pruned)
     * @param  roles
     *         Optional roles to include in prune filter
     *
     * @throws InsufficientPermissionException
     *         If the account doesn't have {@link Permission#KICK_MEMBERS KICK_MEMBER} Permission.
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided days are not in the range from 1 to 30 (inclusive)</li>
     *             <li>If null is provided</li>
     *             <li>If any of the provided roles is not from this guild</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction} - Type: Integer
     *         <br>Provides the amount of Members that were pruned from the Guild, if wait is true.
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Integer> prune(int days, boolean wait, @Nonnull Role... roles);

    /**
     * Kicks the {@link Member Member} from the {@link Guild Guild}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User}
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be kicked due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} to kick
     *         from the from the {@link Guild Guild}.
     * @param  reason
     *         The reason for this action or {@code null} if there is no specified reason
     *
     * @throws IllegalArgumentException
     *         If the provided member is not a Member of this Guild or is {@code null}
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#KICK_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot kick the other member due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     *         Kicks the provided Member from the current Guild
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> kick(@Nonnull Member member, @Nullable String reason);

    /**
     * Kicks the {@link Member Member} specified by the userId from the from the {@link Guild Guild}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User}
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be kicked due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  userId
     *         The id of the {@link User User} to kick
     *         from the from the {@link Guild Guild}.
     * @param  reason
     *         The reason for this action or {@code null} if there is no specified reason
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#KICK_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot kick the other member due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         If the user for the provided id cannot be kicked from this Guild or the provided {@code userId} is blank/null.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> kick(@Nonnull String userId, @Nullable String reason);

    /**
     * Kicks a {@link Member Member} from the {@link Guild Guild}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User}
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be kicked due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} to kick from the from the {@link Guild Guild}.
     *
     * @throws IllegalArgumentException
     *         If the provided member is not a Member of this Guild or is {@code null}
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#KICK_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot kick the other member due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     *         Kicks the provided Member from the current Guild
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> kick(@Nonnull Member member)
    {
        return kick(member, null);
    }

    /**
     * Kicks the {@link Member Member} specified by the userId from the from the {@link Guild Guild}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User}
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be kicked due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  userId
     *         The id of the {@link User User} to kick from the from the {@link Guild Guild}.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#KICK_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot kick the other member due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         If the userId provided does not correspond to a Member in this Guild or the provided {@code userId} is blank/null.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> kick(@Nonnull String userId)
    {
        return kick(userId, null);
    }

    /**
     * Bans the {@link User User} and deletes messages sent by the user
     * based on the amount of delDays.
     * <br>If you wish to ban a user without deleting any messages, provide delDays with a value of 0.
     *
     * <p>You can unban a user with {@link Guild#unban(User) Guild.unban(User)}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User's}
     * {@link Member Member} object (if the User was in the Guild)
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be banned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  user
     *         The {@link User User} to ban.
     * @param  delDays
     *         The history of messages, in days, that will be deleted.
     * @param  reason
     *         The reason for this action or {@code null} if there is no specified reason
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot ban the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided amount of days (delDays) is less than 0.</li>
     *             <li>If the provided amount of days (delDays) is bigger than 7.</li>
     *             <li>If the provided user is null</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> ban(@Nonnull User user, int delDays, @Nullable String reason);

    /**
     * Bans the user specified by the userId and deletes messages sent by the user
     * based on the amount of delDays.
     * <br>If you wish to ban a user without deleting any messages, provide delDays with a value of 0.
     *
     * <p>You can unban a user with {@link Guild#unban(User) Guild.unban(User)}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User's}
     * {@link Member Member} object (if the User was in the Guild)
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be banned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER UNKNOWN_USER}
     *     <br>The specified User does not exit</li>
     * </ul>
     *
     * @param  userId
     *         The id of the {@link User User} to ban.
     * @param  delDays
     *         The history of messages, in days, that will be deleted.
     * @param  reason
     *         The reason for this action or {@code null} if there is no specified reason
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot ban the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided amount of days (delDays) is less than 0.</li>
     *             <li>If the provided amount of days (delDays) is bigger than 7.</li>
     *             <li>If the provided userId is null</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> ban(@Nonnull String userId, int delDays, @Nullable String reason);

    /**
     * Bans the {@link Member Member} and deletes messages sent by the user
     * based on the amount of delDays.
     * <br>If you wish to ban a member without deleting any messages, provide delDays with a value of 0.
     *
     * <p>You can unban a user with {@link Guild#unban(User) Guild.unban(User)}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the
     * {@link Member Member} until Discord sends the
     * {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be banned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} to ban.
     * @param  delDays
     *         The history of messages, in days, that will be deleted.
     * @param  reason
     *         The reason for this action or {@code null} if there is no specified reason
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot ban the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided amount of days (delDays) is less than 0.</li>
     *             <li>If the provided amount of days (delDays) is bigger than 7.</li>
     *             <li>If the provided member is {@code null}</li>
     *         </ul>
     *
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(@Nonnull Member member, int delDays, @Nullable String reason)
    {
        Checks.notNull(member, "Member");
        //Don't check if the provided member is from this guild. It doesn't matter if they are or aren't.

        return ban(member.getUser(), delDays, reason);
    }

    /**
     * Bans the {@link Member Member} and deletes messages sent by the user
     * based on the amount of delDays.
     * <br>If you wish to ban a member without deleting any messages, provide delDays with a value of 0.
     *
     * <p>You can unban a user with {@link Guild#unban(User) Guild.unban(User)}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the
     * {@link Member Member} until Discord sends the
     * {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be banned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} to ban.
     * @param  delDays
     *         The history of messages, in days, that will be deleted.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot ban the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided amount of days (delDays) is less than 0.</li>
     *             <li>If the provided amount of days (delDays) is bigger than 7.</li>
     *             <li>If the provided member is {@code null}</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(@Nonnull Member member, int delDays)
    {
        return ban(member, delDays, null);
    }

    /**
     * Bans the {@link Member Member} and deletes messages sent by the user
     * based on the amount of delDays.
     * <br>If you wish to ban a member without deleting any messages, provide delDays with a value of 0.
     *
     * <p>You can unban a user with {@link Guild#unban(User) Guild.unban(User)}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the
     * {@link Member Member} until Discord sends the
     * {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be banned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  user
     *         The {@link User User} to ban.
     * @param  delDays
     *         The history of messages, in days, that will be deleted.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot ban the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided amount of days (delDays) is less than 0.</li>
     *             <li>If the provided amount of days (delDays) is bigger than 7.</li>
     *             <li>If the provided user is {@code null}</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(@Nonnull User user, int delDays)
    {
        return ban(user, delDays, null);
    }

    /**
     * Bans the user specified by the userId and deletes messages sent by the user
     * based on the amount of delDays.
     * <br>If you wish to ban a user without deleting any messages, provide delDays with a value of 0.
     *
     * <p>You can unban a user with {@link Guild#unban(User) Guild.unban(User)}.
     *
     * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User's}
     * {@link Member Member} object (if the User was in the Guild)
     * until Discord sends the {@link net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent GuildMemberRemoveEvent}.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be banned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  userId
     *         The id of the {@link User User} to ban.
     * @param  delDays
     *         The history of messages, in days, that will be deleted.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws HierarchyException
     *         If the logged in account cannot ban the other user due to permission hierarchy position.
     *         <br>See {@link Member#canInteract(Member)}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided amount of days (delDays) is less than 0.</li>
     *             <li>If the provided amount of days (delDays) is bigger than 7.</li>
     *             <li>If the provided userId is {@code null}</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(@Nonnull String userId, int delDays)
    {
        return ban(userId, delDays, null);
    }

    /**
     * Unbans the specified {@link User User} from this Guild.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be unbanned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER UNKNOWN_USER}
     *     <br>The specified User is invalid</li>
     * </ul>
     *
     * @param  user
     *         The id of the {@link User User} to unban.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws IllegalArgumentException
     *         If the provided user is null
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> unban(@Nonnull User user)
    {
        Checks.notNull(user, "User");

        return unban(user.getId());
    }

    /**
     * Unbans the a user specified by the userId from this Guild.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be unbanned due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_USER UNKNOWN_USER}
     *     <br>The specified User does not exist</li>
     * </ul>
     *
     * @param  userId
     *         The id of the {@link User User} to unban.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#BAN_MEMBERS} permission.
     * @throws IllegalArgumentException
     *         If the provided id is null or blank
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> unban(@Nonnull String userId);

    /**
     * Sets the Guild Deafened state state of the {@link Member Member} based on the provided
     * boolean.
     *
     * <p><b>Note:</b> The Member's {@link GuildVoiceState#isGuildDeafened() GuildVoiceState.isGuildDeafened()} value won't change
     * until JDA receives the {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent GuildVoiceGuildDeafenEvent} event related to this change.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be deafened due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#USER_NOT_CONNECTED USER_NOT_CONNECTED}
     *     <br>The specified Member is not connected to a voice channel</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} who's {@link GuildVoiceState VoiceState} is being changed.
     * @param  deafen
     *         Whether this {@link Member Member} should be deafened or undeafened.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#VOICE_DEAF_OTHERS} permission.
     * @throws IllegalArgumentException
     *         If the provided member is not from this Guild or null.
     * @throws IllegalStateException
     *         If the provided member is not currently connected to a voice channel.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> deafen(@Nonnull Member member, boolean deafen);

    /**
     * Sets the Guild Muted state state of the {@link Member Member} based on the provided
     * boolean.
     *
     * <p><b>Note:</b> The Member's {@link GuildVoiceState#isGuildMuted() GuildVoiceState.isGuildMuted()} value won't change
     * until JDA receives the {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent GuildVoiceGuildMuteEvent} event related to this change.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The target Member cannot be muted due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The specified Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#USER_NOT_CONNECTED USER_NOT_CONNECTED}
     *     <br>The specified Member is not connected to a voice channel</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} who's {@link GuildVoiceState VoiceState} is being changed.
     * @param  mute
     *         Whether this {@link Member Member} should be muted or unmuted.
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#VOICE_DEAF_OTHERS} permission.
     * @throws IllegalArgumentException
     *         If the provided member is not from this Guild or null.
     * @throws IllegalStateException
     *         If the provided member is not currently connected to a voice channel.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> mute(@Nonnull Member member, boolean mute);

    /**
     * Atomically assigns the provided {@link Role Role} to the specified {@link Member Member}.
     * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
     *
     * <p>If multiple roles should be added/removed (efficiently) in one request
     * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection, Collection)} or similar methods.
     *
     * <p>If the specified role is already present in the member's set of roles this does nothing.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>If the specified Role does not exist</li>
     * </ul>
     *
     * @param  member
     *         The target member who will receive the new role
     * @param  role
     *         The role which should be assigned atomically
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified member/role are not from the current Guild</li>
     *             <li>Either member or role are {@code null}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> addRoleToMember(@Nonnull Member member, @Nonnull Role role);

    /**
     * Atomically assigns the provided {@link Role Role} to the specified member by their user id.
     * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
     *
     * <p>If multiple roles should be added/removed (efficiently) in one request
     * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection, Collection)} or similar methods.
     *
     * <p>If the specified role is already present in the member's set of roles this does nothing.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>If the specified Role does not exist</li>
     * </ul>
     *
     * @param  userId
     *         The id of the target member who will receive the new role
     * @param  role
     *         The role which should be assigned atomically
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified role is not from the current Guild</li>
     *             <li>If the role is {@code null}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> addRoleToMember(long userId, @Nonnull Role role)
    {
        Checks.notNull(role, "Role");
        Checks.check(role.getGuild().equals(this), "Role must be from the same guild! Trying to use role from %s in %s", role.getGuild().toString(), toString());

        Member member = getMemberById(userId);
        if (member != null)
            return addRoleToMember(member, role);
        if (!getSelfMember().hasPermission(Permission.MANAGE_ROLES))
            throw new InsufficientPermissionException(this, Permission.MANAGE_ROLES);
        if (!getSelfMember().canInteract(role))
            throw new HierarchyException("Can't modify a role with higher or equal highest role than yourself! Role: " + role.toString());
        Route.CompiledRoute route = Route.Guilds.ADD_MEMBER_ROLE.compile(getId(), Long.toUnsignedString(userId), role.getId());
        return new AuditableRestActionImpl<>(getJDA(), route);
    }

    /**
     * Atomically assigns the provided {@link Role Role} to the specified member by their user id.
     * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
     *
     * <p>If multiple roles should be added/removed (efficiently) in one request
     * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection, Collection)} or similar methods.
     *
     * <p>If the specified role is already present in the member's set of roles this does nothing.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>If the specified Role does not exist</li>
     * </ul>
     *
     * @param  userId
     *         The id of the target member who will receive the new role
     * @param  role
     *         The role which should be assigned atomically
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified role is not from the current Guild</li>
     *             <li>If the role is {@code null}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> addRoleToMember(@Nonnull String userId, @Nonnull Role role)
    {
        return addRoleToMember(MiscUtil.parseSnowflake(userId), role);
    }

    /**
     * Atomically removes the provided {@link Role Role} from the specified {@link Member Member}.
     * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
     *
     * <p>If multiple roles should be added/removed (efficiently) in one request
     * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection, Collection)} or similar methods.
     *
     * <p>If the specified role is not present in the member's set of roles this does nothing.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>If the specified Role does not exist</li>
     * </ul>
     *
     * @param  member
     *         The target member who will lose the specified role
     * @param  role
     *         The role which should be removed atomically
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified member/role are not from the current Guild</li>
     *             <li>Either member or role are {@code null}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> removeRoleFromMember(@Nonnull Member member, @Nonnull Role role);

    /**
     * Atomically removes the provided {@link Role Role} from the specified member by their user id.
     * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
     *
     * <p>If multiple roles should be added/removed (efficiently) in one request
     * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection, Collection)} or similar methods.
     *
     * <p>If the specified role is not present in the member's set of roles this does nothing.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>If the specified Role does not exist</li>
     * </ul>
     *
     * @param  userId
     *         The id of the target member who will lose the specified role
     * @param  role
     *         The role which should be removed atomically
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified role is not from the current Guild</li>
     *             <li>The role is {@code null}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> removeRoleFromMember(long userId, @Nonnull Role role)
    {
        Checks.notNull(role, "Role");
        Checks.check(role.getGuild().equals(this), "Role must be from the same guild! Trying to use role from %s in %s", role.getGuild().toString(), toString());

        Member member = getMemberById(userId);
        if (member != null)
            return removeRoleFromMember(member, role);
        if (!getSelfMember().hasPermission(Permission.MANAGE_ROLES))
            throw new InsufficientPermissionException(this, Permission.MANAGE_ROLES);
        if (!getSelfMember().canInteract(role))
            throw new HierarchyException("Can't modify a role with higher or equal highest role than yourself! Role: " + role.toString());
        Route.CompiledRoute route = Route.Guilds.REMOVE_MEMBER_ROLE.compile(getId(), Long.toUnsignedString(userId), role.getId());
        return new AuditableRestActionImpl<>(getJDA(), route);
    }

    /**
     * Atomically removes the provided {@link Role Role} from the specified member by their user id.
     * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
     *
     * <p>If multiple roles should be added/removed (efficiently) in one request
     * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection, Collection)} or similar methods.
     *
     * <p>If the specified role is not present in the member's set of roles this does nothing.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>If the specified Role does not exist</li>
     * </ul>
     *
     * @param  userId
     *         The id of the target member who will lose the specified role
     * @param  role
     *         The role which should be removed atomically
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified role is not from the current Guild</li>
     *             <li>The role is {@code null}</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> removeRoleFromMember(@Nonnull String userId, @Nonnull Role role)
    {
        return removeRoleFromMember(MiscUtil.parseSnowflake(userId), role);
    }

    /**
     * Modifies the {@link Role Roles} of the specified {@link Member Member}
     * by adding and removing a collection of roles.
     * <br>None of the provided roles may be the <u>Public Role</u> of the current Guild.
     * <br>If a role is both in {@code rolesToAdd} and {@code rolesToRemove} it will be removed.
     *
     * <h2>Example</h2>
     * <pre>{@code
     * public static void promote(Member member) {
     *     Guild guild = member.getGuild();
     *     List<Role> pleb = guild.getRolesByName("Pleb", true); // remove all roles named "pleb"
     *     List<Role> knight = guild.getRolesByName("Knight", true); // add all roles named "knight"
     *     // update roles in single request
     *     guild.modifyMemberRoles(member, knight, pleb).queue();
     * }
     * }</pre>
     *
     * <h1>Warning</h1>
     * <b>This may <u>not</u> be used together with any other role add/remove/modify methods for the same Member
     * within one event listener cycle! The changes made by this require cache updates which are triggered by
     * lifecycle events which are received later. This may only be called again once the specific Member has been updated
     * by a {@link net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent GenericGuildMemberEvent} targeting the same Member.</b>
     *
     * <p>This is logically equivalent to:
     * <pre>{@code
     * Set<Role> roles = new HashSet<>(member.getRoles());
     * roles.addAll(rolesToAdd);
     * roles.removeAll(rolesToRemove);
     * RestAction<Void> action = guild.modifyMemberRoles(member, roles);
     * }</pre>
     *
     * <p>You can use {@link #addRoleToMember(Member, Role)} and {@link #removeRoleFromMember(Member, Role)} to make updates
     * independent of the cache.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  member
     *         The {@link Member Member} that should be modified
     * @param  rolesToAdd
     *         A {@link Collection Collection} of {@link Role Roles}
     *         to add to the current Roles the specified {@link Member Member} already has, or null
     * @param  rolesToRemove
     *         A {@link Collection Collection} of {@link Role Roles}
     *         to remove from the current Roles the specified {@link Member Member} already has, or null
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the target member is {@code null}</li>
     *             <li>If any of the specified Roles is managed or is the {@code Public Role} of the Guild</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nullable Collection<Role> rolesToAdd, @Nullable Collection<Role> rolesToRemove);

    /**
     * Modifies the complete {@link Role Role} set of the specified {@link Member Member}
     * <br>The provided roles will replace all current Roles of the specified Member.
     *
     * <h1>Warning</h1>
     * <b>This may <u>not</u> be used together with any other role add/remove/modify methods for the same Member
     * within one event listener cycle! The changes made by this require cache updates which are triggered by
     * lifecycle events which are received later. This may only be called again once the specific Member has been updated
     * by a {@link net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent GenericGuildMemberEvent} targeting the same Member.</b>
     *
     * <p><b>The new roles <u>must not</u> contain the Public Role of the Guild</b>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * <h2>Example</h2>
     * <pre>{@code
     * public static void removeRoles(Member member) {
     *     Guild guild = member.getGuild();
     *     // pass no role, this means we set the roles of the member to an empty array.
     *     guild.modifyMemberRoles(member).queue();
     * }
     * }</pre>
     *
     * @param  member
     *         A {@link Member Member} of which to override the Roles of
     * @param  roles
     *         New collection of {@link Role Roles} for the specified Member
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If any of the provided arguments is {@code null}</li>
     *             <li>If any of the provided arguments is not from this Guild</li>
     *             <li>If any of the specified {@link Role Roles} is managed</li>
     *             <li>If any of the specified {@link Role Roles} is the {@code Public Role} of this Guild</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     *
     * @see    #modifyMemberRoles(Member, Collection)
     */
    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Role... roles)
    {
        return modifyMemberRoles(member, Arrays.asList(roles));
    }

    /**
     * Modifies the complete {@link Role Role} set of the specified {@link Member Member}
     * <br>The provided roles will replace all current Roles of the specified Member.
     *
     * <p><u>The new roles <b>must not</b> contain the Public Role of the Guild</u>
     *
     * <h1>Warning</h1>
     * <b>This may <u>not</u> be used together with any other role add/remove/modify methods for the same Member
     * within one event listener cycle! The changes made by this require cache updates which are triggered by
     * lifecycle events which are received later. This may only be called again once the specific Member has been updated
     * by a {@link net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent GenericGuildMemberEvent} targeting the same Member.</b>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * <h2>Example</h2>
     * <pre>{@code
     * public static void makeModerator(Member member) {
     *     Guild guild = member.getGuild();
     *     List<Role> roles = new ArrayList<>(member.getRoles()); // modifiable copy
     *     List<Role> modRoles = guild.getRolesByName("moderator", true); // get roles with name "moderator"
     *     roles.addAll(modRoles); // add new roles
     *     // update the member with new roles
     *     guild.modifyMemberRoles(member, roles).queue();
     * }
     * }</pre>
     *
     * @param  member
     *         A {@link Member Member} of which to override the Roles of
     * @param  roles
     *         New collection of {@link Role Roles} for the specified Member
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
     * @throws HierarchyException
     *         If the provided roles are higher in the Guild's hierarchy
     *         and thus cannot be modified by the currently logged in account
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If any of the provided arguments is {@code null}</li>
     *             <li>If any of the provided arguments is not from this Guild</li>
     *             <li>If any of the specified {@link Role Roles} is managed</li>
     *             <li>If any of the specified {@link Role Roles} is the {@code Public Role} of this Guild</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     *
     * @see    #modifyMemberRoles(Member, Collection)
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Collection<Role> roles);

    /**
     * Transfers the Guild ownership to the specified {@link Member Member}
     * <br>Only available if the currently logged in account is the owner of this Guild
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The currently logged in account lost ownership before completing the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
     *     <br>The target Member was removed from the Guild before finishing the task</li>
     * </ul>
     *
     * @param  newOwner
     *         Not-null Member to transfer ownership to
     *
     * @throws net.dv8tion.jda.api.exceptions.PermissionException
     *         If the currently logged in account is not the owner of this Guild
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the specified Member is {@code null} or not from the same Guild</li>
     *             <li>If the specified Member already is the Guild owner</li>
     *             <li>If the specified Member is a bot account ({@link net.dv8tion.jda.api.AccountType#BOT AccountType.BOT})</li>
     *         </ul>
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> transferOwnership(@Nonnull Member newOwner);

    /**
     * Creates a new {@link TextChannel TextChannel} in this Guild.
     * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The channel could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
     *     <br>The maximum number of channels were exceeded</li>
     * </ul>
     *
     * @param  name
     *         The name of the TextChannel to create
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_CHANNEL} permission
     * @throws IllegalArgumentException
     *         If the provided name is {@code null} or empty or greater than 100 characters in length
     *
     * @return A specific {@link ChannelAction ChannelAction}
     *         <br>This action allows to set fields for the new TextChannel before creating it
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<TextChannel> createTextChannel(@Nonnull String name);

    /**
     * Creates a new {@link VoiceChannel VoiceChannel} in this Guild.
     * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The channel could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
     *     <br>The maximum number of channels were exceeded</li>
     * </ul>
     *
     * @param  name
     *         The name of the VoiceChannel to create
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_CHANNEL} permission
     * @throws IllegalArgumentException
     *         If the provided name is {@code null} or empty or greater than 100 characters in length
     *
     * @return A specific {@link ChannelAction ChannelAction}
     *         <br>This action allows to set fields for the new VoiceChannel before creating it
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name);

    /**
     * Creates a new {@link Category Category} in this Guild.
     * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The channel could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
     *     <br>The maximum number of channels were exceeded</li>
     * </ul>
     *
     * @param  name
     *         The name of the Category to create
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_CHANNEL} permission
     * @throws IllegalArgumentException
     *         If the provided name is {@code null} or empty or greater than 100 characters in length
     *
     * @return A specific {@link ChannelAction ChannelAction}
     *         <br>This action allows to set fields for the new Category before creating it
     */
    @Nonnull
    @CheckReturnValue
    ChannelAction<Category> createCategory(@Nonnull String name);

    /**
     * Creates a copy of the specified {@link GuildChannel GuildChannel}
     * in this {@link Guild Guild}.
     * <br>The provided channel need not be in the same Guild for this to work!
     *
     * <p>This copies the following elements:
     * <ol>
     *     <li>Name</li>
     *     <li>Parent Category (if present)</li>
     *     <li>Voice Elements (Bitrate, Userlimit)</li>
     *     <li>Text Elements (Topic, NSFW)</li>
     *     <li>All permission overrides for Members/Roles</li>
     * </ol>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The channel could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
     *     <br>The maximum number of channels were exceeded</li>
     * </ul>
     *
     * @param  <T>
     *         The channel type
     * @param  channel
     *         The {@link GuildChannel GuildChannel} to use for the copy template
     *
     * @throws IllegalArgumentException
     *         If the provided channel is {@code null}
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission
     *
     * @return A specific {@link ChannelAction ChannelAction}
     *         <br>This action allows to set fields for the new GuildChannel before creating it!
     *
     * @since  3.1
     *
     * @see    #createTextChannel(String)
     * @see    #createVoiceChannel(String)
     * @see    ChannelAction ChannelAction
     */
    @Nonnull
    @CheckReturnValue
    @SuppressWarnings("unchecked") // we need to do an unchecked cast for the channel type here
    default <T extends GuildChannel> ChannelAction<T> createCopyOfChannel(@Nonnull T channel)
    {
        Checks.notNull(channel, "Channel");
        return (ChannelAction<T>) channel.createCopy(this);
    }

    /**
     * Creates a new {@link Role Role} in this Guild.
     * <br>It will be placed at the bottom (just over the Public Role) to avoid permission hierarchy conflicts.
     * <br>For this to be successful, the logged in account has to have the {@link Permission#MANAGE_ROLES MANAGE_ROLES} Permission
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The role could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MAX_ROLES_PER_GUILD MAX_ROLES_PER_GUILD}
     *     <br>There are too many roles in this Guild</li>
     * </ul>
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_ROLES} Permission
     *
     * @return {@link RoleAction RoleAction}
     *         <br>Creates a new role with previously selected field values
     */
    @Nonnull
    @CheckReturnValue
    RoleAction createRole();

    /**
     * Creates a new {@link Role Role} in this {@link Guild Guild}
     * with the same settings as the given {@link Role Role}.
     * <br>The position of the specified Role does not matter in this case!
     *
     * <p>It will be placed at the bottom (just over the Public Role) to avoid permission hierarchy conflicts.
     * <br>For this to be successful, the logged in account has to have the {@link Permission#MANAGE_ROLES MANAGE_ROLES} Permission
     * and all {@link Permission Permissions} the given {@link Role Role} has.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The role could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MAX_ROLES_PER_GUILD MAX_ROLES_PER_GUILD}
     *     <br>There are too many roles in this Guild</li>
     * </ul>
     *
     * @param  role
     *         The {@link Role Role} that should be copied
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_ROLES} Permission and every Permission the provided Role has
     * @throws IllegalArgumentException
     *         If the specified role is {@code null}
     *
     * @return {@link RoleAction RoleAction}
     *         <br>RoleAction with already copied values from the specified {@link Role Role}
     */
    @Nonnull
    @CheckReturnValue
    default RoleAction createCopyOfRole(@Nonnull Role role)
    {
        Checks.notNull(role, "Role");
        return role.createCopy(this);
    }

    /**
     * Creates a new {@link Emote Emote} in this Guild.
     * <br>If one or more Roles are specified the new Emote will only be available to Members with any of the specified Roles (see {@link Member#canInteract(Emote)})
     * <br>For this to be successful, the logged in account has to have the {@link Permission#MANAGE_EMOTES MANAGE_EMOTES} Permission.
     *
     * <p><b><u>Unicode emojis are not included as {@link Emote Emote}!</u></b>
     *
     * <p>Note that a guild is limited to 50 normal and 50 animated emotes by default.
     * Some guilds are able to add additional emotes beyond this limitation due to the
     * {@code MORE_EMOJI} feature (see {@link Guild#getFeatures() Guild.getFeatures()}).
     * <br>Due to simplicity we do not check for these limits.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The emote could not be created due to a permission discrepancy</li>
     * </ul>
     *
     * @param  name
     *         The name for the new Emote
     * @param  icon
     *         The {@link Icon} for the new Emote
     * @param  roles
     *         The {@link Role Roles} the new Emote should be restricted to
     *         <br>If no roles are provided the Emote will be available to all Members of this Guild
     *
     * @throws InsufficientPermissionException
     *         If the logged in account does not have the {@link Permission#MANAGE_EMOTES MANAGE_EMOTES} Permission
     *
     * @return {@link AuditableRestAction AuditableRestAction} - Type: {@link Emote Emote}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Emote> createEmote(@Nonnull String name, @Nonnull Icon icon, @Nonnull Role... roles);

    /**
     * Modifies the positional order of {@link Guild#getCategories() Guild.getCategories()}
     * using a specific {@link RestAction RestAction} extension to allow moving Channels
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up}/{@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}
     * or {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     * <br>This uses <b>ascending</b> order with a 0 based index.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
     *     <br>One of the channels has been deleted before the completion of the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild</li>
     * </ul>
     *
     * @return {@link ChannelOrderAction ChannelOrderAction} - Type: {@link Category Category}
     */
    @Nonnull
    @CheckReturnValue
    ChannelOrderAction modifyCategoryPositions();

    /**
     * Modifies the positional order of {@link Guild#getTextChannels() Guild.getTextChannels()}
     * using a specific {@link RestAction RestAction} extension to allow moving Channels
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up}/{@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}
     * or {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     * <br>This uses <b>ascending</b> order with a 0 based index.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
     *     <br>One of the channels has been deleted before the completion of the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild</li>
     * </ul>
     *
     * @return {@link ChannelOrderAction ChannelOrderAction} - Type: {@link TextChannel TextChannel}
     */
    @Nonnull
    @CheckReturnValue
    ChannelOrderAction modifyTextChannelPositions();

    /**
     * Modifies the positional order of {@link Guild#getVoiceChannels() Guild.getVoiceChannels()}
     * using a specific {@link RestAction RestAction} extension to allow moving Channels
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up}/{@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}
     * or {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     * <br>This uses <b>ascending</b> order with a 0 based index.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
     *     <br>One of the channels has been deleted before the completion of the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild</li>
     * </ul>
     *
     * @return {@link ChannelOrderAction ChannelOrderAction} - Type: {@link VoiceChannel VoiceChannel}
     */
    @Nonnull
    @CheckReturnValue
    ChannelOrderAction modifyVoiceChannelPositions();

    /**
     * Modifies the positional order of {@link Category#getTextChannels() Category#getTextChannels()}
     * using an extension of {@link ChannelOrderAction ChannelOrderAction}
     * specialized for ordering the nested {@link TextChannel TextChannels} of this
     * {@link Category Category}.
     * <br>Like {@code ChannelOrderAction}, the returned {@link CategoryOrderAction CategoryOrderAction}
     * can be used to move TextChannels {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up},
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}, or
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     * <br>This uses <b>ascending</b> order with a 0 based index.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
     *     <br>One of the channels has been deleted before the completion of the task.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild.</li>
     * </ul>
     *
     * @param  category
     *         The {@link Category Category} to order
     *         {@link TextChannel TextChannels} from.
     *
     * @return {@link CategoryOrderAction CategoryOrderAction} - Type: {@link TextChannel TextChannel}
     */
    @Nonnull
    @CheckReturnValue
    CategoryOrderAction modifyTextChannelPositions(@Nonnull Category category);

    /**
     * Modifies the positional order of {@link Category#getVoiceChannels() Category#getVoiceChannels()}
     * using an extension of {@link ChannelOrderAction ChannelOrderAction}
     * specialized for ordering the nested {@link VoiceChannel VoiceChannels} of this
     * {@link Category Category}.
     * <br>Like {@code ChannelOrderAction}, the returned {@link CategoryOrderAction CategoryOrderAction}
     * can be used to move VoiceChannels {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up},
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}, or
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     * <br>This uses <b>ascending</b> order with a 0 based index.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
     *     <br>One of the channels has been deleted before the completion of the task.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild.</li>
     * </ul>
     *
     * @param  category
     *         The {@link Category Category} to order
     *         {@link VoiceChannel VoiceChannels} from.
     *
     * @return {@link CategoryOrderAction CategoryOrderAction} - Type: {@link VoiceChannel VoiceChannels}
     */
    @Nonnull
    @CheckReturnValue
    CategoryOrderAction modifyVoiceChannelPositions(@Nonnull Category category);

    /**
     * Modifies the positional order of {@link Guild#getRoles() Guild.getRoles()}
     * using a specific {@link RestAction RestAction} extension to allow moving Roles
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up}/{@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}
     * or {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     *
     * <p>This uses <b>ascending</b> ordering which means the lowest role is first!
     * <br>This means the highest role appears at index {@code n - 1} and the lower role at index {@code 0}.
     * <br>Providing {@code true} to {@link #modifyRolePositions(boolean)} will result in the ordering being
     * in ascending order, with the lower role at index {@code n - 1} and the highest at index {@code 0}.
     * <br>As a note: {@link Member#getRoles() Member.getRoles()}
     * and {@link Guild#getRoles() Guild.getRoles()} are both in descending order.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>One of the roles was deleted before the completion of the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild</li>
     * </ul>
     *
     * @return {@link RoleOrderAction RoleOrderAction}
     */
    @Nonnull
    @CheckReturnValue
    default RoleOrderAction modifyRolePositions()
    {
        return modifyRolePositions(true);
    }

    /**
     * Modifies the positional order of {@link Guild#getRoles() Guild.getRoles()}
     * using a specific {@link RestAction RestAction} extension to allow moving Roles
     * {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveUp(int) up}/{@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveDown(int) down}
     * or {@link net.dv8tion.jda.api.requests.restaction.order.OrderAction#moveTo(int) to} a specific position.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
     *     <br>One of the roles was deleted before the completion of the task</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The currently logged in account was removed from the Guild</li>
     * </ul>
     *
     * @param  useAscendingOrder
     *         Defines the ordering of the OrderAction. If {@code false}, the OrderAction will be in the ordering
     *         defined by Discord for roles, which is Descending. This means that the highest role appears at index {@code 0}
     *         and the lowest role at index {@code n - 1}. Providing {@code true} will result in the ordering being
     *         in ascending order, with the lower role at index {@code 0} and the highest at index {@code n - 1}.
     *         <br>As a note: {@link Member#getRoles() Member.getRoles()}
     *         and {@link Guild#getRoles() Guild.getRoles()} are both in descending order.
     *
     * @return {@link RoleOrderAction RoleOrderAction}
     */
    @Nonnull
    @CheckReturnValue
    RoleOrderAction modifyRolePositions(boolean useAscendingOrder);

    //////////////////////////

    /**
     * Represents the idle time allowed until a user is moved to the
     * AFK {@link VoiceChannel} if one is set
     * ({@link Guild#getAfkChannel() Guild.getAfkChannel()}).
     */
    enum Timeout
    {
        SECONDS_60(60),
        SECONDS_300(300),
        SECONDS_900(900),
        SECONDS_1800(1800),
        SECONDS_3600(3600);

        private final int seconds;

        Timeout(int seconds)
        {
            this.seconds = seconds;
        }

        /**
         * The amount of seconds represented by this {@link Timeout}.
         *
         * @return An positive non-negative int representing the timeout amount in seconds.
         */
        public int getSeconds()
        {
            return seconds;
        }

        /**
         * Retrieves the {@link Timeout Timeout} based on the amount of seconds requested.
         * <br>If the {@code seconds} amount provided is not valid for Discord, an IllegalArgumentException will be thrown.
         *
         * @param  seconds
         *         The amount of seconds before idle timeout.
         *
         * @throws IllegalArgumentException
         *         If the provided {@code seconds} is an invalid timeout amount.
         *
         * @return The {@link Timeout Timeout} related to the amount of seconds provided.
         */
        @Nonnull
        public static Timeout fromKey(int seconds)
        {
            for (Timeout t : values())
            {
                if (t.getSeconds() == seconds)
                    return t;
            }
            throw new IllegalArgumentException("Provided key was not recognized. Seconds: " + seconds);
        }
    }

    /**
     * Represents the Verification-Level of the Guild.
     * The Verification-Level determines what requirement you have to meet to be able to speak in this Guild.
     * <p>
     * <br><b>None</b>      {@literal ->} everyone can talk.
     * <br><b>Low</b>       {@literal ->} verified email required.
     * <br><b>Medium</b>    {@literal ->} you have to be member of discord for at least 5min.
     * <br><b>High</b>      {@literal ->} you have to be member of this guild for at least 10min.
     * <br><b>Very High</b> {@literal ->} you must have a verified phone on your discord account.
     */
    enum VerificationLevel
    {
        NONE(0),
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        VERY_HIGH(4),
        UNKNOWN(-1);

        private final int key;

        VerificationLevel(int key)
        {
            this.key = key;
        }

        /**
         * The Discord id key for this Verification Level.
         *
         * @return Integer id key for this VerificationLevel.
         */
        public int getKey()
        {
            return key;
        }

        /**
         * Used to retrieve a {@link VerificationLevel VerificationLevel} based
         * on the Discord id key.
         *
         * @param  key
         *         The Discord id key representing the requested VerificationLevel.
         *
         * @return The VerificationLevel related to the provided key, or {@link #UNKNOWN VerificationLevel.UNKNOWN} if the key is not recognized.
         */
        @Nonnull
        public static VerificationLevel fromKey(int key)
        {
            for (VerificationLevel level : VerificationLevel.values())
            {
                if(level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }

    /**
     * Represents the Notification-level of the Guild.
     * The Verification-Level determines what messages you receive pings for.
     * <p>
     * <br><b>All_Messages</b>   {@literal ->} Every message sent in this guild will result in a message ping.
     * <br><b>Mentions_Only</b>  {@literal ->} Only messages that specifically mention will result in a ping.
     */
    enum NotificationLevel
    {
        ALL_MESSAGES(0),
        MENTIONS_ONLY(1),
        UNKNOWN(-1);

        private final int key;

        NotificationLevel(int key)
        {
            this.key = key;
        }

        /**
         * The Discord id key used to represent this NotificationLevel.
         *
         * @return Integer id for this NotificationLevel.
         */
        public int getKey()
        {
            return key;
        }

        /**
         * Used to retrieve a {@link NotificationLevel NotificationLevel} based
         * on the Discord id key.
         *
         * @param  key
         *         The Discord id key representing the requested NotificationLevel.
         *
         * @return The NotificationLevel related to the provided key, or {@link #UNKNOWN NotificationLevel.UNKNOWN} if the key is not recognized.
         */
        @Nonnull
        public static NotificationLevel fromKey(int key)
        {
            for (NotificationLevel level : values())
            {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }

    /**
     * Represents the Multifactor Authentication level required by the Guild.
     * <br>The MFA Level restricts administrator functions to account with MFA Level equal to or higher than that set by the guild.
     * <p>
     * <br><b>None</b>             {@literal ->} There is no MFA level restriction on administrator functions in this guild.
     * <br><b>Two_Factor_Auth</b>  {@literal ->} Users must have 2FA enabled on their account to perform administrator functions.
     */
    enum MFALevel
    {
        NONE(0),
        TWO_FACTOR_AUTH(1),
        UNKNOWN(-1);

        private final int key;

        MFALevel(int key)
        {
            this.key = key;
        }

        /**
         * The Discord id key used to represent this MFALevel.
         *
         * @return Integer id for this MFALevel.
         */
        public int getKey()
        {
            return key;
        }

        /**
         * Used to retrieve a {@link MFALevel MFALevel} based
         * on the Discord id key.
         *
         * @param  key
         *         The Discord id key representing the requested MFALevel.
         *
         * @return The MFALevel related to the provided key, or {@link #UNKNOWN MFALevel.UNKNOWN} if the key is not recognized.
         */
        @Nonnull
        public static MFALevel fromKey(int key)
        {
            for (MFALevel level : values())
            {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }

    /**
     * The Explicit-Content-Filter Level of a Guild.
     * <br>This decides whom's messages should be scanned for explicit content.
     */
    enum ExplicitContentLevel
    {
        OFF(0, "Don't scan any messages."),
        NO_ROLE(1, "Scan messages from members without a role."),
        ALL(2, "Scan messages sent by all members."),

        UNKNOWN(-1, "Unknown filter level!");

        private final int key;
        private final String description;

        ExplicitContentLevel(int key, String description)
        {
            this.key = key;
            this.description = description;
        }

        /**
         * The key for this level
         *
         * @return key
         */
        public int getKey()
        {
            return key;
        }

        /**
         * Description of this level in the official Discord Client (as of 5th May, 2017)
         *
         * @return Description for this level
         */
        @Nonnull
        public String getDescription()
        {
            return description;
        }

        @Nonnull
        public static ExplicitContentLevel fromKey(int key)
        {
            for (ExplicitContentLevel level : values())
            {
                if (level.key == key)
                    return level;
            }
            return UNKNOWN;
        }
    }

    /**
     * The boost tier for this guild.
     * <br>Each tier unlocks new perks for a guild that can be seen in the {@link #getFeatures() features}.
     *
     * @since  4.0.0
     */
    enum BoostTier
    {
        /**
         * The default tier.
         * <br>Unlocked at 0 boosters.
         */
        NONE(0, 96000, 50),
        /**
         * The first tier.
         * <br>Unlocked at 2 boosters.
         */
        TIER_1(1, 128000, 100),
        /**
         * The second tier.
         * <br>Unlocked at 15 boosters.
         */
        TIER_2(2, 256000, 150),
        /**
         * The third tier.
         * <br>Unlocked at 30 boosters.
         */
        TIER_3(3, 384000, 250),
        /**
         * Placeholder for future tiers.
         */
        UNKNOWN(-1, Integer.MAX_VALUE, Integer.MAX_VALUE);

        private final int key;
        private final int maxBitrate;
        private final int maxEmotes;

        BoostTier(int key, int maxBitrate, int maxEmotes)
        {
            this.key = key;
            this.maxBitrate = maxBitrate;
            this.maxEmotes = maxEmotes;
        }

        /**
         * The API key used to represent this tier, identical to the ordinal.
         *
         * @return The key
         */
        public int getKey()
        {
            return key;
        }

        /**
         * The maximum bitrate that can be applied to voice channels when this tier is reached.
         *
         * @return The maximum bitrate
         *
         * @see    Guild#getMaxBitrate()
         */
        public int getMaxBitrate()
        {
            return maxBitrate;
        }

        /**
         * The maximum amount of emotes a guild can have when this tier is reached.
         *
         * @return The maximum emotes
         * 
         * @see    Guild#getMaxEmotes()
         */
        public int getMaxEmotes() 
        {
            return maxEmotes;
        }

        /**
         * The maximum size for files that can be uploaded to this Guild.
         *
         * @return The maximum file size of this Guild
         *
         * @see    Guild#getMaxFileSize()
         */
        public long getMaxFileSize()
        {
            if (key == 2)
                return 50 << 20;
            else if (key == 3)
                return 100 << 20;
            return Message.MAX_FILE_SIZE;
        }

        /**
         * Resolves the provided API key to the boost tier.
         *
         * @param  key
         *         The API key
         *
         * @return The BoostTier or {@link #UNKNOWN}
         */
        @Nonnull
        public static BoostTier fromKey(int key)
        {
            for (BoostTier tier : values())
            {
                if (tier.key == key)
                    return tier;
            }
            return UNKNOWN;
        }
    }

    /**
     * Represents a Ban object.
     *
     * @see #retrieveBanList()
     * @see <a href="https://discord.com/developers/docs/resources/guild#ban-object" target="_blank">Discord Docs: Ban Object</a>
     */
    class Ban
    {
        protected final User user;
        protected final String reason;

        public Ban(User user, String reason)
        {
            this.user = user;
            this.reason = reason;
        }

        /**
         * The {@link User User} that was banned
         *
         * @return The banned User
         */
        @Nonnull
        public User getUser()
        {
            return user;
        }

        /**
         * The reason why this user was banned
         *
         * @return The reason for this ban, or {@code null}
         */
        @Nullable
        public String getReason()
        {
            return reason;
        }

        @Override
        public String toString()
        {
            return "GuildBan:" + user + (reason == null ? "" : '(' + reason + ')');
        }
    }

    /**
     * Meta-Data for a Guild
     *
     * @since 4.2.0
     */
    class MetaData
    {
        private final int memberLimit;
        private final int presenceLimit;
        private final int approximatePresences;
        private final int approximateMembers;

        public MetaData(int memberLimit, int presenceLimit, int approximatePresences, int approximateMembers)
        {
            this.memberLimit = memberLimit;
            this.presenceLimit = presenceLimit;
            this.approximatePresences = approximatePresences;
            this.approximateMembers = approximateMembers;
        }

        /**
         * The active member limit for this guild.
         * <br>This limit restricts how many users can be member for this guild at once.
         *
         * @return The member limit
         */
        public int getMemberLimit()
        {
            return memberLimit;
        }

        /**
         * The active presence limit for this guild.
         * <br>This limit restricts how many users can be connected/online for this guild at once.
         *
         * @return The presence limit
         */
        public int getPresenceLimit()
        {
            return presenceLimit;
        }

        /**
         * The approximate number of online members in this guild.
         *
         * @return The approximate presence count
         */
        public int getApproximatePresences()
        {
            return approximatePresences;
        }

        /**
         * The approximate number of members in this guild.
         *
         * @return The approximate member count
         */
        public int getApproximateMembers()
        {
            return approximateMembers;
        }
    }
}

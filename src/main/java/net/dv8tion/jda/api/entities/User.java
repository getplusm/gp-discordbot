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
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a Discord User.
 * Contains all publicly available information about a specific Discord User.
 *
 * <h1>Formattable</h1>
 * This interface extends {@link java.util.Formattable Formattable} and can be used with a {@link java.util.Formatter Formatter}
 * such as used by {@link String#format(String, Object...) String.format(String, Object...)}
 * or {@link java.io.PrintStream#printf(String, Object...) PrintStream.printf(String, Object...)}.
 *
 * <p>This will use {@link #getAsMention()} rather than {@link Object#toString()}!
 * <br>Supported Features:
 * <ul>
 *     <li><b>Alternative</b>
 *     <br>   - Uses the <u>Discord Tag</u> (Username#Discriminator) instead
 *              (Example: {@code %#s} - results in <code>{@link User#getName()}#{@link User#getDiscriminator()}
 *              {@literal ->} Minn#6688</code>)</li>
 *
 *     <li><b>Width/Left-Justification</b>
 *     <br>   - Ensures the size of a format
 *              (Example: {@code %20s} - uses at minimum 20 chars;
 *              {@code %-10s} - uses left-justified padding)</li>
 *
 *     <li><b>Precision</b>
 *     <br>   - Cuts the content to the specified size
 *              (Example: {@code %.20s})</li>
 * </ul>
 *
 * <p>More information on formatting syntax can be found in the {@link java.util.Formatter format syntax documentation}!
 *
 * @see User#openPrivateChannel()
 *
 * @see JDA#getUserCache()
 * @see JDA#getUserById(long)
 * @see JDA#getUserByTag(String)
 * @see JDA#getUserByTag(String, String)
 * @see JDA#getUsersByName(String, boolean)
 * @see JDA#getUsers()
 *
 * @see JDA#retrieveUserById(String)
 */
public interface User extends IMentionable, IFakeable
{
    /**
     * Compiled pattern for a Discord Tag: {@code (.{2,32})#(\d{4})}
     */
    Pattern USER_TAG = Pattern.compile("(.{2,32})#(\\d{4})");

    /** Template for {@link #getAvatarUrl()}. */
    String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s.%s";
    /** Template for {@link #getDefaultAvatarUrl()} */
    String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/%s.png";

    /**
     * The username of the {@link User User}. Length is between 2 and 32 characters (inclusive).
     *
     * @return Never-null String containing the {@link User User}'s username.
     */
    @Nonnull
    String getName();

    /**
     * <br>The discriminator of the {@link User User}. Used to differentiate between users with the same usernames.
     * <br>This only contains the 4 digits after the username and the #.
     * Ex: 6297
     *
     * @return Never-null String containing the {@link User User} discriminator.
     */
    @Nonnull
    String getDiscriminator();

    /**
     * The Discord Id for this user's avatar image.
     * If the user has not set an image, this will return null.
     *
     * @return Possibly-null String containing the {@link User User} avatar id.
     */
    @Nullable
    String getAvatarId();

    /**
     * The URL for the user's avatar image.
     * If the user has not set an image, this will return null.
     *
     * @return Possibly-null String containing the {@link User User} avatar url.
     */
    @Nullable
    default String getAvatarUrl()
    {
        String avatarId = getAvatarId();
        return avatarId == null ? null : String.format(AVATAR_URL, getId(), avatarId, avatarId.startsWith("a_") ? "gif" : "png");
    }

    /**
     * The Discord Id for this user's default avatar image.
     *
     * @return Never-null String containing the {@link User User} default avatar id.
     */
    @Nonnull
    String getDefaultAvatarId();

    /**
     * The URL for the for the user's default avatar image.
     *
     * @return Never-null String containing the {@link User User} default avatar url.
     */
    @Nonnull
    default String getDefaultAvatarUrl()
    {
        return String.format(DEFAULT_AVATAR_URL, getDefaultAvatarId());
    }

    /**
     * The URL for the user's avatar image
     * If they do not have an avatar set, this will return the URL of their
     * default avatar
     *
     * @return  Never-null String containing the {@link User User} effective avatar url.
     */
    @Nonnull
    default String getEffectiveAvatarUrl()
    {
        String avatarUrl = getAvatarUrl();
        return avatarUrl == null ? getDefaultAvatarUrl() : avatarUrl;
    }

    /**
     * The "tag" for this user
     * <p>This is the equivalent of calling {@link String#format(String, Object...) String.format}("%#s", user)
     *
     * @return Never-null String containing the tag for this user, for example DV8FromTheWorld#6297
     */
    @Nonnull
    String getAsTag();

    /**
     * Whether or not the currently logged in user and this user have a currently open
     * {@link PrivateChannel PrivateChannel} or not.
     *
     * @return True if the logged in account shares a PrivateChannel with this user.
     */
    boolean hasPrivateChannel();

    /**
     * Opens a {@link PrivateChannel PrivateChannel} with this User.
     * <br>If a channel has already been opened with this user, it is immediately returned in the RestAction's
     * success consumer without contacting the Discord API.
     *
     * <h2>Examples</h2>
     * <pre>{@code
     * // Send message without response handling
     * public void sendMessage(User user, String content) {
     *     user.openPrivateChannel()
     *         .flatMap(channel -> channel.sendMessage(content))
     *         .queue();
     * }
     *
     * // Send message and delete 30 seconds later
     * public RestAction<Void> sendSecretMessage(User user, String content) {
     *     return user.openPrivateChannel() // RestAction<PrivateChannel>
     *                .flatMap(channel -> channel.sendMessage(content)) // RestAction<Message>
     *                .delay(30, TimeUnit.SECONDS) // RestAction<Message> with delayed response
     *                .flatMap(Message::delete); // RestAction<Void> (executed 30 seconds after sending)
     * }
     * }</pre>
     *
     * @throws UnsupportedOperationException
     *         If the recipient User is the currently logged in account (represented by {@link SelfUser SelfUser})
     *
     * @return {@link RestAction RestAction} - Type: {@link PrivateChannel PrivateChannel}
     *         <br>Retrieves the PrivateChannel to use to directly message this User.
     *
     * @see    JDA#openPrivateChannelById(long)
     */
    @Nonnull
    @CheckReturnValue
    RestAction<PrivateChannel> openPrivateChannel();

    /**
     * Finds and collects all {@link Guild Guild} instances that contain this {@link User User} within the current {@link JDA JDA} instance.<br>
     * <p>This method is a shortcut for {@link JDA#getMutualGuilds(User...) JDA.getMutualGuilds(User)}.</p>
     *
     * @return Immutable list of all {@link Guild Guilds} that this user is a member of.
     */
    @Nonnull
    List<Guild> getMutualGuilds();

    /**
     * Returns whether or not the given user is a Bot-Account (special badge in client, some different behaviour)
     *
     * @return If the User's Account is marked as Bot
     */
    boolean isBot();

    /**
     * Returns the {@link JDA JDA} instance of this User
     *
     * @return the corresponding JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * Returns the {@link UserFlag UserFlags} of this user.
     * 
     * @return EnumSet containing the flags of the user.
     */
    @Nonnull
    EnumSet<UserFlag> getFlags();

    /**
     * Returns the bitmask representation of the {@link UserFlag UserFlags} of this user.
     * 
     * @return bitmask representation of the user's flags.
     */
    int getFlagsRaw();

    /**
     * Represents the bit offsets used by Discord for public flags
     */
    enum UserFlag
    {
        STAFF(             0, "Discord Employee"),
        PARTNER(           1, "Discord Partner"),
        HYPESQUAD(         2, "HypeSquad Events"),
        BUG_HUNTER_LEVEL_1(3, "Bug Hunter Level 1"),

        // HypeSquad
        HYPESQUAD_BRAVERY(   6, "HypeSquad Bravery"),
        HYPESQUAD_BRILLIANCE(7, "HypeSquad Brilliance"),
        HYPESQUAD_BALANCE(   8, "HypeSquad Balance"),

        EARLY_SUPPORTER(    9, "Early Supporter"),
        TEAM_USER(         10, "Team User"),
        SYSTEM(            12, "System User"),
        BUG_HUNTER_LEVEL_2(14, "Bug Hunter Level 2"),
        VERIFIED_BOT(      16, "Verified Bot"),
        VERIFIED_DEVELOPER(17, "Verified Bot Developer"),
        
        UNKNOWN(-1, "Unknown");

        /**
         * Empty array of UserFlag enum, useful for optimized use in {@link Collection#toArray(Object[])}.
         */
        public static final UserFlag[] EMPTY_FLAGS = new UserFlag[0];
        
        private final int offset;
        private final int raw;
        private final String name;

        UserFlag(int offset, @Nonnull String name)
        {
            this.offset = offset;
            this.raw = 1 << offset;
            this.name = name;
        }

        /**
         * The readable name as used in the Discord Client.
         * 
         * @return The readable name of this UserFlag.
         */
        @Nonnull
        public String getName()
        {
            return this.name;
        }

        /**
         * The binary offset of the flag.
         * 
         * @return The offset that represents this UserFlag.
         */
        public int getOffset()
        {
            return offset;
        }

        /**
         * The value of this flag when viewed as raw value.
         * <br>This is equivalent to: <code>1 {@literal <<} {@link #getOffset()}</code>
         * 
         * @return The raw value of this specific flag.
         */
        public int getRawValue()
        {
            return raw;
        }

        /**
         * Gets the first UserFlag relating to the provided offset.
         * <br>If there is no UserFlag that matches the provided offset,
         * {@link #UNKNOWN} is returned.
         * 
         * @param  offset
         *         The offset to match a UserFlag to.
         *         
         * @return UserFlag relating to the provided offset.
         */
        @Nonnull
        public static UserFlag getFromOffset(int offset)
        {
            for (UserFlag flag : values())
            {
                if (flag.offset == offset)
                    return flag;
            }
            return UNKNOWN;
        }
        
        /**
         * A set of all UserFlags that are specified by this raw int representation of
         * flags.
         * 
         * @param  flags
         *         The raw {@code int} representation if flags.
         *         
         * @return Possibly-empty EnumSet of UserFlags.
         */
        @Nonnull
        public static EnumSet<UserFlag> getFlags(int flags)
        {
            final EnumSet<UserFlag> foundFlags = EnumSet.noneOf(UserFlag.class);
            
            if (flags == 0)
                return foundFlags; //empty
            
            for (UserFlag flag : values())
            {
                if (flag != UNKNOWN && (flags & flag.raw) == flag.raw)
                    foundFlags.add(flag);
            }
                    
            return foundFlags;
        }

        /**
         * This is effectively the opposite of {@link #getFlags(int)}, this takes 1 or more UserFlags
         * and returns the bitmask representation of the flags.
         * 
         * @param  flags
         *         The array of flags of which to form into the raw int representation.
         *
         * @throws IllegalArgumentException
         *         When the provided UserFlags are null.
         *         
         * @return bitmask representing the provided flags.
         */
        public static int getRaw(@Nonnull UserFlag... flags){
            Checks.noneNull(flags, "UserFlags");
            
            int raw = 0;
            for (UserFlag flag : flags)
            {
                if (flag != null && flag != UNKNOWN)
                    raw |= flag.raw;
            }
            
            return raw;
        }

        /**
         * This is effectively the opposite of {@link #getFlags(int)}. This takes a collection of UserFlags
         * and returns the bitmask representation of the flags.
         * <br>Example: {@code getRaw(EnumSet.of(UserFlag.STAFF, UserFlag.HYPESQUAD))}
         *
         * @param  flags
         *         The flags to convert
         *
         * @throws IllegalArgumentException
         *         When the provided UserFLags are null.
         *
         * @return bitmask representing the provided flags.
         * 
         * @see EnumSet EnumSet
         */
        public static int getRaw(@Nonnull Collection<UserFlag> flags)
        {
            Checks.notNull(flags, "Flag Collection");
            
            return getRaw(flags.toArray(EMPTY_FLAGS));
        }
    }
}

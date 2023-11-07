package org.bsdevelopment.tellraw;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.google.common.collect.Lists;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bsdevelopment.strings.Colorize;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class for creating and sending JSON-based chat messages in Minecraft.
 */
public class Tellraw {
    private List<Part> messageParts = new ArrayList<>();
    private String jsonString = null;
    private boolean dirty = false;

    /**
     * Returns a new `Tellraw` object with the text set to the specified text.
     *
     * @param text The text to be displayed.
     * @return A new instance of `Tellraw`.
     */
    public static Tellraw getInstance(String text) {
        return new Tellraw().then(text);
    }

    /**
     * Constructs a `Tellraw` object from a legacy string containing ChatColors with the '&' symbol.
     *
     * @param text The original text with ChatColors.
     * @return A new `Tellraw` object.
     */
    public static Tellraw fromLegacy(String text) {
        return new Tellraw().fromLegacy0(text);
    }

    /**
     * Converts a string with ChatColors using the "&cString" format to the JSON equivalent for the `/tellraw` command.
     *
     * @param text The string to convert.
     * @return A new `Tellraw` object.
     */
    private Tellraw fromLegacy0(String text) {
        if ((text == null) || text.isEmpty()) throw new NullPointerException("Missing text input");
        Tellraw message = new Tellraw();
        List<Part> split = Colorize.splitMessageToParts(text);
        message.messageParts = split;
        message.jsonString = Colorize.convertParts2Json(split).toString();
        return message;
    }

    /**
     * Adds color to the latest message in various ways:
     * - String (in hex color format, e.g., "#FFFFFF")
     * - `java.awt.Color`
     * - `org.bukkit.Color`
     * - `net.md_5.bungee.api.ChatColor`
     * - `org.bukkit.ChatColor`
     *
     * @param obj The object to be converted to a color.
     * @return The `Tellraw` object.
     */
    public Tellraw color(Object obj) {
        latest().color = null;
        latest().customColor = null;

        if (obj instanceof String) {
            latest().customColor = Colorize.hex2Color((String) obj);
        } else if (obj instanceof java.awt.Color) {
            java.awt.Color color = (java.awt.Color) obj;
            latest().customColor = Colorize.hex2Color(Colorize.toHex(color.getRed(), color.getGreen(), color.getBlue()));
        } else if (obj instanceof Color) {
            latest().customColor = (Color) obj;
        } else if (obj instanceof net.md_5.bungee.api.ChatColor) {
            net.md_5.bungee.api.ChatColor color = (net.md_5.bungee.api.ChatColor) obj;
            try {
                Method method = obj.getClass().getMethod("getColor");
                return color(method.invoke(color));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return color(ChatColor.valueOf(color.name()));
            }
        } else if (obj instanceof ChatColor) {
            ChatColor color = (ChatColor) obj;
            if (!color.isColor()) throw new IllegalArgumentException(color.name() + " is not a color");
            latest().color = color;
        } else {
            throw new IllegalArgumentException(obj.getClass().getSimpleName() + " is not a valid input.");
        }
        this.dirty = true;
        return this;
    }

    /**
     * Sets the font of the latest message.
     * Examples:
     * minecraft:default - Default minecraft font
     * minecraft:illageralt - Language of the Illagers
     * minecraft:alt - Enchantment language
     * minecraft:uniform - Similar to the default one
     *
     * @param font The font to use (e.g., "minecraft:default").
     * @return The `Tellraw` object.
     */
    public Tellraw font(String font) {
        if ((font == null) || (font.isEmpty())) throw new NullPointerException("Font cannot be null");
        latest().font = font;
        this.dirty = true;
        return this;
    }

    /**
     * Sets the style of the latest message.
     *
     * @param styles An array of `ChatColor` styles to apply.
     * @return The `Tellraw` object.
     */
    public Tellraw style(ChatColor... styles) {
        for (ChatColor style : styles) {
            if (!style.isFormat()) throw new IllegalArgumentException(style.name() + " is not a style");
        }
        latest().styles = Lists.newArrayList(styles);
        this.dirty = true;
        return this;
    }

    /**
     * Adds a click event to the `Tellraw` message that opens the specified file when clicked.
     *
     * @param path The path to the file to open.
     * @return The `Tellraw` object.
     */
    public Tellraw file(String path) {
        onClick("open_file", path);
        return this;
    }

    /**
     * Adds a click event to the `Tellraw` message that opens the specified URL in the player's browser when clicked.
     *
     * @param url The URL to open.
     * @return The `Tellraw` object.
     */
    public Tellraw link(String url) {
        onClick("open_url", url);
        return this;
    }

    /**
     * Adds a click event to the `Tellraw` message that suggests the given command when clicked.
     *
     * @param command The command to suggest.
     * @return The `Tellraw` object.
     */
    public Tellraw suggest(String command) {
        onClick("suggest_command", command);
        return this;
    }

    /**
     * Adds a command to the click event of the `Tellraw` message.
     *
     * @param command The command to run when clicked.
     * @return The `Tellraw` object.
     */
    public Tellraw command(String command) {
        onClick("run_command", command);
        return this;
    }

    /**
     * Adds a hover event to the `Tellraw` message that shows the achievement with the given name when hovered over.
     *
     * @param name The name of the achievement.
     * @return The `Tellraw` object.
     */
    public Tellraw achievementTooltip(String name) {
        onHover("show_achievement", "achievement." + name);
        return this;
    }

    /**
     * Sets the hover event to show the item tooltip of the item specified by the given JSON string.
     *
     * @param itemJSON The item JSON to show in the tooltip.
     * @return The `Tellraw` object.
     */
    public Tellraw itemTooltip(String itemJSON) {
        onHover("show_item", itemJSON);
        return this;
    }

    /**
     * Returns a `Tellraw` object with the given lines as a tooltip.
     *
     * @param lines The lines of text to display in the tooltip.
     * @return A `Tellraw` object.
     */
    public Tellraw tooltip(List<String> lines) {
        return tooltip(lines.toArray(new String[lines.size()]));
    }

    /**
     * Adds a tooltip to the `Tellraw` message.
     *
     * @param lines The lines of text to display in the tooltip.
     * @return The `Tellraw` object.
     */
    public Tellraw tooltip(String... lines) {
        onHover("show_text", combineArray(0, "\n", lines));
        return this;
    }

    /**
     * Appends an object to the message. If the object is a `Part`, it is added to the `messageParts` list;
     * otherwise, a new `Part` is created with the object's `toString()` as the text.
     *
     * @param obj The object to add to the message.
     * @return The `Tellraw` object.
     */
    public Tellraw then(Object obj) {
        if (obj instanceof Part) {
            this.messageParts.add((Part) obj);
        } else {
            this.messageParts.add(new Part(obj.toString()));
        }
        this.dirty = true;
        return this;
    }

    /**
     * Removes the last part of the message.
     *
     * @return The `Tellraw` object.
     */
    public Tellraw removeLastPart() {
        if (messageParts.isEmpty()) return this;
        messageParts.remove((messageParts.size() - 1));
        return this;
    }

    /**
     * Converts the `Tellraw` message to a JSON string. If the message is not dirty, it returns the cached JSON string.
     *
     * @return A JSON string.
     */
    public String toJSONString() {
        if ((!this.dirty) && (this.jsonString != null)) return this.jsonString;
        JsonObject jsonObject = new JsonObject();
        if (this.messageParts.size() == 1) {
            jsonObject = latest().toJson();
        } else {
            jsonObject.set("text", "");
            JsonArray extras = new JsonArray();
            for (Part part : this.messageParts) {
                extras.add(part.toJson());
            }
            jsonObject.set("extra", extras);
        }
        this.jsonString = jsonObject.toString();
        this.dirty = false;
        return this.jsonString;
    }

    /**
     * Sends the `Tellraw` message to a `CommandSender`.
     *
     * @param sender The `CommandSender` to send the message to.
     */
    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            send((Player) sender);
            return;
        }
        StringBuilder builder = new StringBuilder();
        messageParts.forEach(part -> {
            if (part.color != null) builder.append(part.color);
            if (part.customColor != null) builder.append(Colorize.fetchColor(part.customColor));
            if ((part.styles != null) && !part.styles.isEmpty()) {
                for (ChatColor style : part.styles) builder.append(style);
            }
            builder.append(part.text);
        });
        sender.sendMessage(builder.toString());
    }

    /**
     * Sends the `Tellraw` message to a player.
     *
     * @param player The player to send the message to.
     */
    public void send(Player player) {
        player.spigot().sendMessage(ComponentSerializer.parse(toJSONString()));
    }

    /**
     * Sends the `Tellraw` message to a collection of players.
     *
     * @param players The players to send the message to.
     */
    public void send(Iterable<Player> players) {
        for (Player player : players)
            send(player);
    }

    /**
     * Sends the `Tellraw` message to a collection of players.
     *
     * @param players The players to send the message to.
     */
    public void send(Collection<? extends Player> players) {
        for (Player player : players)
            send(player);
    }

    /**
     * Retrieves the latest part in the messageParts list.
     *
     * @return The latest Part object.
     */
    private Part latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }

    /**
     * Combines an array of strings into a single string using the specified separator.
     * The combination starts at the specified index and goes up to the end of the array.
     *
     * @param startIndex The index to start combining from.
     * @param separator  The separator to place between the combined strings.
     * @param stringArray The array of strings to combine.
     * @return The combined string.
     */
    private String combineArray(int startIndex, String separator, String... stringArray) {
        return combineArray(startIndex, stringArray.length, separator, stringArray);
    }

    /**
     * Combines an array of strings into a single string using the specified separator.
     * The combination starts at the startIndex and goes up to the endIndex.
     *
     * @param startIndex The index to start combining from.
     * @param endIndex   The index to stop combining at.
     * @param separator  The separator to place between the combined strings.
     * @param stringArray The array of strings to combine.
     * @return The combined string.
     */
    private String combineArray(int startIndex, int endIndex, String separator, String... stringArray) {
        if (stringArray != null && startIndex < endIndex) {
            StringBuilder builder = new StringBuilder();
            for (int i = startIndex; i < endIndex; ++i) {
                builder.append(Colorize.translateBungeeHex(stringArray[i]));
                builder.append(separator);
            }
            builder.delete(builder.length() - separator.length(), builder.length());
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * Sets the click action for the latest Part in the message.
     *
     * @param name The name of the click action.
     * @param data The data associated with the click action.
     */
    private void onClick(String name, String data) {
        Part latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }

    /**
     * Sets the hover action for the latest Part in the message.
     *
     * @param name The name of the hover action.
     * @param data The data associated with the hover action.
     */
    private void onHover(String name, String data) {
        Part latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }
}
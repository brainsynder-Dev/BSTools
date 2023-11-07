package org.bsdevelopment.tellraw;

import com.eclipsesource.json.JsonObject;
import org.bsdevelopment.strings.Colorize;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.List;

/**
 * Represents a part of a Tellraw message, containing text and formatting information.
 */
public final class Part {
    public ChatColor color = null;                // Text color (Minecraft ChatColor)
    public Color customColor = null;              // Custom color (RGB/HEX) for versions 1.16+
    public List<ChatColor> styles = null;         // Text styles (e.g., bold, italic, underlined, etc.)
    public String text = "";                      // The text content of this part
    public String font = null;                    // The font to use (e.g., minecraft:default, minecraft:alt)

    public String clickActionName = null;         // The click action name (e.g., open_url, suggest_command)
    public String clickActionData = null;         // Data associated with the click action
    public String hoverActionName = null;         // The hover action name (e.g., show_text, show_item)
    public String hoverActionData = null;         // Data associated with the hover action

    public Part() {
    }

    /**
     * Constructs a Part with the specified text.
     *
     * @param text The text content of this part.
     */
    public Part(String text) {
        this.text = text;
    }

    /**
     * Converts this part into a JSON object representing its formatting and actions.
     *
     * @return A JsonObject representing this part's formatting and actions.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.set("text", text);

        if (this.color != null) {
            // Use the ChatColor variable (Default MC colors)
            json.set("color", this.color.name().toLowerCase());
        } else if (customColor != null) {
            // Use the Color (Allows RGB/HEX colors) [1.16+]
            // Since 1.16 added the ability to have RGB/HEX colored messages
            json.set("color", Colorize.toHex(customColor.getRed(), customColor.getGreen(), customColor.getBlue()));
        }

        if (this.font != null) json.set("font", font.toLowerCase());

        if (this.styles != null) {
            for (ChatColor style : this.styles) json.set(style.name().toLowerCase(), true);
        }

        if (this.clickActionName != null && this.clickActionData != null) {
            JsonObject action = new JsonObject();
            action.set("action", clickActionName);
            action.set("value", clickActionData);
            json.set("clickEvent", action);
        }

        if (this.hoverActionName != null && this.hoverActionData != null) {
            JsonObject action = new JsonObject();
            action.set("action", hoverActionName);
            action.set("value", hoverActionData);
            json.set("hoverEvent", action);
        }

        return json;
    }
}

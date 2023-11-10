package org.bsdevelopment.command;

import com.google.common.collect.Lists;
import org.bsdevelopment.command.annotation.ICommand;
import org.bsdevelopment.strings.Colorize;
import org.bsdevelopment.tellraw.Tellraw;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.function.Consumer;

public class SubCommand implements CommandExecutor, TabCompleter {
    private final Map<Integer, List<String>> tabCompletion = new HashMap<>();
    private final Map<Integer, List<Complete>> tabCompletionArg = new HashMap<>();
    private final List<String> tabKeyWords = new ArrayList<>();
    private String masterFormat;

    public void run(CommandSender sender) {
        run(sender, new String[0]);
    }

    public void run(CommandSender sender, String[] args) {
        run(sender);
    }

    public void setMasterFormat(String masterFormat) {
        this.masterFormat = masterFormat;
    }

    public String getMasterFormat() {
        return masterFormat;
    }

    protected void registerTabCompleteKeyword(String keyword) {
        tabKeyWords.add(keyword);
    }

    /**
     * Use this method to handle addon the tab completions when it finds a registered key word
     *
     * @param keyword     The keyword that was found
     * @param completions Current list of complretions
     * @param sender      Command Sender
     * @param index       Current index (length of args)
     * @param args        The arguments for the command so far
     * @return Will return a List of strings (the new completions)
     */
    public List<String> fetchCompletionsFromKeyword(String keyword, List<String> completions, CommandSender sender, int index, String[] args) {
        return completions;
    }

    public List<String> handleCompletions(List<String> completions, CommandSender sender, int index, String[] args) {
        ICommand command = getCommand(getClass());
        if (command == null) return completions;
        String usage = command.usage();
        if (usage.isEmpty()) return completions;


        // This will replace
        String[] split = usage.split(" ");
        int current = 1;
        for (String value : split) {
            for (String keyword : tabKeyWords) {
                if (current == index) {
                    if (value.contains(keyword)) {
                        completions.addAll(fetchCompletionsFromKeyword(keyword, completions, sender, index, args));
                        return completions;
                    }
                }
            }
            current++;
        }

        String string = "";
        if (args.length != 0) string = args[index - 1];

        for (String keyword : tabKeyWords) {
            if (keyword.startsWith(string)) {
                completions.addAll(fetchCompletionsFromKeyword(keyword, completions, sender, index, args));
                return completions;
            }
        }

        return completions;
    }


    /**
     * Generate your own tab completion
     *
     * @param length       - The arg you want to add a tab complete value to
     * @param replacements - the list of values you wish to add as tab complete values
     */
    protected void registerCompletion(int length, List<String> replacements) {
        Objects.requireNonNull(replacements, "Arguments cannot be null");
        tabCompletion.put(length, replacements);
    }

    /**
     * Generate your own tab completion
     *
     * @param length   - The arg you want to add a tab complete value to
     * @param complete
     */
    protected void registerCompletion(int length, Complete complete) {
        Objects.requireNonNull(complete, "Arguments cannot be null");
        List<Complete> completes = tabCompletionArg.getOrDefault(length, new ArrayList<>());
        completes.add(complete);
        tabCompletionArg.put(length, completes);
    }

    public String getUsageStyle() {
        ICommand command = getCommand(getClass());
        if ((command == null) || (command.style().isEmpty())) return "/{name} {usage} - {description}";
        return command.style();
    }

    /**
     * Send the sender the command usage
     *
     * @param sender - the target of where you want the usage to go to
     */
    public void sendUsage(CommandSender sender) {
        ICommand command = getCommand(getClass());
        if (command == null) return;
        String usage = command.usage();
        String description = command.description();

        String style = command.consoleStyle();
        if (sender instanceof Player) style = getUsageStyle();
        if (masterFormat != null) style = masterFormat;

        style = style.replace("{name}", command.name());
        style = style.replace("{usage}", usage.isEmpty() ? "" : usage);
        if (sender instanceof Player) {
            style = Colorize.translateBungeeHex(style.replace(" - {description}", ""));
            Tellraw.fromLegacy(style).tooltip(ChatColor.GRAY + description).send((Player) sender);
        } else {
            style = style.replace("{description}", description.isEmpty() ? "" : description);
            sender.sendMessage(Colorize.translateBungeeHex(style.replace("/", " - ")));
        }
    }

    public ICommand getCommand(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(ICommand.class)) return null;
        return clazz.getAnnotation(ICommand.class);
    }

    private boolean failedLastCompletion(String[] args, List<Complete> previous, int index, CommandSender sender, String last) {
        List<String> replace = tabCompletion.getOrDefault(index, new ArrayList<>());

        for (Complete complete : previous) {
            if (!complete.handleReplacement(sender, replace, last.toLowerCase(Locale.ENGLISH))) {
                return false;
            }
        }
        replace.addAll(handleCompletions(Lists.newArrayList(), sender, index, args));
        return replace.isEmpty();
    }

    public Map<Integer, List<String>> getTabCompletion() {
        return tabCompletion;
    }

    public void tabComplete(List<String> completions, CommandSender sender, String[] args) {
        Objects.requireNonNull(sender, "Sender cannot be null");
        Objects.requireNonNull(args, "Arguments cannot be null");
        int length = args.length;
        if (length == 0) return;

        try {
            int previous = (length - 1);
            if (((length - 3) >= 0)
                    && failedLastCompletion(args, tabCompletionArg.getOrDefault(previous, new ArrayList<>()), previous, sender, args[length - 3]))
                return;
        } catch (Exception ignored) {
        }

        String toComplete = args[length - 1].toLowerCase(Locale.ENGLISH);
        List<String> replacements = tabCompletion.getOrDefault(length, new ArrayList<>());

        if ((!tabCompletion.isEmpty()) || (!tabCompletionArg.isEmpty())) {
            if ((length - 2) >= 0) {
                List<Complete> completes = tabCompletionArg.getOrDefault(length, new ArrayList<>());
                if (!completes.isEmpty()) {
                    for (Complete complete : completes) {
                        List<String> replace = new ArrayList<>();
                        if (complete.handleReplacement(sender, replace, args[length - 2].toLowerCase(Locale.ENGLISH))) {
                            replacements = replace;
                            break;
                        }
                    }
                }
            }
        }

        List<String> strings = handleCompletions(Lists.newArrayList(), sender, length, args);
        if (!strings.isEmpty()) {
            for (String s : strings) {
                if (!replacements.contains(s)) replacements.add(s);
            }
        }


        for (String command : replacements) {
            if (command.isEmpty()) continue;
            if (StringUtil.startsWithIgnoreCase(net.md_5.bungee.api.ChatColor.stripColor(command), net.md_5.bungee.api.ChatColor.stripColor(toComplete))) {
                completions.add(command);
            }
        }
    }

    /**
     * Can the sender run the command?
     *
     * @param sender - The sender you wish to check
     * @return true - sender can run the command
     * false - sender can not run the command
     */
    public boolean canExecute(CommandSender sender) {
        return true;
    }

    /**
     * Will allow you to combine a String[] into a single message
     *
     * @param args  - The Array that needs to be merged
     * @param start - The location of where you want to start merging
     * @return - The Combined String
     */
    public String messageMaker(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (canExecute(sender)) run(sender);
        } else {
            if (canExecute(sender)) run(sender, args);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        Objects.requireNonNull(sender, "Sender cannot be null");
        Objects.requireNonNull(args, "Arguments cannot be null");
        List<String> completions = new ArrayList();
        tabComplete(completions, sender, args);
        if (!completions.isEmpty()) return completions;
        return new ArrayList<>();
    }

    String[] newArgs(String[] args) {
        ArrayList<String> newArgs = new ArrayList<>();
        Collections.addAll(newArgs, args);
        newArgs.remove(0);
        return newArgs.toArray(new String[newArgs.size()]);
    }

    protected interface Complete {
        boolean handleReplacement(CommandSender sender, List<String> replacements, String name);
    }

    /**
     * Will target the player/players specified as the argument
     *
     * <br>
     * Allowed Arguments: <Customizable>
     *
     * @param argument targeted player(s)
     * @param value    fetched player(s)
     */
    public void send(Argument argument, Consumer<Player> value) {
        List<String> targets = new ArrayList<>();
        argument.target(targets);
        targets.forEach(arg -> send(arg, value));
    }

    /**
     * Will target the player/players specified as the argument
     *
     * <br>
     * Allowed Arguments: <Customizable>
     *
     * @param argument targeted player(s)
     * @param value    fetched player(s)
     * @param type     return type
     *                 SUCCESS
     *                 NO_PLAYER <If there was no player found>
     *                 NO_TEAM <If the team selected was not found>
     */
    public void send(Argument argument, Consumer<Player> value, Consumer<ReturnType> type) {
        List<String> targets = new ArrayList<>();
        argument.target(targets);
        targets.forEach(arg -> send(arg, value, type));
    }

    /**
     * Will target the player/players specified as the argument
     *
     * <br>
     * Allowed Arguments:
     * - @a (all players)
     * - team=<scoreboard team> (Will target a certain team and all of its members)
     * - <name> (Will target the selected player)
     * - <uuid> (Will target the selected player)
     *
     * @param argument targeted player(s)
     * @param value    fetched player(s)
     */
    public void send(String argument, Consumer<Player> value) {
        send(argument, value, value1 -> {
        });
    }

    /**
     * Will target the player/players specified as the argument
     *
     * <br>
     * Allowed Arguments:
     * - @a (all players)
     * - team=<scoreboard team> (Will target a certain team and all of its members)
     * - <name> (Will target the selected player)
     * - <uuid> (Will target the selected player)
     *
     * @param argument targeted player(s)
     * @param value    fetched player(s)
     * @param type     return type
     *                 SUCCESS
     *                 NO_PLAYER <If there was no player found>
     *                 NO_TEAM <If the team selected was not found>
     */
    public void send(String argument, Consumer<Player> value, Consumer<ReturnType> type) {
        if (argument.equals("@a")) {
            type.accept(ReturnType.SUCCESS);
            Bukkit.getOnlinePlayers().forEach(value::accept);
            return;
        }

        // Checks if the argument is a team selector
        if (argument.startsWith("team=")) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(argument.replaceFirst("team=", ""));
            if (team == null) {
                type.accept(ReturnType.NO_TEAM);
                return;
            }
            type.accept(ReturnType.SUCCESS);
            for (String name : team.getEntries()) {
                Player target = Bukkit.getPlayer(name);
                if (target != null) value.accept(target);
            }
            return;
        }

        // Checks if the argument is a UUID
        try {
            UUID uuid = UUID.fromString(argument);
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) {
                type.accept(ReturnType.NO_PLAYER);
                return;
            }
            type.accept(ReturnType.SUCCESS);
            value.accept(target);
            return;
        } catch (Exception ignored) {
        }

        // Checks if the argument is a player name
        Player target = Bukkit.getPlayer(argument);
        if (target == null) {
            type.accept(ReturnType.NO_PLAYER);
            return;
        }
        type.accept(ReturnType.SUCCESS);
        value.accept(target);
    }

    public enum ReturnType {
        SUCCESS,
        NO_TEAM,
        NO_PLAYER
    }

    public interface Argument {
        void target(List<String> targets);
    }

    public List<String> getOnlinePlayers() {
        List<String> list = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        return list;
    }
}
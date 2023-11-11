package org.bsdevelopment.command;

import org.bsdevelopment.command.annotation.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class CommandRegistry<P extends JavaPlugin> {
    private final P plugin;

    /**
     * @param plugin - The plugin that is using the registry
     */
    public CommandRegistry(P plugin) {
        this.plugin = plugin;
    }

    /**
     * Will register the targeted command
     *
     * @param commandCore - targeted {@link ParentCommand} that you wish to register
     * @throws Exception - If the method fails {EG: No {@link ICommand} Annotation or there is no command name}
     */
    public void register(ParentCommand commandCore) throws Exception {
        Class<?> clazz = commandCore.getClass();
        if (!clazz.isAnnotationPresent(ICommand.class))
            throw new Exception(clazz.getSimpleName() + " Missing ICommand.class");
        ICommand annotation = clazz.getAnnotation(ICommand.class);
        if (annotation.name().isEmpty()) throw new Exception(clazz.getSimpleName() + " missing name");

        PluginCommand command = plugin.getCommand(annotation.name());
        command.setExecutor(commandCore);
        command.setTabCompleter(commandCore);
        if (!annotation.description().isEmpty()) command.setDescription(annotation.description());
        if (annotation.alias().length != 0) command.setAliases(Arrays.asList(annotation.alias()));
    }


    public void registerDirectly(ParentCommand commandCore) throws Exception {
        Class<?> clazz = commandCore.getClass();
        if (!clazz.isAnnotationPresent(ICommand.class))
            throw new Exception(clazz.getSimpleName() + " Missing ICommand.class");
        ICommand annotation = clazz.getAnnotation(ICommand.class);
        if (annotation.name().isEmpty()) throw new Exception(clazz.getSimpleName() + " missing name");

        BasicCommand command = new BasicCommand(annotation, commandCore);
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(plugin.getName(), command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class BasicCommand extends BukkitCommand implements TabCompleter {
        private final ParentCommand commandCore;

        private BasicCommand(ICommand command, ParentCommand commandCore) {
            super(command.name());
            if ((command.description() != null) && (!command.description().isEmpty())) setDescription(command.description());
            if ((command.usage() != null) && (!command.usage().isEmpty())) setUsage(command.usage());
            if (command.alias().length != 0) setAliases(Arrays.asList(command.alias()));

            this.commandCore = commandCore;
        }


        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            commandCore.run(sender, args);
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return commandCore.onTabComplete(sender, command, label, args);
        }
    }
}
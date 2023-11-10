package org.bsdevelopment.command;

import org.bsdevelopment.command.annotation.ICommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

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
}
package fun.corruptedsmp.contextRun;

import fun.corruptedsmp.contextRun.command.Run;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ContextRun extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand command = Objects.requireNonNull(getCommand("run"));
        command.setExecutor(new Run(this));
        command.setTabCompleter(new Run(this));

        Bukkit.getConsoleSender().sendMessage(TextDecoration.BOLD + (NamedTextColor.GREEN + "Plugin enabled successfully"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(TextDecoration.BOLD + (NamedTextColor.GREEN + "Plugin disabled successfully"));
    }
}

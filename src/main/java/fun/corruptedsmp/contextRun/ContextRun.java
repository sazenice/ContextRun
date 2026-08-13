package fun.corruptedsmp.contextRun;

import fun.corruptedsmp.contextRun.command.Run;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
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

        if (getConfig().getBoolean("telemetry")){
            Bukkit.getConsoleSender().sendMessage(TextDecoration.BOLD + (NamedTextColor.GREEN + "Enabled telemetry"));
            int pluginId = 33346;
            Metrics metrics = new Metrics(this, pluginId);

            metrics.addCustomChart(
                    new SimplePie("allow_chaining", () -> String.valueOf(getConfig().getBoolean("allow-chaining")))
            );
        }else{
            Bukkit.getConsoleSender().sendMessage(TextDecoration.BOLD + (NamedTextColor.GREEN + "Disabled telemetry"));
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(TextDecoration.BOLD + (NamedTextColor.GREEN + "Plugin disabled successfully"));
    }
}

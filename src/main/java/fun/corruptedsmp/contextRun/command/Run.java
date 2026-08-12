package fun.corruptedsmp.contextRun.command;

import fun.corruptedsmp.contextRun.ContextRun;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.GameRules;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Run implements CommandExecutor, TabCompleter {
    private final ContextRun plugin;

    public Run(ContextRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (args.length < 2) {
            sender.sendMessage(NamedTextColor.RED + "Usage: /run <console|player|block> [player] <command...>");
            return true;
        }

        for (String arg : args) {
            if (arg.equalsIgnoreCase("run")) {
                if (plugin.getConfig().getBoolean("allow-chaining")) { continue; }
                sender.sendMessage(NamedTextColor.DARK_RED + "Command not allowed");
                return true;
            }
        }

        String context = args[0].toLowerCase();

        switch (context) {
            case "console": {
                String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                sender.sendMessage(NamedTextColor.GREEN + "Dispatching command...");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                break;
            }
            case "player": {
                if (args.length < 3) {
                    sender.sendMessage(NamedTextColor.RED + "Usage: /run player <player> <command...>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(NamedTextColor.RED + "Player not found or not online!");
                    return true;
                }
                String cmd = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                sender.sendMessage(NamedTextColor.GREEN + "Dispatching command...");
                Bukkit.dispatchCommand(target, cmd);
                break;
            }
            case "block": {
                World world = Bukkit.getWorlds().getFirst();
                if (sender instanceof Player p) { world = p.getWorld(); }
                if (world == null) { sender.sendMessage(NamedTextColor.RED + "Cannot get world data"); return true; }
                if (Boolean.FALSE.equals(world.getGameRuleValue(GameRules.COMMAND_BLOCKS_WORK))){
                    sender.sendMessage(NamedTextColor.RED + "Command blocks aren't enabled");
                    return true;
                }

                Block targetBlock = world.getBlockAt(0,0,0);
                Block powerBlock = world.getBlockAt(1, 0, 0);

                Chunk chunk = world.getChunkAt(targetBlock);

                chunk.setForceLoaded(true);

                BlockData oldBData = targetBlock.getBlockData();
                BlockData oldBData2 = powerBlock.getBlockData();

                String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                targetBlock.setType(Material.AIR);
                powerBlock.setType(Material.AIR);

                sender.sendMessage(NamedTextColor.GREEN + "Dispatching command...");

                targetBlock.setType(Material.COMMAND_BLOCK);
                CommandBlock cmdBlock = (CommandBlock) targetBlock.getState();
                cmdBlock.setCommand(cmd);
                cmdBlock.name(Component.text(NamedTextColor.GREEN + "ContextRun"));
                cmdBlock.update();
                powerBlock.setType(Material.REDSTONE_BLOCK);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    targetBlock.setBlockData(oldBData);
                    powerBlock.setBlockData(oldBData2);
                    chunk.setForceLoaded(false);
                }, 5L);
                break;
            }
            default:
                sender.sendMessage(NamedTextColor.RED + "Unknown context: " + context + ". Use 'console', 'block', or 'player'.");
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("contextrun.run")) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("console");
            completions.add("player");
            completions.add("block");
        } else if (args[0].equalsIgnoreCase("player") && args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        String prefix = args[args.length - 1].toLowerCase();
        if (!prefix.isEmpty()) {
            completions.removeIf(value -> !value.toLowerCase().startsWith(prefix));
        }
        return completions;
    }
}

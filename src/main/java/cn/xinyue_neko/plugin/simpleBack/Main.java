package cn.xinyue_neko.plugin.simpleBack;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.*;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    private static final String TITLE = "[SimpleBack] ";
    private final Map<UUID, Location> dLocations = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getConsoleSender().sendMessage(TITLE + ChatColor.AQUA + "简易自杀与返回死亡点插件已启用");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        dLocations.put(player.getUniqueId(), player.getLocation().clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dLocations.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TITLE + ChatColor.RED + "命令只能在游戏中执行");
            return true;
        }

        Player player = (Player) sender;
        Location dPoint = dLocations.get(player.getUniqueId());

        switch (command.getName().toLowerCase()) {
            case "die":
                player.setHealth(0);
                return true;
            case "back":
                if (dPoint == null) {
                    sender.sendMessage(TITLE + ChatColor.RED + "你没有死亡记录");
                    return true;
                }
                dPoint.getWorld().getChunkAt(dPoint.getBlockX() >> 4, dPoint.getBlockZ() >> 4).load();
                try {
                    if (player.isOnline()) {
                        player.teleport(dPoint);
                        player.sendMessage(TITLE + ChatColor.GREEN + "已返回死亡点");
                        dLocations.remove(player.getUniqueId());
                    }
                } catch (Exception e) {
                    player.sendMessage(TITLE + ChatColor.RED + "发生错误, 请重试");
                    getLogger().warning("玩家 \"" + player.getName() + "\" 返回死亡点时发生错误: " + e.getMessage());
                }
            default:
                return false;
        }
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(TITLE + ChatColor.AQUA + "Bye");
    }
}

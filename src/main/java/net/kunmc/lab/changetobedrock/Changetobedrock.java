package net.kunmc.lab.changetobedrock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Changetobedrock extends JavaPlugin implements Listener {
    private static boolean isEnabled = false;
    private static int range;
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        config = getConfig();
        range = config.getInt("range");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("視線の先が岩盤になるプラグインが有効になりました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("視線の先が岩盤になるプラグインが無効になりました");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(!isEnabled){
            return;
        }
        Player player = event.getPlayer();
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = null;
        boolean isBlock = false;
        while (iter.hasNext()) {
            lastBlock = iter.next();
            Material material = lastBlock.getType();
            if (material == Material.AIR ||
                material == Material.CAVE_AIR) {
                continue;
            }
            isBlock = true;
            break;
        }
        if(isBlock) lastBlock.setType(Material.BEDROCK);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(!cmd.getName().equalsIgnoreCase("changetobedrock")){
            return false;
        }
        if(args.length < 1){
            sender.sendMessage("引数が足りません");
            return false;
        }else if(args.length > 2){
            sender.sendMessage("引数が多すぎます");
            return false;
        }
        String arg1 = args[0];
        getLogger().info(arg1);
        if(arg1.equalsIgnoreCase("on")){
            isEnabled = true;
            sender.sendMessage("視線の先が岩盤になるプラグインをオンにしました");
            return true;
        } else if(arg1.equalsIgnoreCase("off")){
            isEnabled = false;
            sender.sendMessage("視線の先が岩盤になるプラグインをオフにしました");
            return true;
        } else if(arg1.equalsIgnoreCase("range")){
        } else {
            sender.sendMessage("「on」「off」または「distance」と入力してください");
            return false;
        }
        String arg2 = args[1];
        if(arg1.equalsIgnoreCase("range")){
            try {
                range = Integer.parseInt(arg2);
                sender.sendMessage("有効範囲が" + range + "ブロックに変更されました");
                config.set("range", range);
                saveConfig();
                return true;
            }catch(NumberFormatException e) {
                sender.sendMessage("第2引数には整数値を入力してください");
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return Stream.of("on", "off", "range")
                        .filter(e -> e.startsWith(args[0]))
                        .collect(Collectors.toList());
            case 2:
                if(args[0].equalsIgnoreCase("range")){
                    return Collections.singletonList("0");
                }
        }
        return Collections.emptyList();
    }
}

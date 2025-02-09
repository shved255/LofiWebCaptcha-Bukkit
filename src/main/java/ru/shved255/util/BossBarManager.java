package ru.shved255.util;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class BossBarManager {

    private final Map<String, BossBar> bossBars = new HashMap<>();

    public BossBar createBossBar(String key, String title, BarColor color, BarStyle style) {
        if(!bossBars.containsKey(key)) {
            BossBar bossBar = Bukkit.createBossBar(title, color, style);
            bossBars.put(key, bossBar);
        }
        return bossBars.get(key);
    }

    public void setTitle(String key, String title) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            bossBar.setTitle(title);
        }
    }

    public void setColor(String key, BarColor color) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            bossBar.setColor(color);
        }
    }

    public void setProgress(String key, double progress) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            bossBar.setProgress(Math.max(0, Math.min(1, progress)));
        }
    }

    public void addPlayer(String key, Player player) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            bossBar.addPlayer(player);
        }
    }

    public void removePlayer(String key, Player player) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    public void showToAll(String key) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                bossBar.addPlayer(player);
            }
        }
    }

    public void hideFromAll(String key) {
        BossBar bossBar = bossBars.get(key);
        if(bossBar != null) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                bossBar.removePlayer(player);
            }
        }
    }

    public void removeBossBar(String key) {
        BossBar bossBar = bossBars.remove(key);
        if(bossBar != null) {
            bossBar.removeAll();
        }
    }
    
    public Map<String, BossBar> getBossBars() {
    	return bossBars;
    }
    
}
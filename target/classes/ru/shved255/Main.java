package ru.shved255;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.shved255.Listeners.Listeners;
import ru.shved255.util.Config;

public class Main extends JavaPlugin {

	private static Main inst;
	private Config config;
	
	@Override
	public void onLoad() {
    	(inst = this).saveDefaultConfig();
	}
	
	@Override
	public void onEnable() {
    	config = new Config(this);
    	File cfg = new File(getDataFolder() + File.separator + "config.yml"); {
    		if(!cfg.exists()) {
    			saveDefaultConfig();
    		}
    	}
    	File cfg1 = new File(getDataFolder() + File.separator + "players.yml"); {
    		if(!cfg1.exists()) {
    			saveResource("players.yml", false); 
    		}
    	}
		Bukkit.getPluginManager().registerEvents(new Listeners(), this);
	}
	
	@Override
	public void onDisable() {
		
	}

	public static Main getInst() {
		return inst;
	}

	public Config config() {
		return config;
	}
	
}

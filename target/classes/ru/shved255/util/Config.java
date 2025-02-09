package ru.shved255.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ru.shved255.Main;

public class Config {
    
    private Main plugin;
    private FileConfiguration cfg;
    private String success;
    private String proverka;
    private String site;
    private String key;
    private Boolean title;
    private String titleUp;
    private String titleDown;
    private String titleUpNo;
    private String titleDownNo;
    private int hours;
	private List<String> commandsPlayer;
	private List<String> commandsServer;
	private int time;
	private String timeKick;
	private boolean barOn;
	private boolean barTimer;
	private String barStyle;
	private String barColor;
	private String barText;
    
    public Config(Main plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder() + File.separator, "config.yml"); 
        this.cfg = YamlConfiguration.loadConfiguration(file);
        success = cfg.getString("messages.success");
        proverka = cfg.getString("messages.message");
        site = cfg.getString("site");
        key = cfg.getString("key");
	    commandsPlayer = cfg.getStringList("playerCommands");
	    commandsServer = cfg.getStringList("serverCommands");
	    title = cfg.getBoolean("titles.titleOn");
	    titleUp = cfg.getString("titles.title");
	    titleDown = cfg.getString("titles.subTittle");
	    titleUpNo = cfg.getString("titles.titleNo");
	    titleDownNo = cfg.getString("titles.subTittleNo");
	    hours = cfg.getInt("hours");
	    time = cfg.getInt("time");
	    timeKick = cfg.getString("messages.timeKick");
	    barOn = cfg.getBoolean("bossbar.enable");
	    barTimer = cfg.getBoolean("bossbar.timer");
	    barStyle = cfg.getString("bossbar.style");
	    barColor = cfg.getString("bossbar.color");
	    barText = cfg.getString("bossbar.text");
    }
    
    public FileConfiguration getConfig() {
        return this.cfg;
    }
    
    public String ChatColor(String text) {
        return Hex.toChatColorString(text);
    }

    public String getSuccess() {
        return ChatColor(success);
    }

    public String getProverka() {
        return ChatColor(proverka);
    }

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }

	public List<String> getCommandsPlayer() {
		return commandsPlayer;
	}

	public List<String> getCommandsServer(Player player) {
	    String nick = player.getName();
	    List<String> result = new ArrayList<>();
	    for(String command : this.commandsServer) {
	        result.add(command.replace("{PLAYER}", nick));
	    }
	    return result;
	}

	public Boolean getTitle() {
		return title;
	}

	public String getTitleUp() {
		return ChatColor(titleUp);
	}

	public String getTitleDown() {
		return ChatColor(titleDown);
	}

	public String getTitleUpNo() {
		return ChatColor(titleUpNo);
	}

	public String getTitleDownNo() {
		return ChatColor(titleDownNo);
	}

	public int getHours() {
		return hours;
	}

	public int getTime() {
		return time;
	}

	public String getTimeKick() {
		return ChatColor(timeKick);
	}

	public String getBarText() {
		return ChatColor(barText);
	}

	public boolean getBarOn() {
		return barOn;
	}

	public boolean getBarTimer() {
		return barTimer;
	}

	public String getBarStyle() {
		return barStyle;
	}

	public String getBarColor() {
		return barColor;
	}
}

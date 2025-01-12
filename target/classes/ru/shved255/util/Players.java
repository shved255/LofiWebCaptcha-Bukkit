package ru.shved255.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ru.shved255.Main;

public class Players {
	
	private Main plugin = Main.getInst();
    private File filePlayers = new File(plugin.getDataFolder() + File.separator + "players.yml");
    private FileConfiguration yml = (FileConfiguration)YamlConfiguration.loadConfiguration(this.filePlayers);
	
    public boolean needVerifed(Player player) {
    	try {
			yml.load(filePlayers);
	        UUID uuid = player.getUniqueId();
	        String id = uuid.toString();
	        if (!yml.contains(id)) {
	            return true; 
	        }
	        int hours = plugin.config().getHours();
	        String oldIp = yml.getString(id + ".ip");
	        String newIp = player.getAddress().getAddress().getHostAddress();
	        if(!oldIp.equals(newIp)) {
	            return true; 
	        }
	        Instant oldInst = Instant.parse(yml.getString(id + ".date"));
	        Instant newInst = Instant.now();
	        long timeDiff = ChronoUnit.HOURS.between(oldInst, newInst);
	        if(timeDiff >= hours) {
	            return true; 
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	        return false; 
		} catch (IOException e) {
			e.printStackTrace();
	        return false; 
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
	        return false; 
		}
        return false; 
    }

    public boolean setVerifed(Player player) {
        UUID uuid = player.getUniqueId();
        String id = uuid.toString();
        yml.set(id + ".ip", player.getAddress().getAddress().getHostAddress());
        yml.set(id + ".name", player.getName());
        yml.set(id + ".date", Instant.now().toString());
        try {
        	yml.save(filePlayers);
            return true; 
        } catch (IOException e) {
            return false; 
        }
    }
	
}

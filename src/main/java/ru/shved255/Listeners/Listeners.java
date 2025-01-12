package ru.shved255.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.shved255.Main;
import ru.shved255.GetVerify.SiteGet;
import ru.shved255.util.Players;
import ru.shved255.util.Utils;

public class Listeners implements Listener {
	
	private List<String> on = new ArrayList<String>();
	private List<String> no = new ArrayList<String>();
	private final SiteGet l = new SiteGet();
	private Main plugin = Main.getInst();
	private Utils u = new Utils();
	private Players p = new Players();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
	    Player player = event.getPlayer();
	    String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();
        String base64 = u.code(nick);
        if(p.needVerifed(player)) {
        	if(!on.contains(nick)) {
        		on.add(nick);
        	} 
        	if(on.contains(nick)) {
        		if(plugin.config().getTitle()) {
        			player.sendTitle(plugin.config().getTitleUp(), plugin.config().getTitleDown(), 10, 70, 20);
        		}
        		String key = plugin.config().getKey();
        		Boolean save = l.Get(plugin.config().getSite() + "/save-ip.php?ip=" + nick + "/" + ip + "&secret=" + key);
        		String site = plugin.config().getSite() + "?username=" + base64;
        		final int[] taskId = {-1}; 
        		taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
        			@Override
	            	public void run() {
        				if(player.isOnline()) {
        					player.sendMessage(plugin.config().getProverka().replace("{url}", String.valueOf(site)));
	        				Boolean dataIsFound = l.isDataPresent(nick + "/" + ip, plugin.config().getSite() + "/data.php?secret=" + key);
		                    if(dataIsFound & save) {
		                    	Boolean get = l.Get(plugin.config().getSite() + "/remove.php?id=" + nick + "/" + ip + "&file=verified_ips.txt&secret=" + key);
		                    	if(get) {
		                    		on.remove(nick);
		                    		no.add(nick);
		                    	}
		                    }
		                    if(no.contains(nick)) {
		                    	no.remove(nick);
		                    	player.sendMessage(plugin.config().getSuccess());
		                    	Bukkit.getScheduler().runTaskLater(plugin, () -> { 
		                    		List<String> commandsPlayer = plugin.config().getCommandsPlayer();
		                    		List<String> commandsServer = plugin.config().getCommandsServer(player);
		                    		for(String command : commandsPlayer)
		                    			Bukkit.dispatchCommand((CommandSender)player, command); 
		                    		for(String command : commandsServer)
		                    			Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), command); 
            	        		}, 20 * 1);
		                    	if(plugin.config().getTitle()) {
		                    		player.sendTitle(plugin.config().getTitleUpNo(), plugin.config().getTitleDownNo(), 10, 70, 20);
		                    	}
		                    	Bukkit.getScheduler().cancelTask(taskId[0]);
		                    }
        				} else {
        					Bukkit.getScheduler().cancelTask(taskId[0]);
        				}
        			}
        		}, 20, 20);
        	}
        }
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(on.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(on.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(on.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String nick = player.getName();
		if(on.contains(nick)) {
			on.remove(nick);
		}
		if(no.contains(nick)) {
			no.remove(nick);
		}
	}
	
}

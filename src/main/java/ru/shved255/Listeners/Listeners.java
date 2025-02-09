package ru.shved255.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
import ru.shved255.util.BossBarManager;
import ru.shved255.util.Players;
import ru.shved255.util.Utils;

public class Listeners implements Listener {
	
	private List<String> on = new ArrayList<String>();
	private List<String> no = new ArrayList<String>();
	private final SiteGet l = new SiteGet();
	private Main plugin = Main.getInst();
	private Utils u = new Utils();
	private Players p = new Players();
	private BossBarManager manager = new BossBarManager();
	private Map<Player, Integer> timer = new ConcurrentHashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {		
	    Player player = event.getPlayer();
		String key = plugin.config().getKey();
	    String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();
        String base64 = u.code(nick);
        if(p.needVerifed(player)) {
    	    startTask(player);
    	    if(plugin.config().getBarOn()) {
    	    	startBossBar(player);
    	    }
        	if(!on.contains(nick)) {
        		on.add(nick);
        	} 
        	if(on.contains(nick)) {
        		if(plugin.config().getTitle()) {
        			player.sendTitle(plugin.config().getTitleUp(), plugin.config().getTitleDown(), 10, 70, 20);
        		}
        		Boolean save = l.Get(plugin.config().getSite() + "/save-ip.php?ip=" + nick + "/" + ip + "&secret=" + key);
        		String site = plugin.config().getSite() + "?username=" + base64;
        		final int[] taskId = {-1}; 
        		taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
        			@Override
	            	public void run() {
        				if(player.isOnline()) {
        					player.sendMessage(plugin.config().getProverka().replace("{url}", String.valueOf(site)).replace("{TIME}", String.valueOf(timer.get(player))));
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
		                    	onSuccess(player);
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
		onKick(player);
	}
	
	public void startBossBar(Player player) {
		if(plugin.config().getBarOn()) {
			BarStyle style = BarStyle.valueOf(plugin.config().getBarStyle());
			BarColor color = BarColor.valueOf(plugin.config().getBarColor());
			BossBar bar = manager.createBossBar(player.getName(), plugin.config().getBarText().replace("{TIME}", String.valueOf(timer.get(player))), color, style);
			bar.addPlayer(player);
			int[] task = {1};
			task[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
				if(!player.isOnline()) {
					Bukkit.getScheduler().cancelTask(task[0]);
				}
				if(!on.contains(player.getName())) {
					Bukkit.getScheduler().cancelTask(task[0]);
				}
				if(plugin.config().getBarTimer() && player.isOnline() && on.contains(player.getName())) {
					manager.setProgress(player.getName(), (double) timer.get(player) / plugin.config().getTime());
				}
				manager.setTitle(player.getName(), plugin.config().getBarText().replace("{TIME}", String.valueOf(timer.get(player))));
			}, 20, 20);
		}
	}
	
	public void startTask(Player player) {
		int[] task = {1};
		int[] time = {1};
		time[0] = plugin.config().getTime();
		timer.put(player, time[0]);
		task[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if(!player.isOnline()) {
				Bukkit.getScheduler().cancelTask(task[0]);
			}
			if(!on.contains(player.getName())) {
				Bukkit.getScheduler().cancelTask(task[0]);
			}
			time[0]--;
			timer.put(player, time[0]);
			if(time[0] <= 0) {
				onKick(player);
				player.kickPlayer(plugin.config().getTimeKick());
			}
		}, 20, 20);
	}
	
	public void onKick(Player player) {
		String key = plugin.config().getKey();
	    String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();
		l.Get(plugin.config().getSite() + "/remove.php?id=" + nick + "/" + ip + "&file=need_verif.txt&secret=" + key);
		if(timer.containsKey(player)) {
			timer.remove(player);	
		}
		if(on.contains(player.getName())) {
			on.remove(player.getName());
		}
		if(no.contains(player.getName())) {
			no.remove(player.getName());
		}
		if(manager.getBossBars().containsKey(player.getName())) {
			manager.removePlayer(player.getName(), player);
			manager.removeBossBar(player.getName());
		}
	}
	
	public void onSuccess(Player player) {
		if(timer.containsKey(player)) {
			timer.remove(player);	
		}
		if(on.contains(player.getName())) {
			on.remove(player.getName());
		}
		if(no.contains(player.getName())) {
			no.remove(player.getName());
		}
		if(manager.getBossBars().containsKey(player.getName())) {
			manager.removePlayer(player.getName(), player);
			manager.removeBossBar(player.getName());
		}
    	player.sendMessage(plugin.config().getSuccess());
    	Bukkit.getScheduler().runTaskLater(plugin, () -> { 
    		p.setVerifed(player);
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
	}
	
}

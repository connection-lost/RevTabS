package me.crafter.mc.revtabs;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Team;
import org.mcsg.double0negative.tabapi.TabAPI;

public class TabListener implements Listener {
	
	private Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("RevTab");
	private List<String> playerlist = new ArrayList<String>();
	private Chat chat;
	private boolean vault;
	
	public TabListener(Plugin plug){
		for (Player player : Bukkit.getOnlinePlayers()){
			playerlist.add(player.getName());
		}
		vault = setupChat();
		if (vault);
//		{
//			Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("RevTab"), new Runnable(){
//				public void run(){
//					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reloader reload revtab");
//				}
//			}, 1200);
//		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player p = event.getPlayer();
		TabAPI.setPriority(plugin, p, 2);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("RevTab"), new Runnable(){
			public void run(){
				updateAll();
			}
		}, 4);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("RevTab"), new Runnable(){
			public void run(){
				updateAll();
			}
		}, 4);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("RevTab"), new Runnable(){
			public void run(){
				updateAll();
			}
		}, 4);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("RevTab"), new Runnable(){
			public void run(){
				updateAll();
			}
		}, 4);
	}
	
	public void updateTab(Player p){
//		TabAPI.resetTabList(p);
		TabAPI.setTabString(plugin, p, 0, 0, ChatColor.GREEN + " ===============", 0);
		TabAPI.setTabString(plugin, p, 0, 1, ChatColor.GOLD + "     Rev-Craft", 0);
		TabAPI.setTabString(plugin, p, 0, 2, ChatColor.GREEN + "=============== ", 0);

		
		
		
		TabAPI.setTabString(plugin, p, 1, 0, nullFor(2,0), 0);
		TabAPI.setTabString(plugin, p, 1, 1, nullFor(2,1), 0);
		TabAPI.setTabString(plugin, p, 1, 2, nullFor(2,2), 0);
		
		
		
		List<Player> players = get48Players();
		int x = 2;
		int y = 0;
		for (Player player : players){
			TabAPI.setTabString(plugin, p, x, y, getPlayerName(player), ((CraftPlayer)player).getHandle().ping);
			y += 1;
			if (y > 2){
				y = 0;
				x += 1;
			}
		}
//		while (x < 18){
//			TabAPI.setTabString(plugin, p, x, y, ChatColor.GRAY+"(¿Õ) "+(x*3-6+y+1)+"/99", 0);
//			y += 1;
//			if (y > 2){
//				y = 0;
//				x += 1;
//			}
//		}
		
		while (x < 19){
			TabAPI.setTabString(plugin, p, x, y, TabAPI.nextNull(), 0);
			y += 1;
			if (y > 2){
				y = 0;
				x += 1;
			}
		}
		
		TabAPI.setTabString(plugin, p, 18, 0, getDay(p));
		TabAPI.setTabString(plugin, p, 18, 1, p.getName(), 5);
		TabAPI.setTabString(plugin, p, 18, 2, ChatColor.YELLOW+"Ping >> "+ String.valueOf(((CraftPlayer)p).getHandle().ping/2) , ((CraftPlayer)p).getHandle().ping/2);
		//TabAPI.setTabString(plugin, p, 18, 1, String.valueOf(((CraftPlayer)p).getHandle().by), 5);
		TabAPI.setTabString(plugin, p, 17, 2, String.valueOf(Bukkit.getOnlinePlayers().size()) + "/99", 0);
		
		TabAPI.setTabString(plugin, p, 19, 0, ChatColor.DARK_GREEN + " ===============", 0);
		TabAPI.setTabString(plugin, p, 19, 1, ChatColor.DARK_AQUA + "     Rev-Craft", 0);
		TabAPI.setTabString(plugin, p, 19, 2, ChatColor.DARK_GREEN + "=============== ", 0);
		TabAPI.updatePlayer(p);

	}
	
	public String getWeather(Player p){
		return (ChatColor.YELLOW + String.valueOf(p.getWorld().getTime()/1200) + "·Ö" + String.valueOf(p.getWorld().getTime()/20%60) + "Ãë").replace("9", "¢á");
	}
	
	public String getDay(Player p){
		return (ChatColor.YELLOW + String.valueOf(p.getWorld().getFullTime()/24000) + "ÈÕ").replace("9", "¢á");
	}

	public void updateAll(){
		for (Player p : Bukkit.getOnlinePlayers()){
			updateTab(p);
		}
	}

	
	
	
	
	
    private boolean setupChat() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
        	return false;
        }
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
        	return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }
    
    public void setChat(Chat c){
    	chat = c;
    }
    
    public String getPlayerName(Player p){
    	if (chat == null) return p.getName();
    	if (p.isDead()) return ChatColor.GRAY + p.getName();
    	Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p);
    	if (team != null && !(team.getName().startsWith("__")) && team.getPrefix() != null){
    		return ChatColor.getLastColors(Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p).getPrefix())+ChatColor.stripColor(p.getName());
    	} else {
    		return ChatColor.getLastColors(chat.getPlayerPrefix(p))+ChatColor.stripColor(p.getName());
    	}
    }
    
    public List<Player> get48Players(){
    	List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
    	if (players.size() <= 48) return players;
    	else return players.subList(0, 47);
    }
	
//    public int getBarPing(){
//    	int bar = (int) ((Bukkit.getWorld("world").getFullTime()/20%5));
//    	if (bar == 4) return 0;
//    	if (bar == 3) return 250;
//    	if (bar == 2) return 500;
//    	if (bar == 1) return 900;
//    	return 2000;    	
//    }
    
    
   public String nullFor(int x, int y){
	   String ret = "";
	   int loc = 3*x+y;
	   if (loc < 10){
		   ret += ChatColor.COLOR_CHAR+"1";
		   for (int n = 0; n < loc; n++){
			   ret += " ";
		   }
	   } else if (loc < 20){
		   loc -= 9;
		   ret += ChatColor.COLOR_CHAR+"2";
		   for (int n = 0; n < loc; n++){
			   ret += " ";
		   }
	   } else if (loc < 30){
		   loc -= 19;
		   ret += ChatColor.COLOR_CHAR+"3";
		   for (int n = 0; n < loc; n++){
			   ret += " ";
		   }
	   } else if (loc < 40){
		   loc -= 29;
		   ret += ChatColor.COLOR_CHAR+"4";
		   for (int n = 0; n < loc; n++){
			   ret += " ";
		   }
	   } else if (loc < 50){
		   loc -= 39;
		   ret += ChatColor.COLOR_CHAR+"5";
		   for (int n = 0; n < loc; n++){
			   ret += " ";
		   }
	   } else if (loc < 60){
		   loc -= 49;
		   ret += ChatColor.COLOR_CHAR+"6";
		   for (int n = 0; n < loc; n++){
			   ret += " ";
		   }
	   }
	   return ret;
   }
	

}
   
   



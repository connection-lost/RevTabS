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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class TabListener implements Listener {

	private List<String> playerlist = new ArrayList<String>();
	private Chat chat;

	private String templateheader = ChatColor.GOLD + "Rev-Craft " + ChatColor.GRAY + "- " + ChatColor.GREEN + "@day@ @time@";
	private String templatefooter = ChatColor.RESET + " @realname@" + ChatColor.RESET + " Ping: @ping@";
	
	public TabListener(Plugin plug){
		for (Player player : Bukkit.getOnlinePlayers()){
			playerlist.add(player.getName());
		}
		setupChat();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		//final Player p = event.getPlayer();
		//TabAPI.setPriority(plugin, p, 2);
		scheduleUpdate();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		scheduleUpdate();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		scheduleUpdate();
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		scheduleUpdate();
	}
	
	public void scheduleUpdate(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("RevTabS"), new Runnable(){
			public void run(){
				updateAll();
			}
		}, 4);
	}

	public void updateAll(){
		for (Player p : Bukkit.getOnlinePlayers()){
			updateTab(p);
		}
	}
	
	public void updateTab(Player p){
		sendHeaderFooter(p);
	}


	public void sendHeaderFooter(Player p){
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
	    String[] histab = getHeaderFooter(p);
	    pc.getChatComponents().write(0, WrappedChatComponent.fromText(histab[0])).write(1, WrappedChatComponent.fromText(histab[1]));
	    try {
	    	protocolManager.sendServerPacket(p, pc);
	    } catch (Exception e){}
	}
	
	public String[] getHeaderFooter(Player p){
		String header = templateheader.replaceAll("@realname@", getPlayerName(p)).replaceAll("@name@", p.getName())
				.replaceAll("@ping@", getTextPing(p)).replaceAll("@day@", getDay(p)).replaceAll("@time@", getTime(p));
		String footer = templatefooter.replaceAll("@realname@", getPlayerName(p)).replaceAll("@name@", p.getName())
				.replaceAll("@ping@", getTextPing(p)).replaceAll("@day@", getDay(p)).replaceAll("@time@", getTime(p));
		String[] ret = {header,footer};
		return ret;
	}
	
	public String getTextPing(Player p) {
		int ping = ((CraftPlayer)p).getHandle().ping/2;
		if (ping == 0){
			return ChatColor.GRAY + "?";
		} else if (ping <= 60){
			return ChatColor.GREEN + "" + ping;
		} else if (ping <= 120){
			return ChatColor.DARK_GREEN + "" + ping;
		} else if (ping <= 250){
			return ChatColor.YELLOW + "" + ping;
		} else if (ping <= 400){
			return ChatColor.GOLD + "" + ping;
		} else if (ping <= 600){
			return ChatColor.RED + "" + ping;
		} else if (ping <= 1000){
			return ChatColor.DARK_RED + "" + ping;
		} else {
			return ChatColor.DARK_PURPLE + "" + ping;
		}
		
	}

	public String getTime(Player p){
		String ret = "";
		int currtime = ((int)(p.getWorld().getTime()) + 6000) % 24000;

		int hour = currtime/1000;
		String hours = String.valueOf(hour);
		if (hour < 10){
			hours = "0" + hours;
		}
		
		int min = ((currtime % 1000) * 60) / 1000;
		String mins = String.valueOf(min);
		if (min < 10){
			mins = "0" + mins;
		}
		
		ret = hours + ":" + mins;
		return ret;
	}
	
	public String getDay(Player p){
		return (ChatColor.YELLOW + String.valueOf(p.getWorld().getFullTime()/24000) + "ÈÕ");
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
    
//    public List<Player> get48Players(){
//    	List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
//    	if (players.size() <= 48) return players;
//    	else return players.subList(0, 47);
//    }
	
//    public int getBarPing(){
//    	int bar = (int) ((Bukkit.getWorld("world").getFullTime()/20%5));
//    	if (bar == 4) return 0;
//    	if (bar == 3) return 250;
//    	if (bar == 2) return 500;
//    	if (bar == 1) return 900;
//    	return 2000;    	
//    }
    
    
//   public String nullFor(int x, int y){
//	   String ret = "";
//	   int loc = 3*x+y;
//	   if (loc < 10){
//		   ret += ChatColor.COLOR_CHAR+"1";
//		   for (int n = 0; n < loc; n++){
//			   ret += " ";
//		   }
//	   } else if (loc < 20){
//		   loc -= 9;
//		   ret += ChatColor.COLOR_CHAR+"2";
//		   for (int n = 0; n < loc; n++){
//			   ret += " ";
//		   }
//	   } else if (loc < 30){
//		   loc -= 19;
//		   ret += ChatColor.COLOR_CHAR+"3";
//		   for (int n = 0; n < loc; n++){
//			   ret += " ";
//		   }
//	   } else if (loc < 40){
//		   loc -= 29;
//		   ret += ChatColor.COLOR_CHAR+"4";
//		   for (int n = 0; n < loc; n++){
//			   ret += " ";
//		   }
//	   } else if (loc < 50){
//		   loc -= 39;
//		   ret += ChatColor.COLOR_CHAR+"5";
//		   for (int n = 0; n < loc; n++){
//			   ret += " ";
//		   }
//	   } else if (loc < 60){
//		   loc -= 49;
//		   ret += ChatColor.COLOR_CHAR+"6";
//		   for (int n = 0; n < loc; n++){
//			   ret += " ";
//		   }
//	   }
//	   return ret;
//   }
	

}
   
   



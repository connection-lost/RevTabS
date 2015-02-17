package me.crafter.mc.revtabs;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TabWorker {
	
	Player p;
	private Chat chat;
	
	public TabWorker(Player pp){
		p=pp;
	}


//    private boolean setupChat() {
//        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
//                return false;
//        }
//        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
//        if (rsp == null) {
//                return false;
//        }
//        chat = rsp.getProvider();
//        return chat != null;
//    }
	
    public String getPlayerName(Player p){
    	if (chat == null) return p.getName();
    	if (p.isDead()) return ChatColor.GRAY + p.getName();
    	return ChatColor.getLastColors(chat.getPlayerPrefix(p))+p.getName();
    }
    
    public List<Player> get48Players(){
    	List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
    	if (players.size() <= 48) return players;
    	else return players.subList(0, 47);
    }
	
    public int getBarPing(){
    	int bar = (int) ((Bukkit.getWorld("world").getFullTime()/20%5));
    	if (bar == 4) return 0;
    	if (bar == 3) return 250;
    	if (bar == 2) return 500;
    	if (bar == 1) return 900;
    	return 2000;    	
    }
}

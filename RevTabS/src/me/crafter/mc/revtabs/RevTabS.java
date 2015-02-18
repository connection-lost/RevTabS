package me.crafter.mc.revtabs;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RevTabS extends JavaPlugin {

	public final Logger logger = Logger.getLogger("Mincraft");
	public final TabListener pl = new TabListener(this);
	

    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.pl, this);
        
        Chat chat;
        chat = setupChat();
        
        if (chat != null){
        	pl.setChat(chat);
        }
        
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
			public void run(){
				pl.updateAll();
			}
		}, 21, 17);
        
    }
 

    public void onDisable() {
    	
    }
    
    private Chat setupChat() {
    	Chat ret;
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
                return null;
        }
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
                return null;
        }
        ret = rsp.getProvider();
        return ret;
    }

	
}

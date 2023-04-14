package com.ultrahstudios.AntiIPS;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Painting;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Struct;
import java.util.List;


public class Main extends JavaPlugin implements Listener{

	public String path;
	
	@Override
	public void onEnable () {
		SaveNewConfig();
		System.out.println ("AntiIPS Active");
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	
	@Override
	public void onDisable () {
		System.out.println ("AntiIPS Desactive");
	}
	
	@EventHandler
	public void onPlayerChat (AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		String playerName = event.getPlayer().getName();
		String[] ipComponentString = msg.split("\\s");
		String domainRegex = "^[a-zA-Z0-9]+\\s+[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(domainRegex);
        Matcher matcher = pattern.matcher(msg);
        String[] DetectSpacePort = msg.split(" ");
		
		List<String> msgStrings = this.getConfig().getStringList("Messages");
		
		if (msg.contains(":")) {
		    String[] parts = msg.split(":");
		    String ip = parts[0];
		    int port = Integer.parseInt(parts[1]);
		    
		    for (int i = 0; i < msgStrings.size(); i++) {
				String textString = msgStrings.get(i);
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', textString.replace("%player%", playerName)));
				event.setCancelled(true);
		    }
		    
		    event.setCancelled(true);
		    SaveWords(playerName, msg);
		}
		
		
		String[] parts = msg.split(" ");
		for (String part : parts) {
		    try {
		        int port = Integer.parseInt(part);
		        if (port >= 1 && port <= 65535) {
		        	for (int i = 0; i < msgStrings.size(); i++) {
						String textString = msgStrings.get(i);
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', textString.replace("%player%", playerName)));
						event.setCancelled(true);
				    }
				    
				    event.setCancelled(true);
				    SaveWords(playerName, msg);
		        }
		    } catch (NumberFormatException e) {
		    }
		}
		
		
		
		 if (matcher.matches()) {
			 for (int i = 0; i < msgStrings.size(); i++) {
					String textString = msgStrings.get(i);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', textString.replace("%player%", playerName)));
					event.setCancelled(true);
			 }
				SaveWords(playerName, msg);
		 }

		 if (ipComponentString.length == 4) {
			try {
				int[] ipAddress = new int[4];
				
				for (int i = 0; i < 4; i++) {
					ipAddress[i] = Integer.parseInt (ipComponentString[i]);
					
					if(ipAddress[i] < 0 || ipAddress[i] > 255) {
						return;
					}
				}
				
				for (int i = 0; i < msgStrings.size(); i++) {
					String textString = msgStrings.get(i);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', textString.replace("%player%", playerName)));
				}
				event.setCancelled(true);
				SaveWords(playerName, msg);
			} catch (Exception e) {

			}
		}
		
		try {
			InetAddress address = InetAddress.getByName(msg);
			
			if (address instanceof java.net.Inet4Address || address instanceof java.net.Inet6Address) {
				for (int i = 0; i < msgStrings.size(); i++) {
					String textString = msgStrings.get(i);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', textString.replace("%player%", playerName)));
				}		
				SaveWords(playerName, msg);
				event.setCancelled(true);
			}
			
		} catch (UnknownHostException e) {

		}
	}
	
	
	
	public void SaveWords (String plrName, String wordSend) {
		
		if (this.getConfig() == null) {
			System.out.println ("AntiIPS Config file not exist ");
			SaveNewConfig();
		}
		
		List<String> list = this.getConfig().getStringList ("MessageReceived." + plrName + ".Message");
		list.add(wordSend);
		
		this.getConfig().set("MessageReceived." + plrName + ".Message", list);
		
	    saveConfig();
		System.out.println ("AntiIPS New Message Saved player " + plrName);
	}
	
	public void SaveNewConfig() {
		File configFile = new File(this.getDataFolder(), "config.yml");
		path = configFile.getPath();
		
		if (!configFile.exists()) {
			this.getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
	}
}
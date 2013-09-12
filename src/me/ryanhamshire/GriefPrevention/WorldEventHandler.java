package me.ryanhamshire.GriefPrevention;

import java.io.File;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldEventHandler implements Listener {
	//convenience reference for the singleton datastore
	private DataStore dataStore;
	
	public WorldEventHandler(DataStore dataStore)
	{
		this.dataStore = dataStore;
	}
	
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event)
	{
		if(GriefPrevention.instance == null)
			return;
		
		//Check if we care about the world(Exists in config) and add it to the internal list
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DataStore.configFilePath));
		World world = event.getWorld();
		GriefPrevention gp = GriefPrevention.instance;
		
		if(world == null || gp == null)
			return;
		
		System.out.println("New World Loading: " + world.getName());
		
		//Claim worlds
		List<String> claimsEnabledWorldNames = config.getStringList("GriefPrevention.Claims.Worlds");
		if(claimsEnabledWorldNames != null && claimsEnabledWorldNames.size() != 0)
		{	
			if(claimsEnabledWorldNames.contains(world.getName()))
			{
				//Add to list of enabled worlds
				gp.config_claims_enabledWorlds.add(world.getName());
				//Load claims
				dataStore.loadUnloadedClaims(world.getName());
			}
		}
		
		//Creative Worlds
		List<String> creativeWorlds = config.getStringList("GriefPrevention.Claims.CreativeRulesWorlds");
		if(creativeWorlds != null && creativeWorlds.size() != 0)
		{
			if(creativeWorlds.contains(world.getName()))
			{
				//Add to list of enabled creative worlds
				gp.config_claims_enabledCreativeWorlds.add(world.getName());
			}
		}
		
		//PvP Worlds
		List<String> pvpWorlds = config.getStringList("GriefPrevention.PvP.Worlds");
		if(pvpWorlds != null && pvpWorlds.size() != 0)
		{
			if(pvpWorlds.contains(world.getName()))
			{
				//Add to list of enabled pvp worlds
				gp.config_pvp_enabledWorlds.add(world);
			}
		}

	}
	
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event)
	{
		//Remove from world lists and unload claims
		GriefPrevention gp = GriefPrevention.instance;
		World world = event.getWorld();
		
		if(gp == null || world == null)
			return;
		
		//Enabled world
		if(gp.config_claims_enabledWorlds.remove(world.getName())) //Remove returns true if it removed something
		{
			dataStore.unloadClaims(world);
		}
		//Creative
		gp.config_claims_enabledCreativeWorlds.remove(world.getName());
		//PvP
		gp.config_pvp_enabledWorlds.remove(world.getName());

		System.out.println("Unloaded world " + world);
		
	}
}

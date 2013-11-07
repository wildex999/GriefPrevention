package me.ryanhamshire.GriefPrevention.baseprotect;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import w999.baseprotect.BaseProtect;
import w999.baseprotect.IClaimManager;
import w999.baseprotect.WorldInteract;

public class BaseProtectHandler implements IClaimManager {
	
	GriefPrevention griefPrevention;
	Claim cachedClaim; //The same interactor will do many checks against the same block and same claim during a tick, Cache it.
	
	public BaseProtectHandler(GriefPrevention gp)
	{
		griefPrevention = gp;
	}

	@Override
	public boolean claimCanBuild(WorldInteract interactor, Location loc) {
		w999.baseprotect.PlayerData owner = interactor.getItemOwner();
		
		//Get the claim at the location
		Claim claim = griefPrevention.dataStore.getClaimAt(loc, false, cachedClaim);
		
		//Cache tke result
		cachedClaim = claim;
		
		//If no claim, just allow it(For now)
		if(claim == null)
			return true;
			
		//If no owner and inside a claim, don't let it through
		if(owner == null || owner.getBukkitPlayer() == null)
			return false;
		
		//Check if player allowed to build in claim
		if(claim.allowBuild(owner.getBukkitPlayer()) == null)
			return true;
		
		return false;
	}

	@Override
	public boolean claimCanInteract(WorldInteract interactor, Location loc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean claimCanContainer(WorldInteract interactor, Location loc) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Get the owner of the currently ticking item from BaseProtect
	public Player getCurrentOwner()
	{
		return BaseProtect.getCurrentOwner();
	}

	@Override
	public void setSkipEvent(boolean skip) {
		GriefPrevention.ignoreEvents = skip;
	}

}

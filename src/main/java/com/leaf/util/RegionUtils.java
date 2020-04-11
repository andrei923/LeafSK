package com.leaf.util;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;


public class RegionUtils {
	 
	  static WorldGuardWrapper wrapper = WorldGuardWrapper.getInstance();
	  
	  public static IWrappedRegion createRegion(Player p, Location pos1, Location pos2) {
		  pos1.setY(0);
		  pos2.setY(256);
		  String regionName = p.getUniqueId().toString() + "-id-" + RegionUtils.getRegionsSize(p).toString();
		  wrapper.addCuboidRegion(regionName, pos1, pos2);
		  Optional<IWrappedRegion> region = wrapper.getRegion(pos1.getWorld(), regionName.toString());
		  RegionUtils.addOwner(region.get(), p);
		  return region.get();
	  }
	  //
	  public static void deleteRegion(Object region, World w) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  wrapper.removeRegion(w, regionOne.getId());
	  }	
	  //
	  public static void setEnterMessage(IWrappedRegion region, String name) {
		  region.setFlag(wrapper.getFlag("greeting", String.class).orElse(null), name);
	  }	
	  public static void setEnterMessage(Object region, String name) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  regionOne.setFlag(wrapper.getFlag("greeting", String.class).orElse(null), name);
	  }	
	  //
	  public static void setLeaveMessage(IWrappedRegion region, String name) {
		  region.setFlag(wrapper.getFlag("farewell", String.class).orElse(null), name);
	  }		
	  public static void setLeaveMessage(Object region, String name) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  regionOne.setFlag(wrapper.getFlag("farewell", String.class).orElse(null), name);
	  }	
	  //
	  public static void setFlag(IWrappedRegion region, String flag, WrappedState value) {
	       region.setFlag(wrapper.getFlag(flag, WrappedState.class).orElse(null), value);
	  }	 
	  public static void setFlag(Object region, String flag, WrappedState value) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  regionOne.setFlag(wrapper.getFlag(flag, WrappedState.class).orElse(null), value);
	  }
	  //
	  public static void addOwner(IWrappedRegion region, Player p) {
		  region.getOwners().addPlayer(p.getUniqueId());
	  }	  
	  public static void addOwner(Object region, Player p) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  regionOne.getOwners().addPlayer(p.getUniqueId());
	  }	 	  
	  //
	  public static void addMember(IWrappedRegion region, Player p) {
		  region.getMembers().addPlayer(p.getUniqueId());
	  }	  	  
	  public static void addMember(Object region, Player p) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  regionOne.getMembers().addPlayer(p.getUniqueId());
	  }
	  //
	  public static boolean isMember(IWrappedRegion region, Player p) {
		  return (region.getMembers().getPlayers().contains(p.getUniqueId()));
	  }	 
	  public static boolean isMember(Object region, Player p) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  return (regionOne.getMembers().getPlayers().contains(p.getUniqueId()));
	  }	  
	  //
	  public static void removeMember(IWrappedRegion region, Player p) {
		  region.getMembers().removePlayer(p.getUniqueId());
	  }	
	  public static void removeMember(Object region, Player p) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;		  
		  regionOne.getMembers().removePlayer(p.getUniqueId());
	  }	
	  //
	  public static boolean isOwner(IWrappedRegion region, Player p) {
		  return (region.getOwners().getPlayers().contains(p.getUniqueId()));
	  }	
	  public static boolean isOwner(Object region, Player p) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;			  
		  return (regionOne.getOwners().getPlayers().contains(p.getUniqueId()));
	  }		  
	  //
	  public static boolean checkOverlap(Location loc1, Location loc2) {
		  return wrapper.getRegions(loc1, loc2).size() > 0;
	  }
	  //
	  public static Set<UUID> getMembers(IWrappedRegion region) {
		  return region.getMembers().getPlayers();
	  }	  
	  public static Set<UUID> getMembers(Object region) {
		  IWrappedRegion regionOne = (IWrappedRegion) region;
		  return regionOne.getMembers().getPlayers();
	  }	  
	  //
	  public static IWrappedRegion getRegionAt(Location loc) {
		  Set<Entry<String, IWrappedRegion>> set = wrapper.getRegions(loc.getWorld()).entrySet();
		  for (Entry<String, IWrappedRegion> region : set) {
			  if (region.getValue().contains(loc)) {;
			  	return region.getValue();
			  }
		}
		return null;
	  }	
		
	  public static Integer getRegionsSize(Player p) {
		  Integer amount = 0;
		  Set<Entry<String, IWrappedRegion>> set = wrapper.getRegions(p.getWorld()).entrySet();
		  for (Entry<String, IWrappedRegion> test : set) {
			  if (test.getKey().contains(p.getUniqueId().toString())) {;
			  	amount = amount + 1;
			  }
		}
		return amount;		  
		  
	  }
}
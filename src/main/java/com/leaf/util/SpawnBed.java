package com.leaf.util;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;

public class SpawnBed {
	
    //BedWars bed.
	public static void setBed(Block start, BlockFace facing, Material material) {
	    for (Bed.Part part : Bed.Part.values()) {
	        start.setBlockData(Bukkit.createBlockData(material, (data) -> {
	           ((Bed) data).setPart(part);
	           ((Bed) data).setFacing(facing);
	        }));
	        start = start.getRelative(facing.getOppositeFace());
	    }
	}	

}
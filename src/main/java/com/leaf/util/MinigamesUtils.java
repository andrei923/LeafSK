package com.leaf.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

public class MinigamesUtils {
	
    private static Field fieldFireballDirX;
    private static Field fieldFireballDirY;
    private static Field fieldFireballDirZ;

    private static Method craftFireballHandle;
    
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

	//BedWars Fireball.
    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String nmsFireball = "net.minecraft.server." + version + "EntityFireball";
        String craftFireball = "org.bukkit.craftbukkit." + version + "entity.CraftFireball";
        try {
            Class<?> fireballClass = Class.forName(nmsFireball);

            //should be accessible by default.
            fieldFireballDirX = fireballClass.getDeclaredField("dirX");
            fieldFireballDirY = fireballClass.getDeclaredField("dirY");
            fieldFireballDirZ = fireballClass.getDeclaredField("dirZ");

            craftFireballHandle = Class.forName(craftFireball).getDeclaredMethod("getHandle");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static Fireball setDirection(Fireball fireball, Vector direction) {
        try {
            Object handle = craftFireballHandle.invoke(fireball);
            fieldFireballDirX.set(handle, direction.getX() * 0.10D);
            fieldFireballDirY.set(handle, direction.getY() * 0.10D);
            fieldFireballDirZ.set(handle, direction.getZ() * 0.10D);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return fireball;
    }	
	
}
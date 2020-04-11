package com.leaf.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import com.leaf.util.ReflectionUtils;

public class ActionBarNew implements ActionBarAPI{
	
	ReflectionUtils reflection = new ReflectionUtils();
	
    public void sendActionBar(Player player, String message) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, NoSuchFieldException {
    	Class<?> clsIChatBaseComponent = ReflectionUtils.getNMSClass("IChatBaseComponent");
        Class<?> clsChatMessageType = ReflectionUtils.getNMSClass("ChatMessageType");
        Object chatBaseComponent = clsIChatBaseComponent.getClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + message + "\"}");
        //Object chatBaseComponent = reflection.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, "{\"text\": \"" + message + "\"}");
        Object chatMessageType = clsChatMessageType.getMethod("valueOf", String.class).invoke(null, "GAME_INFO");
        Object packetPlayOutChat = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(clsIChatBaseComponent, clsChatMessageType).newInstance(chatBaseComponent, chatMessageType);
        Method sendPacket = ReflectionUtils.getConnection ( player ).getClass().getMethod ( "sendPacket", ReflectionUtils.getNMSClass ( "Packet" ));
        sendPacket.invoke (ReflectionUtils.getConnection(player), packetPlayOutChat );
    }
}
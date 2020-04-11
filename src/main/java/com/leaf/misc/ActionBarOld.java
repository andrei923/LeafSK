package com.leaf.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

import com.leaf.util.ReflectionUtils;

public class ActionBarOld implements ActionBarAPI {
	
	ReflectionUtils reflection = new ReflectionUtils();
	
    public void sendActionBar(Player player, String message) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, NoSuchFieldException {
    	Class< ? > packetPlayOutChat = ReflectionUtils.getNMSClass ( "PacketPlayOutChat" );
        Constructor< ? > packetConstructor = packetPlayOutChat.getConstructor ( ReflectionUtils.getNMSClass ( "IChatBaseComponent" ), byte.class );
        Class< ? > ichat = ReflectionUtils.getNMSClass ( "IChatBaseComponent" );
        Class< ? > chatSerializer = ichat.getClasses ( )[ 0 ];
        Method csA = chatSerializer.getMethod ( "a", String.class );
        Object component = csA.invoke ( chatSerializer, "{\"text\": \"" + message + "\"}" );
        Object packet = packetConstructor.newInstance ( component, ( byte ) 2 );
        Method sendPacket = ReflectionUtils.getConnection ( player ).getClass().getMethod ( "sendPacket", ReflectionUtils.getNMSClass ( "Packet" ));
        sendPacket.invoke (ReflectionUtils.getConnection(player), packet );
    }
}
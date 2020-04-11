package com.leaf.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Just a simple reflection class, just to not depend on Skript 2.2+ (I think it is the only thing I use from it)
 * @author
 */
public class ReflectionUtils {

	public static final String packageVersion = Bukkit.getServer().getClass().getPackage().getName().split(".v")[1];
	
	/**
	 * Check if a class exists.
	 * @param clz - The class path, like 'org.bukkit.entity.Player'
	 * @return true if it exists
	 */
	public static boolean hasClass(String clz){
		try {
			Class.forName(clz);
			return true;
		} catch (Exception e){

		}
		return false;
		
	}
	/**
	 * Get a class from a string.
	 * @param clz - The string path of a class
	 * @return The class
	 */
	public static Class<?> getClass(String clz){
		try {
			return Class.forName(clz);
		} catch (Exception e){

		}
		return null;
	}
	public static <T> Constructor<T> getConstructor(Class<T> clz, Class<?>...parameters){
		try {
			return clz.getConstructor(parameters);
		} catch (Exception e){

		}
		return null;
	}
	/**
	 * Checks if a method exists or not
	 * @param clz - The class to check.
	 * @param method - The method's name
	 * @param parameters - The parameters of method, can be null if none
	 * @return - true if it exists
	 */
	public static boolean hasMethod(Class<?> clz, String method, Class<?>...parameters){
		try{
			return getMethod(clz, method, parameters) != null;
		} catch(Exception e){

		}
		return false;
	}
	
	public static Method getMethod(Class<?> clz, String method, Class<?>... parameters){
		try {
			return clz.getDeclaredMethod(method, parameters); 
		} catch (Exception e){

		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Class<?> clz, String method, Object instance, Object... parameters){
		try {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			int x = 0;
			for (Object obj : parameters)
				parameterTypes[x++] = obj.getClass();
			Method m = clz.getDeclaredMethod(method, parameterTypes);
			m.setAccessible(true);
			return (T) m.invoke(instance, parameters);
		} catch (Exception e){

		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Method method, Object instance, Object... parameters){
		try {
			method.setAccessible(true);
			return (T) method.invoke(instance, parameters);
		} catch (Exception e){

		}
		return null;
	}
	/**
	 * Return a new instance of a class.
	 * @param clz - The class
	 * @return A instance object of clz.
	 */
	public static <T> T newInstance(Class<T> clz){
		try {
			Constructor<T> c = clz.getDeclaredConstructor();
			c.setAccessible(true);
			return c.newInstance();
		} catch (Exception e) {

		}
		return null;
	}
	/**
	 * Return a new instance of a class.
	 * @return A instance object of clz.
	 */
	public static <T> T newInstance(Constructor<T> constructor, Object...objects){
		try {
			return constructor.newInstance(objects);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Use to set a object from a private field.
	 * @param from - The class to set the field
	 * @param obj - The instance of class, you can use null if the field is static.
	 * @param field - The field name
	 * @return True if successful.
	 */
	public static <T> boolean setField(Class<T> from, Object obj, String field, Object newValue){
		try {
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			f.set(obj, newValue);
			return true;
		} catch (Exception e){

		}
		return false;
	}
	/**
	 * Use to get a object from a private field. If it will return null in case it was unsuccessful.
	 * @param from - The class to get the field
	 * @param obj - The instance of class, you can use null if the field is static.
	 * @param field - The field name
	 * @return The object value.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Class<?> from, Object obj, String field){
		try{
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (Exception e){

		}
		return null;
		
	}
	public static Class<?> getNMSClass(String classString) {
		String version = getVersion();
		String name = "net.minecraft.server." + version + classString;
		Class<?> nmsClass = null;
		try {
			nmsClass = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().warning("Unable to get NMS class \'" + name + "\'! You are probably running an unsupported version!");
			return null;
		}
		return nmsClass;
	}
	
	public static Class<?> getOBCClass(String classString) {
		String name = "org.bukkit.craftbukkit." + getVersion() + classString;
		@SuppressWarnings("rawtypes")
		Class obcClass = null;
		try {
			obcClass = Class.forName(name);
		}
		catch (ClassNotFoundException error) {
			error.printStackTrace();
			return null;
		}
		return obcClass;
	}
	
	public static Channel getChannel(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object connection = getConnection(player);
		Field connectionField = connection.getClass().getField("networkManager");
		Object networkManager = connectionField.get(connection);
		Field channelField = networkManager.getClass().getField("channel");
		return (Channel) channelField.get(networkManager);
	}
	
	public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object nmsPlayer = getHandle(player);
		Field connectionField = nmsPlayer.getClass().getField("playerConnection");
		return connectionField.get(nmsPlayer);
	}
	
	public static Object getHandle(Object obj) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (obj != null) {
			Method getHandle = obj.getClass().getMethod("getHandle");
			getHandle.setAccessible(true);
			Object nmsPlayer = getHandle.invoke(obj);
			return nmsPlayer;
		}
		return null;
	}
	
	public static void sendPacket(Player player, Object object) throws NoSuchMethodException {
		try {
			Method method = getConnection(player).getClass().getMethod("sendPacket", getNMSClass("Packet"));
			method.invoke(getConnection(player), object);
		} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static Object getField(String field, Class<?> clazz, Object object) {
		Field f = null;
		Object obj = null;
		try {
			f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			obj = f.get(object);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
		Field f = null;
		try {
			f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			f.set(object, toSet);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}
	
	public static Object getNMSBlock(Block block) {
		try {
			Method method = ReflectionUtils.getOBCClass("util.CraftMagicNumbers").getDeclaredMethod("getBlock", Block.class);
			method.setAccessible(true);
			return method.invoke(ReflectionUtils.getOBCClass("util.CraftMagicNumbers"), block);
		} catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
	}	
	
}

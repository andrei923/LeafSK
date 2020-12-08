package com.leaf;

import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import ch.njol.skript.Skript;
import org.bukkit.ChatColor;
import ch.njol.skript.SkriptAddon;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.leaf.gui.GUIManager;
import com.leaf.misc.ActionBarAPI;
import com.leaf.misc.ActionBarNew;
import com.leaf.misc.ActionBarOld;
import com.leaf.skriptmirror.LibraryLoader;
import com.leaf.skriptmirror.ParseOrderWorkarounds;
import com.leaf.yaml.api.ConstructedClass;
import com.leaf.yaml.api.RepresentedClass;
import com.leaf.yaml.utils.SkriptYamlUtils;
import com.leaf.yaml.utils.yaml.SkriptYamlConstructor;
import com.leaf.yaml.utils.yaml.SkriptYamlRepresenter;
import com.leaf.yaml.utils.yaml.YAMLProcessor;

public class Leaf extends JavaPlugin {
	public static Leaf plugin;	
	private ActionBarAPI actionbar;	
	private int serverVersion;			
	private static RowSetFactory rowSetFactory;
	private static SkriptYamlRepresenter representer;
	private static SkriptYamlConstructor constructor;
	private static GUIManager gui;
	private final static HashMap<String, String> REGISTERED_TAGS = new HashMap<String, String>();	
	
	public final static Logger LOGGER = Bukkit.getServer() != null ? Bukkit.getLogger() : Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public final static HashMap<String, YAMLProcessor> YAML_STORE = new HashMap<String, YAMLProcessor>();
	public static boolean isTagRegistered(String tag) { return REGISTERED_TAGS.containsKey(tag);}
	
	public Leaf() {
		if (plugin != null)
			throw new IllegalStateException("LeafSK can't have two instances.");
		plugin = this;	
	}

	public static void registerTag(JavaPlugin plugin, String tag, Class<?> c, RepresentedClass<?> rc, ConstructedClass<?> cc) {
		String prefix = plugin.getName().toLowerCase() + "-";
		if (!tag.startsWith(prefix))
			tag = prefix + tag;
		if (!REGISTERED_TAGS.containsKey(tag)) {
			if (!representer.contains(c)) {
				if (SkriptYamlUtils.getType(rc.getClass()) == c) {
					if (SkriptYamlUtils.getType(cc.getClass()) == c) {
						REGISTERED_TAGS.put(tag, plugin.getName());
						representer.register(tag, c, rc);
						constructor.register(tag, cc);
					} else {
						warn("The class '" + c.getSimpleName() + "' that the plugin '" + plugin.getName()
								+ "' is trying to register does not match constructed class '"
								+ SkriptYamlUtils.getType(cc.getClass()).getSimpleName() + "' for constructor '"
								+ cc.getClass().getSimpleName() + "' the tag '" + tag + "' was not registered");
					}
				} else {
					warn("The class '" + c.getSimpleName() + "' that the plugin '" + plugin.getName()
							+ "' is trying to register does not match represented class '"
							+ SkriptYamlUtils.getType(rc.getClass()).getSimpleName() + "' for representer '"
							+ rc.getClass().getSimpleName() + "' the tag '" + tag + "' was not registered");
				}
			} else {
				warn("The class '" + c.getSimpleName() + "' that the plugin '" + plugin.getName()
						+ "' is trying to register for the tag '" + tag + "' is already registered");
			}
		} else {
			warn("The plugin '" + plugin.getName() + "' is trying to register the tag '" + tag
					+ "' but it's already registered to '" + REGISTERED_TAGS.get(tag) + "'");
		}
	}
	
	@Override
	public void onEnable() {
        String[] strings = Bukkit.getBukkitVersion().split("-");
        String[] version = strings[0].split("\\.");
        serverVersion = Integer.parseInt(version[1]);
        
		representer = new SkriptYamlRepresenter();
		constructor = new SkriptYamlConstructor();
		Boolean hasSkript = plugin.getServer().getPluginManager().isPluginEnabled("Skript");
		if (!hasSkript || !Skript.isAcceptRegistrations()) {
			if (!hasSkript)
				log("Error 404 - Skript not found.", Level.SEVERE);
			else
				log("LeafSK can't be loaded when the server is already loaded.", Level.SEVERE);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		SkriptAddon leaf = Skript.registerAddon(this).setLanguageFileDirectory("lang");
		if (serverVersion <= 18) {
    		actionbar = new ActionBarOld();
		} else {
    		actionbar = new ActionBarNew();
    	}
		try {
			rowSetFactory = RowSetProvider.newFactory();
			leaf.loadClasses(getClass().getPackage().getName(), "db", "effects", "expressions", "yaml", "skript", "conditions", "events", "misc", "skriptgui");	
			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
				leaf.loadClasses(getClass().getPackage().getName(), "papi");	
			}	
			//SkriptMirror.
			leaf.loadClasses("com.leaf.skriptmirror");	
		    Path dataFolder = Leaf.getInstance().getDataFolder().toPath();
		    LibraryLoader.loadLibraries(dataFolder);
		    ParseOrderWorkarounds.reorderSyntax();		    
			//			    
		} catch (Exception e) {
			e.printStackTrace();
		}}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
	public static Leaf getInstance(){
		return plugin;
	}
    public ActionBarAPI getActionbar() {
        return actionbar;
    }
	public static void log(String msg){
	    log(msg, Level.INFO);
	}
	public static void log(String msg, Level lvl){
	    plugin.getLogger().log(lvl, msg);
	}
	public static RowSetFactory getRowSetFactory() {
	    return rowSetFactory;
	}
	public static String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}	
	public SkriptYamlRepresenter getRepresenter() {
		return representer;
	}
	public SkriptYamlConstructor getConstructor() {
		return constructor;
	}
	public int getServerVersion() {
		return serverVersion;
	}
	public static void warn(String error) {
		LOGGER.warning("[LeafSK] " + error);
	}
	public static void error(String error) {
		LOGGER.severe("[LeafSK] " + error);
	}	
	public static GUIManager getGUIManager(){
		if (gui == null)
			 gui = new GUIManager(getInstance());
	    return gui;
	}	
	  
}
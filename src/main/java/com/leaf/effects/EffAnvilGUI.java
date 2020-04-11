package com.leaf.effects;

import com.leaf.Leaf;
import com.leaf.events.AnvilGUICloseEvent;
import com.leaf.events.AnvilGUICompleteEvent;
import com.leaf.util.Registry;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.wesjd.anvilgui.AnvilGUI;

public class EffAnvilGUI extends Effect {
	
	static {
		Registry.newEffect(EffAnvilGUI.class, "[leaf] open anvil gui with %itemstack% and name %string% to %player%");
	}
	
	private Expression<Player> player;
	private Expression<String> search;	
	private Expression<ItemStack> item;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		item = (Expression<ItemStack>) arg0[0];
		search = (Expression<String>) arg0[1];		
		player = (Expression<Player>) arg0[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "[leaf] open anvil gui to %player%";
	}

	@Override
	protected void execute(Event arg0) {
		Player p = player.getSingle(arg0);
		ItemStack i = item.getSingle(arg0);
		String s = search.getSingle(arg0);
		new AnvilGUI.Builder()
	    .onClose(player -> {                      //called when the inventory is closing
	        AnvilGUICloseEvent AnvilGUICloseEvent = new AnvilGUICloseEvent(p);	        
	        Bukkit.getServer().getPluginManager().callEvent(AnvilGUICloseEvent);
	    })
	    .onComplete((player, text) -> {           //called when the inventory output slot is clicked	          
	    	AnvilGUICompleteEvent AnvilGUICompleteEvent = new AnvilGUICompleteEvent(p, text);	        
	    	Bukkit.getServer().getPluginManager().callEvent(AnvilGUICompleteEvent);
	     return AnvilGUI.Response.close();
	    })
	//    .preventClose()                           //prevents the inventory from being closed
	    .text(s)     //sets the text the GUI should start with
	    .item(i) //use a custom item for the first slot
	    //.title("Enter your answer.")              //set the title of the GUI (only works in 1.14+)
	    .plugin(Leaf.getInstance())                 //set the plugin instance
	    .open(p);                          //opens the GUI for the player provided;
		}
    }

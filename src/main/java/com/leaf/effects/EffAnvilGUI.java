package com.leaf.effects;

import com.leaf.Leaf;
import com.leaf.events.AnvilGUICloseEvent;
import com.leaf.events.AnvilGUICompleteEvent;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.wesjd.anvilgui.AnvilGUI;

public class EffAnvilGUI extends Effect {
	
	static {
		Skript.registerEffect(EffAnvilGUI.class, "[leaf] open anvil gui with %itemstack% and %itemstack% named %string% to %player%");
	}
		
	private Expression<Player> player;
	private Expression<String> search;	
	private Expression<ItemStack> itemLeft;
	private Expression<ItemStack> itemRight;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		itemLeft = (Expression<ItemStack>) arg0[0];
		itemRight = (Expression<ItemStack>) arg0[1];		
		search = (Expression<String>) arg0[2];		
		player = (Expression<Player>) arg0[3];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "[leaf] open anvil gui to %player%";
	}

	@Override
	protected void execute(Event arg0) {
		Player p = player.getSingle(arg0);
		ItemStack itmLeft = itemLeft.getSingle(arg0);
		ItemStack itmRight = itemRight.getSingle(arg0);
		String name = search.getSingle(arg0);
		new AnvilGUI.Builder()
	    .onClose(player -> {                      //called when the inventory is closing
	        AnvilGUICloseEvent AnvilGUICloseEvent = new AnvilGUICloseEvent(p);	        
	        Bukkit.getServer().getPluginManager().callEvent(AnvilGUICloseEvent);
	    })
	    .onComplete((player, output) -> {           //called when the inventory output slot is clicked	          
	    	AnvilGUICompleteEvent AnvilGUICompleteEvent = new AnvilGUICompleteEvent(p, output);	        
	    	Bukkit.getServer().getPluginManager().callEvent(AnvilGUICompleteEvent);
	     return AnvilGUI.Response.close();
	    })
	    .text(name) 
	    .itemLeft(itmLeft)
	    .itemRight(itmRight)	    
	    .plugin(Leaf.getInstance())  
	    .open(p);                         
		}
    }

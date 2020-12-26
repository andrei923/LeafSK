package com.leaf.effects;

import com.leaf.Leaf;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffUnformatGUI extends Effect{
	static {
		Skript.registerEffect(EffUnformatGUI.class, 
				"(unformat|remove|clear|reset) [the] gui slot %numbers% of %players%",
				"(unformat|remove|clear|reset) [all] [the] gui slots of %players%");
	}
	
	private Expression<Player> p;
	private Expression<Number> s;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg1 == 1)
			p = (Expression<Player>) arg[0];
		else{
			p = (Expression<Player>) arg[1];
			s = (Expression<Number>) arg[0];
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	protected void execute(Event e) {
		if (p.getArray(e) != null)
			for (Player player : p.getArray(e))
					if (player != null)
						if (s != null){
							for (Number slot : s.getArray(e))
								if (Leaf.getGUIManager().isGUI(player.getOpenInventory().getTopInventory(), slot.intValue()))
									Leaf.getGUIManager().remove(player.getOpenInventory().getTopInventory(), slot.intValue());
						} else if (Leaf.getGUIManager().hasGUI(player.getOpenInventory().getTopInventory()))
								Leaf.getGUIManager().removeAll(player.getOpenInventory().getTopInventory());
						
		
	}

}

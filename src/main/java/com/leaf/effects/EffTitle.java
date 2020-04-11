package com.leaf.effects;

import com.leaf.Leaf;
import com.leaf.util.Registry;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffTitle extends Effect {
	
	static {
		Registry.newEffect(EffTitle.class, "[leaf] send title %string% with subtitle %string% to %players% with %integer% fadein and %integer% fadeout for %integer%");
	}
	
	private Expression<String> title;
	private Expression<String> subtitle;
	private Expression<Player> player;
	private Expression<Integer> fadein;	
	private Expression<Integer> fadeout;
	private Expression<Integer> time;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		title = (Expression<String>) arg0[0];
		subtitle = (Expression<String>) arg0[1];
		player = (Expression<Player>) arg0[2];
		fadein = (Expression<Integer>) arg0[3];
		fadeout = (Expression<Integer>) arg0[4];
		time = (Expression<Integer>) arg0[5];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "send title to player";
	}

	@Override
	protected void execute(Event arg0) {
		// TODO Auto-generated method stub
        try {
        	for(Player p : player.getAll(arg0)){
        		Leaf.getInstance().getTitle().sendTitle(p, title.getSingle(arg0), subtitle.getSingle(arg0), fadein.getSingle(arg0), fadeout.getSingle(arg0), time.getSingle(arg0));
        	}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
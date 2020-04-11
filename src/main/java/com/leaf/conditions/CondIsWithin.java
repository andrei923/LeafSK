package com.leaf.conditions;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.leaf.util.CuboidRegion;
import com.leaf.util.Registry;
import org.bukkit.Location;
import org.bukkit.event.Event;

public class CondIsWithin extends Condition {

    private Expression<Location> loc, pos1, pos2;
    
	static {
		Registry.newCondition(CondIsWithin.class, "%location% is within %location% to %location%", "%location% is not within %location% to %location%");
	}
	
    @Override
    public boolean check(Event event) {
        Location p1 = pos1.getSingle(event);
        Location p2 = pos2.getSingle(event);
        Location l = loc.getSingle(event);
        if(loc == null || p1 == null || p2 == null) return isNegated();
        return isNegated() ? !new CuboidRegion(p1, p2).checkHas(l.toVector()) : new CuboidRegion(p1, p2).checkHas(l.toVector());
    }

    @Override
    public String toString(Event event, boolean b) {
        return "is within";
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        loc = (Expression<Location>) expressions[0];
        pos1 = (Expression<Location>) expressions[1];
        pos2 = (Expression<Location>) expressions[2];
        setNegated(i == 1);
        return true;
    }
}

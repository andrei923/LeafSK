package com.leaf.papi;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.leaf.papi.PlaceholderAPIEvent;
import org.bukkit.event.Event;


public class ExprPapiPrefix extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprPapiPrefix.class, String.class, ExpressionType.SIMPLE, "[the] [(placeholder[api]|papi)] (prefix|placeholder)");
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (!ScriptLoader.isCurrentEvent(PlaceholderAPIEvent.class)) {
			Skript.error("The PlaceholderAPI prefix can only be used in a PlaceholderAPI request event", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		return true;
	}

	@Override
	protected String[] get(final Event e) {
		return new String[]{((PlaceholderAPIEvent) e).getPrefix()};
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "the placeholderapi prefix";
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}

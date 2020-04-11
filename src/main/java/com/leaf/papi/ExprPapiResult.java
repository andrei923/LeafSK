package com.leaf.papi;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.leaf.papi.PlaceholderAPIEvent;
import org.bukkit.event.Event;


public class ExprPapiResult extends SimpleExpression<String> {

	static {
			Skript.registerExpression(ExprPapiResult.class, String.class, ExpressionType.SIMPLE, "[the] [(placeholder[api]|papi)] result");
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (!ScriptLoader.isCurrentEvent(PlaceholderAPIEvent.class)) {
			Skript.error("The PlaceholderAPI result can only be used in a PlaceholderAPI request event", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		return true;
	}

	@Override
	protected String[] get(final Event e) {
		return new String[]{((PlaceholderAPIEvent) e).getResult()};
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "the placeholderapi result";
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, ChangeMode mode) {
		switch (mode) {
			case SET:
				((PlaceholderAPIEvent) e).setResult((String) delta[0]);
				break;
			case RESET:
			case DELETE:
				((PlaceholderAPIEvent) e).setResult(null);
				break;
		default:
			break;
		}
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}

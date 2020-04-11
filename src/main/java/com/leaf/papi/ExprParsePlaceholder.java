package com.leaf.papi;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import java.util.ArrayList;
import java.util.List;

public class ExprParsePlaceholder extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprParsePlaceholder.class, String.class, ExpressionType.SIMPLE,
				"[the] ([value of] placeholder[s]|placeholder [value] [of]) %strings% [from %players%]",
				"parse placeholder[s] %strings% [(for|as) %players%]");
	}

	private Expression<String> placeholders;
	private Expression<Player> players;

	private String formatPlaceholder(String placeholder) {
		if (placeholder == null) {
			return null;
		}
		if (placeholder.charAt(0) == '%') {
			placeholder = placeholder.substring(1);
		}
		if (placeholder.charAt(placeholder.length() - 1) == '%') {
			placeholder = placeholder.substring(0, placeholder.length() - 1);
		}
		return "%" + placeholder + "%";
	}

	private String getPlaceholder(String placeholder, Player player) {
		String value;
		placeholder = formatPlaceholder(placeholder);
		if (PlaceholderAPI.containsPlaceholders(placeholder)) {
			value = PlaceholderAPI.setPlaceholders(player, placeholder);
			if (value.equals(placeholder) || "".equals(value))
				return null;
			return value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		placeholders = (Expression<String>) exprs[0];
		players = (Expression<Player>) exprs[1];
		return true;
	}

	@Override
	protected String[] get(final Event e) {
		String[] placeholders = this.placeholders.getArray(e);
		Player[] players = this.players.getArray(e);
		List<String> values = new ArrayList<>();
		if (players.length != 0) {
			for (String ph : placeholders) {
				for (Player p : players) {
					values.add(getPlaceholder(ph, p));
				}
			}
		} else {
			for (String ph : placeholders) {
				values.add(getPlaceholder(ph, null));
			}
		}
		return values.toArray(new String[0]);
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "the value of placeholder " + placeholders.toString(e, debug) + " from " + players.toString(e, debug);
	}

	@Override
	public boolean isSingle() {
		return placeholders.isSingle() && players.isSingle();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}

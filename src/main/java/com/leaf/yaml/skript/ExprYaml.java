package com.leaf.yaml.skript;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import com.leaf.Leaf;
import com.leaf.yaml.SimpleExpressionFork;
import com.leaf.yaml.utils.StringUtil;
import com.leaf.yaml.utils.yaml.YAMLNode;
import com.leaf.yaml.utils.yaml.YAMLProcessor;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("YAML")
@Description("Gets, sets, removes values/nodes etc.. of a cached yaml file" +
		"\n  - Requires the id used/created from the load effect" +
		"\n  - This expression does not save to file" +
		"\n  - Lists accept list variables for input" +
		"\n  - Using 'without string checks' optional is a tiny bit faster but doesn't check/convert strings for numbers or booleans")
@Examples({
		"set yaml value \"test1.test2\" from \"config\" to \"test3\"",
		"set yaml list \"list.name\" from \"config\" to {_list::*}",
		" ",
		"set {_test} to yaml value \"test1.test2\" from \"config\"",
		"broadcast \"%{_test}%\""
})
@Since("1.0.0")
public class ExprYaml<T> extends SimpleExpressionFork<T> {

	static {
		Skript.registerExpression(ExprYaml.class, Object.class, ExpressionType.SIMPLE,
				"[[skript-]y[a]ml] (1¦value|2¦(node|path) list|3¦(node|path)[s with] key[s]|4¦list) %string% (of|in|from) %string% [without string checks]");
	}

	private boolean checks = false;
	private Expression<String> node, file;

	public static enum YamlState {
		VALUE, NODES, NODE_KEYS, LIST
	}

	YAMLProcessor config;
	
	private YamlState state;

	private final ExprYaml<?> source;
	private final Class<T> superType;

	@SuppressWarnings("unchecked")
	public ExprYaml() {
		this(null, (Class<? extends T>) Object.class);
	}

	@SuppressWarnings("unchecked")
	private ExprYaml(ExprYaml<?> source, Class<? extends T>... types) {
		this.source = source;
		if (source != null) {
			this.node = source.node;
			this.file = source.file;
			this.state = source.state;
			this.checks = source.checks;
		}
		this.superType = (Class<T>) Utils.getSuperType(types);
	}
	
	@Override
	public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
		return new ExprYaml<>(this, to);
	}

	@Override
	public Expression<?> getSource() {
		return source == null ? this : source;
	}
	
	@Override
	public Class<? extends T> getReturnType() {
		return getReturnType(state);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends T> getReturnType(YamlState state) {
		if (state == YamlState.NODES || state == YamlState.NODE_KEYS)
			return (Class<? extends T>) String.class;
		return superType;
	}

	public YamlState getState() {
		return state;
	}

	public String getNode(Event event) {
		return node.getSingle(event);
	}

	public String getId(Event event) {
		return file.getSingle(event);
	}

	@Override
	public boolean isSingle() {
		return state == YamlState.VALUE ? true : false;
	}

	@Override
	public String toString(@Nullable Event event, boolean b) {
		return "yaml " + state.toString().toLowerCase() + " " + this.node.toString(event, b) + " from " + this.file.toString(event, b) + (!checks ? "" : " without string checks");
	}

	@Override
	@Nullable
	protected T[] get(Event event) {
		return get(event, this.node.getSingle(event), this.state);
	}

	public T[] get(Event event, YamlState state) {
		return get(event, this.node.getSingle(event), state);
	}

	public T[] get(Event event, String path) {
		return get(event, path, this.state);
	}

	@SuppressWarnings("unchecked")
	public T[] get(Event event, String path, YamlState state) {
		final String name = this.file.getSingle(event);
		//final String path = this.node.getSingle(event);
		if (!Leaf.YAML_STORE.containsKey(name)) {
			Leaf.warn("No yaml by the name '" + name + "' has been loaded" + "->" + event.getEventName() + " Value " + path);
			return null;
		}

		YAMLProcessor config = Leaf.YAML_STORE.get(name);

		if (state == YamlState.VALUE) {
			Object o = config.getProperty(path);
			if (o != null) {
				if (!checks && String.class.isAssignableFrom(o.getClass()))
					o = ChatColor.translateAlternateColorCodes('&', ((String) o));
				try {
					return convertToArray(o, (Class<T>) o.getClass());
				} catch (ClassCastException e) {
					return (T[]) Array.newInstance(o.getClass(), 0);
				}
			}
			return null;
		} else if (state == YamlState.NODES) {
			if (path.equals("")) {
				Set<String> rootNodes = config.getMap().keySet();
				return lazyConvert(rootNodes.toArray(new String[rootNodes.size()]));
			}
			YAMLNode node = config.getNode(path);
			if (node == null)
				return null;
			Map<String, Object> nodes = node.getMap();
			List<String> keys = new ArrayList<String>();
			for (String key : nodes.keySet()) {
				keys.add(path + "." + key);
			}
			return lazyConvert(keys.toArray(new String[keys.size()]));
		} else if (state == YamlState.NODE_KEYS) {
			List<String> nodesKeys = config.getKeys(path);
			if (nodesKeys == null)
				return null;
			return lazyConvert(nodesKeys.toArray(new String[nodesKeys.size()]));
		} else if (state == YamlState.LIST) {
			List<Object> items = config.getList(path);
			if (items == null)
				return null;
			try {
				return convertArray(items.toArray(), superType);
			} catch (ClassCastException e) {
				return (T[]) Array.newInstance(superType, 0);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T[] lazyConvert(Object[] original) {
		try {
			return convertArray(original, (Class<T>) String.class);
		} catch (ClassCastException e) {
			return (T[]) Array.newInstance(String.class, 0);
		}
	}

	@SuppressWarnings("unchecked")
	public final static <T> T[] convertToArray(Object original, Class<T> to) throws ClassCastException {
		T[] end = (T[]) Array.newInstance(to, 1);
		T converted = Converters.convert(original, to);
		if (converted != null) {
			end[0] = converted;
		} else {
			throw new ClassCastException();
		}
		return end;
	}

	//This method is found at ch.njol.util.coll.CollectionUtils but is here for backwards compatibility with older Skript versions
	@SuppressWarnings("unchecked")
	public final static <T> T[] convertArray(Object[] original, Class<T> to) throws ClassCastException {
		T[] end = (T[]) Array.newInstance(to, original.length);
		for (int i = 0; i < original.length; i++) {
			T converted = Converters.convert(original[i], to);
			if (converted != null) {
				end[i] = converted;
			} else {
				throw new ClassCastException();
			}
		}
		return end;
	}

	@Override
	public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
		final String name = this.file.getSingle(event);
		final String path = this.node.getSingle(event);

		if (!Leaf.YAML_STORE.containsKey(name)) {
			Leaf.warn("No yaml by the name '" + name + "' has been loaded" + "->" + event.getEventName() + " Value " + path);
			return;
		}

		YAMLProcessor config = Leaf.YAML_STORE.get(name);

		if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			config.removeProperty(path);
			return;
		}

		if (state == YamlState.VALUE) {
			if (mode == ChangeMode.SET)
				config.setProperty(path, parseString(delta[0]));
		} else if (state == YamlState.NODE_KEYS) {
			if (mode == ChangeMode.ADD)
				config.setProperty(path + (delta[0] == null ? "" : "." + delta[0]), null);
				//config.addNode(path + (delta[0] == null ? "" : "." + delta[0]));
			else if (mode == ChangeMode.REMOVE)
				config.removeProperty(path + (delta[0] == null ? "" : "." + delta[0]));
				//config.setProperty(path + (delta[0] == null ? "" : "." + delta[0]), null);
		} else if (state == YamlState.LIST) {
			List<Object> objects = config.getList(path);
			if (mode == ChangeMode.ADD) {
				if (objects == null)
					config.setProperty(path, arrayToList(new LinkedList<Object>(), delta));
				else 
					config.setProperty(path, arrayToList(objects, delta));
			} else if (mode == ChangeMode.REMOVE) {
				for (Object o : delta)
					objects.remove(parseString(o));
			} else if (mode == ChangeMode.SET) {
				if (objects == null) {
					config.setProperty(path, arrayToList(new LinkedList<Object>(), delta));
				} else {
					objects.clear();
					config.setProperty(path, arrayToList(objects, delta));
				}
			}
		}
	}

	private List<Object> arrayToList(List<Object> list, Object[] array) {
		for (Object o : array)
			list.add(parseString(o));
		return list;
	}

	private Object parseString(Object delta) {
		if (!checks && String.class.isAssignableFrom(delta.getClass())) {
			String s = StringUtil.translateColorCodes(((String) delta));
			if (s.matches("true|false|yes|no|on|off")) {
				return s.matches("true|yes|on");
			} else if (s.matches("(-)?\\d+")) {
				try {
					return Long.parseLong(s);
				} catch (NumberFormatException ex) {
//TODO force people to use 'without string checks' syntax or add conversion
//					return new BigInteger(s);
				}
				
			} else if (s.matches("(-)?\\d+(\\.\\d+)")) {
				return Double.parseDouble(s);
			} else {
				return s;
			}
		}
		return delta;
	}


	@Override
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
			return CollectionUtils.array(Object.class);
		}
		if (state == YamlState.VALUE) {
			if (mode == Changer.ChangeMode.SET)
				return CollectionUtils.array(Object.class);
		} else if (state == YamlState.NODE_KEYS) {
			if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
				return CollectionUtils.array(Object[].class);
		} else if (state == YamlState.LIST) {
			if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.SET)
				return CollectionUtils.array(Object[].class);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] e, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
		if (parse.mark == 1)
			state = YamlState.VALUE;
		else if (parse.mark == 2)
			state = YamlState.NODES;
		else if (parse.mark == 3)
			state = YamlState.NODE_KEYS;
		else if (parse.mark == 4)
			state = YamlState.LIST;
		node = (Expression<String>) e[0];
		file = (Expression<String>) e[1];
		if (parse.expr.toLowerCase().endsWith(" without string checks"))
			this.checks = true;
		return true;
	}

	@Override
	public boolean isLoopOf(final String s) {
		return state != YamlState.VALUE && (s.equalsIgnoreCase("index") || s.equalsIgnoreCase("value")
				|| s.equalsIgnoreCase("id") || s.equalsIgnoreCase("val") || s.equalsIgnoreCase("list")
				|| s.equalsIgnoreCase("node") || s.equalsIgnoreCase("key") || s.toLowerCase().startsWith("subnodekey"));
	}

	private boolean isInLoop() {
		Node node = SkriptLogger.getNode();
		if (node == null) {
			return false;
		}
		String key = node.getKey();
		//int ln = node.getLine();
		if (key == null) {
			return false;
		}
		return key.startsWith("loop ");
	}
}
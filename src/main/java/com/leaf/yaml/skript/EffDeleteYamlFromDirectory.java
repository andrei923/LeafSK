package com.leaf.yaml.skript;

import java.io.File;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.leaf.Leaf;
import com.leaf.yaml.AsyncEffectOld;
import com.leaf.yaml.utils.SkriptYamlUtils;
import com.leaf.yaml.utils.StringUtil;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Delete all YAML from one or more directories")
@Description("Syntax 1: Deletes all YAML files from one or more directories and removes them from memory."
		+ "\nSyntax 2&3: Only deletes any loaded YAML files from one or more directories and removes them from memory."
		+ "\n  - The input is one or more directories (ie. \"plugins/MyAwesomePlugin/\" and \"plugins/skript-yaml/\").")
@Examples({
	"delete all yaml from directory \"/plugins/skript-yaml/test\"",
	" ",
	"delete all loaded yaml from directory \"/plugins/skript-yaml/test\"",
	" ",
	"delete all loaded yaml from directory \"/plugins/skript-yaml/test\" using the filename as the id"
	})
@Since("1.2.1")
public class EffDeleteYamlFromDirectory extends AsyncEffectOld {

	static {
		Skript.registerEffect(EffDeleteYamlFromDirectory.class, 
				"delete (all|any) [y[a]ml] from [(1¦non[(-| )]relative)] director(y|ies) %strings%",
				"delete (all|any) loaded [y[a]ml] from [(1¦non[(-| )]relative)] director(y|ies) %strings%",
				"delete (all|any) loaded [y[a]ml] from [(1¦non[(-| )]relative)] director(y|ies) %strings% using [the] filename as [the] id");
	}

	private Expression<String> directories;
	private int mark;
	private int matchedPattern;

	@Override
	protected void execute(@Nullable Event event) {
		for (String name : this.directories.getAll(event)) {
			for (File yamlFile : SkriptYamlUtils.directoryFilter(StringUtil.checkSeparator(name), mark == 1, "Delete")) {
				if (matchedPattern == 0) {
					yamlFile.delete();
				} else {
					String n = null;
					if (matchedPattern == 1) {
						n = StringUtil.checkLastSeparator(name) + yamlFile.getName();
					} else if (matchedPattern == 2) {
						n = StringUtil.stripExtention(yamlFile.getName());
					}
					if (Leaf.YAML_STORE.containsKey(n)) {
						Leaf.YAML_STORE.get(n).getFile().delete();
						Leaf.YAML_STORE.remove(n);
					}
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean b) {
		return "delete all" + (matchedPattern == 0 ? " loaded " : " ") + "yaml from" + (mark == 1 ? " non-relative " : " ") + "director" + (directories.isSingle() ? "y " : "ies ") + this.directories.toString(event, b) + (matchedPattern == 2 ? " using the filename as the id" : "");
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
		directories = (Expression<String>) exprs[0];
		this.mark = parse.mark;
		this.matchedPattern = matchedPattern;
		return true;
	}
}

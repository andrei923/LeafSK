package com.leaf.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityExplodeEvent;
import com.leaf.util.Registry;

import java.util.List;


public class ExprExplodedBlocks extends SimpleExpression<Block> {

	static {
		Registry.newSimple(ExprExplodedBlocks.class, "[the] (destroyed|exploded|boom boomed) blocks");
	}
	
    @Override
    protected Block[] get(Event event) {
        List<Block> blockList = ((EntityExplodeEvent) event).blockList();
        return blockList.toArray(new Block[blockList.size()]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "boom boom blocks";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(EntityExplodeEvent.class)) {
            Skript.error("Boom Boomed Blocks can only be used in an explode event.");
            return false;
        }
        return true;
    }
}

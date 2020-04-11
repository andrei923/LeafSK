package com.leaf.util;

import ch.njol.util.NullableChecker;
import ch.njol.util.coll.iterator.CheckedIterator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class CuboidIterator extends CheckedIterator<Block> {
    public CuboidIterator(final Location point1, final Location point2) {
        super(new CuboidRegion(point1, point2).iterator(), new NullableChecker<Block>() {
            @Override
            public boolean check(Block block) {
                return new CuboidRegion(point1, point2).checkHas(block.getLocation().toVector());
            }
        });
    }

    public CuboidIterator(final Chunk chunk) {
        super(new CuboidRegion(chunk.getBlock(0, 0, 0).getLocation(), chunk.getBlock(15, chunk.getWorld().getMaxHeight() - 1, 15).getLocation()).iterator(), new NullableChecker<Block>() {
            @Override
            public boolean check(Block block) {
                return new CuboidRegion(chunk.getBlock(0, 0, 0).getLocation(), chunk.getBlock(15, chunk.getWorld().getMaxHeight() - 1, 15).getLocation()).checkHas(block.getLocation().toVector());
            }
        });
    }
}

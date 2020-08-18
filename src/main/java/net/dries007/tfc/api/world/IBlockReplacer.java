/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Interface for block replacements.
 *
 * @see ITFCChunkGenerator for usage of how to get a block replacer and register instances
 */
@FunctionalInterface
public interface IBlockReplacer
{
    BlockState getReplacement(RockData rockData, int x, int y, int z, float rainfall, float temperature, float noise);

    /**
     * Override to do additional things on post placement, such as schedule ticks
     */
    default void updatePostPlacement(IWorld world, BlockPos pos, BlockState state) {}
}
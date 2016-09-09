package reoseah.rgen.util;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

/**
 * Interface similar to IWorldGenerator provided by Forge, but this is ran
 * before biomes block replacements. Also it receives ChunkPrimer, which is more
 * effective for massive block placement, than using world instance.
 * 
 * 
 * World should not be used to obtain block information neither in this chunk
 * nor in neighbors.
 * 
 * 
 * Register generators in PrimeGenRegistry.register(...).
 * 
 * @author Reoseah
 *
 */
public interface IPrimeWorldGenerator {
	/**
	 * 
	 * @param primer
	 *            to be used to place blocks in the chunk
	 * @param world
	 *            world in which generation occurs. Do not place blocks through
	 *            it
	 * @param random
	 *            to can be reused by something like:
	 * 
	 *            <pre>
	 *            random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L + uniqueNumber);
	 *            </pre>
	 * 
	 * @param chunkX
	 * @param chunkZ
	 * @param generator
	 */
	void generate(ChunkPrimer primer, World world, Random random, int chunkX, int chunkZ, IChunkGenerator generator);
}

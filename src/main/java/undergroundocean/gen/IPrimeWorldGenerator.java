package undergroundocean.gen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

public interface IPrimeWorldGenerator {
  void generate(Random random, int chunkX, int chunkZ, World world, ChunkPrimer primer, IChunkGenerator chunkProvider);
}

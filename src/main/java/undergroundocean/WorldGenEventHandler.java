package undergroundocean;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent.ReplaceBiomeBlocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import undergroundocean.gen.IPrimeWorldGenerator;
import undergroundocean.gen.WorldPrimeGenUndergroundOcean;

public class WorldGenEventHandler {
  public static final IPrimeWorldGenerator generator = new WorldPrimeGenUndergroundOcean();
  
  @SubscribeEvent
  public void on(ReplaceBiomeBlocks event) {
    World world = event.getWorld();
    
    generator.generate(new Random(world.getSeed()), event.getX(), event.getZ(), world, event.getPrimer(),
        event.getGenerator());
  }
}

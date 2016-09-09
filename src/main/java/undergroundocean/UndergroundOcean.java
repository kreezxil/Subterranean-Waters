package undergroundocean;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reoseah.rgen.util.PrimeGenRegistry;
import undergroundocean.config.UCConfig;
import undergroundocean.gen.WorldPrimeGenUndergroundOcean;

@Mod(modid = "UndergroundOcean", name = "UndergroundOcean", version = "${version}")
public class UndergroundOcean {
  @Mod.Instance
  public static UndergroundOcean instance;
  
  public static int lowOceanBound = 6, highOceanBound = 2, size = -8, waterLevel = 25;
  public static String fluid = "minecraft:water";
  
  public static int[] dimensionIds = { 0 };
  
  @Mod.EventHandler
  public void on(FMLPreInitializationEvent event) {
    Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    
    UCConfig.load(config, event.getModConfigurationDirectory());
        
    PrimeGenRegistry.register(new WorldPrimeGenUndergroundOcean(), 15);
  }
}

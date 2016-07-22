package undergroundocean;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "UndergroundOcean", name = "UndergroundOcean", version = "${version}")
public class UndergroundOcean {
  @Mod.Instance
  public static UndergroundOcean instance;
  
  public static int lowOceanBound = 4, highOceanBound = 6, size = -8, waterLevel = 25, fluidType = 0;
  public static int[] dimensionIds = { 0 };
  
  @Mod.EventHandler
  public void on(FMLPreInitializationEvent event) {
    Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    
    config.load();
    
    dimensionIds = config.get("Generation", "Dimensions", dimensionIds, "Dimensions to generate oceans in.").getIntList();    
    lowOceanBound = config.getInt("Low Height Bound", "Generation", lowOceanBound, 2, 30,
        "Low bound where generation begins to halt. (For real world height multiply it by 4.)");
    highOceanBound = config.getInt("Low Height Bound", "Generation", highOceanBound, 3, 31,
        "High bound where generation begins to halt. (For real world height multiply it by 4.)");
    size = config.getInt("Size", "Generation", size, -60, 60,
        "Size of oceans, really - value added to it. Negative values may result in small but more interesting caves. Positive values will result in turning large parts of space between bound into ocean with round floor and ceiling 'overwhelming' any noise.");
    waterLevel = config.getInt("Water Level", "Generation", waterLevel, 1, 128,
        "Blocks under this height will be replaced with air. Small value can make just large empty caverns instead of ocean. Value is actual y-coordinate in world. ");
    fluidType = config.getInt("Fluid Type", "Generation", fluidType, 0, 1,
        "Liquid used to fill ocean. 1 is water. 2 is lava.");
    
    config.save();
        
    MinecraftForge.EVENT_BUS.register(new WorldGenEventHandler());
  }
}

package reoseah.mods.undergroundocean;

import java.util.Random;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent.ReplaceBiomeBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "underground_ocean", name = "UndergroundOcean", version = "${version}")
public class UndergroundOcean {
	public static final ModGenerator generator = new ModGenerator();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		ModConfig.load(config, event.getModConfigurationDirectory());

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void on(ReplaceBiomeBlocks event) {
		generator.generate(event.getPrimer(), event.getWorld(), new Random(event.getWorld().getSeed()), event.getX(),
				event.getZ(), event.getGenerator());
	}

	
}

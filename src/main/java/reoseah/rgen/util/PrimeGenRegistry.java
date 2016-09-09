package reoseah.rgen.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent.ReplaceBiomeBlocks;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PrimeGenRegistry {
	static final List<Pair<IPrimeWorldGenerator, Integer>> generators = new ArrayList<>();
	static boolean sorted = false;

	public static void register(IPrimeWorldGenerator generator, int weight) {
		generators.add(Pair.of(generator, weight));
		sorted = false;
	}

	/**
	 * Underlying generator-weight pairs list. Changes to list will change
	 * registry.
	 * 
	 * @return
	 */
	public static List<Pair<IPrimeWorldGenerator, Integer>> generators() {
		sorted = false;
		return generators;
	}

	static {
		MinecraftForge.EVENT_BUS.register(PrimeGenRunner.INSTANCE);
	}

	/**
	 * Internal class used by RGen lib. Do not use outside.
	 * 
	 * @author Reoseah
	 *
	 */
	enum PrimeGenRunner {
		INSTANCE;

		@SubscribeEvent
		public void sortAndCallGenerators(ReplaceBiomeBlocks event) {
			if (!sorted) {
				sortGeneratorsByWeight();
			}

			callGenerators(event, event.getWorld());
		}

		private static void sortGeneratorsByWeight() {
			generators.sort((a, b) -> {
				return Integer.compare(a.getRight(), b.getRight());
			});
			sorted = true;
		}

		private static void callGenerators(ReplaceBiomeBlocks event, World world) {
			// FMLLog.info("Called prime egenerators by %s", event);
			PrimeGenRegistry.generators.forEach(entry -> {
				entry.getLeft().generate(event.getPrimer(), world, world.rand, event.getX(), event.getZ(),
						event.getGenerator());
			});
		}
	}
}

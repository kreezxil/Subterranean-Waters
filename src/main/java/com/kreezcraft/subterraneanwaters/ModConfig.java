package com.kreezcraft.subterraneanwaters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;

public class ModConfig {
	public static int[] dimensions;
	public static OceanConfig[] configs;
	public static Map<Integer, OceanConfig> map = new HashMap<>();

	public static void load(Configuration config, File configDir) {
		config.load();

		dimensions = config
				.get("Generation", "Dimension IDs", new int[] { 0 },
						"Dimensions to generate in. Configuration file will be created for each dimension on startup. ")
				.getIntList();

		configs = new OceanConfig[dimensions.length];

		for (int i = 0; i < dimensions.length; i++) {
			try {
				int dimID = dimensions[i];
				DimensionType dimension = DimensionType.getById(dimID);
				String name = dimension.getName();

				File dimFile = new File(configDir, "UndergroundOcean/" + name + ".cfg");
				Configuration dimConfig = new Configuration(dimFile);

				configs[i] = new OceanConfig();
				configs[i].load(dimConfig);

				map.put(dimID, configs[i]);
			} catch (Exception e) {
				FMLLog.log(Level.ERROR, e, "Failed to load dimension ocean config!", (Object[]) null);
			}
		}

		config.save();
	}

	public static class OceanConfig {
		public int lowLimit, lowOverheadLimit;
		public int highLimit, highOverheadLimit;
		public int noiseSummand;
		public int liquidLevel;

		public void load(Configuration config) {
			config.load();

			config.addCustomCategoryComment("GenerationVariables",
					"Height changes with step of 4 during noise generation. So 32 here ~ 128 in world.");

			lowLimit = config.getInt("LowLimit", "GenerationVariables", 6, 1, 33,
					"Height at which noise value begins to halt.");
			lowOverheadLimit = config.getInt("LowOverheadLimit", "GenerationVariables", 3, 1, 32,
					"Height at which noise is guaranteed to halt.");

			highLimit = config.getInt("HighLimit", "GenerationVariables", 16, 0, 32,
					"Height at which noise value begins to halt.");
			highOverheadLimit = config.getInt("HighOverheadLimit", "GenerationVariables", 30, 1, 32,
					"Height at which noise is guaranteed to halt.");

			noiseSummand = config.getInt("NoiseSummand", "GenerationVariables", 16, -100, 100,
					"Value simply added to noise before height controling, allows to control size of caverns.");

			liquidLevel = config.getInt("LiquidLevel", "GenerationVariables", 45, 1, 128,
					"Water level. Actual Y-coordinate in world.");

			config.save();
		}
	}
}

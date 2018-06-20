package com.kreezcraft.subterraneanwaters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = SubH2o.MODID, category = "")
public class SubH2o_Config {

	@Config.Comment({ "Biome Exclusions" })
	@Config.Name("General Settings")
	public static General general = new General();

	public static class General {
		@Config.Comment("Biomes to not generate in, use title case. Example: Deep Ocean")
		@Config.Name("Exclusions")
		public static String[] exclude = new String[] { "Deep Ocean", "Ocean" };

		@Config.Comment({
				"Dimensions to generate in. Configuration file will be created for each dimension on startup. " })
		@Config.Name("Dimensions IDs")
		public static int[] dimensions = new int[] { 0 };

		@Config.Comment({ "Configs by the dimension" })
		@Config.Name("Dimensional Configs")
		public static OceanConfig[] configs;

		// public static Map<Integer, OceanConfig> map = new HashMap<>();

		configs=new OceanConfig[dimensions.length];

		for(
		int i = 0;i<dimensions.length;i++)
		{
			try {
				int dimID = dimensions[i];
				DimensionType dimension = DimensionType.getById(dimID);
				String name = dimension.getName();

				// File dimFile = new File(SubH2o.configDir, "SubterraneanWaters/" + name +
				// ".cfg");
				// Configuration dimConfig = new Configuration(dimFile);

				configs[i] = new OceanConfig(name);
				// configs[i]load(dimConfig);

				// map.put(dimID, configs[i]);
			} catch (Exception e) {
				FMLLog.log(Level.ERROR, e, "Failed to load dimension ocean config!", (Object[]) null);
			}
		}

		@Config.Comment({ "Height changes with step of 4 during noise generation. So 32 here ~ 128 in world." })
		@Config.Name("Generation Variables")
		public static OceanConfig oceanConfig = new OceanConfig(null);

		public static class OceanConfig {

			public OceanConfig(String name2) {
				this.name = name2;
			}

			@Config.Comment({ "Dimension with an ocean" })
			@Config.Name("Ocean-ish Dimension?")
			public String name = "default";

			@Config.Comment({ "Height at which noise value begins to halt." })
			@Config.Name("Low Limit")
			@Config.RangeInt(min = 1, max = 33)
			public int lowLimit = 6;

			@Config.Comment({ "Height at which noise is guaranteed to halt." })
			@Config.Name("Low Overhead Limit")
			@Config.RangeInt(min = 1, max = 32)
			public int lowOverheadLimit = 3;

			@Config.Comment({ "Height at which noise value begins to halt." })
			@Config.Name("High Limit")
			@Config.RangeInt(min = 0, max = 32)
			public int highLimit = 9;

			@Config.Comment({ "Height at which noise is guaranteed to halt." })
			@Config.Name("High Overhead Limit")
			@Config.RangeInt(min = 1, max = 32)
			public int highOverheadLimit = 12;

			@Config.Comment({
					"Value simply added to noise before height controling, allows to control size of caverns. Any value above -3 yield enormous caverns." })
			@Config.Name("Noise Summand")
			@Config.RangeInt(min = -100, max = 100)
			public int noiseSummand = -8;

			@Config.Comment({ "Water level. Actual Y-coordinate in world." })
			@Config.Name("Liquid Level")
			@Config.RangeInt(min = 1, max = 128)
			public int liquidLevel = 45;
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(SubH2o.MODID)) {
			ConfigManager.sync(SubH2o.MODID, Config.Type.INSTANCE);
		}
	}

}}

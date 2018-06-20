package com.kreezcraft.subterraneanwaters;

import java.io.File;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent.ReplaceBiomeBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SubH2o.MODID, name = SubH2o.NAME, version = SubH2o.VERSION, acceptableRemoteVersions = "*")
public class SubH2o {
	public static final String NAME = "Subterranean Waters";
	public static final String MODID = "subterraneanwaters";
	public static final String VERSION = "@VERSION@";
	
	public static final SubH2o_Generator generator = new SubH2o_Generator();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void on(ReplaceBiomeBlocks event) {
		Chunk theChunk = new Chunk(event.getWorld(), event.getX(), event.getZ());
		BlockPos pos = new BlockPos(event.getX() << 4, 1, event.getZ() << 4);
		Biome theBiome = theChunk.getBiome(pos, event.getWorld().getBiomeProvider());
		//System.out.println("Biome: ["+theBiome.getBiomeName()+"]");
		if (!ArrayUtils.contains(SubH2o_Config.general.exclude, theBiome.getBiomeName()))
			generator.generate(event.getPrimer(), event.getWorld(), new Random(event.getWorld().getSeed()),
					event.getX(), event.getZ(), event.getGenerator());
	}
}

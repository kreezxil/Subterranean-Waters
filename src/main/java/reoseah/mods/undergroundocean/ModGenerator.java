package reoseah.mods.undergroundocean;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import reoseah.mods.undergroundocean.ModConfig.OceanConfig;

public class ModGenerator {
	private static final IBlockState WATER = Blocks.WATER.getDefaultState();
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();

	private boolean initialized = false;

	private Random random;
	private NoiseGeneratorOctaves noiseGenStone1;
	private NoiseGeneratorOctaves noiseGenStone2;
	private NoiseGeneratorOctaves noiseGenStoneMask;
	private NoiseGeneratorOctaves noiseGenStoneMask2;

	private double[] tempArrayStone1;
	private double[] tempArrayStone2;
	private double[] tempArrayStoneMask;
	private double[] tempArrayStoneMask2;
	private double[] tempArrayStone;

	static OceanConfig config;

	public void generate(ChunkPrimer primer, World world, Random random, int chunkX, int chunkZ,
			IChunkGenerator generator) {
		if (ArrayUtils.contains(ModConfig.dimensions, world.provider.getDimension())) {

			config = ModConfig.map.get(world.provider.getDimension());

			if (!initialized) {
				this.random = new Random(world.getSeed());

				this.noiseGenStone1 = new NoiseGeneratorOctaves(this.random, 16);
				this.noiseGenStone2 = new NoiseGeneratorOctaves(this.random, 16);
				this.noiseGenStoneMask = new NoiseGeneratorOctaves(this.random, 8);
				this.noiseGenStoneMask2 = new NoiseGeneratorOctaves(this.random, 8);

				tempArrayStone = new double[3 * 33 * 3];

				initialized = true;
			}

			doGenerate(chunkX, chunkZ, primer);
		}
	}

	private void initNoise(int shiftX, int shiftY, int shiftZ, int sizeX, int sizeY, int sizeZ) {
		double noiseScaleHorisontal = 684.412D * 2.0D;
		double noiseScaleVertical = 684.412D;

		tempArrayStoneMask = noiseGenStoneMask.generateNoiseOctaves(tempArrayStoneMask, shiftX, shiftY, shiftZ, sizeX,
				sizeY, sizeZ, noiseScaleHorisontal / 80.0D, noiseScaleVertical / 160.0D, noiseScaleHorisontal / 80.0D);
		tempArrayStoneMask2 = noiseGenStoneMask2.generateNoiseOctaves(tempArrayStoneMask2, shiftX, shiftY, shiftZ,
				sizeX, sizeY, sizeZ, noiseScaleHorisontal / 80.0D, noiseScaleVertical / 160.0D,
				noiseScaleHorisontal / 80.0D);
		tempArrayStone1 = noiseGenStone1.generateNoiseOctaves(tempArrayStone1, shiftX, shiftY, shiftZ, sizeX, sizeY,
				sizeZ, noiseScaleHorisontal / 2, noiseScaleVertical / 2, noiseScaleHorisontal / 2);
		tempArrayStone2 = noiseGenStone2.generateNoiseOctaves(tempArrayStone2, shiftX, shiftY, shiftZ, sizeX, sizeY,
				sizeZ, noiseScaleHorisontal / 2, noiseScaleVertical / 2, noiseScaleHorisontal / 2);

		int k = 0;

		for (int posX = 0; posX < sizeX; ++posX) {
			for (int posZ = 0; posZ < sizeZ; ++posZ) {
				for (int posY = 0; posY < sizeY; ++posY) {

					double noiseValue1 = tempArrayStone1[k] / 512.0D;
					double noiseValue2 = tempArrayStone2[k] / 512.0D;
					double noiseValueMask = (tempArrayStoneMask[k] / 10.0D + 1.0D) / 2.0D;
					double noiseValueMask2 = (tempArrayStoneMask2[k] / 10.0D + 1.0D) / 2.0D;

					tempArrayStone[k] = calculateNoiseValue(posY, noiseValue1, noiseValue2, noiseValueMask)
							- 3 * MathHelper.sin(posY / 1.5F);

					tempArrayStone[k] = interpolateLinear(tempArrayStone[k], -3, noiseValueMask2);

					++k;
				}
			}
		}
	}

	private static double calculateNoiseValue(int posY, double noiseValue1, double noiseValue2, double noiseValueMask) {
		double noiseValue = interpolateLinear(noiseValue1, noiseValue2, noiseValueMask) + config.noiseSummand;

		if (posY > config.highLimit) {
			double heightOverhead = (posY - (double) (config.highLimit)) / config.highOverheadLimit;
			noiseValue = noiseValue * (1.0D - heightOverhead) + -100.0D * heightOverhead;
		}
		if (posY < config.lowLimit) {
			double heightOverhead = ((double) config.lowLimit - posY) / config.lowOverheadLimit;
			noiseValue = noiseValue * (1.0D - heightOverhead) + -30.0D * heightOverhead;
		}
		return noiseValue;
	}

	private static double interpolateLinear(double value1, double value2, double k) {
		return value1 + (value2 - value1) * MathHelper.clamp(k, 0.0, 1.0);
	}

	private void doGenerate(int x, int z, ChunkPrimer primer) {
		initNoise(x * 2, 0, z * 2, 3, 33, 3);

		for (int sampleX = 0; sampleX < 2; ++sampleX) {
			for (int sampleZ = 0; sampleZ < 2; ++sampleZ) {
				for (int sampleY = 0; sampleY < 33 - 18; ++sampleY) {
					double bottomNearLeft = tempArrayStone[((sampleX + 0) * 3 + sampleZ + 0) * 33 + sampleY + 0];
					double bottomNearRight = tempArrayStone[((sampleX + 0) * 3 + sampleZ + 1) * 33 + sampleY + 0];
					double bottomFarLeft = tempArrayStone[((sampleX + 1) * 3 + sampleZ + 0) * 33 + sampleY + 0];
					double bottomFarRight = tempArrayStone[((sampleX + 1) * 3 + sampleZ + 1) * 33 + sampleY + 0];

					double topNearLeft = tempArrayStone[((sampleX + 0) * 3 + sampleZ + 0) * 33 + sampleY + 1];
					double topNearRight = tempArrayStone[((sampleX + 0) * 3 + sampleZ + 1) * 33 + sampleY + 1];
					double topFarLeft = tempArrayStone[((sampleX + 1) * 3 + sampleZ + 0) * 33 + sampleY + 1];
					double topFarRight = tempArrayStone[((sampleX + 1) * 3 + sampleZ + 1) * 33 + sampleY + 1];

					double dNearLeft = (topNearLeft - bottomNearLeft) / 4;
					double dNearRight = (topNearRight - bottomNearRight) / 4;
					double dFarLeft = (topFarLeft - bottomFarLeft) / 4;
					double dFarRight = (topFarRight - bottomFarRight) / 4;

					for (int shiftY = 0; shiftY < 4; ++shiftY) {
						double currentLeft = bottomNearLeft;
						double currentRight = bottomNearRight;
						double dXLeft = (bottomFarLeft - bottomNearLeft) / 18;
						double dXRight = (bottomFarRight - bottomNearRight) / 18;

						for (int shiftX = 0; shiftX < 18; ++shiftX) {
							double currentValue = currentLeft;
							double dZ = (currentRight - currentLeft) / 18;

							for (int shiftZ = 0; shiftZ < 18; ++shiftZ) {

								if (currentValue > 0D) {
									int worldX = shiftX + sampleX * 18;
									int worldY = shiftY + sampleY * 4;
									int worldZ = shiftZ + sampleZ * 18;

									IBlockState block = worldY > config.liquidLevel ? AIR : WATER;
									primer.setBlockState(worldX, worldY, worldZ, block);
								}

								currentValue += dZ;
							}

							currentLeft += dXLeft;
							currentRight += dXRight;
						}

						bottomNearLeft += dNearLeft;
						bottomNearRight += dNearRight;
						bottomFarLeft += dFarLeft;
						bottomFarRight += dFarRight;
					}
				}
			}
		}
	}
}

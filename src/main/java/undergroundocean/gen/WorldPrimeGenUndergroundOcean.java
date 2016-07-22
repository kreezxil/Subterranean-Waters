package undergroundocean.gen;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import undergroundocean.UndergroundOcean;

public class WorldPrimeGenUndergroundOcean implements IPrimeWorldGenerator {
  private static final int chunkSizeXZ = 16;
  private static final int chunkSizeY = 128;
  
  private static final int samplesXZ = 2;
  private static final int blocksPerSampleXZ = chunkSizeXZ / samplesXZ;
  private static final int samplesXPlusOne = samplesXZ + 1;
  
  // We go from Y=0 upwards interpolating current and next values,
  // so there is 33 instead 32 y-samples to correctly interpolate last one.
  private static final int samplesY = 33;
  private static final int blockPerSampleY = chunkSizeY / (samplesY - 1); // 4
  private static final int samplesZPlusOne = samplesXZ + 1;
  
  private boolean initialized = false;
  
  private Random random;
  private NoiseGeneratorOctaves noiseGenStone1;
  private NoiseGeneratorOctaves noiseGenStone2;
  private NoiseGeneratorOctaves noiseGenStoneMask;
  
  private double[] tempArrayStone1;
  private double[] tempArrayStone2;
  private double[] tempArrayStoneMask;
  private double[] tempArrayStone;
  private Biome[] tempArrayBiomes;
  
  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, ChunkPrimer primer,
      IChunkGenerator chunkProvider) {
    if(!ArrayUtils.contains(UndergroundOcean.dimensionIds, world.provider.getDimension())) return;
    
    initGenerators(world, random);
    
    // generator runs before biomes in world are setted and biomes map is not obtainable in any other way,
    // so we have to generate them if we want to use them
    tempArrayBiomes = world.getBiomeProvider().loadBlockGeneratorData(this.tempArrayBiomes, chunkX * 16, chunkZ * 16,
        16, 16);
        
    setChunk(chunkX, chunkZ, primer);
  }
  
  private void initGenerators(World world, Random random) {
    if (!initialized) {
      this.random = random;
      
      this.noiseGenStone1 = new NoiseGeneratorOctaves(this.random, 16);
      this.noiseGenStone2 = new NoiseGeneratorOctaves(this.random, 16);
      this.noiseGenStoneMask = new NoiseGeneratorOctaves(this.random, 8);
      
      tempArrayStone = new double[samplesXPlusOne * samplesY * samplesZPlusOne];
      
      initialized = true;
    }
  }
  
  private void initNoise(int shiftX, int shiftY, int shiftZ, int sizeX, int sizeY, int sizeZ) {
    double noiseScaleHorisontal = 684.412D * 2.0D;
    double noiseScaleVertical = 684.412D;
    tempArrayStoneMask = noiseGenStoneMask.generateNoiseOctaves(tempArrayStoneMask, shiftX, shiftY, shiftZ, sizeX,
        sizeY, sizeZ, noiseScaleHorisontal / 80.0D, noiseScaleVertical / 160.0D, noiseScaleHorisontal / 80.0D);
    tempArrayStone1 = noiseGenStone1.generateNoiseOctaves(tempArrayStone1, shiftX, shiftY, shiftZ, sizeX, sizeY, sizeZ,
        noiseScaleHorisontal / 2, noiseScaleVertical / 2, noiseScaleHorisontal / 2);
    tempArrayStone2 = noiseGenStone2.generateNoiseOctaves(tempArrayStone2, shiftX, shiftY, shiftZ, sizeX, sizeY, sizeZ,
        noiseScaleHorisontal / 2, noiseScaleVertical / 2, noiseScaleHorisontal / 2);
        
    int k = 0;
    
    for (int posX = 0; posX < sizeX; ++posX) {
      for (int posZ = 0; posZ < sizeZ; ++posZ) {
        for (int posY = 0; posY < sizeY; ++posY) {
          
          double noiseValue1 = tempArrayStone1[k] / 512.0D;
          double noiseValue2 = tempArrayStone2[k] / 512.0D;
          double noiseValueMask = (tempArrayStoneMask[k] / 10.0D + 1.0D) / 2.0D;
          
          tempArrayStone[k] = calculateNoiseValue(posY, noiseValue1, noiseValue2, noiseValueMask);
          
          ++k;
        }
      }
    }
  }
  
  private static double calculateNoiseValue(int posY, double noiseValue1, double noiseValue2, double noiseValueMask) {
    double noiseValue = interpolateLinear(noiseValue1, noiseValue2, noiseValueMask) - 8.0D;
    
    if (posY > UndergroundOcean.highOceanBound) {
      double dY = (posY - UndergroundOcean.highOceanBound);
      noiseValue *= 1.0D - dY / 12D;
      noiseValue -= dY + 5;
    }
    
    if (posY < UndergroundOcean.lowOceanBound) {
      double dY = (UndergroundOcean.lowOceanBound - posY);
      noiseValue *= 1.0D - dY / 4D;
      noiseValue -= dY * 4;
    }
    
    return noiseValue;
  }
  
  private static double interpolateLinear(double value1, double value2, double k) {
    return value1 + (value2 - value1) * MathHelper.clamp_double(k, 0.0, 1.0);
  }
  
  private void setChunk(int x, int z, ChunkPrimer primer) {
    initNoise(x * samplesXZ, 0, z * samplesXZ, samplesXPlusOne, samplesY, samplesZPlusOne);
    
    for (int sampleX = 0; sampleX < samplesXZ; ++sampleX) {
      for (int sampleZ = 0; sampleZ < samplesXZ; ++sampleZ) {
        for (int sampleY = 0; sampleY < samplesY - 18; ++sampleY) {
          double bottomNearLeft = tempArrayStone[((sampleX + 0) * samplesZPlusOne + sampleZ + 0) * samplesY + sampleY
              + 0];
          double bottomNearRight = tempArrayStone[((sampleX + 0) * samplesZPlusOne + sampleZ + 1) * samplesY + sampleY
              + 0];
          double bottomFarLeft = tempArrayStone[((sampleX + 1) * samplesZPlusOne + sampleZ + 0) * samplesY + sampleY
              + 0];
          double bottomFarRight = tempArrayStone[((sampleX + 1) * samplesZPlusOne + sampleZ + 1) * samplesY + sampleY
              + 0];
              
          double topNearLeft = tempArrayStone[((sampleX + 0) * samplesZPlusOne + sampleZ + 0) * samplesY + sampleY + 1];
          double topNearRight = tempArrayStone[((sampleX + 0) * samplesZPlusOne + sampleZ + 1) * samplesY + sampleY
              + 1];
          double topFarLeft = tempArrayStone[((sampleX + 1) * samplesZPlusOne + sampleZ + 0) * samplesY + sampleY + 1];
          double topFarRight = tempArrayStone[((sampleX + 1) * samplesZPlusOne + sampleZ + 1) * samplesY + sampleY + 1];
          
          double dNearLeft = (topNearLeft - bottomNearLeft) / blockPerSampleY;
          double dNearRight = (topNearRight - bottomNearRight) / blockPerSampleY;
          double dFarLeft = (topFarLeft - bottomFarLeft) / blockPerSampleY;
          double dFarRight = (topFarRight - bottomFarRight) / blockPerSampleY;
          
          for (int shiftY = 0; shiftY < blockPerSampleY; ++shiftY) {
            double currentLeft = bottomNearLeft;
            double currentRight = bottomNearRight;
            double dXLeft = (bottomFarLeft - bottomNearLeft) / blocksPerSampleXZ;
            double dXRight = (bottomFarRight - bottomNearRight) / blocksPerSampleXZ;
            
            for (int shiftX = 0; shiftX < blocksPerSampleXZ; ++shiftX) {
              double currentValue = currentLeft;
              double dZ = (currentRight - currentLeft) / blocksPerSampleXZ;
              
              for (int shiftZ = 0; shiftZ < blocksPerSampleXZ; ++shiftZ) {
                
                if (currentValue > 0D) {
                  int worldX = shiftX + sampleX * blocksPerSampleXZ;
                  int worldY = shiftY + sampleY * blockPerSampleY;
                  int worldZ = shiftZ + sampleZ * blocksPerSampleXZ;
                  
                  // Biome biome = tempArrayBiomes[worldX + worldZ * 16];
                  //
                  // if (BiomeDictionary.isBiomeOfType(biome, Type.OCEAN)) {
                  // currentValue = currentValue * 0.8 - Math.max(0, worldY - 35) / 2;
                  // }
                  
                  IBlockState block = worldY > UndergroundOcean.waterLevel ? Blocks.AIR.getDefaultState()
                      : (UndergroundOcean.fluidType == 0 ? Blocks.WATER.getDefaultState()
                          : Blocks.LAVA.getDefaultState());
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

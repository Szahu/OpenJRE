package org.solar.appllication.terrain;

public class ChunkData {
    
    private double[][][] m_grid;
    private float[][] m_heightMap;

    public double[][][] getGrid() {
        return m_grid;
    }

    public float[][] getHeightMap() {
        return m_heightMap;
    }

    public ChunkData(double[][][] grid, float[][] heightMap) {
        m_grid = grid;
        m_heightMap = heightMap;
    }

}

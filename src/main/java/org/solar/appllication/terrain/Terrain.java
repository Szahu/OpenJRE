package org.solar.appllication.terrain;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL;
import org.solar.engine.Utils;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Terrain {

    public static int RENDERING_DSTSNCE = 10;
    private Map<Vector2i, Chunk> chunks = new HashMap<>();
    private Map<Vector2i, Chunk> toCreateVao = new HashMap<>();
    private List<Vector2i> queueKeys = new ArrayList<>();
    private double isolevel = 0.0;
    private Function<Vector3f, ChunkData> generator;
    private OpenSimplexNoise m_noise;
    private OpenSimplexNoise m_noise2;
    private OpenSimplexNoise m_noise3;

    class ChunkLoader extends Thread {

        private Vector4i scope = new Vector4i();
        public ChunkLoader(Vector4i scope) {
            this.scope = scope;
        }

        @Override
        public void run() {
            scopedLoading(scope);
        } 

    }

    private void scopedLoading(Vector4i scope) {
        for (int i = scope.x;i != scope.z;i++) {
            for (int j = scope.y;j != scope.w;j++) {
                addNewChunk(new Vector2i(i,j));
            }
        }
    }
    
    public void syncLoading(Vector4i scope) {
        scopedLoading(scope);
        initAllChunksInQueue();
    }

    public void multiThreadedLoading(Vector4i scope) {
        Thread t1 = new ChunkLoader(scope);

        t1.start();  
    }

    public Terrain() {

        m_noise = new OpenSimplexNoise(123123);
        m_noise2 = new OpenSimplexNoise(456);
        m_noise3 = new OpenSimplexNoise(789);
    	double frequency = 0.0009f;
    	double frequency2 = 0.05f;
    	double frequency3 = 0.003f;

        generator = (offset) -> {
			double[][][] res = new double[Chunk.CHUNK_SIZE+1][Chunk.CHUNK_HEIGHT+1][Chunk.CHUNK_SIZE+1];
            float[][] heightMap = new float[Chunk.CHUNK_SIZE+1][Chunk.CHUNK_SIZE+1];
			for(int x = 0;x <= Chunk.CHUNK_SIZE;x++){
				for(int y = 0;y <= Chunk.CHUNK_HEIGHT;y++){
					for(int z = 0;z <= Chunk.CHUNK_SIZE;z++){
						
                        Vector3f location = new Vector3f((x + 1) * Chunk.CELL_SIZE + offset.z, y * Chunk.CELL_SIZE, (z + 1) * Chunk.CELL_SIZE + offset.x);

                        float density = -y - offset.y;
                        float heightVal = 0;
                        heightVal += Math.clamp((m_noise.noise2(frequency * location.x, frequency *  location.z) + 1) * 6, 0, 1);
                        heightVal += Math.clamp((m_noise2.noise2(frequency2 * location.z, frequency2 *  location.x) + 1) * 0.2, 0, 1);
                        heightVal += Math.clamp((m_noise3.noise2(frequency3 * location.x, frequency3 *  location.z) + 1) * 2, 0, 1);
                        density += heightVal;
                        heightMap[x][z] = heightVal * Chunk.CELL_SIZE;
                        res[x][y][z] = density;
	
					}	
				}
			}
			return new ChunkData(res, heightMap);
		};
    }

    public void generateNewChunk(Vector2i offset) {
        Chunk chunk = new Chunk(offset, generator, isolevel);
        chunk.generate();
        toCreateVao.put(offset, chunk);
        queueKeys.add(offset);
    }

    public void initOneChunkInQueue() {
        if (queueKeys.size() > 0 && toCreateVao.get(queueKeys.get(0)) != null) {
            Vector2i offset = queueKeys.get(0);
            Chunk chunk = toCreateVao.get(offset); 
            chunk.createVertexArray();
            chunks.put(offset, chunk);
            toCreateVao.remove(queueKeys.get(0));
            queueKeys.remove(0);
        }
    }

    public void initAllChunksInQueue() {
        for (Chunk chunk : toCreateVao.values()) {
            chunk.createVertexArray();
            chunks.put(chunk.getOffset(), chunk);
        }
    }

    public Map<Vector2i, Chunk> getChunks() {
        return chunks;
    }

    public void addNewChunk(Vector2i offset) {
        if(!chunks.containsKey(offset) && !toCreateVao.containsKey(offset) && !queueKeys.contains(offset)) {
            generateNewChunk(offset);
        }
    }
    

}
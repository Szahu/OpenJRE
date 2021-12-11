package org.solar.appllication.terrain;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import org.solar.engine.Utils;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Terrain {

    //public VertexArray getVertexArray() {return m_vertexArray;}
    private Map<Vector2i, VertexArray> chunksVertexArrays = new HashMap<>();
    private double isolevel = 0.0;
    private Function<Vector3f, double[][][]> generator;
    private OpenSimplexNoise m_noise;
    private OpenSimplexNoise m_noise2;

    public Terrain() {

        m_noise = new OpenSimplexNoise(123);
        m_noise2 = new OpenSimplexNoise(456);
    	double frequency = 0.01f;
    	double frequency2= 0.4f;

        generator = (offset) -> {
			double[][][] res = new double[Chunk.CHUNK_SIZE+1][Chunk.CHUNK_HEIGHT+1][Chunk.CHUNK_SIZE+1];
			for(int x = 0;x <= Chunk.CHUNK_SIZE;x++){
				for(int y = 0;y <= Chunk.CHUNK_HEIGHT;y++){
					for(int z = 0;z <= Chunk.CHUNK_SIZE;z++){
						
						float density = -y - offset.y;
						Vector2f location = new Vector2f(x + offset.z + 1, z + offset.x + 1);
						density += Math.clamp((m_noise.noise2(frequency * location.x, frequency *  location.y) + 1) * 6, 0, 1);
                        //density += Math.clamp((m_noise2.noise2(frequency2 * location.x, frequency2 *  location.y) + 1) * 0.1, 0, 1);
						res[x][y][z] = density;
	
					}	
				}
			}
			return res;
		};
    }

    public void generateNewChunk(Vector2i offset) {
        Chunk chunk = new Chunk(new Vector3f(offset.x * Chunk.CHUNK_SIZE, 0, offset.y * Chunk.CHUNK_SIZE), generator, isolevel);
        chunksVertexArrays.put(offset, chunk.generate());
    }

    public Map<Vector2i, VertexArray> getVertexArrays() {
        return chunksVertexArrays;
    }

    public void addNewChunk(Vector2i offset) {
        if(!chunksVertexArrays.containsKey(offset)) {
            generateNewChunk(offset);
        }
    }
    

}
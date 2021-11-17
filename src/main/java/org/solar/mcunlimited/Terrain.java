package org.solar.mcunlimited;

public class Terrain {

    private final int BLOCK_SIZE = 1;
    private final int CHUNK_SIZE = 16;

    private int[] m_vertices;
    private int[] m_indices;

    public int[] getVertices() {return m_vertices;}
    public int[] getIndices() {return m_indices;}

    private int[] mulByConst(int k, int[] arr) {
        int[] res = new int[arr.length];
        for(int i = 0;i < arr.length;i++) {
            res[i] = arr[i] * k;
        }
        return res;
    }

    private int[] elevateByConstInt(int k, int[] arr) {
        int[] res = new int[arr.length];
        for(int i = 0;i < arr.length;i++) {
            res[i] = arr[i] + k;
        }
        return res;
    }

    private float[] elevateByConstFloat(float k, float[] arr) {
        float[] res = new float[arr.length];
        for(int i = 0;i < arr.length;i++) {
            res[i] = arr[i] + k;
        }
        return res;
    }

    public void generateMesh() {


        int[] squareVertecies = {
            0, 0, 0,
            0, 0, 1,
            1, 0, 1,
            1, 0, 0
        };

        int[] sqareIndices = {
            0, 1, 2,
            2, 0, 3
        };

        m_vertices = new int[CHUNK_SIZE * CHUNK_SIZE * squareVertecies.length];
        m_indices = new int[CHUNK_SIZE * CHUNK_SIZE * sqareIndices.length];

        squareVertecies = mulByConst(BLOCK_SIZE, squareVertecies);

        int i,j;
        for(i = 0;i < CHUNK_SIZE;i ++) {
            for(j = 0;j < CHUNK_SIZE;j ++) {

                for(int k = 0;k < squareVertecies.length;k++) {
                    m_vertices[k + j * CHUNK_SIZE + i * CHUNK_SIZE] = (squareVertecies[k] + j * BLOCK_SIZE + i * BLOCK_SIZE);
                }

                for(int k = 0;k < sqareIndices.length;k++) {
                    m_indices[k + j * sqareIndices.length + i * sqareIndices.length] = (sqareIndices[k] + ((i+j) * 6));
                }

            }
        }

    }

    public void initialise() {

    }

    public void update() {

    }

    public void terminate() {

    }

}

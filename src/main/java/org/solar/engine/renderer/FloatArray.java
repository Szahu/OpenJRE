package org.solar.engine.renderer;

/**
 * This class is used to wrap raw float[] data like vertex positions or normals and pass it into the VertexArray.
 * @author Stanislaw Solarewicz
 */
public class FloatArray {
    public int step;
    public int varstep;
    public float[] data;

    /**
     * Creates a new Float Array object.
     * @param step How many values create one data point.
     * @param data The data itself. 
     */
    public FloatArray(int step, float[] data) {
        this.step = step;
        this.varstep = 0;
        this.data = data;
    }

    /**
     * Create a new Float Array object with indexed data.
     * @param step How many values create one data point.
     * @param inData The data itself. 
     * @param indices Index array of the data. 
     */
    public FloatArray(int step, float[] inData, int[] indices) {
        this.step = step;
        varstep = 0;
        float[] indexedData = new float[indices.length * step];
        for(int i = 0, k = 0; i < indices.length; i++){
            for(int j = 0; j < step; j++)
                indexedData[k++] = inData[indices[i]*step + j];
        }
        data = indexedData;
    }

    protected float[] getNextLine(){
        float[] res = new float[step];
        System.arraycopy(data, varstep, res, 0, step);
        varstep += step;
        return res;
    }

    /**
     * Removes uneccessary data. 
     */
    public void cleanup() {
        data = null;
    }
}
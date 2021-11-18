package org.solar.engine.renderer;

import org.solar.engine.Utils;

public class FloatArray {
    public int step;
    public int varstep;
    public float[] data;
    public FloatArray(int intStep, float[] inData) {
        step = intStep;
        varstep = 0;
        data = inData;
    }

    public FloatArray(int intStep, float[] inData, int[] indices) {
        step = intStep;
        varstep = 0;
        float[] indexedData = new float[indices.length * step];
        for(int i = 0, k = 0 ; i < indices.length; i++){
            for(int j = 0; j < step; j++)
                indexedData[k++] = inData[indices[i]*step + j];
        }
        data = indexedData;
    }

    public float[] getNextLine(){
        float[] res = new float[step];
        System.arraycopy(data, varstep, res, 0, step);
        varstep += step;
        return res;
    }

    public void cleanup() {
        data = null;
    }
}
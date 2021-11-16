package org.solar.engine.renderer;

public class FloatArray {
    public int step;
    public int varstep;
    public float[] data;
    public FloatArray(int intStep, float[] inData) {
        step = intStep;
        varstep = 0;
        data = inData;
    }
    public float[] getNextLine(){
        float[] res = new float[step];
        for(int i = 0;i < step; i++) {
            res[i] = data[i + varstep];
        }
        varstep += step;
        return res;
    }

    public void cleanup() {
        data = null;
    }
}
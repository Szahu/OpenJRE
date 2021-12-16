package org.solar.engine.renderer;

public class VertexData {
    public FloatArray[] arrays;
    public float[] rawData;
    public int[] indices;

    public VertexData(FloatArray ...data) {
        arrays = data;
        rawData = new float[getSumSize()];

        int index = 0;
        while(getSumVarStep() < getSumSize()) {
            for(FloatArray arr: arrays) {
                float[] line = arr.getNextLine();
                for (float v : line) {
                    rawData[index] = v;
                    index++;
                }
            }
        }
    }

    public VertexData(int[] indices, FloatArray ...data) {
        this(data);
        this.indices = indices;
    }

    private int getSumVarStep() {
        int total = 0;
        for(FloatArray arr: arrays) {
            total += arr.varstep;
        }
        return total;
    }

    public int getSumStep() {
        int total = 0;
        for(FloatArray arr: arrays) {
            total += arr.step;
        }
        return total;
    }

    private int getSumSize() {
        int total = 0;
        for(FloatArray arr: arrays) {
            total += arr.data.length;
        }
        return total;
    }
}

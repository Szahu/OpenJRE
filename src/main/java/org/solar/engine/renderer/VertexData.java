package org.solar.engine.renderer;

public class VertexData {
    public FloatArray[] arrays;
    public float[] rawData;

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

    public VertexData(FloatArray ...data) {
        arrays = data;
        rawData = new float[getSumSize()];

        int index = 0;
        while(getSumVarStep() < getSumSize()) {       
            for(FloatArray arr: arrays) {
                float[] line = arr.getNextLine();
                for(int i = 0;i < line.length;i++){
                    rawData[index] = line[i];
                    index++;
                }
            }
        } 
    }
}

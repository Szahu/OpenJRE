package org.solar.engine;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.VertexData;

public class ModelLoader {
    

    public static VertexArray loadModel(String path) {;
		String inputObjContent = Utils.getWholeFileAsString(path);
		float[] vertices = getVertices( inputObjContent ); // non-indexed!
		int[] verticesIndices = getIndices ( inputObjContent, VERTICES_IDX);
		float[] texels = getTexels  ( inputObjContent ); // non-indexed!
		int[] texelsIndices	= getIndices ( inputObjContent, TEXELS_IDX);

        FloatArray vertexes = new FloatArray(3, vertices, verticesIndices);
		int[] plainIndices = new int[verticesIndices.length];
		for(int i = 0; i < verticesIndices.length; i++) plainIndices[i] = i;
		VertexArray result = new VertexArray(plainIndices, new VertexData(vertexes,  new FloatArray(2, texels, texelsIndices)));
        return result;
    }

    private final static String VERTICES_PATTERN = "(v (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)\n)+";
    private final static String INDICES_PATTERN = "(f [0-9]+/([0-9]+)/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+\n)+";
    private final static String TEXELS_PATTERN = "(vt (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)\n)+";
    public  final static char VERTICES_IDX = 0;
    public  final static char TEXELS_IDX = 1;

    private static float[] getVertices(String fileContent){
        Vector<Float> vertices = new Vector<>();
        Pattern pattern = Pattern.compile(VERTICES_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( verticeAsString ->
                Arrays.stream(verticeAsString.replaceAll("^v ", "").split(" ")).forEach(verticePart ->
                    vertices.add(Float.parseFloat(verticePart))));
            int i = 0;
            float[] verticesArray = new float[vertices.size()];
            for(Float verticePart: vertices) verticesArray[i++] = ( verticePart != null? verticePart: Float.NaN);
            return verticesArray;
        }
        throw new RuntimeException("No vertices found at input file!");
    }

    private static int[] getIndices(String fileContent, int index ){
        Vector<Integer> indices = new Vector<>();
        Pattern pattern = Pattern.compile(INDICES_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( indiceAsString ->
                Arrays.stream( indiceAsString.replaceAll("^f ", "").split(" ")).forEach(indicePart ->
                     indices.add(Integer.parseInt( indicePart.split("/")[index] ))
            ));
            int i = 0;
            int[] indicesArray = new int[indices.size()];
            for(Integer indicePart: indices) indicesArray[i++] = ( indicePart != null? indicePart - 1 : 0);
            return indicesArray;
        }
        throw new RuntimeException("No indices found at input file!");
    }

    private static float[] getTexels(String fileContent ){
        Vector<Float> texels = new Vector<>();
        Pattern pattern = Pattern.compile(TEXELS_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( texelsAsString ->
                Arrays.stream( texelsAsString.replaceAll("^vt ", "").split(" ")).forEach(texelPart ->
                    texels.add(Float.parseFloat( texelPart ))
            ));
            int i = 0;
            float[] texelArray = new float[texels.size()];
            for(Float texelPart: texels) texelArray[i++] = ( texelPart != null? texelPart: Float.NaN);
            return texelArray;
        }
        throw new RuntimeException("No indices found at input file!");
    }

}

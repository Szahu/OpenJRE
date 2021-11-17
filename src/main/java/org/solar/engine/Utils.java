package org.solar.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.joml.Vector3f;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Utils {

    public  final static String  ABS_PROJECT_PATH       = "src/main/resources/shaders/";
    public  final static char    VERTEX_SHADER_IDX      = 0;
    public  final static char    FRAGMENT_SHADER_IDX    = 1;
    private final static String VERTEX_SHADER_TOKEN     = "#vertexShader";
    private final static String FRAGMENT_SHADER_TOKEN   = "#fragmentShader";
    private final static String ANSI_RESET              = "\u001B[0m";
    private final static String ANSI_BLACK              = "\u001B[30m";
    private final static String ANSI_RED                = "\u001B[31m";
    private final static String ANSI_GREEN              = "\u001B[32m";
    private final static String ANSI_YELLOW             = "\u001B[33m";
    private final static String ANSI_BLUE               = "\u001B[34m";
    private final static String ANSI_PURPLE             = "\u001B[35m";
    private final static String ANSI_CYAN               = "\u001B[36m";
    private final static String ANSI_WHITE              = "\u001B[37m";
    private final static String VERTICES_PATTERN        = "(v (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)\n)+";
    private final static String INDICES_PATTERN         = "(f [0-9]+/([0-9]+)/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+\n)+";
    private final static String TEXELS_PATTERN          = "(vt ([0-9]+)/[0-9]+/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+\n)+";
    private       static long   m_startDeltaTime        = 0;
    private       static float  m_deltaTime             = 0;

    public static String getWholeFileAsString(String shaderName) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader br = new BufferedReader( new FileReader( shaderName ) );
        br.lines()
                .forEach(line -> stringBuffer.append(line).append("\n"));
        br.close();
        return stringBuffer.toString();
    }

    public static String[] getTwoShaderStringsFromFile(String shaderName) throws IOException{
        boolean foundVertexShader = false;
        boolean foundFragmentShader = false;
        String[] result = {"",""};
        List<String> lines = Files.readAllLines( Paths.get( shaderName ), StandardCharsets.UTF_8 );
        for ( String line : lines ) {
            if (line.contains(VERTEX_SHADER_TOKEN)) {
                foundVertexShader = true;
                foundFragmentShader = false;
                continue;
            }
            else if ( line.contains(FRAGMENT_SHADER_TOKEN) ) {
                foundVertexShader = false;
                foundFragmentShader = true;
                continue;
            }
            if ( foundVertexShader ) {
                result[VERTEX_SHADER_IDX] += (line + "\n");
            } else if ( foundFragmentShader ) {
                result[FRAGMENT_SHADER_IDX] += (line + "\n");
            }
        }
        return result;
    }

    public static float[] getVertices( String fileContent ){
        Vector<Float> vertices = new Vector<>();
        Pattern pattern = Pattern.compile(VERTICES_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( verticeAsString -> {
                Arrays.stream(verticeAsString.replaceAll("^v ", "").split(" ")).forEach( verticePart -> {
                    vertices.add(Float.parseFloat(verticePart));
                });
            });
            int i = 0;
            float[] verticesArray = new float[vertices.size()];
            for(Float verticePart: vertices) verticesArray[i++] = ( verticePart != null? verticePart: Float.NaN);
            return verticesArray;
        }
        throw new RuntimeException("No vertices found at input file!");
    }

    public static int[] getIndices(String fileContent ){
        Vector<Integer> indices = new Vector<>();
        Pattern pattern = Pattern.compile(INDICES_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( indiceAsString -> {
                Arrays.stream( indiceAsString.replaceAll("^f ", "").split(" ")).forEach( indicePart -> {
                    indices.add(Integer.parseInt( indicePart.split("/")[0] ));
                });
            });
            int i = 0;
            int[] indicesArray = new int[indices.size()];
            for(Integer indicePart: indices) indicesArray[i++] = ( indicePart != null? indicePart - 1 : 0);
            return indicesArray;
        }
        throw new RuntimeException("No indices found at input file!");
    }

    public static void updateDeltaTime() {
        long time = System.nanoTime();
        m_deltaTime = ((float)(time - m_startDeltaTime)) / 100000000f;
        m_startDeltaTime = time;
    }

    public static float     getDeltaTime ()                 { return m_deltaTime; }
    public static void      LOG_SUCCESS  (Object o)         { System.out.println(ANSI_GREEN + o.toString() + ANSI_RESET); }
    public static void      LOG_ERROR    (Object o)         { System.out.println(ANSI_RED + o.toString() + ANSI_RESET); }
    public static void      LOG_WARNING  (Object o)         { System.out.println(ANSI_YELLOW + o.toString() + ANSI_RESET); }
    public static void      LOG_INFO     (Object o)         { System.out.println(ANSI_BLUE + o.toString() + ANSI_RESET); }
    public static void      LOG          (Object o)         { System.out.println( o.toString() ); }
    public static float[]   vec3fToArray (Vector3f vec)     { return new float[] {vec.get(0), vec.get(1), vec.get(2)}; }
}
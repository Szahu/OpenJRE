package org.solar.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.joml.Vector3f;

import java.nio.charset.*;

public class Utils {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static void print(Object o){
        System.out.println(o.toString());
    }
    public static void LOG_SUCCESS(Object o) {System.out.println(ANSI_GREEN + o.toString() + ANSI_RESET);}
    public static void LOG_ERROR(Object o) {System.out.println(ANSI_RED + o.toString() + ANSI_RESET);}
    public static void LOG_WARNING(Object o) {System.out.println(ANSI_YELLOW + o.toString() + ANSI_RESET);}
    public static void LOG_INFO(Object o) {System.out.println(ANSI_BLUE + o.toString() + ANSI_RESET);}
    public static void LOG(Object o) {System.out.println(o.toString());}

    public static float[] vec3fToArray(Vector3f vec) {float[] res = {vec.get(0), vec.get(1), vec.get(2)}; return res;}

    private static long m_startDeltaTime = 0;
    private static float m_deltaTime  = 0;
    public static void updateDeltaTime() {
        long time = System.nanoTime();
        m_deltaTime = ((float)(time - m_startDeltaTime)) / 100000000f;
        m_startDeltaTime = time;
    }
    public static float getDeltaTime() {return m_deltaTime;}

    private final static String ABS_PROJECT_PATH = "src/main/resources/shaders/";
    //Returning content of the text file as String
    public static String FileToString(String shaderName) {
        StringBuffer stringBuffer = new StringBuffer();
        try{
            FileReader fr = new FileReader(ABS_PROJECT_PATH + shaderName);
            BufferedReader br = new BufferedReader( fr );
            br.lines()
                .forEach(line -> stringBuffer.append(line + "\n"));
            fr.close();
            br.close();
            return stringBuffer.toString();
        }
        catch (Exception e) {System.out.println("Exception at reading file: " + shaderName + "\n" + e.getStackTrace());}
        return null;
    }
    //This function takes a text file and splits it into two after each token
    public static String[] multipleShadersFromFile(String shaderName) throws IOException{
        String vertexShaderToken = "#vertexShader";
        String fragmentShaderToken = "#fragmentShader";
        String vertexShaderContent = "";
        String fragmentShaderContent = "";
        String path = ABS_PROJECT_PATH + shaderName;
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        boolean foundVertexShader = false;
        boolean foundFragmentShader = false;
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            //Checking if the line is our token
            if (line.contains(vertexShaderToken)) {
                foundVertexShader = true;
                foundFragmentShader = false;
                continue;
            }
            //Checking if the line is our token
            else if (line.contains(fragmentShaderToken)) {
                foundVertexShader = false;
                foundFragmentShader = true;
                continue;
            }
            if (foundVertexShader) {vertexShaderContent += (lines.get(i) + "\n") ;}
            else if (foundFragmentShader) {fragmentShaderContent += (lines.get(i) + "\n");}
        }

        String[] result = new String[2];
        result[0] = vertexShaderContent;
        result[1] = fragmentShaderContent;
        return result;
    }
}
package org.solar.engine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.*;
public class Utils {
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
        catch (Exception e) {System.out.println("Exception at ");}
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
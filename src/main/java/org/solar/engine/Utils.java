package org.solar.engine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.*;

public class Utils {
    public final static String ABS_PROJECT_PATH        = "src/main/resources/shaders/";
    public final static char    VERTEX_SHADER_IDX       = 0;
    public final static char    FRAGMENT_SHADER_IDX     = 1;
    private final static String VERTEX_SHADER_TOKEN     = "#vertexShader";
    private final static String FRAGMENT_SHADER_TOKEN   = "#fragmentShader";

    //Returning content of the text file as String
    public static String getShaderStringFromFile(String shaderName) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader br = new BufferedReader( new FileReader( shaderName ) );
        br.lines()
            .forEach(line -> stringBuffer.append(line).append("\n"));
        br.close();
        return stringBuffer.toString();
    }
    //This function takes a text file and splits it into String array after each token
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
}
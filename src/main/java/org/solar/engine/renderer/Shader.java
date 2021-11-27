package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.solar.engine.Utils;

public class Shader {

    private final int m_programId; //openGL id of the shader program
    private int vertexShaderId; //openGl id of the vertex shader program
    private int fragmentShaderId; //openGl id of the fragment shader program
    private final Map<String, Integer> m_uniforms; //map of uniform names and locations
    private FloatBuffer m_floatBuffer16; //FloatBuffer for loading matrices

    public static final String uniformProjectionMatrixToken = "u_projectionMatrix"; 
    public static final String uniformViewMatrixToken = "u_viewMatrix"; 
    public static final String uniformTransformMatrixToken = "u_worldMatrix"; 

    public Shader() {
        m_uniforms = new HashMap<>();
        m_programId = glCreateProgram();
        if (m_programId == 0) Utils.LOG_ERROR("System could not create the shader program");
    }

    /**
     * Creates a shader program from a file which stores two shader, each one starting with a proper token (#vertexShader, #fragmentShader).
     * @param bothShadersFileName Name of the file (including suffix) in which both of our shaders are stored
     */
    public Shader(String bothShadersFileName) throws Exception {

        m_uniforms = new HashMap<>();
        m_programId = glCreateProgram();
        if (m_programId == 0) Utils.LOG_ERROR("System could not create the shader program");
        load(bothShadersFileName);
    }

    /**
     * Creates a shader program from two files containing vertex and fragment shaders.
     * @param vertexShaderName Name of the file (including suffix) containing the vertex shader (no token).
     * @param fragmentShaderName Name of the file (including suffix) containing the fragment shader (no token).
     */
    public Shader(String vertexShaderName, String fragmentShaderName) throws IOException {

        m_uniforms = new HashMap<>();
        m_programId = glCreateProgram();
        if (m_programId == 0) Utils.LOG_ERROR("System could not create the shader program");
        load(vertexShaderName, fragmentShaderName);
    }

    /**
     * Sets a value of a uniform in a shader.
     * @param uniformName Name of the uniform to be set.
     * @param value Data to be sent to the gpu.
     */
    public void setUniform(String uniformName, Matrix4f value) throws RuntimeException {

        // Dump the matrix into a float buffer
        if(m_uniforms.containsKey(uniformName)) {
            if (m_floatBuffer16==null)
                m_floatBuffer16 = BufferUtils.createFloatBuffer(16);
            m_floatBuffer16.clear();
            value.get(m_floatBuffer16);
            glUniformMatrix4fv(m_uniforms.get(uniformName), false, m_floatBuffer16);
            m_floatBuffer16.flip();
        } else {
            Utils.LOG_ERROR("Trying to set value of the uniform that does not exist: " + uniformName);
        }
    }

    public void setUniform(String uniformName, Vector3f value) throws RuntimeException {
        if(m_uniforms.containsKey(uniformName)) {
            glUniform3f(m_uniforms.get(uniformName), value.x, value.y, value.z);
        } else {
            Utils.LOG_ERROR("Trying to set value of the uniform that does not exist: " + uniformName);
        }
    }

     /**
     * Sets a value of a uniform in a shader.
     * @param uniformName Name of the uniform to be set.
     * @param value Data to be sent to the gpu.
     */
    public void setUniform(String uniformName, int value) {
        if(m_uniforms.containsKey(uniformName)) {
            glUniform1i(m_uniforms.get(uniformName), value);
        } else {
            Utils.LOG_ERROR("Trying to set value of the uniform that does not exist: " + uniformName);
        }
    }

    private void generateUniforms(String shaderCode) throws RuntimeException {

        class StructData {
            public StructData(String name,  List<String> fieldNames) {
                this.name = name;
                this.fieldNames = fieldNames;
            }
            String name;
            List<String> fieldNames;
        }

        List<StructData> structs = new ArrayList<>();

        final int searchLimit = 50;

        //for each struct in the shader
        for(int index = shaderCode.indexOf("struct ");index >= 0; index = shaderCode.indexOf("struct ", index + 1)) {
            
            int nameBeginIndex = index + 7;
            int nameEndIndex = nameBeginIndex;

            while(shaderCode.charAt(nameEndIndex) != '{' && nameEndIndex - index < searchLimit) {
                if(nameEndIndex - index == searchLimit - 1) {continue;}
                nameEndIndex++;
            }
            if(shaderCode.charAt(nameEndIndex - 1) == ' ') {nameEndIndex--;}

            int structContentBeginIndex = nameEndIndex + 1;
            int structContentEndIndex = structContentBeginIndex;

            while(shaderCode.charAt(structContentEndIndex - 1) != '}') {structContentEndIndex++;}

            String structName = shaderCode.substring(nameBeginIndex, nameEndIndex);
            String structContent = shaderCode.substring(structContentBeginIndex, structContentEndIndex);
            List<String> structFields = new ArrayList<>();


            for(int i = structContent.indexOf(";");i >= 0; i = structContent.indexOf(";", i + 1)) {
                int fieldNameBegin = i;

                while(structContent.charAt(fieldNameBegin - 1) != ' ') {fieldNameBegin--;}

                String fieldName = structContent.substring(fieldNameBegin, i);
                structFields.add(fieldName);
            }
            
            structs.add(new StructData(structName, structFields));

        }

        for(int index = shaderCode.indexOf("uniform");index >= 0; index = shaderCode.indexOf("uniform", index + 1)) {

            int nameBeginIndex = index;
            int numOfSpaces = 0;
            int typeStartIndex = -1;

            while (numOfSpaces != 2) {
                if(shaderCode.charAt(nameBeginIndex) == ' ') {
                    numOfSpaces++;
                    if(numOfSpaces == 1) {
                        typeStartIndex = nameBeginIndex;
                    }
                }
                nameBeginIndex++;
            }

            int nameEndIndex = nameBeginIndex;
            while(shaderCode.charAt(nameEndIndex) != ';') {
                nameEndIndex++;
            }

            String uniformName = shaderCode.substring(nameBeginIndex, nameEndIndex);
            String typeName = shaderCode.substring(typeStartIndex + 1, nameBeginIndex - 1);

            int uniformLocation = -1;

            boolean isStruct = false;
            StructData uniformStructData = null;

            for(StructData data : structs) {
                if(typeName.equals(data.name)) {
                    isStruct = true;
                    uniformStructData = data;
                }
            }

            if(isStruct) {

                for(String fieldName : uniformStructData.fieldNames) {
                    uniformLocation = glGetUniformLocation(m_programId, uniformName + "." + fieldName);
                    if (uniformLocation < 0) {
                        throw new RuntimeException("Could not find uniform (or it is not used): " + uniformName);
                    }
                    m_uniforms.put("u_struct.color", uniformLocation);
                }    

            } else {
                uniformLocation = glGetUniformLocation(m_programId, uniformName);
                if (uniformLocation < 0) {
                    throw new RuntimeException("Could not find uniform (or it is not used): " + uniformName);
                }
                m_uniforms.put(uniformName, uniformLocation);
            }

     

        }

        //m_uniforms.keySet().forEach((key) -> {Utils.LOG(key);});
    }

    /**
     * Loads a shader program from a single file. Vertex shader must beging with #vertexShader 
     * and fragment shader needs to begin with #fragmentShader
     * @param bothShadersFileName Name of the file containing shader code. 
     */
    private void load(String bothShadersFileName) throws Exception {
        
        /* try {
            String[] shadersContent = multipleShadersFromFile(bothShadersFileName);
            createVertexShader(shadersContent[0]);
            createFragmentShader(shadersContent[1]);
            link();
            generateUniforms(shadersContent[0]);
            generateUniforms(shadersContent[1]);
        } catch (Exception e) {
            Utils.LOG_ERROR("Error while loading shaders from path: " + bothShadersFileName + " , " + e.toString());
        } */
        String[] shadersContent = multipleShadersFromFile(bothShadersFileName);
        createVertexShader(shadersContent[0]);
        createFragmentShader(shadersContent[1]);
        link();
        generateUniforms(shadersContent[0]);
        generateUniforms(shadersContent[1]);

    }

    /**
     * Cretes and loads a shader object from two differente files.
     * @param vertexShaderName Name of the file containing vertex shader.
     * @param fragmentShaderName Name of the file containing fragment shader.
     */
    public void load(String vertexShaderName, String fragmentShaderName) {

        try {
            String shaderCode = Utils.getFileAsString(vertexShaderName);
            createVertexShader(shaderCode);
            generateUniforms(shaderCode);
        } catch (Exception e) {

            Utils.LOG_ERROR("Error while loading shaders from path: " + vertexShaderName + " , " + e.toString());
        }

        try{
            String shaderCode = Utils.getFileAsString(fragmentShaderName);
            createFragmentShader(shaderCode);
        } catch (Exception e) {
            Utils.LOG_ERROR("Error while loading shaders from path: " + fragmentShaderName + " , " + e.toString());
        }
        link();
    }

    private void createVertexShader(String shaderCode) throws RuntimeException{
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    private void createFragmentShader(String shaderCode) throws RuntimeException {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType){
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        glAttachShader(m_programId, shaderId);
        return shaderId;
    }

    private void link() throws RuntimeException {
        glLinkProgram(m_programId);
        if (glGetProgrami(m_programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(m_programId, 1024));
        }
        if (vertexShaderId != 0) {
            glDetachShader(m_programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(m_programId, fragmentShaderId);
        }
        glValidateProgram(m_programId);
        if (glGetProgrami(m_programId, GL_VALIDATE_STATUS) == 0) {
            Utils.LOG_WARNING("Warning validating Shader code: " + glGetProgramInfoLog(m_programId, 1024));
        }
    }

    /**
     * Binds the shader.
     */
    public void bind() { glUseProgram(m_programId); }
    /**
     * Unbinds the shader.
     */
    public void unbind() { glUseProgram(0); }

    /**
     * Deletes the shader.
     */
    public void cleanup() {
        unbind();
        if (m_programId != 0) {
            glDeleteProgram(m_programId);
        }
    }
    
    private final static String SHADERS_FOLDER_PATH = "src/main/resources/shaders/";
    private final static int VERTEX_SHADER_IDX = 0;
    private final static int FRAGMENT_SHADER_IDX = 1;
    private final static String VERTEX_SHADER_TOKEN = "#vertexShader";
    private final static String FRAGMENT_SHADER_TOKEN = "#fragmentShader";

    //This function takes a text file and splits it into two after each token
    private static String[] multipleShadersFromFile(String shaderName) throws IOException{
        String vertexShaderContent = "";
        String fragmentShaderContent = "";
        String path = SHADERS_FOLDER_PATH + shaderName;
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);

        boolean foundVertexShader = false;
        boolean foundFragmentShader = false;
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            //Checking if the line is our token
            if (line.contains(VERTEX_SHADER_TOKEN)) {
                foundVertexShader = true;
                foundFragmentShader = false;
                continue;
            }
            //Checking if the line is our token
            else if (line.contains(FRAGMENT_SHADER_TOKEN)) {
                foundVertexShader = false;
                foundFragmentShader = true;
                continue;
            }
            if (foundVertexShader) {vertexShaderContent += (lines.get(i) + "\n") ;}
            else if (foundFragmentShader) {fragmentShaderContent += (lines.get(i) + "\n");}
        }

        String[] result = new String[2];
        result[VERTEX_SHADER_IDX] = vertexShaderContent;
        result[FRAGMENT_SHADER_IDX] = fragmentShaderContent;
        return result;
    }
}
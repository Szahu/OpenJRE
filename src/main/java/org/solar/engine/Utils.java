package org.solar.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.joml.Vector3f;

public class Utils {

    /**
     * Loads content of a file to a String.
     * @param path Path of the file to load.
     * @return Returns the content of the file as a string.
     */
    public static String getFileAsString(String path) {
        try {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader br = new BufferedReader( new FileReader( path ) );
        br.lines().forEach(line -> stringBuffer.append(line).append("\n"));
        br.close();
        return stringBuffer.toString();
        } catch (IOException e) {
            Utils.LOG_ERROR("Couldn't load file as a String from path: " + path);
            Utils.LOG_ERROR("Error message: " + path);
            return "";
        }
    }

    private static long m_startDeltaTime = 0;
    private static float m_deltaTime  = 0;

    public static void updateDeltaTime() {
        long time = System.nanoTime();
        m_deltaTime = ((float)(time - m_startDeltaTime)) / 100000000f;
        m_startDeltaTime = time;
    }
    private final static String ANSI_RESET              = "\u001B[0m";
    private final static String ANSI_RED                = "\u001B[31m";
    private final static String ANSI_GREEN              = "\u001B[32m";
    private final static String ANSI_YELLOW             = "\u001B[33m";
    private final static String ANSI_BLUE               = "\u001B[34m";
    public static float     getDeltaTime ()                 { return m_deltaTime; }
    public static void      LOG_SUCCESS  (Object o)         { System.out.println(ANSI_GREEN + o.toString() + ANSI_RESET); }
    public static void      LOG_ERROR    (Object o)         { System.out.println(ANSI_RED + o.toString() + ANSI_RESET); }
    public static void      LOG_WARNING  (Object o)         { System.out.println(ANSI_YELLOW + o.toString() + ANSI_RESET); }
    public static void      LOG_INFO     (Object o)         { System.out.println(ANSI_BLUE + o.toString() + ANSI_RESET); }
    public static void      LOG          (Object o)         { System.out.println( o.toString() ); }
    public static void      LOG          (Vector3f vec)         { System.out.println(vec.x + " " + vec.y + " " + vec.z); }
    public static float[]   vec3fToArray (Vector3f vec)     { return new float[] {vec.get(0), vec.get(1), vec.get(2)}; }
}
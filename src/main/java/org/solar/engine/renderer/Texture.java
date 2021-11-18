package org.solar.engine.renderer;

import org.lwjgl.BufferUtils;
import org.solar.engine.Utils;

import static org.lwjgl.opengl.GL11.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

public class Texture {
    private static final int BYTES_PER_PIXEL = 4;//3 for RGB, 4 for RGBA
    private int m_TextureId;

    public int getTextureId() {
        return m_TextureId;
    }

    public Texture(String pathToFile){
        BufferedImage image = loadImage(pathToFile);
        assert image != null;
        m_TextureId = loadTexture(image);
    }

    public static int loadTexture(BufferedImage image){

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for(int y = image.getHeight()-1; y > 0 ; y--){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using 
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
        return textureID;
    }

       public static BufferedImage loadImage(String loc)
       {
            try {
               return ImageIO.read(new FileInputStream(loc));
            } catch (IOException e) {
                Utils.LOG_ERROR(e.toString());
            }
           return null;
       }
}

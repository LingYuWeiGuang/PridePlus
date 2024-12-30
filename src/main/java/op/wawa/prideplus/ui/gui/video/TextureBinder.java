package op.wawa.prideplus.ui.gui.video;

import lombok.Getter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Texture-binder in LWJGL.
 *
 * @version 1.0.0
 *
 * @author LingYuWeiGuang
 * @author HyperTap
 */

public class TextureBinder {
    private int imageWidth, imageHeight, internalformat;
    @Getter
    private ByteBuffer imageBuffer;

    /**
     * Set image.
     *
     * @param buffer image object
     * @param width image width
     * @param height image height
     */
    public void setBuffer(ByteBuffer buffer, int width, int height) {
        internalformat = GL_RGB;
        imageWidth = width;
        imageHeight = height;
        imageBuffer = buffer;
    }

    /**
     * Binding texture and play video frame.
     *
     * @param buffer image object
     * @param width image width
     * @param height image height
     * @param internalformat image color format
     */
    public void setBuffer(ByteBuffer buffer, int width, int height, int internalformat) {
        this.internalformat = internalformat;
        imageWidth = width;
        imageHeight = height;
        imageBuffer = buffer;
    }

    /**
     * Binding texture form buffered images.
     */
    public void bindTexture() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, -1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, internalformat, imageWidth, imageHeight, 0, internalformat, GL_UNSIGNED_BYTE, imageBuffer);
    }
}

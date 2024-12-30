package op.wawa.prideplus.ui.gui.video;

import op.wawa.prideplus.utils.render.RenderUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Video-player in LWJGL. No sound.
 *
 * <p> This code isn't public, so don't shared it. </p>
 *
 * @version 1.0.0
 *
 * @author LingYuWeiGuang
 * @author HyperTap
*/

public class VideoPlayer {
    private FFmpegFrameGrabber frameGrabber;
    private TextureBinder textureBinder;

    private double frameRate; // fps
    private int count; // frames counter
    private long lastTime;

    public boolean suspended = false;
    private boolean stopped = false;

    private static final Logger logger = LogManager.getLogger("VideoPlayer");

    /**
     * Start video-player object.
     *
     * @param videoFile
     * your video file object
     */
    public void init(File videoFile) throws FFmpegFrameGrabber.Exception {
        frameGrabber = new FFmpegFrameGrabber(videoFile.getPath());
        frameGrabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
        avutil.av_log_set_level(avutil.AV_LOG_QUIET); // Log level -> quiet
        textureBinder = new TextureBinder();

        lastTime = 0;
        count = 0;
        stopped = false;
        frameGrabber.start();
        frameRate = frameGrabber.getFrameRate();

        while (true) {
            Frame frame = frameGrabber.grab();
            if (frame != null) {
                if (frame.image != null) {
                    textureBinder.setBuffer((ByteBuffer) frame.image[0], frame.imageWidth, frame.imageHeight);

                    lastTime = System.currentTimeMillis();
                    count++;
                    break;
                }
            }
        }

        final Thread thread = getThread();
        thread.start();
    }

    private Thread getThread() {
        final Thread thread = new Thread("Video Background") {
            @Override
            public void run() {
                try {
                    while (!stopped) {
                        if (System.currentTimeMillis() - lastTime > 1000 / frameRate && !suspended) {
                            doGetBuffer();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                interrupt();
            }
        };

        thread.setDaemon(true);
        return thread;
    }

    private void doGetBuffer() throws FFmpegFrameGrabber.Exception {
        int fLength = frameGrabber.getLengthInFrames() - 5;
        if (count < fLength) {
            Frame frame = frameGrabber.grab();
            if (frame != null) {
                if (frame.image != null) {
                    textureBinder.setBuffer((ByteBuffer) frame.image[0], frame.imageWidth, frame.imageHeight);

                    lastTime = System.currentTimeMillis();
                    count++;
                }
            }
        } else {
            count = 0;
            frameGrabber.setFrameNumber(0);
        }
    }

    /**
     * Binding texture and play video frame.
     *
     * @param left rect left
     * @param top rect top
     * @param right rect right
     * @param bottom rect bottom
     */
    public void render(int left, int top, int right, int bottom) throws FrameGrabber.Exception {
        if (stopped) return;

        suspended = false;

        textureBinder.bindTexture();

        // draw Rect
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(left, bottom, 0);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(right, bottom, 0);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(right, top, 0);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(left, top, 0);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Stop play video frame.
     */
    public void stop() throws FFmpegFrameGrabber.Exception {
        if (stopped) return;

        stopped = true;

        textureBinder = null;

        lastTime = 0;
        count = 0;

        frameGrabber.stop();
        frameGrabber.release();
        frameGrabber = null;
    }
}

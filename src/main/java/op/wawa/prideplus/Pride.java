package op.wawa.prideplus;

import op.wawa.prideplus.manager.ConfigManager;
import op.wawa.prideplus.event.EventManager;
import op.wawa.prideplus.manager.RotationManager;
import op.wawa.prideplus.module.ModuleManager;
import op.wawa.prideplus.ui.gui.alt.AltManager;
import op.wawa.prideplus.ui.gui.clickgui.ClickGUI;
import op.wawa.prideplus.ui.gui.video.VideoPlayer;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Pride {
    public static final String NAME = "PridePlus";
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "LingYuWeiGuang";

    public static final Pride INSTANCE = new Pride();
    public static final Logger LOGGER = LogManager.getLogger("PridePlus");

    public EventManager eventManager;
    public RotationManager rotationManager;
    public ModuleManager moduleManager;
    public ConfigManager configManager;

    public VideoPlayer videoPlayer;

    public ClickGUI clickGui;

    public void startClient() {
        videoPlayer = new VideoPlayer();
        eventManager = new EventManager();
        rotationManager = new RotationManager();
        moduleManager = new ModuleManager();
        
        moduleManager.init();

        configManager = new ConfigManager();
        configManager.read("modules");

        loadVideo();

        clickGui = new ClickGUI();

        AltManager.INSTANCE = new AltManager();

        try {
            AltManager.INSTANCE.readAlt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startThread();
    }

    public void stopClient() {
        configManager.save("modules");
    }

    public static void startThread() {
        final Thread thread = new Thread("Save Config Thread") {
            @Override
            public void run() {
                while (Minecraft.getMinecraft().running) {
                    try {
                        AltManager.INSTANCE.saveAlt();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                interrupt();
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    public static void loadVideo() {
        LOGGER.info("开始加载视频背景。");

        String resourceFilePath = "assets/minecraft/" + Pride.NAME.toLowerCase() + "/background.mp4";
        File videoFile = new File(INSTANCE.configManager.getClientDirectory(), "background.mp4");
        Path destinationFilePath = videoFile.toPath();

        try {
            if (!videoFile.exists()) {
                if (videoFile.getParentFile().exists() || videoFile.getParentFile().mkdirs()) {
                    videoFile.createNewFile();
                    InputStream inputStream = Pride.class.getClassLoader().getResourceAsStream(resourceFilePath);
                    if (inputStream != null) {
                        OutputStream outputStream = Files.newOutputStream(destinationFilePath);

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        inputStream.close();
                        outputStream.close();
                        LOGGER.info("视频文件已成功写入: {}", destinationFilePath);
                    } else {
                        LOGGER.error("视频资源不存在: {}", resourceFilePath);
                    }
                } else {
                    LOGGER.error("无法创建视频文件: {}", destinationFilePath);
                }
            } else {
                LOGGER.info("视频文件已存在，跳过创建视频文件。");
            }
            INSTANCE.videoPlayer.init(videoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("视频背景加载完毕。");
    }
}

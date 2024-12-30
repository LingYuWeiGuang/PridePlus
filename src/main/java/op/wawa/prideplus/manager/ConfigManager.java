package op.wawa.prideplus.manager;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.value.Value;
import op.wawa.prideplus.value.values.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class ConfigManager implements MinecraftInstance {
    private final Gson gson = new Gson();
    @Getter
    private final File clientDirectory = new File(Minecraft.getMinecraft().mcDataDir, Pride.NAME + "/");
    private final Logger logger = LogManager.getLogger("Config Manager");

    public ConfigManager() {
        if (!clientDirectory.exists())
            clientDirectory.mkdir();
    }

    public boolean exists(String name) {
        File config = new File(clientDirectory, name + ".json");
        return config.exists();
    }

    public void read(String name) {
        File config = new File(clientDirectory, name + ".json");
        if (config.exists()) {
            for (Module m : Pride.INSTANCE.moduleManager.getModuleMap().values()) {
                if (m.isEnable())
                    m.setEnable(false);
            }
            try {
                loadJson(new JsonParser().parse(new InputStreamReader(Files.newInputStream(config.toPath()))));
            } catch (IOException e) {
                logger.catching(e);
            }
            if (Minecraft.getMinecraft().thePlayer != null)
                mc.thePlayer.addChatMessage("Config " + name + " was loaded.");
        } else {
            if (Minecraft.getMinecraft().thePlayer != null)
                mc.thePlayer.addChatMessage("Config " + name + " not existed.");
        }
    }

    public void save(String name) {
        File config = new File(clientDirectory, name + ".json");
        try {
            FileOutputStream fos = new FileOutputStream(config);
            fos.write(gson.toJson(toJson()).getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public void delete(String name) {
        File config = new File(clientDirectory, name + ".json");
        config.delete();
    }

    public JsonObject toJson() {
        final JsonObject moduleObject = new JsonObject();

        for (Module m : Pride.INSTANCE.moduleManager.getModuleMap().values()) {
            final JsonObject settingObject = new JsonObject();

            settingObject.addProperty("Enabled", m.isEnable());
            settingObject.addProperty("Key", m.getKeyCode());
            settingObject.addProperty("Hidden", m.isHide());

/*            if (m instanceof HUDModule hudModule) {
                settingObject.addProperty("PositionX", hudModule.getPositionX().toString());
                settingObject.addProperty("PositionY", hudModule.getPositionY().toString());
                settingObject.addProperty("OffsetX", hudModule.getOffsetX());
                settingObject.addProperty("OffsetY", hudModule.getOffsetY());
            }*/

            final List<Value<?>> values = m.getValues();

            if (!values.isEmpty()) {
                for (Value<?> value : values) {
                    if (value instanceof ColorValue val) {
                        final JsonObject property = new JsonObject();

                        property.addProperty("Color", val.getValue().getRGB());
                        property.addProperty("Hue", val.hue.getValue());
                        property.addProperty("Saturation", val.saturation.getValue());
                        property.addProperty("Brightness", val.brightness.getValue());
                        property.addProperty("Opacity", val.opacity.getValue());
                        property.addProperty("Speed", val.speed.getValue());
                        property.addProperty("Rainbow", val.rainbow.getValue());
                        property.addProperty("Fade", val.fade.getValue());

                        settingObject.add(val.getValueName(), property);
                    } else settingObject.addProperty(value.getValueName(), value.getValue().toString());
                }
            }

            moduleObject.add(m.getModuleName(), settingObject);
        }

        return moduleObject;
    }

    public void loadJson(JsonElement element) {
        if (!element.isJsonObject())
            return;

        final JsonObject moduleObject = element.getAsJsonObject();
/*        for (Module hud : Pride.INSTANCE.moduleManager.getModuleMap().values()) {
            if (!moduleObject.has(hud.getModuleName())) continue;

*//*            if (hud instanceof HUDModule hudModule) {
                JsonObject hudObject = moduleObject.get(hud.getModuleName()).getAsJsonObject();

                if (hudObject.has("PositionX")) hudModule.setPositionX(HUDModule.Position.valueOf(hudObject.get("PositionX").getAsString()));
                if (hudObject.has("PositionY")) hudModule.setPositionY(HUDModule.Position.valueOf(hudObject.get("PositionY").getAsString()));
                if (hudObject.has("OffsetX"))hudModule.setOffsetX(hudObject.get("OffsetX").getAsFloat());
                if (hudObject.has("OffsetY"))hudModule.setOffsetY(hudObject.get("OffsetY").getAsFloat());
            }*//*


        }*/
        for (Map.Entry<String, JsonElement> moduleElement : moduleObject.entrySet()) {
            final Module module = Pride.INSTANCE.moduleManager.getModuleFromName(moduleElement.getKey());

            if (module == null)
                continue;

            final JsonElement settingElement = moduleElement.getValue();

            if (!settingElement.isJsonObject())
                continue;

            final JsonObject settingObject = settingElement.getAsJsonObject();

            for (Map.Entry<String, JsonElement> settingSet : settingObject.entrySet()) {
                switch (settingSet.getKey()) {
                    case "Enabled": {
                        module.setEnable(settingSet.getValue().getAsBoolean());
                        break;
                    }
                    case "Key": {
                        module.setKeyCode(settingSet.getValue().getAsInt());
                        break;
                    }
                    case "Hidden": {
                        module.setHide(settingSet.getValue().getAsBoolean());
                        break;
                    }
                    default: {
                        final Value<?> option = module.getValue(settingSet.getKey());

                        if (option == null)
                            continue;

                        try {
                            switch (option) {
                                case BooleanValue opt -> opt.setValue(settingSet.getValue().getAsBoolean());
                                case NumberValue opt -> opt.setValue(settingSet.getValue().getAsDouble());
                                case ModeValue opt -> opt.setValue(settingSet.getValue().getAsString());
                                case TextValue opt -> opt.setValue(settingSet.getValue().getAsString());
                                case ColorValue opt -> {
                                    final JsonObject settings = settingSet.getValue().getAsJsonObject();
                                    opt.setValue(new Color(settings.get("Color").getAsInt(), true));
                                    opt.hue.setValue(settings.get("Hue").getAsDouble());
                                    opt.saturation.setValue(settings.get("Saturation").getAsDouble());
                                    opt.brightness.setValue(settings.get("Brightness").getAsDouble());
                                    opt.opacity.setValue(settings.get("Opacity").getAsDouble());
                                    opt.speed.setValue(settings.get("Speed").getAsDouble());
                                    opt.rainbow.setValue(settings.get("Rainbow").getAsBoolean());
                                    opt.fade.setValue(settings.get("Fade").getAsBoolean());
                                }
                                default -> {
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }


}

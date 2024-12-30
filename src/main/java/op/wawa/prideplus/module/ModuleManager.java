package op.wawa.prideplus.module;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.impl.fight.AntiBot;
import op.wawa.prideplus.module.impl.fight.KillAura;
import op.wawa.prideplus.module.impl.fight.Target;
import op.wawa.prideplus.module.impl.fight.Velocity;
import op.wawa.prideplus.module.impl.move.NoSlow;
import op.wawa.prideplus.module.impl.move.Speed;
import op.wawa.prideplus.module.impl.move.Sprint;
import op.wawa.prideplus.module.impl.visual.ClickGUIMod;
import op.wawa.prideplus.module.impl.visual.HUD;
import op.wawa.prideplus.module.impl.visual.PostProcessing;
import op.wawa.prideplus.module.impl.world.GroundScaffold;
import op.wawa.prideplus.module.impl.world.Scaffold;
import op.wawa.prideplus.value.Value;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.compare;

@Getter
public final class ModuleManager {

    private final LinkedHashMap<String, Module> moduleMap = new LinkedHashMap<>();

    public void init() {
        Pride.LOGGER.info("Start loading modules.");

        // Fight
        registerModules(new AntiBot(), new KillAura(), new Velocity(), new Target());

        // Visual
        registerModules(new HUD(), new ClickGUIMod(), new PostProcessing());

        // Move
        registerModules(new Sprint(), new NoSlow(), new Speed());

        // World
        registerModules(new Scaffold(), new GroundScaffold());

        Pride.LOGGER.info("Loaded {} modules.", moduleMap.size());
    }

    public void onKey(int keyCode) {
        if (keyCode == Keyboard.KEY_NONE) return;

        moduleMap.values().stream().filter(module -> module.getKeyCode() == keyCode).forEach(Module::toggle);
    }

    public List<Module> getModsSorted() {
        Stream<Module> stream = moduleMap.values().stream();

        stream = stream.sorted((mod1, mod2) -> compare(HUD.fontDrawer.getStringWidth(mod2.getModRenderNameWithTag()),
                HUD.fontDrawer.getStringWidth(mod1.getModRenderNameWithTag())));
        return stream.filter(m -> !m.isHide()).collect(Collectors.toList());
    }

    public boolean getModuleEnable(String modName) {
        return getModuleFromName(modName).isEnable();
    }

    public Module getModuleFromName(String modName) {
        return moduleMap.get(modName.toLowerCase());
    }

    public void registerModules(Module... modules) {
        for (Module module : modules) {
            for (final Field field : module.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    final Object obj = field.get(module);
                    if (obj instanceof Value) module.getValues().add((Value<?>) obj);
                } catch (IllegalAccessException e) {
                    Pride.LOGGER.log(Level.ERROR, "ERROR: ", e);
                }
            }
            moduleMap.put(module.getModuleName().toLowerCase(), module);
        }
    }

    public List<Module> getCategoryModules(Module.Category category) {
        return getModuleMap().values().stream().filter((module -> module.getCategory() == category)).collect(Collectors.toList());
    }

}

package nl.tomax.packtoggle;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Instellingen, opgeslagen in .minecraft/config/tomaxpacktoggle.properties */
public final class TomaxConfig {

    public static boolean hitboxEnabled = true;
    public static float width = 4.0F;
    /** true = gewone Minecraft hitbox (geen pack-kleur, normale dikte, pijl zichtbaar) */
    public static boolean vanillaHitbox = false;

    public static final float MIN_WIDTH = 0.5F;
    public static final float MAX_WIDTH = 10.0F;

    private TomaxConfig() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("tomaxpacktoggle.properties");
    }

    public static float clampWidth(float w) {
        w = Math.round(w * 2F) / 2F;
        return Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, w));
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (InputStream in = Files.newInputStream(f)) {
            Properties p = new Properties();
            p.load(in);
            hitboxEnabled = Boolean.parseBoolean(p.getProperty("hitboxEnabled", "true"));
            vanillaHitbox = Boolean.parseBoolean(p.getProperty("vanillaHitbox", "false"));
            width = clampWidth(Float.parseFloat(p.getProperty("width", "4.0")));
        } catch (IOException | NumberFormatException ignored) {
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("hitboxEnabled", Boolean.toString(hitboxEnabled));
        p.setProperty("vanillaHitbox", Boolean.toString(vanillaHitbox));
        p.setProperty("width", Float.toString(width));
        try (OutputStream out = Files.newOutputStream(file())) {
            p.store(out, "Tomax Pack Toggle");
        } catch (IOException ignored) {
        }
    }
}

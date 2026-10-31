package nl.tomax.packtoggle;

import net.minecraft.client.Minecraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Bepaalt de hitbox-kleur aan de hand van welke lTomaxMC pack actief is.
 * Pas hieronder de RGB-waarden (0.0 - 1.0) aan als je een kleur anders wilt.
 */
public final class PackHitboxColor {

    private record Entry(Pattern pattern, float[] rgb) {}

    private static final Logger LOGGER = LoggerFactory.getLogger("tomaxpacktoggle");
    private static String lastLogged = null;

    private static final Map<String, Entry> COLORS = new LinkedHashMap<>();

    private static void add(String name, float r, float g, float b) {
        COLORS.put(name, new Entry(
                Pattern.compile("(?<![a-z])" + name + "(?![a-z])"),
                new float[]{r, g, b}));
    }

    static {
        add("purple", 0.65f, 0.25f, 1.00f);
        add("orange", 1.00f, 0.55f, 0.00f);
        add("gucci",  0.00f, 0.55f, 0.28f);
        add("green",  0.20f, 1.00f, 0.20f);
        add("blue",   0.20f, 0.40f, 1.00f);
        add("red",    1.00f, 0.15f, 0.15f);
        add("pink",   1.00f, 0.40f, 0.75f);
        add("yellow", 1.00f, 0.90f, 0.10f);
    }

    private PackHitboxColor() {}

    /** RGB van de actieve Tomax pack, of null als er geen actief is. Bij meerdere wint de bovenste. */
    public static float[] current() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getResourcePackRepository() == null) return null;

        float[] result = null;
        String found = null;
        for (String id : mc.getResourcePackRepository().getSelectedIds()) {
            String lower = id.toLowerCase(Locale.ROOT);
            if (!lower.contains("tomaxmc")) continue;
            for (Entry e : COLORS.values()) {
                if (e.pattern().matcher(lower).find()) {
                    result = e.rgb();
                    found = id;
                }
            }
        }
        if (!java.util.Objects.equals(found, lastLogged)) {
            lastLogged = found;
            LOGGER.info("Hitbox-kleur pack: {}", found == null ? "geen" : found);
        }
        return result;
    }
}

package nl.tomax.packtoggle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

    /** Lijndikte van de hitbox in pixels. Pas dit getal aan als je hem dikker/dunner wilt. */
    public static final float WIDTH = 4.0F;

    /** De entity waarvan de hitbox op dit moment getekend wordt (render thread). */
    public static Entity currentEntity;

    /** True als jij binnen aanvalsbereik (reach) van deze andere speler staat. */
    public static boolean inReach(Entity e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer me = mc.player;
        if (me == null || e == null || e == me || !(e instanceof Player)) return false;
        Vec3 eye = me.getEyePosition();
        AABB b = e.getBoundingBox();
        double dx = Math.max(Math.max(b.minX - eye.x, 0.0), eye.x - b.maxX);
        double dy = Math.max(Math.max(b.minY - eye.y, 0.0), eye.y - b.maxY);
        double dz = Math.max(Math.max(b.minZ - eye.z, 0.0), eye.z - b.maxZ);
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return dist <= me.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
    }

    /**
     * Bepaalt de kleur van een hitbox-lijn.
     * Binnen reach van een tegenstander: hoofd-hitbox wit. Anders: kleur van de actieve pack.
     * Het hoofd (rode ooghoogte-box) krijgt altijd de pack-kleur.
     */
    public static int styleColor(int color) {
        boolean white = color == 0xFFFFFFFF;
        boolean eye = color == 0xFFFF0000;
        if (!white && !eye) return color;
        if (white && inReach(currentEntity)) return 0xFFFFFFFF;
        int argb = currentArgb();
        return argb != 0 ? argb : color;
    }

    /** Zelfde als current(), maar als ARGB-int (0xAARRGGBB). Geeft 0 als er geen pack actief is. */
    public static int currentArgb() {
        float[] c = current();
        if (c == null) return 0;
        int r = Math.round(c[0] * 255F) & 0xFF;
        int g = Math.round(c[1] * 255F) & 0xFF;
        int b = Math.round(c[2] * 255F) & 0xFF;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

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

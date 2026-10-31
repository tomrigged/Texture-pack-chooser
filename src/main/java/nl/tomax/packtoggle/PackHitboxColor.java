package nl.tomax.packtoggle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
        add("grey",   0.65f, 0.65f, 0.65f);
        add("gray",   0.65f, 0.65f, 0.65f);
    }

    private PackHitboxColor() {}

    /** De entity waarvan de hitbox op dit moment getekend wordt (render thread). */
    public static Entity currentEntity;

    /**
     * True alleen als je deze entity (speler of mob) nu echt kunt raken:
     * je crosshair staat op hem EN hij zit binnen reach (dit is Minecrafts eigen doelwit-check).
     */
    public static boolean inReach(Entity e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer me = mc.player;
        if (me == null || e == null || e == me) return false;
        HitResult hit = mc.hitResult;
        return hit instanceof EntityHitResult ehr
                && hit.getType() == HitResult.Type.ENTITY
                && ehr.getEntity() == e;
    }

    /**
     * Bepaalt de kleur van een hitbox-lijn.
     * Als je een tegenstander kunt raken (crosshair erop en binnen reach): hoofd-hitbox wit. Anders: kleur van de actieve pack.
     * Dit geldt voor zowel de hitbox als het hoofd (rode ooghoogte-box).
     */
    public static int styleColor(int color) {
        boolean white = color == 0xFFFFFFFF;
        boolean eye = color == 0xFFFF0000;
        if (!white && !eye) return color;
        if (inReach(currentEntity)) return 0xFFFFFFFF; // hitbox en hoofd wit als je hem kunt raken
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

package nl.tomax.packtoggle;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Menu met alle lTomaxMC packs uit de resourcepacks-map.
 * Klik op een pack om hem aan/uit te zetten, daarna "Toepassen".
 */
public class PackToggleScreen extends Screen {

    private static final String NAME_FILTER = "tomaxmc";

    private final Screen parent;
    private final List<String> packIds = new ArrayList<>();
    private final Set<String> enabled = new LinkedHashSet<>();
    private PackRepository repo;

    public PackToggleScreen(Screen parent) {
        super(Component.literal("Tomax Texture Packs"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();
        repo = mc.getResourcePackRepository();

        // Herlaad de lijst zodat nieuw toegevoegde zips ook verschijnen
        repo.reload();

        packIds.clear();
        enabled.clear();
        for (Pack pack : repo.getAvailablePacks()) {
            String id = pack.getId();
            if (id.startsWith("file/") && id.toLowerCase(Locale.ROOT).contains(NAME_FILTER)) {
                packIds.add(id);
            }
        }
        packIds.sort(String.CASE_INSENSITIVE_ORDER);
        for (String id : repo.getSelectedIds()) {
            if (packIds.contains(id)) enabled.add(id);
        }

        int colW = 170;
        int gap = 10;
        int rowH = 24;
        int left = this.width / 2 - colW - gap / 2;
        int right = this.width / 2 + gap / 2;
        int top = 40;
        int perCol = (packIds.size() + 1) / 2;

        for (int i = 0; i < packIds.size(); i++) {
            final String id = packIds.get(i);
            int col = i / Math.max(perCol, 1);
            int row = i % Math.max(perCol, 1);
            int x = col == 0 ? left : right;
            int y = top + row * rowH;

            Button btn = Button.builder(label(id), b -> {
                if (!enabled.remove(id)) enabled.add(id);
                b.setMessage(label(id));
            }).bounds(x, y, colW, 20).build();
            this.addRenderableWidget(btn);
        }

        int bottomY = Math.min(this.height - 28, top + perCol * rowH + 16);
        this.addRenderableWidget(Button.builder(Component.literal("Toepassen"), b -> {
            apply();
            this.onClose();
        }).bounds(this.width / 2 - colW - gap / 2, bottomY, colW, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Annuleren"), b -> this.onClose())
                .bounds(this.width / 2 + gap / 2, bottomY, colW, 20).build());
    }

    private Component label(String id) {
        String name = id.substring("file/".length());
        if (name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            name = name.substring(0, name.length() - 4);
        }
        // Verkort de naam een beetje
        name = name.replaceAll("(?i)^[0-9a-z]_", "").replace('_', ' ').trim();
        boolean on = enabled.contains(id);
        MutableComponent state = Component.literal(on ? "AAN" : "UIT")
                .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED);
        return Component.literal(name + ": ").append(state);
    }

    private void apply() {
        Minecraft mc = Minecraft.getInstance();

        List<String> selected = new ArrayList<>(repo.getSelectedIds());
        boolean changed = false;

        // Haal alle Tomax packs eruit en voeg de gekozen packs bovenaan toe
        List<String> before = new ArrayList<>(selected);
        selected.removeIf(packIds::contains);
        selected.addAll(enabled);

        if (!selected.equals(before)) changed = true;
        if (!changed) return;

        repo.setSelected(selected);
        // Slaat op in options.txt en herlaadt de resources
        mc.options.updateResourcePacks(repo);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        if (packIds.isEmpty()) {
            g.drawCenteredString(this.font,
                    Component.literal("Geen lTomaxMC packs gevonden in je resourcepacks-map."),
                    this.width / 2, this.height / 2 - 10, 0xFFFF5555);
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}

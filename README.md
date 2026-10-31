# Tomax Pack Toggle (Fabric, Minecraft 1.21.11)

Druk in-game op **Right Shift** → menu met al je lTomaxMC packs → klik om aan/uit te zetten → **Toepassen**.
De toets is aanpasbaar bij Opties → Besturing → "Tomax Pack Toggle".

## Packs installeren
De packs zitten NIET in de mod (elk is ~200 MB). Zet de zip-bestanden in `.minecraft/resourcepacks/`.
Elke zip met `tomaxmc` in de naam verschijnt automatisch in het menu.

Packs hebben ook nodig: Fabric API, Celestial, Respackopts (voor de skybox / opties).

## Bouwen via GitHub
1. Maak een nieuwe repo, upload alle bestanden uit deze map (incl. `.github`).
2. Tab **Actions** → "Build mod" → na het draaien: download artifact `tomax-pack-toggle`.
3. Jar in `.minecraft/mods/`.

## Hitbox-kleur
Zodra een lTomaxMC pack actief is, krijgt de hitbox (F3 + B) de kleur van die pack
(orange = oranje, blue = blauw, enz.). Kleuren aanpassen: `PackHitboxColor.java`.
De rode ooghoogte-lijn en de blauwe kijkrichting-lijn blijven vanilla.

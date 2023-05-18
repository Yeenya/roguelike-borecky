package cz.cuni.gamedev.nail123.roguelike.world.worlds;

import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Rat;
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Stairs;
import cz.cuni.gamedev.nail123.roguelike.events.LoggedEvent;
import cz.cuni.gamedev.nail123.roguelike.world.Area;
import cz.cuni.gamedev.nail123.roguelike.world.World;
import cz.cuni.gamedev.nail123.roguelike.world.builders.AreaBuilder;
import cz.cuni.gamedev.nail123.roguelike.world.builders.EmptyAreaBuilder;
import org.hexworks.zircon.api.data.Position3D;
import org.hexworks.zircon.api.data.Size3D;
import org.jetbrains.annotations.NotNull;

public class SampleJavaWorld extends World {
    int currentLevel = 0;

    public SampleJavaWorld() {
    }

    @NotNull
    @Override
    public Area buildStartingArea() {
        return buildLevel();
    }

    Area buildLevel() {
        // Start with an empty area
        AreaBuilder areaBuilder = (new EmptyAreaBuilder()).create();

        // Place the player at an empty location in the top-left quarter
        areaBuilder.addAtEmptyPosition(
                areaBuilder.getPlayer(),
                Position3D.create(1, 1, 0),
                Size3D.create(areaBuilder.getWidth() / 2 - 2, areaBuilder.getHeight() / 2 - 2, 1)
        );
        // Place the stairs at an empty location in the top-right quarter
        areaBuilder.addAtEmptyPosition(
                new Stairs(),
                Position3D.create(areaBuilder.getWidth() / 2, areaBuilder.getHeight() / 2, 0),
                Size3D.create(areaBuilder.getWidth() / 2 - 2, areaBuilder.getHeight() / 2 - 2, 1)
        );

        for (int i = 0; i <= currentLevel; ++i) {
            areaBuilder.addAtEmptyPosition(new Rat(), Position3D.defaultPosition(), areaBuilder.getSize());
        }

        // Build it into a full Area
        return areaBuilder.build();
    }

    /**
     * Moving down - goes to a brand new level.
     */
    @Override
    public void moveDown() {
        ++currentLevel;
        (new LoggedEvent(this, "Descended to level " + (currentLevel + 1))).emit();
        if (currentLevel >= getAreas().getSize()) getAreas().add(buildLevel());
        goToArea(getAreas().get(currentLevel));
    }

    /**
     * Moving up would be for revisiting past levels, we do not need that. Check [DungeonWorld] for an implementation.
     */
    @Override
    public void moveUp() {
        // Not implemented
    }
}

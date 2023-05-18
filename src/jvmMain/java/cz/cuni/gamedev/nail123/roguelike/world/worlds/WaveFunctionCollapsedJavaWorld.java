package cz.cuni.gamedev.nail123.roguelike.world.worlds;

import cz.cuni.gamedev.nail123.roguelike.GameConfig;
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Stairs;
import cz.cuni.gamedev.nail123.roguelike.mechanics.Pathfinding;
import cz.cuni.gamedev.nail123.roguelike.world.Area;
import cz.cuni.gamedev.nail123.roguelike.world.builders.AreaBuilder;
import cz.cuni.gamedev.nail123.roguelike.world.builders.wavefunctioncollapse.WFCAreaBuilder;
import cz.cuni.gamedev.nail123.roguelike.world.worlds.DungeonWorld;
import kotlin.random.Random;
import org.hexworks.zircon.api.data.Position3D;
import org.hexworks.zircon.api.data.Size3D;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WaveFunctionCollapsedJavaWorld extends DungeonWorld {
    @NotNull
    @Override
    public Area buildLevel(int floor) {
        Size3D areaSize = Size3D.create(
                GameConfig.WINDOW_WIDTH - GameConfig.SIDEBAR_WIDTH,
                GameConfig.WINDOW_HEIGHT - GameConfig.LOG_AREA_HEIGHT, 1
        );

        AreaBuilder area = (new WFCAreaBuilder(areaSize, areaSize)).create();

        area.addAtEmptyPosition(
                area.getPlayer(),
                Position3D.create(0, 0, 0),
                areaSize
        );

        // Add stairs up
        if (floor > 0) area.addEntity(new Stairs(false), area.getPlayer().getPosition());

        // Add stairs down
        Map<Position3D, Integer> floodFill = Pathfinding.INSTANCE.floodFill(
                area.getPlayer().getPosition(),
                area,
                Pathfinding.INSTANCE.getEightDirectional(),
                Pathfinding.INSTANCE.getDoorOpening()
        );

        Position3D[] exitPositions = {};
        exitPositions = floodFill.keySet().toArray(exitPositions);

        Position3D staircasePosition = exitPositions[Random.Default.nextInt(0, exitPositions.length)];

        area.addEntity(new Stairs(), staircasePosition);

        return area.build();
    }
}

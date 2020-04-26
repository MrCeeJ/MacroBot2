package com.mrceej.sc2.macrobot2.things;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macrobot2.MacroBot2;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import static com.github.ocraft.s2client.protocol.data.Units.*;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_LAIR;

@Slf4j
public class PoolFirstExpandBuildOrder extends BuildOrder {

    public PoolFirstExpandBuildOrder(MacroBot2 agent) {
        super(agent);
        buildOrderEntries = new ArrayList<>();
        buildOrderEntries.add(new BuildOrderEntry(1, 14, ZERG_SPAWNING_POOL, 1));
        buildOrderEntries.add(new BuildOrderEntry(2, 15, ZERG_EXTRACTOR, 1));
        buildOrderEntries.add(new BuildOrderEntry(3, 16, ZERG_HATCHERY, 2));
        buildOrderEntries.add(new BuildOrderEntry(4, 18, ZERG_EXTRACTOR, 2));
        buildOrderEntries.add(new BuildOrderEntry(5, 20, ZERG_ROACH_WARREN, 1));
        buildOrderEntries.add(new BuildOrderEntry(6, 25, ZERG_EVOLUTION_CHAMBER, 2));
        buildOrderEntries.add(new BuildOrderEntry(7, 30, ZERG_LAIR, 1));
        buildSteps = buildOrderEntries.size();
    }
    @Override
    protected Units getNextProductionItem(int workers, int minerals, int gas) {
        for (BuildOrderEntry entry : buildOrderEntries) {
            if (haveSufficientWorkers(workers, entry)) {
                if (needAdditionalUnits(entry)){
                    return entry.getUnit();
                }
            }
        }
        return null;
    }

    @Override
    protected Units getDefaultConstruction() {
        return Units.ZERG_DRONE;
    }
}

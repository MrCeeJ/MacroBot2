package com.mrceej.sc2.macrobot2.things;

import com.mrceej.sc2.macrobot2.MacroBot2;

import java.util.ArrayList;

import static com.github.ocraft.s2client.protocol.data.Units.*;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_LAIR;

public class PoolFirstExpandBuildOrder extends BuildOrder {

    public PoolFirstExpandBuildOrder(MacroBot2 agent) {
        super(agent);
        buildOrderEntries = new ArrayList<>();
        buildOrderEntries.add(new BuildOrderEntry(1, 14, ZERG_SPAWNING_POOL, true));
        buildOrderEntries.add(new BuildOrderEntry(2, 20, ZERG_ROACH_WARREN, true));
        buildOrderEntries.add(new BuildOrderEntry(3, 25, ZERG_EVOLUTION_CHAMBER, true));
        buildOrderEntries.add(new BuildOrderEntry(4, 30, ZERG_LAIR, true));
        buildSteps = buildOrderEntries.size();
    }
}

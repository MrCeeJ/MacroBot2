package com.mrceej.sc2.macrobot2.things;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macrobot2.Data;
import com.mrceej.sc2.macrobot2.MacroBot2;
import com.mrceej.sc2.macrobot2.Utils;
import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class BuildOrder {

    private final MacroBot2 agent;
    private final Utils utils;
    private final Data data;
    @Getter
    ArrayList<BuildOrderEntry> buildOrderEntries;
    @Getter
    int buildSteps;
    String name;

    public BuildOrder(MacroBot2 agent) {
        this.agent = agent;
        this.utils = agent.getUtils();
        this.data = agent.getData();

    }
    public BuildOrderEntry getBuildOrderEntry(int entry) {
        if (entry <= buildSteps ) {
            return buildOrderEntries.get(entry);
        } else {
            log.error("Asked for non-existant build order entry step:"+name+" - "+entry);
            return null;
        }
    }

    public Units getNextProductionItem() {
        int workers = data.getWorkers().size();
        int minerals = data.getMinerals();
        int gas = data.getGas();
        Units nextItem = getNextProductionItem(workers, minerals, gas);
        if (nextItem != null) {
            return nextItem;
        }
        return getDefaultConstruction();

    }

    private Units getNextProductionItem(int workers, int minerals, int gas) {
        log.warn("Method should be overridden!");
        return null;
    }

    public Units getDefaultConstruction() {
        return Units.ZERG_DRONE;
    }
}

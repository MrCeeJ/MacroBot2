package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macrobot2.things.Base;

public class Military extends CeejBotComponent {

    private Debugger debugger;
    private Utils utils;
    private Data data;

    public Military(MacroBot2 agent) {
        super(agent);
    }

    @Override
    public void init() {
        this.debugger = agent.getDebugger();
        this.utils = agent.getUtils();
        this.data = agent.getData();
    }

    @Override
    public void update() {

    }

    @Override
    public void debug() {
        debugUnits();
    }

    private void debugUnits() {

    }


    public void onUnitCreated(UnitInPool unit) {
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_HATCHERY:
                data.addBase(new Base(agent, utils, unit));
            case ZERG_DRONE:
                data.getNearestBase(unit).allocateWorker(unit);
        }
    }
}

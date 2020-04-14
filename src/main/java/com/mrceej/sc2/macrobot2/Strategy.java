package com.mrceej.sc2.macrobot2;

public class Strategy extends CeejBotComponent {

    private Debugger debugger;
    private Data data;
    private Intel intel;
    private Production production;

    public Strategy(MacroBot2 agent) {
        super(agent);
    }

    private Utils utils;

    @Override
    public void init() {
        this.debugger = agent.getDebugger();
        this.utils = agent.getUtils();
        this.data = agent.getData();
        this.intel = agent.getIntel();
        this.production = agent.getProduction();
    }

    @Override
    public void update() {
        updateProduction();
    }

    private void updateProduction() {
        switch (data.getGameAge()) {
            case EARLY:
                switch (intel.getGameState()) {
                    case CHEESE:
                    case NORMAL:
                    case UNKNOWN:
                    default:
                        production.updateBuild("DEFAULT");
                }
                //Starting Build
            case MID:
            case LATE:
            default:
                production.updateBuild("DEFAULT");


        }


    }

    @Override
    public void debug() {

    }
}

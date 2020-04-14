package com.mrceej.sc2.macrobot2;

import com.mrceej.sc2.macrobot2.things.EnemyStrategy;

public class Intel extends CeejBotComponent {

    private Debugger debugger;
    private Utils utils;
    private Data data;
    private EnemyStrategy enemyStrategy;

    public Intel(MacroBot2 agent) {
        super(agent);
        this.enemyStrategy = EnemyStrategy.UNKNOWN;
    }

    @Override
    public void init() {
        this.debugger = agent.getDebugger();
        this.utils = agent.getUtils();
        this.data = agent.getData();
    }

    @Override
    public void update() {
        updateEnemyStrategy();
    }

    private void updateEnemyStrategy() {
        this.enemyStrategy = EnemyStrategy.UNKNOWN;
    }

    @Override
    public void debug() {

    }

    public EnemyStrategy getGameState() {
       return enemyStrategy;
    }

}

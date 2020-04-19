package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.ClientError;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.mrceej.sc2.CeejBot;
import com.github.ocraft.s2client.bot.setting.PlayerSettings;
import com.github.ocraft.s2client.protocol.game.Race;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MacroBot2 extends CeejBot {

    private static final boolean DEBUG_ENABLED = true;

    @Getter
    private final Debugger debugger;
    @Getter
    private final Utils utils;
    @Getter
    private final Production production;
    @Getter
    private final BuildUtils buildUtils;
    @Getter
    private Data data;
    @Getter
    private Intel intel;
    @Getter
    private Strategy strategy;
    @Getter
    private Military military;

    public MacroBot2(PlayerSettings opponent) {
        super(opponent, Race.ZERG);
        this.utils = new Utils(this);
        this.buildUtils = new BuildUtils(this);
        this.debugger = new Debugger(this, DEBUG_ENABLED);
        this.data = new Data(this);
        this.intel = new Intel(this);
        this.strategy = new Strategy(this);
        this.military = new Military(this);
        this.production = new Production(this);
    }

    private void init() {
        buildUtils.init();
        data.init();
        intel.init();
        strategy.init();
        military.init();
        production.init();
    }

    private void runAI() {
        data.update();
        intel.update();
        strategy.update();
        military.update();
        production.update();


        if (DEBUG_ENABLED) {
            if (data.getCurrentStep() < 500) {
                debugger.debugAlert("Hello Starcraft II bots! MacroBot2 here!");
            }
            data.debug();
            intel.debug();
            strategy.debug();
            military.debug();
            production.debug();
            debugger.sendDebug();
        }
    }

    @Override
    public void onGameStart() {
        init();
    }


    @Override
    public void onStep() {
        runAI();
    }


    @Override
    public void onUnitCreated(UnitInPool unit) {
        data.onUnitCreated(unit);
        military.onUnitCreated(unit);
        production.onUnitCreated(unit);
    }

    @Override
    public void onBuildingConstructionComplete(UnitInPool unit) {
//        unitManager.onBuildingComplete(unit);
//        buildManager.onUnitComplete(unit);
    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool) {
//        unitManager.onUnitIdle(unitInPool);
    }

    @Override
    public void onError(List<ClientError> clientErrors, List<String> protocolErrors) {
        clientErrors.forEach(log::error);
        protocolErrors.forEach(log::error);
    }

    @Override
    public void onUnitEnterVision(UnitInPool unit) {
//        if (unit.unit().getAlliance().equals(Alliance.ENEMY)) {
//            adviser.enemySpotted(unit);
//        }
    }

    @Override
    public void onGameFullStart() {
    }

    @Override
    public void onGameEnd() {
    }

    @Override
    public void onUnitDestroyed(UnitInPool unit) {
        // TODO: Check if this should be based of tags as unit.unit() might not work.
//        if (unit.unit().getAlliance().equals(Alliance.ENEMY)) {
//            adviser.enemyDestroyed(unit);
//        } else {
//            unitManager.onUnitDestroyed(unit);
//        }
    }

    @Override
    public void onNydusDetected() {
    }

    @Override
    public void onNuclearLaunchDetected() {
    }
}

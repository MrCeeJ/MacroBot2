package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.mrceej.sc2.macrobot2.things.BuildOrder;
import com.mrceej.sc2.macrobot2.things.BuildRequest;
import io.vertx.core.net.impl.VertxEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.ocraft.s2client.protocol.data.Units.*;
import static java.lang.Math.max;

@Slf4j
public class Production extends CeejBotComponent {
    private Data data;
    private Debugger debugger;
    private Utils utils;
    private String currentBuildName;
    private int currentBuildStep;
    private BuildOrder currentBuild;
    private LinkedList<BuildRequest> buildRequests;
    private BuildUtils buildUtils;
    private ArrayList<BuildRequest> buildRequestsInProgress;

    public Production(MacroBot2 macroBot2) {
        super(macroBot2);
        currentBuildName = "";
        buildRequests = new LinkedList<>();
        currentBuildStep = 0;
        currentBuild = null;
    }

    @Override
    public void init() {
        this.debugger = agent.getDebugger();
        this.utils = agent.getUtils();
        this.data = agent.getData();
        this.buildUtils = agent.getBuildUtils();
    }

    @Override
    public void update() {
        processBuild();
    }


    private void processBuild() {
        if (needSupply()) {
            if (buildUtils.checkCanMakeUnit(ZERG_OVERLORD, data.getMinerals(), data.getGas())) {
                queueConstruction(new BuildRequest(ZERG_OVERLORD));
            }
//            else {
//                log.info("Need more overlords, but not enough minerals:" + data.getMinerals() + "/" + buildUtils.getMineralCost(ZERG_OVERLORD));
//            }
        } else {
            Units unit = currentBuild.getNextProductionItem();
            if (unit != null && buildUtils.checkCanMakeUnit(unit, data.getMinerals(), data.getGas())) {
                queueConstruction(new BuildRequest(unit));
            }
        }
    }

    private void queueConstruction(BuildRequest request) {
        log.info("Queuing request for a :" + request.getUnit());
        String queue = getQueueAsString();
        log.info("Current queue :[" + queue + "]");

        if (buildRequests.contains(request)) {
            log.warn("Already building :" + request);
        } else {
            buildRequests.add(request);
            boolean successfulBuild = buildUtils.build(request.getUnit());
            if (!successfulBuild) {
                log.info("Failed to build :"+request);
            }
        }
    }

    private String getQueueAsString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BuildRequest b : buildRequests) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(b.getUnit());
        }
        return sb.toString();
    }

    private boolean needSupply() {
        int eggs = data.getAllUnitsOfType(ZERG_LARVA).size();
        int bases = data.getBases().size();
        int realBuffer = (eggs + bases) * 2;
        int defaultBuffer = agent.observation().getFoodUsed() / 6;
        int supplyInProduction = data.getUnitsInProduction(ZERG_OVERLORD).size() * 8;

        return data.getSupplyCap() < 200 &&
                data.getSupplyCap() + supplyInProduction < data.getSupplyUsed() + max(realBuffer, defaultBuffer);
    }

    @Override
    public void debug() {

    }

    void updateBuild(String newBuildName) {
        if (currentBuild == null || !currentBuildName.equals(newBuildName)) {
            log.info("Switching to new build :" + newBuildName);
            currentBuild = data.getBuild(newBuildName);
            currentBuildName = newBuildName;
        }
    }

    public int getCountOfPlannedProductionOfType(Units unit) {
        int count = 0;
        for (BuildRequest request : buildRequests) {
            if (request.getUnit() == unit)
                count += 1;
        }
        return count;
    }

    public void onUnitCreated( UnitInPool unit) {
        Optional<Unit> unitOptional = unit.getUnit();
        if (unitOptional.isPresent()) {
            Units type = (Units) unitOptional.get().getType();
            if (type == ZERG_LARVA) {
                return;
            }
            BuildRequest request = null;
            for (BuildRequest b : buildRequests) {
                if (b.getUnit() == type) {
                    request = b;
                    break;
                }
            }
            if (request != null) {
                buildRequests.remove(request);
                log.info("Unit created successfuly, removing request :" + unit);
            } else {
                if (data.getCurrentStep() < 10) {
                    log.info("Starting unit found :" + unitOptional.get().getType() + " - " + unit.getTag());
                }
                log.info("unit created that was not found in requests :" + unit);
            }
        }
    }
}

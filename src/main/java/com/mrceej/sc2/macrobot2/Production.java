package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macrobot2.things.BuildOrder;
import com.mrceej.sc2.macrobot2.things.BuildRequest;
import io.vertx.core.net.impl.VertxEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        processBuildRequests();
    }

    private void processBuild() {
        if (needSupply()) {
            if (buildUtils.checkCanMakeUnit(ZERG_OVERLORD, data.getMinerals(), data.getGas())) {
                queueConstruction(new BuildRequest(ZERG_OVERLORD));
            } else {
                //log.info("Need more overlords, but not enough minerals:" + data.getMinerals() + "/" + buildUtils.getMineralCost(ZERG_OVERLORD));
            }
        } else {
            Units unit = currentBuild.getNextProductionItem();
            if (unit != null && buildUtils.checkCanMakeUnit(unit, data.getMinerals(), data.getGas())) {
                queueConstruction(new BuildRequest(unit));
            }
        }
    }


    private void processBuildRequests() {
        BuildRequest request = buildRequests.poll();
        while (request != null) {
            log.info("Handling request for a :" + request.getUnit());
            buildUtils.build(request.getUnit());
            request = buildRequests.poll();
        }
//        log.info("All requests handled, queue size :" + buildRequests.size());
    }

    private void queueConstruction(BuildRequest request) {
        log.info("Queuing request for a :" + request.getUnit());
        if (buildRequests.contains(request)) {
            log.warn("Already building :" + request);
        } else {
            buildRequests.add(request);
        }
    }

    private boolean needSupply() {

        int eggs = data.getAllUnitsOfType(ZERG_LARVA).size();
        int bases = data.getBases().size();
        int realBuffer = (eggs + bases) * 2;
        int defaultBuffer = agent.observation().getFoodUsed() / 6;
        int supplyInProduction = data.getUnitsInProduction(ZERG_OVERLORD).size() * 8;

        if (data.getSupplyCap() < 200 &&
                data.getSupplyCap() + supplyInProduction < data.getSupplyUsed() + max(realBuffer, defaultBuffer)) {
            return true;
        }
        return false;
    }

    @Override
    public void debug() {

    }

    void updateBuild(String newBuildName) {
        if (currentBuild != null && currentBuildName.equals(newBuildName)) {
            return;
        } else {
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
}

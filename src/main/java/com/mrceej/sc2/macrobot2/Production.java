package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macrobot2.things.BuildOrder;
import com.mrceej.sc2.macrobot2.things.BuildRequest;
import io.vertx.core.net.impl.VertxEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
        processCurrentBuild();
        processBuildRequests();
    }

    /*
        1) If we need supply, build it. If we can't afford it wait
        2) If we have a build order item, build it. If we can't afford it wait
        3) Build the Default unit(s) of the build order
     */
    private void processCurrentBuild() {

        if (needSupply()) {
            if (buildUtils.checkCanMakeUnit(ZERG_OVERLORD, data.getMinerals(), data.getGas())) {
                queueConstruction(new BuildRequest(ZERG_OVERLORD));
            } else {
                //log.info("Need more overlords, but not enough minerals:" + data.getMinerals() + "/" + buildUtils.getMineralCost(ZERG_OVERLORD));
            }
        } else if (checkBuild()) { //TODO: Needs to hit this every time in case no funds
            Units unit = currentBuild.getBuildOrderEntry(currentBuildStep).getUnit();
            if (buildUtils.checkCanMakeUnit(unit, data.getMinerals(), data.getGas())) {
                queueConstruction(new BuildRequest(unit));
            } else {
                log.info("Unable to build a :" + unit);
            }
        } else if (checkExpansion()) {
            if (data.getMinerals() >= 300) {
                // Check expansion is not already in progress
                queueConstruction(new BuildRequest(ZERG_HATCHERY));
            } else {
                log.info("Need an expansion, but not enough minerals:" + data.getMinerals());
            }
        } else {
            if (currentBuild != null) {
                Units defaultUnits = currentBuild.getDefaultConstruction();
                if (buildUtils.checkCanMakeUnit(defaultUnits, data.getMinerals(), data.getGas())) {
                    log.info("Queuing default unit:" + defaultUnits);
                    queueConstruction(new BuildRequest(defaultUnits));
                }
            } else {
                if (buildUtils.checkCanMakeUnit(ZERG_DRONE, data.getMinerals(), data.getGas())) {
                    queueConstruction(new BuildRequest(ZERG_DRONE));
                    log.info("Queuing emergency backup Drone");
                }
            }
        }
    }

    private void processBuildRequests() {
        BuildRequest request = buildRequests.poll();
        while (request != null) {
            buildUtils.build(request.getUnit());
            request = buildRequests.poll();
        }
    }


    private boolean checkExpansion() {
        return false;
    }

    private void queueConstruction(BuildRequest request) {
        log.info("Queing request for a :" + request.getUnit());
        if (buildRequests.contains(request)) {
            log.warn("Already building :" + request);
        } else {
            buildRequests.add(request);
        }
    }

    private boolean checkBuild() { // TODO: Check if we have the build step items, rather than just doing them once.
        if (currentBuild == null) {
            log.warn("No build selected!");
            return false;
        }
        if (currentBuildStep < currentBuild.getBuildSteps()) {
            int nextStep = currentBuildStep + 1;
            if (data.getWorkers().size() >= currentBuild.getBuildOrderEntry(nextStep).getWorkers()) {
                currentBuildStep = nextStep;
                return true;
            }
        }
        return false;
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
}

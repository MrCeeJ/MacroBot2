package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.mrceej.sc2.macrobot2.things.Base;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.github.ocraft.s2client.protocol.data.Units.*;

@Slf4j
public class BuildUtils extends CeejBotComponent {

    private Data data;
    private static final int PLACEMENT_DISTANCE = 30;
    private static final int LOCATION_MINIMUM_DISTANCE = 6;
    private Utils utils;
    private Random random;

    public BuildUtils(MacroBot2 agent) {
        super(agent);
    }

    @Override
    public void init() {
        this.data = agent.getData();
        this.utils = agent.getUtils();
        random = new Random();
    }

    @Override
    public void update() {

    }

    @Override
    public void debug() {

    }

    boolean checkCanMakeUnit(Units unit, int minerals, int gas) {
        return haveTechForUnit(unit) &&
                canAffordUnit(unit, minerals, gas) &&
                haveLarvaeIfNeeded(unit);
    }

    private boolean haveTechForUnit(Units unit) {
        List<Units> requirements = getRequirements(unit);
        for (Units req : requirements) {
            if (countOfBuilding(req) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canAffordUnit(Units unit, int minerals, int gas) {
        return getMineralCost(unit) <= minerals &&
                getGasCost(unit) <= gas;
    }

    private boolean haveLarvaeIfNeeded(Units unit) {
        switch (unit) {
            case ZERG_DRONE:
            case ZERG_ZERGLING:
            case ZERG_ROACH:
            case ZERG_HYDRALISK:
            case ZERG_MUTALISK:
            case ZERG_OVERLORD:
            case ZERG_CORRUPTOR:
            case ZERG_ULTRALISK:
            case ZERG_INFESTOR:
            case ZERG_SWARM_HOST_MP:
            case ZERG_VIPER:
                return data.getAllUnitsOfType(ZERG_LARVA).size() > 0;
            default:
                return true;
        }
    }

    private List<Units> getRequirements(Units unit) {
        switch (unit) {
            case ZERG_SPAWNING_POOL:
                return List.of(ZERG_HATCHERY);
            case ZERG_EVOLUTION_CHAMBER:
                return List.of(ZERG_HATCHERY);
            case ZERG_EXTRACTOR:
                return List.of();
            case ZERG_BANELING_NEST:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH_WARREN:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_HYDRALISK_DEN:
                return List.of(ZERG_LAIR);
            case ZERG_LURKER_DEN_MP:
                return List.of(ZERG_LAIR, ZERG_HYDRALISK_DEN);
            case ZERG_SPIRE:
                return List.of(ZERG_LAIR);
            case ZERG_LAIR:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_GREATER_SPIRE:
                return List.of(ZERG_HIVE, ZERG_SPIRE);
            case ZERG_ULTRALISK_CAVERN:
                return List.of(ZERG_HIVE);
            case ZERG_DRONE:
                return List.of(ZERG_HATCHERY);
            case ZERG_OVERLORD:
                return List.of(ZERG_HATCHERY);
            case ZERG_QUEEN:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ZERGLING:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL, ZERG_ROACH_WARREN);
            case ZERG_HYDRALISK:
                return List.of(ZERG_LAIR, ZERG_SPAWNING_POOL, ZERG_HYDRALISK_DEN);
            default:
                throw new UnsupportedOperationException("Sorry, I don't know how to make a :" + unit);
        }
    }

    public int getMineralCost(Units unit) {
        switch (unit) {
            case ZERG_EXTRACTOR:
                return 25;
            case ZERG_ZERGLING:
                return 50;
            case ZERG_BANELING_NEST:
            case ZERG_GREATER_SPIRE:
            case ZERG_HYDRALISK_DEN:
            case ZERG_LURKER_DEN_MP:
                return 100;
            case ZERG_EVOLUTION_CHAMBER:
            case ZERG_ULTRALISK_CAVERN:
            case ZERG_LAIR:
            case ZERG_NYDUS_NETWORK:
            case ZERG_ROACH_WARREN:
                return 150;
            case ZERG_SPAWNING_POOL:
            case ZERG_SPIRE:
            case ZERG_HIVE:
                return 200;
            default:
                return queryMineralCost(unit);
        }
    }

    public int getGasCost(Units unit) {
        switch (unit) {
            case ZERG_SPAWNING_POOL:
            case ZERG_EVOLUTION_CHAMBER:
            case ZERG_EXTRACTOR:
            case ZERG_ROACH_WARREN:
                return 0;
            case ZERG_BANELING_NEST:
                return 50;
            case ZERG_HYDRALISK_DEN:
            case ZERG_LAIR:
                return 100;
            case ZERG_LURKER_DEN_MP:
            case ZERG_HIVE:
            case ZERG_GREATER_SPIRE:
                return 150;
            case ZERG_SPIRE:
            case ZERG_NYDUS_NETWORK:
            case ZERG_ULTRALISK_CAVERN:
                return 200;
            default:
                return queryGasCost(unit);
        }
    }

    public int queryMineralCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getMineralCost().orElse(0);
    }

    public int queryGasCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getVespeneCost().orElse(0);
    }

    private int countOfBuilding(Units unit) {
        return data.getAllUnitsOfType(unit).size();
    }

    private boolean build(UnitType unit, Base base) {
        if (base == null) {
            return build(unit);
        }
        Units item = (Units) unit;
        switch (item) {
            case ZERG_QUEEN:
                return buildQueen(base);
            case ZERG_EXTRACTOR:
                return buildExtractor(base);
            case ZERG_LAIR:
                return upgradeToLair(base);
            case ZERG_HIVE:
            default:
                throw new UnsupportedOperationException("Sorry, don't know how to build " + item + " at :" + base);
        }
    }

    private boolean buildQueen(Base base) {
        Unit hatch = base.getBase().unit();
        agent.actions().unitCommand(hatch, Abilities.TRAIN_QUEEN, false);
        return true;
    }

    private boolean buildExtractor(Base base) {
        List<Unit> gasPositions = base.getGases().stream().map(UnitInPool::unit).collect(Collectors.toList());
        List<Point2d> extractorPositions = base.getExtractors().stream().map(u -> u.unit().getPosition().toPoint2d()).collect(Collectors.toList());
        if (gasPositions.size() > extractorPositions.size()) {
            for (Unit position : gasPositions) {
                if (!extractorPositions.contains(position.getPosition().toPoint2d())) {
                    return buildBuilding(ZERG_EXTRACTOR, position, base);
                }
            }
        }
        return false;
    }

    private boolean buildBuilding(UnitType unit, Unit target, Base base) {
        UnitInPool worker;
        if (base != null) {
            worker = base.getWorker();
        } else {
            List<UnitInPool> drones = data.getAllUnitsOfType(Units.ZERG_DRONE);
            worker = drones.get(0);
        }
        if (worker != null) {
            agent.actions().unitCommand(worker.unit(), getAbilityToMakeUnit(unit), target, false);
            return true;
        }
        log.warn("Warning, unable to build building :" + unit);
        return false;
    }

    private boolean buildBuilding(UnitType unit, Point2d location) {
        UnitInPool worker = data.getNearestWorker(location);
        if (worker != null) {
            agent.actions().unitCommand(worker.unit(), getAbilityToMakeUnit(unit), getRandomLocationNearLocationForStructure(unit, location), false);
            return true;
        }
        log.warn("Warning, unable to build building :" + unit + " built by :"+worker + " at location :"+location);
        return false;
    }

    private boolean upgradeToLair(Base base) {
        Unit hatch = base.getBase().unit();
//        if (buildingCounts.get(ZERG_LAIR) != null && buildingCounts.get(ZERG_LAIR) > 0) {
//            log.info("Already building a lair");
//            return false;
//        } else
            if (hatch.getType() == ZERG_LAIR) {
            log.info("Already have a lair. :" + base.getBase().unit().getType());
            return true;
        } else if (hatch.getType() != ZERG_HATCHERY) {
            log.info("Can't make a lair out of a :" + base.getBase().unit().getType());
            return false;
        } else if (hatch.getBuildProgress() < 1f) {
            log.info("Can't upgrade - base isn't finished building!");
            return false;
        } else {
//            buildingCounts.put(ZERG_LAIR, 1);
            agent.actions().unitCommand(hatch, Abilities.MORPH_LAIR, false);
            return true;
        }
    }

    private Point2d getRandomLocationNearLocationForStructure(UnitType structure, Point2d location) {
        Ability ability = getAbilityToMakeUnit(structure);
        Point2d newLocation;
        float dx;
        float dy;
        for (int tries = 0; tries < 1000; tries++) {
            dx = PLACEMENT_DISTANCE * (random.nextFloat() - 0.5f);
            dy = PLACEMENT_DISTANCE * (random.nextFloat() - 0.5f);
            newLocation = Point2d.of(location.getX() + dx, location.getY() + dy);
            if (location.distance(newLocation) > LOCATION_MINIMUM_DISTANCE) {
                if (agent.query().placement(ability, newLocation)) {
                    return newLocation;
                }
            }
        }
        log.warn("Warning, unable to place building!");
        return null;
    }

    public boolean build(UnitType unit) {
        Units item = (Units) unit;
        switch (item) {
            case ZERG_DRONE:
            case ZERG_OVERLORD:
            case ZERG_ZERGLING:
            case ZERG_ROACH:
            case ZERG_HYDRALISK:
            case ZERG_MUTALISK:
            case ZERG_ULTRALISK:
                return buildUnit(unit);
            case ZERG_HATCHERY:
                return buildHatchery();
            default:
                return buildAtLocation(item, agent.observation().getStartLocation().toPoint2d());
        }
    }

    private boolean buildUnit(UnitType unit) {
        if (!checkCanMakeUnit((Units) unit, data.getMinerals(), data.getGas())) {
            return false;
        }

        List<UnitInPool> larvae = data.getAllUnitsOfType(Units.ZERG_LARVA).stream()
                .filter(larva -> larva.unit().getOrders().size() == 0)
                .collect(Collectors.toList());

        if (larvae.size() > 0) {
//            incrementBuildingCount(unit);
            agent.actions().unitCommand(larvae.get(0).unit(), getAbilityToMakeUnit(unit), false);
            return true;
        }
        return false;
    }

    private boolean buildAtLocation(Units unit, Point2d location) {
        switch (unit) {
            case ZERG_SPAWNING_POOL:
            case ZERG_EVOLUTION_CHAMBER:
            case ZERG_ROACH_WARREN:
                return buildBuilding(unit, location);
            default:
                throw new UnsupportedOperationException("Sorry, don't know how to build " + unit);
        }
    }

    private boolean buildHatchery() {
//        if (buildingUnit(ZERG_HATCHERY)) {
//            log.info("Not placing hatchery, building " + buildingCounts.get(ZERG_HATCHERY) + " already.");
//        } else {
        Point2d location = data.getNearestExpansionLocationTo(agent.observation().getStartLocation().toPoint2d());
        UnitInPool unit = getNearestWorker(location);
        if (unit == null) {
            log.info("Insufficient free workers");
        } else if (agent.observation().getMinerals() < 300) {
            log.info("Insufficient minerals :" + agent.observation().getMinerals());
        } else {
            log.info("Drone " + unit.getTag() + " placing hatchery at :" + location);
//                unitManager.removeDroneFromBase(unit);
            agent.actions().unitCommand(unit.unit(), Abilities.BUILD_HATCHERY, location, false);
            return true;
        }
//        }
        return false;
    }

//    boolean buildingUnit(Units unit) {
//        return (buildingCounts.containsKey(unit) && buildingCounts.get(unit) > 0);
//    }


    Ability getAbilityToMakeUnit(UnitType unitType) {
        return agent.observation().getUnitTypeData(false).get(unitType).getAbility().orElse(Abilities.INVALID);
    }

    private UnitInPool getNearestWorker(Point2d location) {
        Base base = data.getNearestBaseWithWorkers(location);
        if (base != null) {
            return base.getWorker();
        } else {
            List<UnitInPool> drones = data.getAllUnitsOfType(ZERG_DRONE);
            if (drones.isEmpty())
                return null;
            else
                return data.getAllUnitsOfType(ZERG_DRONE).get(0);
        }
    }
}

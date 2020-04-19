package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;
import com.mrceej.sc2.macrobot2.things.Base;
import com.mrceej.sc2.macrobot2.things.BuildOrder;
import com.mrceej.sc2.macrobot2.things.GameAge;
import com.mrceej.sc2.macrobot2.things.PoolFirstExpandBuildOrder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_DRONE;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_EGG;

@Slf4j
public class Data extends CeejBotComponent {

    @Getter
    private int minerals;
    @Getter
    private int gas;
    @Getter
    private int supplyCap;
    @Getter
    private int supplyUsed;
    @Getter
    private long currentStep;
    @Getter
    private GameAge gameAge;
    @Getter
    private Map<Tag, UnitInPool> unitsInPool = new HashMap<>();
    @Getter
    private Map<Tag, Base> bases = new HashMap<>();

    private Map<String, BuildOrder> builds;

    private Debugger debugger;
    private Utils utils;
    private Data data;

    public Data(MacroBot2 agent) {
        super(agent);
        this.builds = new HashMap<>();
        minerals = 0;
        gas = 0;
        supplyCap = 0;
        supplyUsed = 0;
    }

    @Override
    public void init() {
        this.debugger = agent.getDebugger();
        this.utils = agent.getUtils();
        this.data = agent.getData();
        initBuilds();
    }

    private void initBuilds() {
        builds.put("DEFAULT", new PoolFirstExpandBuildOrder(agent));
    }

    public void update() {
        updateStats();
    }

    private void updateStats() {
        currentStep = agent.observation().getGameLoop();
        minerals = agent.observation().getMinerals();
        gas = agent.observation().getVespene();
        supplyCap = agent.observation().getFoodCap();
        supplyUsed = agent.observation().getFoodUsed();
        updateGameAge();
        updateEconomy();
    }

    private void updateEconomy() {

    }

    private void updateGameAge() {
        if (currentStep < 1000) {
            this.gameAge = GameAge.EARLY;
        } else if (currentStep < 5000) {
            this.gameAge = GameAge.MID;
        } else {
            this.gameAge = GameAge.LATE;
        }
    }

    @Override
    public void debug() {
        debugger.debugMessage("Game Loop step :" + data.getCurrentStep());
        debugAllUnits();
        debugBases();
    }

    private void debugBases() {
        for (Base base : bases.values()) {
            debugger.debugBase(base);
        }
    }

    public void onUnitCreated(UnitInPool unit) {
        log.info("Unit created :" + unit.unit().getType() + " tag:" + unit.getTag());
        Units type = (Units) unit.unit().getType();
        unitsInPool.put(unit.getTag(), unit);
        switch (type) {
            case ZERG_HATCHERY:
//                checkForMain(unit);
                break;
            case ZERG_DRONE:
//                allocateDrone(unit, getNearestBase(unit));
                break;
            case ZERG_QUEEN:
//                allocateQueen(unit);
                break;
            case ZERG_EXTRACTOR:
//                allocateExtractor(unit);
                break;
            case ZERG_ZERGLING:
            case ZERG_ROACH:
            case ZERG_HYDRALISK:
            case ZERG_MUTALISK:
            case ZERG_ULTRALISK:
            case ZERG_CORRUPTOR:
            case ZERG_BROODLORD:
            case ZERG_RAVAGER:
            case ZERG_LURKER_MP:
//                allocateSoldier(unit);
                break;
        }

    }

    private void debugAllUnits() {
        Unit unit;
        Units type;
        Set<Tag> tags = unitsInPool.keySet();
        for (Tag tag : tags) {
            UnitInPool unitInPool = unitsInPool.get(tag);
            unit = unitInPool.unit();
            type = (Units) unitInPool.unit().getType();
            switch (type) {
                case ZERG_DRONE:
                case ZERG_OVERLORD:
                case ZERG_ZERGLING:
                case ZERG_QUEEN:
                    debugUnit(unit);
                    break;
                default:
                    debugUnit(unit);
                    break;
            }
        }
    }

    private void debugUnit(Unit unit) {
        UnitOrder order;
        if (unit.isOnScreen()) {
            List<UnitOrder> orders = unit.getOrders();
            if (orders != null && orders.size() > 0) {
                order = orders.get(0);
                Optional<Tag> targetTag = order.getTargetedUnitTag();
                if (targetTag.isPresent()) {
                    UnitInPool targetUnitInPool = agent.observation().getUnit(targetTag.get());
                    if (targetUnitInPool != null) {
                        Optional<Unit> targetUnitOptional = targetUnitInPool.getUnit();
                        if (targetUnitOptional.isPresent()) {
                            Unit target = targetUnitOptional.get();
                            if (target.isOnScreen()) {
                                debugger.debugUnitOrder(unit, target, order);
                            }
                        } else {
                            log.info("Target not present :" + unit.getTag());
                        }
                    } else {
                        log.info("Target not in pool :" + unit.getTag());
                    }
                }
//                else {
//                    log.info("No target for unit :" + unit.getTag());
//                }
            }
//            else {
//                 log.info("No orders for unit :" + unit.getTag());
//            }
        }
    }

    public void addBase(Base base) {
        this.bases.put(base.getTag(), base);
    }

    public BuildOrder getBuild(String name) {
        return builds.get(name);
    }

    public List<UnitInPool> getAllUnitsOfType(Units unit) {
        return agent.observation().getUnits(Alliance.SELF, (unitInPool -> unitInPool.unit().getType().equals(unit)));
    }

    public List<UnitInPool> getUnitsInProduction(Units unit) {
        //TODO: Add Queens?
        return agent.observation().getUnits(Alliance.SELF, (unitInPool -> unitInPool.unit().getType().equals(ZERG_EGG))).stream()
                .filter(egg -> egg.unit().getOrders().stream().anyMatch(order -> order.getAbility().equals(getAbilityToMakeUnit(unit))))
                .collect(Collectors.toList());
    }

    Ability getAbilityToMakeUnit(UnitType unitType) {
        return agent.observation().getUnitTypeData(false).get(unitType).getAbility().orElse(Abilities.INVALID);
    }

    public List<UnitInPool> getWorkers() {
        return getAllUnitsOfType(ZERG_DRONE);
    }

    public Base getNearestBaseWithWorkers(Point2d point) {
        if (bases.size() == 1) {
            return (Base) bases.values().toArray()[0];
        } else {
            return bases.values().stream()
                    .filter(base -> base.countMineralWorkers() > 0 || base.countGasWorkers() > 0)
                    .min(getLinearDistanceComparatorForBase(point)).orElse(null);
        }
    }

    public Base getNearestBase(UnitInPool unitInPool) {
        if (bases.size() == 1) {
            return (Base) bases.values().toArray()[0];
        } else {
            Optional<Unit> unitOptional = unitInPool.getUnit();
            if (unitOptional.isEmpty()) {
                log.warn("Attempting to find nearest base for UnitInPool that does not exist: " + unitInPool);
                return null;
            }
            Point2d pos = unitOptional.get().getPosition().toPoint2d();
            return bases.values().stream().min(getLinearDistanceComparatorForBase(pos)).orElse(null);
        }
    }

    public UnitInPool getNearestWorker(Point2d location) {
        Base base = data.getNearestBaseWithWorkers(location);
        if (base != null) {
            return base.getWorker();
        } else {
            List<UnitInPool> drones = getAllUnitsOfType(ZERG_DRONE);
            if (drones.isEmpty())
                return null;
            else
                return drones.get(0);
        }
    }


    Comparator<Base> getLinearDistanceComparatorForBase(Point2d location) {
        return (u1, u2) -> {
            Double d1 = u1.getBase().unit().getPosition().toPoint2d().distance(location);
            Double d2 = u2.getBase().unit().getPosition().toPoint2d().distance(location);
            return d1.compareTo(d2);
        };
    }

    Point2d getNearestExpansionLocationTo(Point2d source) {
        return agent.query().calculateExpansionLocations(agent.observation()).stream()
                .map(Point::toPoint2d)
                .min(getLinearDistanceComparatorForPoint2d(source))
                .orElse(agent.observation().getStartLocation().toPoint2d());
    }

    public Comparator<Point2d> getLinearDistanceComparatorForPoint2d(Point2d source) {
        return (p1, p2) -> {
            Double d1 = p1.distance(source);
            Double d2 = p2.distance(source);
            return d1.compareTo(d2);
        };
    }

    public List<UnitInPool> getAllUnitsInProductionOfType(Units unit) {
        // Assuming this works for everything except queens
        return getUnitsInProduction(unit);
    }
}

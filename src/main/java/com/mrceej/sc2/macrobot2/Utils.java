package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.NEUTRAL_MINERAL_FIELD;

@Log4j2
public class Utils {


    private final MacroBot2 agent;

    public Utils(MacroBot2 agent) {
        this.agent = agent;
    }


    public UnitInPool findNearestMineralPatch(Point2d start) {
        List<UnitInPool> units = agent.observation().getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(NEUTRAL_MINERAL_FIELD));
        double distance = Double.MAX_VALUE;
        UnitInPool target = null;
        for (UnitInPool unitInPool : units) {
            double d = unitInPool.unit().getPosition().toPoint2d().distance(start);
            if (d < distance) {
                distance = d;
                target = unitInPool;
            }
        }
        return target;
    }
}

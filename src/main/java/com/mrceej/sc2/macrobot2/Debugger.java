package com.mrceej.sc2.macrobot2;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.debug.Color;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;
import com.mrceej.sc2.macrobot2.things.Base;
import lombok.extern.log4j.Log4j2;


@Log4j2
class Debugger {
    private static boolean debug_enabled = false;
    private static final Point2d LOCATION_DATA = Point2d.of(0.8f, 0.3f);
    private static final Point2d DEFAULT_ALERT_LOCATION = Point2d.of(0.35f, 0.2f);
    private static final Color DEFAULT_COLOUR = Color.GREEN;
    private static final int SIZE_NORMAL = 10;
    private static final int SIZE_LARGE = 20;

    private static Color RED = Color.RED;
    private static Color BLUE = Color.BLUE;
    private static Color GREEN = Color.GREEN;
    private static Color WHITE = Color.WHITE;


    private MacroBot2 agent;

    public Debugger(MacroBot2 agent, boolean debugEnabled) {
        debug_enabled = debugEnabled;
        this.agent = agent;
    }

    private Color getOrderColour(UnitOrder order) {
        Ability ability = order.getAbility();
        if (ability == Abilities.ATTACK) {
            return RED;
        } else if (ability == Abilities.MOVE) {
            return BLUE;
        } else if (ability == Abilities.HARVEST_GATHER ||
                ability == Abilities.HARVEST_RETURN) {
            return GREEN;
        }
        log.error("Unknown ability colour :" + ability.toString());
        return WHITE;
    }

    public void debugMessage(String message) {
        if (debug_enabled) {
            agent.debug().debugTextOut(message, LOCATION_DATA, DEFAULT_COLOUR, SIZE_NORMAL);
        }
    }

    public void sendDebug() {
        if (debug_enabled) {
            agent.debug().sendDebug();
        }
    }

    public void debugMessage(String message, UnitInPool unit) {
        if (debug_enabled) {
            debugMessage(message, unit.unit());
        }
    }

    public void debugMessage(String message, Unit unit) {
        if (debug_enabled) {
            debugMessage(message, unit.getPosition().toPoint2d());
        }
    }

    public void debugMessage(String message, Point2d location) {
        if (debug_enabled) {
            agent.debug().debugTextOut(message, location, DEFAULT_COLOUR, SIZE_NORMAL);
        }
    }

    public void debugAlert(String alert) {
        if (debug_enabled) {
            agent.debug().debugTextOut(alert, DEFAULT_ALERT_LOCATION, Color.RED, SIZE_LARGE);
        }
    }

    public void debugUnitOrder(Unit unit, Unit target, UnitOrder order) {
        agent.debug().debugLineOut(unit.getPosition(), target.getPosition(), getOrderColour(order));
    }

    public void debugBase(Base b) {

    }
}

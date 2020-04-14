package com.mrceej.sc2.macrobot2;

public abstract class CeejBotComponent {

    final MacroBot2 agent;

    public  CeejBotComponent(MacroBot2 macroBot2) {
        this.agent = macroBot2;
    }

    public abstract void init() ;

    public abstract void update() ;

    public abstract void debug() ;
}

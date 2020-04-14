package com.mrceej.sc2.macrobot2.things;

import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import lombok.Data;
import lombok.Getter;

@Data
public class BuildRequest {
    @Getter
    Units unit;

    public BuildRequest(Units unit) {
        this.unit = unit;
    }

    public BuildRequest(Abilities upgrade) {

    }
}

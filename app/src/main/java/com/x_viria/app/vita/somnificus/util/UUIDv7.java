package com.x_viria.app.vita.somnificus.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import java.util.UUID;

public class UUIDv7 {

    public static UUID randomUUID() {
        TimeBasedEpochGenerator tbeg = Generators.timeBasedEpochGenerator();
        return tbeg.generate();
    }

}

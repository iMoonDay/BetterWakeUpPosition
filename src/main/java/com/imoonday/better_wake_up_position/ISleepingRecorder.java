package com.imoonday.better_wake_up_position;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public interface ISleepingRecorder {

    default Optional<Vec3d> preciseRespawn$getSleepingSourcePos() {
        return Optional.empty();
    }

    default void preciseRespawn$setSleepingSourcePos(Vec3d pos) {
    }

    default Optional<Vec3d> preciseRespawn$checkAndClearSleepingSourcePos(World world) {
        return Optional.empty();
    }

}

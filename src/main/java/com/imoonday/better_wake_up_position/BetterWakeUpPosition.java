package com.imoonday.better_wake_up_position;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class BetterWakeUpPosition implements ModInitializer {

    public static final GameRules.Key<GameRules.BooleanRule> WAKE_UP_AT_STARTING_SLEEPING_LOCATION = GameRuleRegistry.register("wakeUpAtStartingSleepingLocation", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    @Override
    public void onInitialize() {
        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {
            ((ISleepingRecorder) player).preciseRespawn$setSleepingSourcePos(player.getPos());
            return null;
        });
        EntitySleepEvents.MODIFY_WAKE_UP_POSITION.register((entity, sleepingPos, bedState, wakeUpPos) -> {
            if (entity instanceof ISleepingRecorder sleepingRecorder) {
                return sleepingRecorder.preciseRespawn$checkAndClearSleepingSourcePos(entity.getWorld()).orElse(wakeUpPos);
            }
            return wakeUpPos;
        });
    }
}

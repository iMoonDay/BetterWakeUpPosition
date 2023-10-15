package com.imoonday.better_wake_up_position.mixin;

import com.imoonday.better_wake_up_position.BetterWakeUpPosition;
import com.imoonday.better_wake_up_position.ISleepingRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements ISleepingRecorder {

    @Unique
    public Vec3d sleepingSourcePos;

    protected PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        throw new IllegalStateException();
    }

    @Override
    public Optional<Vec3d> preciseRespawn$getSleepingSourcePos() {
        return Optional.ofNullable(sleepingSourcePos);
    }

    @Override
    public void preciseRespawn$setSleepingSourcePos(Vec3d pos) {
        this.sleepingSourcePos = pos;
    }

    @Override
    public Optional<Vec3d> preciseRespawn$checkAndClearSleepingSourcePos(World world) {
        Optional<Vec3d> optional = preciseRespawn$getSleepingSourcePos();
        preciseRespawn$setSleepingSourcePos(null);
        if (!world.getGameRules().getBoolean(BetterWakeUpPosition.WAKE_UP_AT_STARTING_SLEEPING_LOCATION) || optional.isPresent() && !world.isSpaceEmpty(this, this.getBoundingBox().offset(optional.get().subtract(this.getPos())))) {
            return Optional.empty();
        }
        return optional;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        preciseRespawn$getSleepingSourcePos().ifPresent(vec3d -> nbt.put("SleepingSourcePos", this.toNbtList(vec3d.x, vec3d.y, vec3d.z)));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = nbt.getList("SleepingSourcePos", NbtElement.DOUBLE_TYPE);
        if (list != null && list.size() == 3) {
            this.preciseRespawn$setSleepingSourcePos(new Vec3d(list.getDouble(0), list.getDouble(1), list.getDouble(2)));
        }
    }
}

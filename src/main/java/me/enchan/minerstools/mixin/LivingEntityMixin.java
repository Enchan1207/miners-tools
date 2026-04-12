package me.enchan.minerstools.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.enchan.minerstools.events.MinersToolsMainHandToolBreakEvent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "sendEquipmentBreakStatus", at = @At("TAIL"))
    public void onBreak(EquipmentSlot slot, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayerEntity player)) {
            return;
        }

        if (slot != EquipmentSlot.MAINHAND) {
            return;
        }

        // ツール破壊イベントを呼び出し
        MinersToolsMainHandToolBreakEvent.EVENT.invoker().onBreak(player);
    }

}

package me.enchan.minerstools.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

        // 壊れたアイテムを取得
        var brokenItemStack = player.getMainHandStack();

        var inventory = player.getInventory().main;
        var candidateIndex = -1;
        for (int i = 0; i < inventory.size(); i++) {
            var itemStack = inventory.get(i);
            if (itemStack.equals(brokenItemStack)) {
                continue;
            }

            if (!itemStack.getItem().equals(brokenItemStack.getItem())) {
                continue;
            }

            // TODO: より耐久の残っているものから利用していくなどの最適化もできるかも
            candidateIndex = i;
        }

        if (candidateIndex < 0) {
            return;
        }
        var alternativeItem = inventory.get(candidateIndex);

        // 持ち替え
        var currentSlot = player.getInventory().selectedSlot;
        inventory.set(candidateIndex, inventory.get(currentSlot));
        inventory.set(currentSlot, alternativeItem);

        player.currentScreenHandler.sendContentUpdates();

        var brokenItemName = brokenItemStack.getItem().getName().getString();
        var message = Text.empty()
                .append(Text.literal("The tool "))
                .append(Text.literal(brokenItemName).formatted(Formatting.AQUA))
                .append(Text.literal(" broke, switched to new one."));
        player.sendMessage(message, true);
    }

}

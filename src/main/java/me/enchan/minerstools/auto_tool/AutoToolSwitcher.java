package me.enchan.minerstools.auto_tool;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AutoToolSwitcher {
    public static void onBreakMainhandTool(ServerPlayerEntity player) {
        // 壊れたアイテムを取得
        var brokenItemStack = player.getMainHandStack();
        var currentSlotIndex = player.getInventory().selectedSlot;

        // 代替アイテムを検索
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
            player.sendMessage(Text.literal("No alternative tools!"), true);
            return;
        }

        // 持ち替え
        swapItemStackAt(player, currentSlotIndex, candidateIndex);

        player.currentScreenHandler.sendContentUpdates();

        player.sendMessage(Text.literal("Switched to new one!"), true);
    }

    private static void swapItemStackAt(ServerPlayerEntity player, int sourceIndex, int destIndex) {
        var inventory = player.getInventory().main;
        var tmp = inventory.get(sourceIndex);
        inventory.set(sourceIndex, inventory.get(destIndex));
        inventory.set(destIndex, tmp);
    }
}

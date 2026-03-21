package me.enchan.minerstools;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.enchan.minerstools.payloads.MinersToolsModeTogglePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class MinersToolsModClient implements ClientModInitializer {
    public static final Logger Logger = LoggerFactory.getLogger("miners-tools-client");

    public static KeyBinding modeToggleKey;

    @Override
    public void onInitializeClient() {
        Logger.info("miners-tools:client");

        modeToggleKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.miners-tools.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_Z,
                        "category.miners-tools"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (modeToggleKey.wasPressed()) {
                ClientPlayNetworking.send(new MinersToolsModeTogglePayload());
            }
        });
    }
}

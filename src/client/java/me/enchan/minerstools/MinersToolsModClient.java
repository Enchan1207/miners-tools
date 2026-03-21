package me.enchan.minerstools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

public class MinersToolsModClient implements ClientModInitializer {
    public static final Logger Logger = LoggerFactory.getLogger("miners-tools-client");

    @Override
    public void onInitializeClient() {
        Logger.info("miners-tools:client");
    }
}

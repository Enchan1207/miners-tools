package me.enchan.minerstools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class MinersToolsMod implements ModInitializer {
    public static final String ModId = "miners-tools";

    public static final Logger Logger = LoggerFactory.getLogger("miners-tools");

    @Override
    public void onInitialize() {
        Logger.info("miners-tools");
    }
}

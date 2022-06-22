package io.github.blaircodesbadly.pagination;

import com.mojang.logging.LogUtils;
import io.github.blaircodesbadly.pagination.commands.PaginationCommand;
import io.github.blaircodesbadly.pagination.config.PaginationConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("pagination")
public class PaginationMod {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Map<UUID, Runnable> callbackRunnables = new HashMap<>();
    public static PaginationConfig CONFIG;

    public PaginationMod() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
        CONFIG = PaginationConfig.sync();
        PaginationCommand.register(event.getServer().getCommands().getDispatcher());
    }
}

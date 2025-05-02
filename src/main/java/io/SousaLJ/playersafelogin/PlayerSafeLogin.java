package io.SousaLJ.playersafelogin;

import com.mojang.logging.LogUtils;
import io.SousaLJ.playersafelogin.commands.PlayerSafeLoginCommands;
import io.SousaLJ.playersafelogin.config.PlayerSafeLoginConfig;
import io.SousaLJ.playersafelogin.network.PlayerSafeLoginPacketHandler;
import io.SousaLJ.playersafelogin.storage.FileStorage;
import io.SousaLJ.playersafelogin.storage.MySQLStorage;
import io.SousaLJ.playersafelogin.storage.SQLiteStorage;
import io.SousaLJ.playersafelogin.storage.StorageProvider;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(PlayerSafeLogin.MODID)
public class PlayerSafeLogin
{
    public static final String MODID = "playersafelogin";

    public static final Logger LOGGER = LogUtils.getLogger();

    private final PlayerSafeLoginPacketHandler packetHandler;

    private static StorageProvider storage;

    public PlayerSafeLogin(IEventBus modEventBus, ModContainer modContainer) {

        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.addListener(PlayerSafeLoginCommands::registerCommands);

        packetHandler = new PlayerSafeLoginPacketHandler(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, PlayerSafeLoginConfig.SERVER_SPEC);
    }


    @SubscribeEvent
    private void onServerStarting(ServerStartingEvent event) {
        switch (PlayerSafeLoginConfig.SERVER.storageType.get()) {
            case FILE -> storage = new FileStorage();
            case MYSQL -> storage = createMySQLStorage();
            case SQLITE -> storage = new SQLiteStorage(PlayerSafeLoginConfig.SERVER.sqlitePath.get());
        }
    }
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PlayerSafeLogin.MODID, path);
    }

    private StorageProvider createMySQLStorage() {
        return new MySQLStorage(
                PlayerSafeLoginConfig.SERVER.mysqlHost.get(),
                PlayerSafeLoginConfig.SERVER.mysqlPort.get(),
                PlayerSafeLoginConfig.SERVER.mysqlUser.get(),
                PlayerSafeLoginConfig.SERVER.mysqlPassword.get(),
                PlayerSafeLoginConfig.SERVER.mysqlDbName.get()
        );
    }

    public static StorageProvider getStorage() {
        return storage;
    }
}

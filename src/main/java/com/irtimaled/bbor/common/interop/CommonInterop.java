package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.StructureProcessor;
import com.irtimaled.bbor.common.events.DataPackReloaded;
import com.irtimaled.bbor.common.events.PlayerLoggedIn;
import com.irtimaled.bbor.common.events.PlayerLoggedOut;
import com.irtimaled.bbor.common.events.PlayerSubscribed;
import com.irtimaled.bbor.common.events.ServerTick;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.events.WorldLoaded;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommonInterop {

    public static void chunkLoaded(@NotNull Chunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.q.ab());
        Map<String, StructureStart> structures = new HashMap<>();
        final IRegistry<Structure> structureFeatureRegistry = chunk.q.s().b(IRegistry.aN);
        for (var es : chunk.g().entrySet()) {
            final Optional<ResourceKey<Structure>> optional = structureFeatureRegistry.c(es.getKey());
            optional.ifPresent(key -> structures.put("structure:" + key.a().toString(), es.getValue()));
        }
        if (structures.size() > 0) {
            EventBus.publish(new StructuresLoaded(structures, dimensionId));
        }
    }

    @Deprecated
    public static void loadWorlds(@NotNull Collection<WorldServer> worlds) {
        for (WorldServer world : worlds) {
            loadWorld(world);
        }
    }

    public static void loadServerStructures(MinecraftServer server) {
        try {
            final IRegistry<Structure> structureFeatureRegistry = server.aX().b(IRegistry.aN);
            loadStructuresFromRegistry(structureFeatureRegistry);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Deprecated(forRemoval = true)
    public static void loadWorldStructures(WorldServer world) {
        try {
            final IRegistry<Structure> structureFeatureRegistry = world.s().b(IRegistry.aN);
            loadStructuresFromRegistry(structureFeatureRegistry);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void loadStructuresFromRegistry(@NotNull IRegistry<Structure> structureFeatureRegistry) {
        Logger.info("Registering structures: " + Arrays.toString(structureFeatureRegistry.f().stream().map(entry -> entry.getKey().a().toString()).distinct().toArray(String[]::new)));
        for (var entry : structureFeatureRegistry.f()) {
            final MinecraftKey value = entry.getKey().a();
            final BoundingBoxType boundingBoxType = BoundingBoxType.register("structure:" + value);
            StructureProcessor.registerSupportedStructure(boundingBoxType);
        }
    }


    public static void loadWorld(WorldServer world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void dataPackReloaded() {
        EventBus.publish(new DataPackReloaded());
    }

    public static void playerLoggedIn(EntityPlayer player) {
        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(@NotNull EntityPlayer player) {
        EventBus.publish(new PlayerLoggedOut(player.ae()));
    }

    public static void playerSubscribed(@NotNull EntityPlayer player) {
        EventBus.publish(new PlayerSubscribed(player.ae(), new ServerPlayer(player)));
    }
}

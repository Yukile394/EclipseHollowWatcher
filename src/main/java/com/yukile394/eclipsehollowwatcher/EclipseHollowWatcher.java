package com.yukile394.eclipsehollowwatcher;

import com.yukile394.eclipsehollowwatcher.config.EclipseConfig;
import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import com.yukile394.eclipsehollowwatcher.entity.ModEntities;
import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineBrain;
import com.yukile394.eclipsehollowwatcher.event.ScareEventManager;
import com.yukile394.eclipsehollowwatcher.event.SummonPhraseListener;
import com.yukile394.eclipsehollowwatcher.network.ModNetworking;
import com.yukile394.eclipsehollowwatcher.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipseHollowWatcher implements ModInitializer {

    public static final String MOD_ID = "eclipsehollowwatcher";
    public static final Logger LOGGER = LoggerFactory.getLogger("EclipseHollowWatcher");

    @Override
    public void onInitialize() {
        LOGGER.info("Eclipse: The Hollow Watcher is waking up.");

        ModSounds.init();
        ModEntities.init();
        FabricDefaultAttributeRegistry.register(ModEntities.HEROBRINE, HerobrineEntity.createAttributes());
        ModNetworking.registerPayloads();

        // Force config to load/create on startup.
        EclipseConfig.get();

        registerChatHooks();
        registerCombatHooks();
        registerTickHooks();
        registerLifecycleHooks();
    }

    private void registerChatHooks() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            String content = message.getContent().getString();
            if (SummonPhraseListener.matches(content)) {
                SummonPhraseListener.handle(sender, content);
            }
            if (EclipseConfig.get().angerFromInsultsInChat && containsInsult(content)) {
                HerobrineBrain.getOrCreate(sender.getUuid())
                        .registerHostileAction(EclipseConfig.get().maxAngerPerHostileAction);
            }
        });
    }

    private void registerCombatHooks() {
        // Raise anger when a tracked player attacks Herobrine specifically,
        // rather than any hostile-mob damage, so ordinary PvE doesn't count.
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof HerobrineEntity && EclipseConfig.get().angerFromAttackingHerobrine) {
                HerobrineBrain.getOrCreate(player.getUuid())
                        .registerHostileAction(EclipseConfig.get().maxAngerPerHostileAction);
            }
            return net.minecraft.util.ActionResult.PASS;
        });
    }

    private void registerTickHooks() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!player.isSpectator()) {
                    ScareEventManager.tick(player);
                }
            }
        });
    }

    private void registerLifecycleHooks() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> HerobrineBrain.clearAll());
    }

    private boolean containsInsult(String content) {
        String lower = content.toLowerCase();
        // Deliberately short, generic list; server owners can extend this
        // via a future config list without needing a mod update.
        return lower.contains("aptal herobrine") || lower.contains("korkak herobrine")
                || lower.contains("stupid herobrine") || lower.contains("coward herobrine");
    }
}

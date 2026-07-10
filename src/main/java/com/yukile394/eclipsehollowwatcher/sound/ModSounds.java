package com.yukile394.eclipsehollowwatcher.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {

    public static final SoundEvent WHISPER_1 = register("whisper_1");
    public static final SoundEvent WHISPER_2 = register("whisper_2");
    public static final SoundEvent WHISPER_NAME_CALL = register("whisper_name_call");
    public static final SoundEvent HEARTBEAT_SLOW = register("heartbeat_slow");
    public static final SoundEvent HEARTBEAT_FAST = register("heartbeat_fast");
    public static final SoundEvent FOOTSTEP_HEAVY = register("footstep_heavy");
    public static final SoundEvent DEEP_BREATH = register("deep_breath");
    public static final SoundEvent STATIC_RADIO = register("static_radio");
    public static final SoundEvent WIND_LOW = register("wind_low");
    public static final SoundEvent SUDDEN_SILENCE = register("sudden_silence");
    public static final SoundEvent HEROBRINE_GROWL = register("herobrine_growl");
    public static final SoundEvent HEROBRINE_ANGRY_ROAR = register("herobrine_angry_roar");
    public static final SoundEvent HEROBRINE_TELEPORT = register("herobrine_teleport");

    private static SoundEvent register(String path) {
        Identifier id = Identifier.of("eclipsehollowwatcher", path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init() {
        // Static initializer trigger; actual registration happens in the field initializers above.
    }

    private ModSounds() {
    }
}

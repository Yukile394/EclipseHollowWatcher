package com.yukile394.eclipsehollowwatcher.network;

import net.minecraft.util.Identifier;

public final class NetworkingConstants {

    public static final String MOD_ID = "eclipsehollowwatcher";

    public static final Identifier SCREEN_EFFECT_ID = Identifier.of(MOD_ID, "screen_effect");
    public static final Identifier DISTANT_EYES_ID = Identifier.of(MOD_ID, "distant_eyes");
    public static final Identifier AMBIENT_CUE_ID = Identifier.of(MOD_ID, "ambient_cue");

    private NetworkingConstants() {
    }
}

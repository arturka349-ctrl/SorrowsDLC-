package com.sorrowsdlc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class SorrowsClient implements ClientModInitializer {

    public static boolean fullbright = true;
    public static float gamma = 12.0f;
    public static boolean zoomEnabled = true;
    public static float zoomFactor = 4.0f;
    public static boolean smoothZoom = true;
    public static boolean hudEnabled = true;
    public static boolean showCoords = true;
    public static boolean showFPS = true;
    public static boolean showSpeed = true;
    public static boolean showBiome = true;
    public static boolean showTime = true;
    public static boolean hitColor = true;
    public static int hitColorRGB = 0xFF3333;
    public static boolean damageTint = true;
    public static boolean dynamicIsland = true;
    public static boolean guiAnimations = true;

    private static KeyBinding zoomKey;
    private static boolean zooming = false;
    private static float currentZoom = 1.0f;
    private static long hitTimestamp = 0;
    private static long damageTimestamp = 0;
    private static float animProgress = 0f;
    private static boolean increasing = true;
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Random random = new Random();

    @Override
    public void onInitializeClient() {
        System.out.println("[sorrowsDLC] Нурсултан визуалс загружен!");

        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Zoom (sorrowsDLC)",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "sorrowsDLC"
        ));

        if (fullbright) mc.options.getGamma().setValue(gamma);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            zooming = zoomKey.isPressed() && zoomEnabled;
            float target = zooming ? zoomFactor : 1.0f;
            if (smoothZoom) currentZoom += (target - currentZoom) * 0.3f;
            else currentZoom = target;
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            renderHUD(context);
            renderDamageTint(context);
            renderDynamicIsland(context);
        });
              }

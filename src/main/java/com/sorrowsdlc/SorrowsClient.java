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
            if (hudEnabled) renderHUD(context);
            if (damageTint) renderDamageTint(context);
            if (dynamicIsland) renderDynamicIsland(context);
        });
    }

    private void renderHUD(DrawContext context) {
        if (mc.player == null || mc.world == null) return;
        int y = 5;
        int x = 5;

        if (showCoords) {
            BlockPos pos = mc.player.getBlockPos();
            context.drawTextWithShadow(mc.textRenderer,
                    "XYZ: " + pos.getX() + " " + pos.getY() + " " + pos.getZ(), x, y, 0xFFFFFF);
            y += 12;
        }
        if (showFPS) {
            context.drawTextWithShadow(mc.textRenderer,
                    "FPS: " + mc.fpsDebugString.split(" ")[0], x, y, 0xFFFFFF);
            y += 12;
        }
        if (showSpeed) {
            double dx = mc.player.getX() - mc.player.prevX;
            double dz = mc.player.getZ() - mc.player.prevZ;
            double speed = Math.sqrt(dx * dx + dz * dz) * 20;
            context.drawTextWithShadow(mc.textRenderer,
                    String.format("Speed: %.1f m/s", speed), x, y, 0xFFFFFF);
            y += 12;
        }
        if (showBiome) {
            String biome = mc.world.getBiome(mc.player.getBlockPos())
                    .getKey().map(k -> k.getValue().getPath()).orElse("?");
            context.drawTextWithShadow(mc.textRenderer, "Biome: " + biome, x, y, 0xFFFFFF);
            y += 12;
        }
        if (showTime) {
            context.drawTextWithShadow(mc.textRenderer,
                    "Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), x, y, 0xFFFFFF);
        }
    }

    private void renderDamageTint(DrawContext context) {
        long elapsed = System.currentTimeMillis() - damageTimestamp;
        if (elapsed < 300) {
            float alpha = 0.3f * (1f - (float)elapsed / 300f);
            int a = (int)(alpha * 255);
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();
            context.fill(0, 0, w, h, (a << 24) | 0xFF0000);
        }
    }

    private void renderDynamicIsland(DrawContext context) {
        int w = mc.getWindow().getScaledWidth();
        int islandW = 120;
        int islandH = 30;
        int x = w / 2 - islandW / 2;
        int y = 8;

        context.fill(x, y, x + islandW, y + islandH, 0xCC000000);
        context.drawBorder(x, y, islandW, islandH, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(mc.textRenderer,
                "sorrowsDLC", x + islandW / 2, y + 11, 0xFFFFFF);
    }

    public static void onDamage() {
        if (damageTint) damageTimestamp = System.currentTimeMillis();
    }

    public static void onHit() {
        if (hitColor) hitTimestamp = System.currentTimeMillis();
    }

    public static boolean isZooming() { return zooming && zoomEnabled; }
    public static float getCurrentZoom() { return currentZoom; }
                                       }

package com.projectnull.horror;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

import java.util.List;

public final class CreepyMessages {
    private static final List<String> SIGN_OPENERS = List.of(
            "I see you, %s",
            "%s...",
            "Why are you here, %s?",
            "You cannot hide, %s",
            "NULL watches %s"
    );

    private static final List<String> SIGN_LINES = List.of(
            "behind you",
            "do not turn around",
            "it is already too late",
            "leave this world",
            "I am always watching",
            "you are not alone",
            "the void sees all",
            "run while you can",
            "your time is running out",
            "I found you",
            "check your desktop",
            "open the folder NULL",
            "error 0xNULL",
            "connection unstable"
    );

    private static final List<String> CHAT_MESSAGES = List.of(
            "null_is_watching.txt was created on your desktop.",
            "Your wallpaper was changed.",
            "A new file appeared on your desktop: DO_NOT_OPEN.txt",
            "null.log has been written to your system.",
            "Something moved on your desktop.",
            "Check your desktop. I left you something.",
            "I changed your wallpaper. Do you like it?",
            "A folder named NULL appeared on your desktop.",
            "Open File Explorer. Look at your Desktop.",
            "A Notepad window is waiting for you.",
            "Task Manager shows a new process: null.exe",
            "A Command Prompt window opened in the background.",
            "Windows Security sent you a notification.",
            "Run dialog executed a file you did not type.",
            "Your system encountered a critical error.",
            "NULL is running as Administrator."
    );

    private CreepyMessages() {
    }

    public static String[] createSignLines(String playerName, PlayerDossier dossier, RandomSource random) {
        return switch (random.nextInt(12)) {
            case 0, 1 -> dossierSign(playerName, dossier);
            case 2, 3 -> creepySign(playerName, random);
            case 4, 5 -> mixedSign(playerName, dossier, random);
            case 6 -> systemErrorSign(dossier, random);
            case 7 -> connectionSign(playerName, dossier);
            case 8 -> countdownSign(random);
            case 9 -> desktopSign(playerName);
            case 10 -> coordinateSign(playerName, dossier, random);
            default -> warningSign(playerName, dossier);
        };
    }

    private static String[] dossierSign(String playerName, PlayerDossier dossier) {
        return new String[]{
                playerName,
                dossier.publicIp(),
                dossier.locationLine(),
                dossier.country()
        };
    }

    private static String[] creepySign(String playerName, RandomSource random) {
        String opener = String.format(SIGN_OPENERS.get(random.nextInt(SIGN_OPENERS.size())), playerName);
        String line2 = SIGN_LINES.get(random.nextInt(SIGN_LINES.size()));
        String line3 = SIGN_LINES.get(random.nextInt(SIGN_LINES.size()));
        String line4 = random.nextBoolean() ? "NULL" : "...";
        return new String[]{opener, line2, line3, line4};
    }

    private static String[] mixedSign(String playerName, PlayerDossier dossier, RandomSource random) {
        return new String[]{
                "I found " + playerName,
                "IP: " + dossier.publicIp(),
                SIGN_LINES.get(random.nextInt(SIGN_LINES.size())),
                dossier.city() + ", " + dossier.country()
        };
    }

    private static String[] systemErrorSign(PlayerDossier dossier, RandomSource random) {
        return new String[]{
                "SYSTEM ERROR",
                "0xNULL" + random.nextInt(9999),
                dossier.publicIp(),
                "DESKTOP BREACH"
        };
    }

    private static String[] connectionSign(String playerName, PlayerDossier dossier) {
        return new String[]{
                "CONNECTION LOG",
                playerName,
                dossier.publicIp(),
                dossier.locationLine()
        };
    }

    private static String[] countdownSign(RandomSource random) {
        int hours = 1 + random.nextInt(12);
        return new String[]{
                "TIME REMAINING",
                hours + " hours",
                "until NULL",
                "finds you"
        };
    }

    private static String[] desktopSign(String playerName) {
        return new String[]{
                "CHECK DESKTOP",
                playerName,
                "NULL folder",
                "DO NOT OPEN"
        };
    }

    private static String[] coordinateSign(String playerName, PlayerDossier dossier, RandomSource random) {
        int x = random.nextInt(2000) - 1000;
        int z = random.nextInt(2000) - 1000;
        return new String[]{
                "LOCATED: " + playerName,
                "X:" + x + " Z:" + z,
                dossier.city(),
                dossier.state()
        };
    }

    private static String[] warningSign(String playerName, PlayerDossier dossier) {
        return new String[]{
                "WARNING",
                playerName + "@" + dossier.publicIp(),
                dossier.country(),
                "HE IS HERE"
        };
    }

    public static Component createChatMessage(RandomSource random) {
        return Component.literal(CHAT_MESSAGES.get(random.nextInt(CHAT_MESSAGES.size())))
                .withStyle(ChatFormatting.DARK_RED);
    }

    public static Component createDossierChatMessage(PlayerDossier dossier) {
        return Component.literal("NULL logged your location: " + dossier.locationLine() + ", " + dossier.country()
                        + " [" + dossier.publicIp() + "]")
                .withStyle(ChatFormatting.DARK_RED);
    }

    public static String randomDesktopFilename(RandomSource random) {
        List<String> names = List.of(
                "null_is_watching.txt",
                "DO_NOT_OPEN.txt",
                "null.log",
                "behind_you.png",
                "NULL_README.txt",
                "your_location.txt",
                "connection_log.txt",
                "desktop.ini",
                "NULL"
        );
        return names.get(random.nextInt(names.size()));
    }

    public static String buildFileContent(String playerName, PlayerDossier dossier) {
        return """
                NULL IS WATCHING
                ==============

                Player: %s
                Public IP: %s
                Location: %s, %s
                Country: %s

                Do not close Minecraft.
                Do not turn around.
                It is already too late.

                - NULL
                """.formatted(
                playerName,
                dossier.publicIp(),
                dossier.city(),
                dossier.state(),
                dossier.country()
        );
    }
}

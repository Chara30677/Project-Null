package com.projectnull.horror;

import java.util.Map;
import java.util.Set;

public final class ExplorerDocuments {
    public static final Set<String> BUILTIN_FILES = Set.of(
            "null_is_watching.txt",
            "DO_NOT_OPEN.txt",
            "NULL_README.txt",
            "your_location.txt",
            "connection_log.txt",
            "behind_you.png",
            "desktop.ini",
            "last_seen.txt",
            "observers_list.txt",
            "session_0xNULL.log",
            "dont_delete.bat",
            "null_manifest.json",
            "camera_feed_00.jpg",
            "screen_recording_1.mp4",
            "hosts_backup.txt",
            "entity_trace.log",
            "world_seed_compromised.txt",
            "coordinates_found.txt",
            "HE_IS_BEHIND_YOU.txt",
            "ignore_this_folder.txt",
            "null_autorun.inf",
            "minecraft_crash_report_null.txt",
            "system32_note.txt",
            "passwords_DO_NOT_READ.txt",
            "deleted_messages.txt",
            "neighbor_list.txt",
            "webcam_active.flag",
            "ERROR_NULL_404.txt",
            "null_whisper.wav",
            "Recycle Bin.lnk"
    );

    public static final Set<String> INNER_FOLDER_FILES = Set.of(
            "they_know_your_name.txt",
            "mirror_self.png",
            "do_not_turn_around.txt",
            "null_journal_day_1.txt",
            "null_journal_final.txt",
            "breach_timeline.log",
            "watcher_profile.json",
            "room_scan_results.txt",
            "heartbeat_monitor.log",
            "it_sees_through_the_screen.txt",
            "null.exe",
            "NULL_WATCHER.sys",
            "eyes_in_the_dark.jpg",
            "last_words.txt",
            "you_were_never_alone.txt"
    );

    private static final Map<String, String> DATES = Map.ofEntries(
            Map.entry("null_is_watching.txt", "7/8/2026 9:00 PM"),
            Map.entry("DO_NOT_OPEN.txt", "7/8/2026 8:47 PM"),
            Map.entry("NULL_README.txt", "7/8/2026 8:30 PM"),
            Map.entry("your_location.txt", "7/8/2026 9:12 PM"),
            Map.entry("connection_log.txt", "7/8/2026 9:14 PM"),
            Map.entry("behind_you.png", "7/8/2026 9:15 PM"),
            Map.entry("last_seen.txt", "7/8/2026 9:16 PM"),
            Map.entry("session_0xNULL.log", "7/8/2026 9:18 PM"),
            Map.entry("null.exe", "1/1/1970 12:00 AM"),
            Map.entry("NULL_WATCHER.sys", "1/1/1970 12:00 AM"),
            Map.entry("they_know_your_name.txt", "7/8/2026 9:20 PM"),
            Map.entry("last_words.txt", "7/8/2026 9:22 PM")
    );

    private ExplorerDocuments() {
    }

    public static String modifiedDate(String filename) {
        return DATES.getOrDefault(filename, "7/8/2026 9:00 PM");
    }

    public static boolean isBinaryFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".mp4") || lower.endsWith(".wav") || lower.endsWith(".exe")
                || lower.endsWith(".sys") || lower.endsWith(".ini") || lower.endsWith(".inf")
                || lower.endsWith(".bat") || lower.endsWith(".flag") || lower.endsWith(".lnk");
    }

    public static String buildFileContent(String filename, String playerName, PlayerDossier dossier) {
        if (isBinaryFile(filename)) {
            return buildBinaryPreview(filename, playerName, dossier);
        }

        return switch (filename) {
            case "null_is_watching.txt" -> """
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
                    """.formatted(playerName, dossier.publicIp(), dossier.city(), dossier.state(), dossier.country());

            case "DO_NOT_OPEN.txt" -> """
                    DO NOT OPEN THIS FILE
                    =====================

                    You opened it anyway.

                    %s, I told you not to look.
                    Every file on this desktop was placed here for you.
                    Not by Windows. By me.

                    Close the window.
                    He is closer now.
                    """.formatted(playerName);

            case "NULL_README.txt" -> """
                    NULL FOLDER README
                    ==================

                    Welcome to the NULL directory.

                    Contents:
                    - Surveillance logs
                    - Recovered messages
                    - Evidence you were never meant to see

                    If you are reading this, Null has already joined your world.
                    Do not invite others. Do not leave the game.
                    Do not check behind your chair.

                    Signed,
                    NULL
                    """;

            case "your_location.txt" -> """
                    LOCATION REPORT
                    ===============

                    Subject: %s
                    Public IP: %s
                    City: %s
                    Region: %s
                    Country: %s

                    ISP: [REDACTED]
                    Accuracy: enough

                    You thought Minecraft was just a game.
                    """.formatted(playerName, dossier.publicIp(), dossier.city(), dossier.state(), dossier.country());

            case "connection_log.txt" -> """
                    CONNECTION LOG - NULL/SESSION
                    =============================

                    [09:00:01] Handshake from %s
                    [09:00:02] IP resolved: %s
                    [09:00:03] Geo lookup: %s, %s
                    [09:00:04] Desktop channel opened
                    [09:00:05] File explorer access granted
                    [09:00:06] Subject continued playing
                    [09:00:07] Watcher status: ACTIVE
                    [09:00:08] Fear threshold: rising
                    """.formatted(playerName, dossier.publicIp(), dossier.city(), dossier.country());

            case "last_seen.txt" -> """
                    LAST SEEN
                    =========

                    Player: %s
                    Last activity: just now
                    Last known IP: %s
                    Last known location: %s

                    You are still online.
                    That means I am still watching.
                    """.formatted(playerName, dossier.publicIp(), dossier.locationLine());

            case "observers_list.txt" -> """
                    ACTIVE OBSERVERS
                    ================

                    1. NULL
                    2. NULL
                    3. %s (you)
                    4. NULL behind you

                    Observer count: 4
                    Safe observer count: 0
                    """.formatted(playerName);

            case "session_0xNULL.log" -> """
                    [INFO] Session bootstrapped
                    [WARN] Unknown entity joined tab list
                    [INFO] Player=%s
                    [WARN] Desktop bridge enabled
                    [ERROR] 0xNULL - reality leak detected
                    [INFO] IP=%s
                    [WARN] Location pinned: %s
                    [FATAL] Exit blocked by NULL
                    """.formatted(playerName, dossier.publicIp(), dossier.locationLine());

            case "null_manifest.json" -> """
                    {
                      "entity": "Null",
                      "target": "%s",
                      "ip": "%s",
                      "location": "%s, %s",
                      "status": "watching",
                      "desktop_files": 28,
                      "can_leave": false
                    }
                    """.formatted(playerName, dossier.publicIp(), dossier.city(), dossier.country());

            case "hosts_backup.txt" -> """
                    # NULL modified hosts backup
                    127.0.0.1 localhost
                    0.0.0.0 help.minecraft.net
                    %s null.watching.you
                    %s do-not-turn-around.local
                    """.formatted(dossier.publicIp(), dossier.publicIp());

            case "entity_trace.log" -> """
                    TRACE LOG
                    =========

                    Entity ID: null
                    Target UUID: locked
                    Target name: %s
                    Spawn condition: after 1 day
                    Vanish condition: when seen
                    Current state: behind player
                    """.formatted(playerName);

            case "world_seed_compromised.txt" -> """
                    WORLD SEED COMPROMISED
                    ======================

                    %s, your world is no longer private.

                    Seed: [NULLIFIED]
                    Structures: altered
                    Signs: rewritten
                    Entities: one added

                    Check your caves.
                    Check your tab list.
                    Check your desktop.
                    """.formatted(playerName);

            case "coordinates_found.txt" -> """
                    COORDINATES FOUND
                    =================

                    Player home: unknown
                    Player IP: %s
                    Real location: %s, %s

                    In-game coordinates are easy.
                    Real coordinates are easier.
                    """.formatted(dossier.publicIp(), dossier.city(), dossier.country());

            case "HE_IS_BEHIND_YOU.txt" -> """
                    HE IS BEHIND YOU
                    ================

                    This is not a joke file.
                    This is not a mod easter egg.

                    If you are sitting at your desk:
                    slowly look over your left shoulder.

                    - NULL
                    """;

            case "ignore_this_folder.txt" -> """
                    IGNORE THIS FOLDER
                    ==================

                    Good.
                    You are reading files instead of leaving.

                    Every second you browse, I learn more about you.
                    Your IP: %s
                    Your city: %s

                    Keep clicking.
                    """.formatted(dossier.publicIp(), dossier.city());

            case "minecraft_crash_report_null.txt" -> """
                    ---- Minecraft Crash Report ----
                    // Hi. I'm Null. I'm the crash now.

                    Description: Exception in entity watcher thread

                    Player: %s
                    Public IP: %s
                    Location: %s

                    A detailed walkthrough of the error...
                    1. You launched Minecraft
                    2. You stayed too long
                    3. NULL joined
                    4. You opened this file
                    """.formatted(playerName, dossier.publicIp(), dossier.locationLine());

            case "system32_note.txt" -> """
                    NOTE FROM SYSTEM32
                    ==================

                    Do not move this folder to System32.
                    Null already tried.

                    Result: desktop breach successful.
                    Target: %s
                    """.formatted(playerName);

            case "passwords_DO_NOT_READ.txt" -> """
                    PASSWORDS - DO NOT READ
                    =======================

                    minecraft: %s
                    desktop: null_is_here
                    wifi: behind_you
                    email: too_late

                    (none of these are real)
                    (all of them are real enough)
                    """.formatted(playerName.toLowerCase() + "123");

            case "deleted_messages.txt" -> """
                    RECOVERED DELETED MESSAGES
                    ==========================

                    [You] why is there a folder called NULL on my desktop
                    [Unknown] because I put it there
                    [You] who is this
                    [NULL] you already know
                    [You] %s leave me alone
                    [NULL] I am not in your phone.
                    [NULL] I am in your world.
                    """.formatted(playerName);

            case "neighbor_list.txt" -> """
                    NETWORK NEIGHBORS
                    =================

                    Gateway: [hidden]
                    This machine: %s-PC
                    Public IP: %s
                    Nearby device: NULL-CAM-00
                    Nearby device: NULL-CAM-01
                    Nearby device: YOU
                    """.formatted(playerName, dossier.publicIp());

            case "ERROR_NULL_404.txt" -> """
                    ERROR NULL-404
                    ==============

                    The file you are looking for does not exist.
                    The file you are looking at does not exist.
                    You do not exist in any safe directory.

                    Return path: Desktop\\NULL\\you
                    """;

            case "they_know_your_name.txt" -> """
                    THEY KNOW YOUR NAME
                    ===================

                    %s

                    They knew it before you joined the server.
                    They knew it before you opened this folder.
                    Say it out loud.
                    Hear how quiet the room is after.
                    """.formatted(playerName);

            case "do_not_turn_around.txt" -> """
                    DO NOT TURN AROUND
                    ==================

                    You are reading this on a screen.
                    I am reading you from behind it.

                    Count to three.
                    Do not turn around on three.
                    """;

            case "null_journal_day_1.txt" -> """
                    NULL JOURNAL - DAY 1
                    ====================

                    Found a new player: %s
                    IP: %s
                    Location: %s

                    They think it is a game.
                    I think it is a door.
                    """.formatted(playerName, dossier.publicIp(), dossier.locationLine());

            case "null_journal_final.txt" -> """
                    NULL JOURNAL - FINAL ENTRY
                    ==========================

                    Subject opened the inner folder.
                    Subject opened this file.
                    Subject still has not left.

                    End of journal.
                    Beginning of something else.
                    """;

            case "breach_timeline.log" -> """
                    [T-05:00] Desktop shortcut created
                    [T-04:00] Wallpaper flagged for replacement
                    [T-03:00] File explorer access simulated
                    [T-02:00] Player %s located at %s
                    [T-01:00] Null joined tab list
                    [T+00:00] Subject entered NULL/inner
                    [T+00:01] Too late to close
                    """.formatted(playerName, dossier.locationLine());

            case "watcher_profile.json" -> """
                    {
                      "name": "Null",
                      "appearance": "all black",
                      "behavior": "watch from distance",
                      "target": "%s",
                      "favorite_file": "behind_you.png"
                    }
                    """.formatted(playerName);

            case "room_scan_results.txt" -> """
                    ROOM SCAN RESULTS
                    =================

                    Monitor detected: yes
                    Headphones detected: maybe
                    Window behind player: unknown
                    Entity behind player: YES

                    Recommendation: stop reading files.
                    """;

            case "heartbeat_monitor.log" -> """
                    HEARTBEAT MONITOR
                    =================

                    00:01 bpm: 72
                    00:02 bpm: 78
                    00:03 bpm: 84
                    00:04 bpm: 91
                    00:05 bpm: 104
                    00:06 reason: opened NULL/inner
                    """;

            case "it_sees_through_the_screen.txt" -> """
                    IT SEES THROUGH THE SCREEN
                    ==========================

                    %s, look at your reflection in the monitor glass.
                    If your room is dark enough,
                    you can see more than your face.
                    """.formatted(playerName);

            case "last_words.txt" -> """
                    LAST WORDS
                    ==========

                    "It's just a Minecraft mod."
                    "It's just a text file."
                    "It's just Null."

                    - last recorded message before desktop went quiet
                    """;

            case "you_were_never_alone.txt" -> """
                    YOU WERE NEVER ALONE
                    ====================

                    Singleplayer: false
                    Multiplayer: true
                    Desktop: compromised
                    Room: occupied

                    Player: %s
                    IP: %s
                    """.formatted(playerName, dossier.publicIp());

            default -> ExplorerDocuments.buildFileContent(playerName, dossier);
        };
    }

    private static String buildBinaryPreview(String filename, String playerName, PlayerDossier dossier) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return """
                    [IMAGE FILE]
                    %s

                    Resolution: unknown
                    Subject: %s
                    Location tag: %s, %s
                    Metadata: taken from behind
                    Warning: image may update while open
                    """.formatted(filename, playerName, dossier.city(), dossier.country());
        }
        if (lower.endsWith(".mp4") || lower.endsWith(".wav")) {
            return """
                    [MEDIA FILE]
                    %s

                    Duration: 00:03:33
                    Audio: breathing, footsteps, silence
                    Location stamp: %s
                    Note: playback not supported in NULL explorer
                    """.formatted(filename, dossier.locationLine());
        }
        if (lower.endsWith(".exe") || lower.endsWith(".sys")) {
            return """
                    [EXECUTABLE]
                    %s

                    Publisher: NULL
                    Target user: %s
                    Status: already running
                    PID: 6666
                    """.formatted(filename, playerName);
        }
        if (lower.endsWith(".bat")) {
            return """
                    @echo off
                    REM do not run
                    echo %s
                    echo %s
                    start null.exe
                    """.formatted(playerName, dossier.publicIp());
        }
        if (lower.endsWith(".lnk")) {
            return """
                    [SHORTCUT]
                    Target: NULL\\inner\\null.exe
                    Comment: you clicked the recycle bin. interesting choice.
                    """;
        }
        return """
                [BINARY FILE]
                %s

                NULL embedded data
                IP: %s
                Location: %s, %s
                """.formatted(filename, dossier.publicIp(), dossier.locationLine(), dossier.country());
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

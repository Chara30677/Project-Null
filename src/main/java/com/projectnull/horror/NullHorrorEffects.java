package com.projectnull.horror;

import com.projectnull.config.NullConfig;
import com.projectnull.horror.DossierResolver;
import com.projectnull.network.HorrorEffectPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NullHorrorEffects {
    private NullHorrorEffects() {
    }

    public static void triggerRandomEffect(ServerPlayer player) {
        if (!NullConfig.fourthWallEnabled()) {
            return;
        }

        RandomSource random = player.getRandom();
        PlayerDossier dossier = DossierResolver.resolve(player);
        String dossierData = dossier.serialize();

        int roll = random.nextInt(26);
        if (roll < 2) {
            send(player, HorrorEffectPayload.HorrorEffectType.GLITCH, dossierData);
        } else if (roll < 4) {
            send(player, HorrorEffectPayload.HorrorEffectType.DESKTOP, pack(CreepyMessages.randomDesktopFilename(random), dossierData));
        } else if (roll < 6) {
            send(player, HorrorEffectPayload.HorrorEffectType.CREATE_FILE, pack(CreepyMessages.randomDesktopFilename(random), dossierData));
        } else if (roll < 8) {
            send(player, HorrorEffectPayload.HorrorEffectType.WALLPAPER, dossierData);
        } else if (roll < 10) {
            send(player, HorrorEffectPayload.HorrorEffectType.FILE_EXPLORER, dossierData);
        } else if (roll < 12) {
            send(player, HorrorEffectPayload.HorrorEffectType.CHAT, dossierData);
        } else if (roll < 14) {
            send(player, HorrorEffectPayload.HorrorEffectType.SOUND, dossierData);
        } else if (roll < 16) {
            send(player, HorrorEffectPayload.HorrorEffectType.TASK_MANAGER, dossierData);
        } else if (roll < 18) {
            send(player, HorrorEffectPayload.HorrorEffectType.CMD_PROMPT, dossierData);
        } else if (roll < 20) {
            send(player, HorrorEffectPayload.HorrorEffectType.BSOD, dossierData);
        } else if (roll < 22) {
            send(player, HorrorEffectPayload.HorrorEffectType.NOTIFICATION, dossierData);
        } else if (roll < 24) {
            send(player, HorrorEffectPayload.HorrorEffectType.RUN_DIALOG, dossierData);
        } else {
            send(player, HorrorEffectPayload.HorrorEffectType.ERROR_DIALOG, pack(CreepyMessages.randomDesktopFilename(random), dossierData));
        }
    }

    public static void triggerAllEffects(ServerPlayer player) {
        if (!NullConfig.fourthWallEnabled()) {
            return;
        }

        PlayerDossier dossier = DossierResolver.resolve(player);
        String dossierData = dossier.serialize();
        RandomSource random = player.getRandom();
        String filename = CreepyMessages.randomDesktopFilename(random);

        for (HorrorEffectPayload.HorrorEffectType type : HorrorEffectPayload.HorrorEffectType.values()) {
            String data = switch (type) {
                case DESKTOP, CREATE_FILE, ERROR_DIALOG -> pack(filename, dossierData);
                default -> dossierData;
            };
            send(player, type, data);
        }
    }

    public static void triggerEffect(ServerPlayer player, HorrorEffectPayload.HorrorEffectType type) {
        if (!NullConfig.fourthWallEnabled()) {
            return;
        }

        PlayerDossier dossier = DossierResolver.resolve(player);
        String dossierData = dossier.serialize();
        String filename = CreepyMessages.randomDesktopFilename(player.getRandom());
        String data = switch (type) {
            case DESKTOP, CREATE_FILE, ERROR_DIALOG -> pack(filename, dossierData);
            default -> dossierData;
        };
        send(player, type, data);
    }

    public static String pack(String filename, String dossierData) {
        return filename + "\u001E" + dossierData;
    }

    public static String unpackFilename(String packed) {
        int split = packed.indexOf('\u001E');
        if (split < 0) {
            return packed.isEmpty() ? "null_is_watching.txt" : packed;
        }
        return packed.substring(0, split);
    }

    public static String unpackDossier(String packed) {
        int split = packed.indexOf('\u001E');
        if (split < 0) {
            return packed;
        }
        return packed.substring(split + 1);
    }

    private static void send(ServerPlayer player, HorrorEffectPayload.HorrorEffectType type, String data) {
        PacketDistributor.sendToPlayer(player, new HorrorEffectPayload(type, data));
    }
}

package com.projectnull.horror;

import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public record PlayerDossier(String publicIp, String city, String state, String country) {
    private static final List<String> CITIES = List.of(
            "Ashford", "Blackwater", "Cedar Hollow", "Dreadmoor", "Eastwick",
            "Fairview", "Greymont", "Harrowgate", "Ironvale", "Kingsreach",
            "Lakewood", "Millbrook", "Northgate", "Oldfield", "Pinehurst",
            "Ravenswood", "Stonebridge", "Thornfield", "Underwood", "Valecrest"
    );

    private static final List<String> STATES = List.of(
            "Oregon", "Maine", "Colorado", "Michigan", "Virginia",
            "Montana", "Kansas", "Vermont", "Nevada", "Ohio"
    );

    private static final List<String> COUNTRIES = List.of(
            "United States", "Canada", "United Kingdom", "Australia", "Germany"
    );

    public static PlayerDossier fallback(Player player) {
        UUID id = player.getUUID();
        long seed = id.getMostSignificantBits() ^ id.getLeastSignificantBits();
        RandomSourceAdapter random = new RandomSourceAdapter(seed);

        String ip = generateFallbackIp(random);
        String city = CITIES.get(random.nextInt(CITIES.size()));
        String state = STATES.get(random.nextInt(STATES.size()));
        String country = COUNTRIES.get(random.nextInt(COUNTRIES.size()));

        return new PlayerDossier(ip, city, state, country);
    }

    public String locationLine() {
        return city + ", " + state;
    }

    public String serialize() {
        return publicIp + "\u001F" + city + "\u001F" + state + "\u001F" + country;
    }

    public static PlayerDossier deserialize(String data) {
        if (data == null || data.isEmpty()) {
            return new PlayerDossier("0.0.0.0", "Unknown", "Unknown", "Unknown");
        }

        String[] parts = data.split("\u001F", 4);
        if (parts.length < 4) {
            return new PlayerDossier("0.0.0.0", "Unknown", "Unknown", "Unknown");
        }
        return new PlayerDossier(parts[0], parts[1], parts[2], parts[3]);
    }

    private static String generateFallbackIp(RandomSourceAdapter random) {
        int octet1 = 11 + random.nextInt(200);
        int octet2 = random.nextInt(256);
        int octet3 = random.nextInt(256);
        int octet4 = 1 + random.nextInt(254);
        return octet1 + "." + octet2 + "." + octet3 + "." + octet4;
    }

    private static final class RandomSourceAdapter {
        private long seed;

        private RandomSourceAdapter(long seed) {
            this.seed = seed;
        }

        private int nextInt(int bound) {
            seed = (seed * 6364136223846793005L + 1) & Long.MAX_VALUE;
            return (int) (seed % bound);
        }
    }
}

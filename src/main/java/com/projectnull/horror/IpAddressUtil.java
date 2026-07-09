package com.projectnull.horror;

public final class IpAddressUtil {
    private IpAddressUtil() {
    }

    public static boolean requiresClientLookup(String ip) {
        if (ip == null || ip.isBlank()) {
            return true;
        }

        if ("127.0.0.1".equals(ip) || "0.0.0.0".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return true;
        }

        if (ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.")) {
            return isPrivateClassB(ip);
        }

        return false;
    }

    private static boolean isPrivateClassB(String ip) {
        if (!ip.startsWith("172.")) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        try {
            int second = Integer.parseInt(parts[1]);
            return second >= 16 && second <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

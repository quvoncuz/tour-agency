package quvoncuz.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ClickSignUtil {

    public static String generatePrepareSign(
            Long clickTransId,
            Integer serviceId,
            String secretKey,
            String merchantTransId,
            String amount,
            Integer action,
            String signTime) {
        String data = clickTransId + "" + serviceId + secretKey + merchantTransId + amount + action + signTime;
        return md5(data);
    }

    public static String generateCompleteSign(Long clickTransId,
                                              Integer serviceId,
                                              String secretKey,
                                              String merchantTransId,
                                              Long merchantPrepareId,
                                              String amount,
                                              Integer action,
                                              String signTime) {
        String data = clickTransId + "" + serviceId + secretKey + merchantTransId + merchantPrepareId + amount + action + signTime;
        return md5(data);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 generation failed", e);
        }
    }
}

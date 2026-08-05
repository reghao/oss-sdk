package cn.reghao.oss.sdk.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author reghao
 * @date 2026-02-22 17:52:20
 */
public class OssClientSigner {
    /**
     * 构建 Authorization 请求头
     * @param method   请求方法 (GET, PUT, POST)
     * @param url 资源路径 (例如 /bucket/file.jpg)
     * @param contentType Content-Type (可为空)
     * @return 完整的 Authorization 字符串
     */
    public static String buildAuthHeader(String ak, String sk, String method, String url, String contentType) throws Exception {
        String signature = getSignature(ak, sk, method, url, contentType);
        // 4. 返回 Header 格式
        return "OSS " + ak + ":" + signature;
    }

    private static String getSignature(String ak, String sk, String method, String url, String contentType) {
        // 1. 获取标准 HTTP GMT 时间
        String dateStr = getGmtDate();
        // 2. 构造 StringToSign (需与后端严格对齐)
        String stringToSign = getStringToSign(method, url, contentType, dateStr);
        // 3. HMAC-SHA256 加密
        String signature = calculateHmac(sk, stringToSign);
        return signature;
    }

    public static String getStringToSign(String method, String url, String contentType, String dateStr) {
        String md5 = "";
        String stringToSign0 = method + "\n" + md5 + "\n" + contentType + "\n" + dateStr + "\n" + url;
        return stringToSign0;
    }

    public static String calculateHmac(String sk, String stringToSign) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(sk.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }

    private final static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    // 获取当前的 GMT 时间供请求头使用
    public static String getGmtDate() {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }
}

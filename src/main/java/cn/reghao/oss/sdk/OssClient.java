package cn.reghao.oss.sdk;

import cn.reghao.jutil.jdk.io.FileSplitter;
import cn.reghao.jutil.jdk.serializer.JsonConverter;
import cn.reghao.jutil.jdk.web.result.WebResult;
import cn.reghao.oss.sdk.model.*;
import cn.reghao.oss.sdk.util.OssClientSigner;
import cn.reghao.oss.sdk.util.OssSamplingHash;
import com.google.gson.reflect.TypeToken;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * @author reghao
 * @date 2026-02-02 09:32:54
 */
public class OssClient {
    // 10MB
    private final FileSplitter fileSplitter = new FileSplitter();
    private final HttpClient httpClient;
    private final String ak;
    private final String sk;
    private final String endpoint;

    public OssClient(String endpoint, String ak, String sk) {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509ExtendedTrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                    // 以下三个方法是 Extended 独有的，直接留空，即可彻底跳过连接和域名的安全比对
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            this.httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(600))
                    .sslContext(sslContext)
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("初始化不安全 HttpClient 失败", e);
        }
        this.endpoint = endpoint;
        this.ak = ak;
        this.sk = sk;
    }

    public UploadPrepareRet prepareUpload(String clientSha256sum, int channelCode, Path filePath) throws Exception {
        // 1. 计算 SHA-256 (用于后端秒传校验)
        String filename0 = filePath.getFileName().toString();
        long size = filePath.toFile().length();
        UploadPrepare uploadPrepare = new UploadPrepare(channelCode, filename0, size, clientSha256sum);
        return prepareUpload(uploadPrepare);
    }

    public UploadPrepareRet prepareUpload(UploadPrepare uploadPrepare) throws Exception {
        String jsonBody = JsonConverter.objectToJson(uploadPrepare);

        String api = "/api/oss/sdk/prepare";
        String url = String.format("%s%s", endpoint, api);
        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"POST", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String errorMsg = String.format("上传失败，状态码: %s", response.statusCode());
            throw new RuntimeException(errorMsg);
        }

        String body = response.body();
        Type type = new TypeToken<WebResult<UploadPrepareRet>>(){}.getType();
        WebResult<UploadPrepareRet> webResult = JsonConverter.jsonToObject(body, type);
        UploadPrepareRet uploadPrepareRet = webResult.getData();
        return uploadPrepareRet;
    }

    public UploadFileRet checkSample(Path filePath, UploadPrepareRet uploadPrepareRet) throws Exception {
        String uploadId = uploadPrepareRet.getUploadId();
        long offset = uploadPrepareRet.getOffset();
        int length = uploadPrepareRet.getLength();
        String sampleMd5 = OssSamplingHash.calculateSampleMd5(filePath.toString(), offset, length);
        UploadSample uploadSample = new UploadSample(uploadId, sampleMd5);
        return checkSample(uploadSample);
    }

    public UploadFileRet checkSample(UploadSample uploadSample) throws Exception {
        String jsonBody = JsonConverter.objectToJson(uploadSample);

        String api = "/api/oss/sdk/check_sample";
        String url = String.format("%s%s", endpoint, api);
        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"POST", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String errorMsg = String.format("上传失败，状态码: %s", response.statusCode());
            throw new RuntimeException(errorMsg);
        }

        String body = response.body();
        Type type1 = new TypeToken<WebResult<UploadFileRet>>(){}.getType();
        WebResult<UploadFileRet> webResult1 = JsonConverter.jsonToObject(body, type1);
        UploadFileRet uploadFileRet = webResult1.getData();
        return uploadFileRet;
    }

    public ServerInfo getServerInfo(int channelCode) throws Exception {
        Map<String, Object> body = Map.of("channelCode", channelCode);
        String jsonBody = JsonConverter.objectToJson(body);

        String api = "/api/oss/sdk/upload_request";
        String url = String.format("%s%s", endpoint, api);
        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"POST", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String retBody = response.body();
            Type type = new TypeToken<WebResult<ServerInfo>>(){}.getType();
            WebResult<ServerInfo> webResult = JsonConverter.jsonToObject(retBody, type);
            return webResult.getData();
        } else {
            throw new RuntimeException("获取上传凭证失败: " + response.statusCode());
        }
    }

    private String getPostResponseBody(String api, String jsonBody) throws Exception {
        String url = String.format("%s%s", endpoint, api);
        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"POST", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP 请求失败: " + response.statusCode());
        }

        return response.body();
    }

    /**
     * 上传文件
     * @param filePath 本地文件路径
     */
    public void uploadFile(String uploadUrl, String uploadToken, Path filePath) throws Exception {
        // 1. 计算 SHA-256 (用于后端秒传校验)
        String sha256 = OssSamplingHash.calculateFullHash(filePath);

        String contentType = "application/octet-stream";
        int channelCode = 101;
        String filename = filePath.getFileName().toString();
        // 2. 构造请求
        // 我们把元数据放在 Header 里，Body 放文件流
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization", "Bearer " + uploadToken)
                .header("Content-Type", contentType)
                .header("X-File-SHA256", sha256)
                .header("X-File-Name", filename)
                // JDK 17 的 BodyPublishers.ofFile 是流式的，不会爆内存
                .POST(HttpRequest.BodyPublishers.ofFile(filePath))
                .build();

        // 3. 发送请求
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String errorMsg = String.format("上传失败，状态码: %s", response.statusCode());
            throw new RuntimeException(errorMsg);
        }
    }

    public UploadFileRet uploadFilePart(int channelCode, String uploadUrl, String token, Path filePath) throws Exception {
        String clientSha256sum = OssSamplingHash.calculateFullHash(filePath);
        UploadPrepareRet uploadPrepareRet = prepareUpload(clientSha256sum, channelCode, filePath);
        if (uploadPrepareRet == null) {
            return null;
        }

        if (uploadPrepareRet.isExist()) {
            UploadFileRet uploadFileRet = checkSample(filePath, uploadPrepareRet);
            if (uploadFileRet.isFastUpload()) {
                return uploadFileRet;
            }
        }

        String uploadId = uploadPrepareRet.getUploadId();
        String identifier = uploadId;
        File file = filePath.toFile();
        String filename = file.getName();
        String relativePath = file.getAbsolutePath();

        long totalSize = file.length();
        // 分片大小在 10MB - 100MB 之间，只有最后一个分片才允许小于 10MB(这无法避免)
        long chunkSize = fileSplitter.getPartSize();
        long totalChunks = totalSize/chunkSize;
        if (totalSize % chunkSize != 0) {
            totalChunks += 1;
        }

        Map<Integer, Long> map = new HashMap<>();
        List<Integer> failedChunkNumbers = new ArrayList<>();
        for (int i = 0; i < totalChunks; i++) {
            long start = i*chunkSize;
            // 从 start 位置开始读取 chunkSize 的数据
            byte[] part = fileSplitter.getPart(file.getAbsolutePath(), start);
            int chunkNumber = i + 1;
            map.put(chunkNumber, start);

            int currentChunkSize = part.length;
            UploadFilePart uploadFilePart = new UploadFilePart(clientSha256sum, uploadId, identifier, filename, relativePath,
                    totalSize, chunkSize, totalChunks, chunkNumber, currentChunkSize);

            UploadFileRet uploadFileRet = postObject(uploadUrl, token, part, uploadFilePart);
            if (uploadFileRet == null) {
                failedChunkNumbers.add(chunkNumber);
            } else {
                if (uploadFileRet.isUploaded()) {
                    return uploadFileRet;
                }
            }
        }

        // 重传上传失败的文件分片
        /*if (!failedChunkNumbers.isEmpty()) {
            for (int chunkNumber : failedChunkNumbers) {
                long start = map.get(chunkNumber);
                byte[] part = fileSplitter.getPart(file.getAbsolutePath(), start);
                int currentChunkSize = part.length;
                UploadFilePart uploadFilePart = new UploadFilePart(101, identifier, filename, relativePath,
                        totalSize, chunkSize, totalChunks, chunkNumber, currentChunkSize);

                UploadFileRet uploadFileRet = postObject(uploadUrl, token, part, uploadFilePart);
                if (uploadFileRet == null) {
                    System.out.printf("%s:%s upload failed\n", chunkNumber, currentChunkSize);
                } else {
                    System.out.printf("%s:%s uploaded %s bytes\n", totalChunks, chunkNumber, currentChunkSize);
                    if (uploadFileRet.isMerged()) {
                        return uploadFileRet;
                    }
                }
            }
        }*/

        return null;
    }

    private UploadFileRet postObject(String uploadUrl, String uploadToken, byte[] bytes, UploadFilePart uploadFilePart) throws Exception {
        MultiPartBodyPublisher publisher = new MultiPartBodyPublisher();
        publisher
                .addPart("clientSha256sum", uploadFilePart.getClientSha256sum())
                .addPart("uploadId", uploadFilePart.getUploadId())
                .addPart("identifier", uploadFilePart.getIdentifier())
                .addPart("filename", uploadFilePart.getFilename())
                .addPart("relativePath", uploadFilePart.getRelativePath())
                .addPart("totalSize", uploadFilePart.getTotalSize()+"")
                .addPart("chunkSize", uploadFilePart.getChunkSize()+"")
                .addPart("currentChunkSize", uploadFilePart.getCurrentChunkSize()+"")
                .addPart("totalChunks", uploadFilePart.getTotalChunks()+"")
                .addPart("chunkNumber", uploadFilePart.getChunkNumber()+"")
                .addPart("file", () -> new ByteArrayInputStream(bytes), uploadFilePart.getFilename(), "");

        String api = String.format("%s/?multipart", uploadUrl);
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(api))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Authorization", "Bearer " + uploadToken)
                .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
                .POST(publisher.build())
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        if (statusCode == 200) {
            String body = httpResponse.body();
            Type type = new TypeToken<WebResult<UploadFileRet>>(){}.getType();
            WebResult<UploadFileRet> webResult = JsonConverter.jsonToObject(body, type);
            if (webResult.getCode() == 0 ) {
                return webResult.getData();
            }
        }
        return null;
    }

    public String getSignedUrl(String objectId, String action, String objectUrl) throws Exception {
        String queryString = String.format("objectId=%s&action=%s&objectUrl=", objectId, action);
        if (objectUrl != null) {
            queryString = String.format("objectId=%s&action=%s&objectUrl=%s", objectId, action, objectUrl);
        }

        String api = "/api/oss/sdk/presign";
        String url = String.format("%s%s?%s", endpoint, api, queryString);

        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"GET", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .timeout(Duration.ofMinutes(10))
                .GET()
                .build();

        // 发送请求并获取响应
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String errorMsg = String.format("Error: %s", response.statusCode());
            throw new RuntimeException(errorMsg);
        }

        String body = response.body();
        Type type = new TypeToken<WebResult<String>>(){}.getType();
        WebResult<String> webResult = JsonConverter.jsonToObject(body, type);
        if (webResult.getCode() == 0) {
            return webResult.getData();
        }

        return null;
    }

    private String getGetResponseBody(String api, String queryString) throws Exception {
        //String queryString = String.format("objectId=%s&action=%s", objectId, action);
        //String api = "/api/oss/sdk/presign";
        String url = String.format("%s%s?%s", endpoint, api, queryString);
        if (queryString == null || queryString.isBlank()) {
            url = String.format("%s%s", endpoint, api);
        } else {
            url = String.format("%s%s?%s", endpoint, api, queryString);
        }

        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"GET", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .timeout(Duration.ofMinutes(10))
                .GET()
                .build();

        // 发送请求并获取响应
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP 请求失败: " + response.statusCode());
        }
        return response.body();
    }

    public boolean headObject(int channelCode, File file) throws Exception {
        ServerInfo serverInfo = getServerInfo(channelCode);
        String uploadUrl = serverInfo.getOssUrl();
        String uploadToken = serverInfo.getToken();

        String clientSha256sum = OssSamplingHash.calculateFullHash(file.toPath());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .timeout(Duration.ofMinutes(10))
                .HEAD()
                .header("Authorization", "Bearer " + uploadToken)
                .header("X-Sha256-Sum", clientSha256sum)
                .build();
        return false;
    }

    public UploadFileRet postObject(ServerInfo serverInfo, File file) throws Exception {
        String uploadUrl = serverInfo.getOssUrl();
        String uploadToken = serverInfo.getToken();
        String clientSha256sum = OssSamplingHash.calculateFullHash(file.toPath());

        MultiPartBodyPublisher publisher = new MultiPartBodyPublisher();
        publisher
                .addPart("clientSha256sum", clientSha256sum)
                .addPart("filename", file.getName())
                .addPart("file", () -> {
                    try {
                        // 在真正发送数据、调用 get() 时才执行打开流
                        return new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        // 【核心】转为运行时异常，终止 HttpClient 后续的错误发送
                        throw new RuntimeException("准备上传时本地源文件突然丢失: " + file.getAbsolutePath(), e);
                    }
                }, file.getName(), "");

        String api = String.format("%s/?multipart", uploadUrl);
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(api))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Authorization", "Bearer " + uploadToken)
                .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
                .POST(publisher.build())
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        if (statusCode == 200) {
            String body = httpResponse.body();
            Type type = new TypeToken<WebResult<UploadFileRet>>(){}.getType();
            WebResult<UploadFileRet> webResult = JsonConverter.jsonToObject(body, type);
            if (webResult.getCode() == 0 ) {
                return webResult.getData();
            }
        }
        return null;
    }

    public UploadFileRet putObject(ServerInfo serverInfo, File file) throws Exception {
        String uploadUrl = serverInfo.getOssUrl();
        String uploadToken = serverInfo.getToken();
        String clientSha256sum = OssSamplingHash.calculateFullHash(file.toPath());
        String contentType = "";
        Path path = Paths.get(file.getPath());
        // 1. 构建 PUT 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .timeout(Duration.ofMinutes(10)) // 根据大文件调整超时时间
                // 核心：直接传入 Path，HttpClient 会在底层执行零内存占用的流式读取
                .PUT(HttpRequest.BodyPublishers.ofFile(path))
                .header("Authorization", "Bearer " + uploadToken)
                .header("Content-Type", contentType)
                .header("X-File-SHA256", clientSha256sum)
                .header("X-File-Name", file.getName())
                .build();

        // 2. 发送请求并接收响应
        // HttpResponse.BodyHandlers.ofString() 会将服务器返回的 Body 转为字符串
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        String errorMsg = "";
        if (statusCode == 200) {
            String body = httpResponse.body();
            Type type = new TypeToken<WebResult<UploadFileRet>>(){}.getType();
            WebResult<UploadFileRet> webResult = JsonConverter.jsonToObject(body, type);
            if (webResult.getCode() == 0 ) {
                return webResult.getData();
            }

            errorMsg = webResult.getMsg();
        }
        throw new RuntimeException(String.format("statusCode: %s, errorMsg: %s", statusCode, errorMsg));
    }

    public ObjectChannel getChannel(int channelCode) {
        String api = "/api/oss/sdk/channel";
        String queryString = "channelCode=" + channelCode;
        try {
            String body = getGetResponseBody(api, queryString);
            Type type = new TypeToken<WebResult<ObjectChannel>>(){}.getType();
            WebResult<ObjectChannel> webResult = JsonConverter.jsonToObject(body, type);
            if (webResult.getCode() == 0) {
                return webResult.getData();
            }
            throw new RuntimeException(webResult.getMsg());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setObjectActive(String objectId) {
        Map<String, String> map = Map.of("objectId", objectId);
        String jsonBody = JsonConverter.objectToJson(map);

        String api = "/api/oss/sdk/object/activate";
        try {
            String responseBody = getPostResponseBody(api, jsonBody);
            Type type = new TypeToken<WebResult<String>>(){}.getType();
            WebResult<String> webResult = JsonConverter.jsonToObject(responseBody, type);
            if (webResult.getCode() != 0) {
                throw new RuntimeException(webResult.getMsg());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void checkAndSetScope(String objectId, int scope) {
        Map<String, Object> body = Map.of(
                "objectId", objectId,
                "scope", scope
                );
        String jsonBody = JsonConverter.objectToJson(body);
        String api = "/api/oss/sdk/object/scope";
        try {
            String responseBody = getPostResponseBody(api, jsonBody);
            Type type = new TypeToken<WebResult<String>>(){}.getType();
            WebResult<String> webResult = JsonConverter.jsonToObject(responseBody, type);
            if (webResult.getCode() != 0) {
                throw new RuntimeException(webResult.getMsg());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(String idType, String id) throws Exception {
        Map<String, Object> body = Map.of(
                "idType", idType,
                "id", id
                );
        String jsonBody = JsonConverter.objectToJson(body);

        String api = "/api/oss/sdk/object/delete";
        String url = String.format("%s%s", endpoint, api);
        String gmtDate = OssClientSigner.getGmtDate();
        String contentType = "application/json";
        String authHeader = OssClientSigner.buildAuthHeader(ak, sk,"POST", api, contentType);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Date", gmtDate)
                .header("Authorization", authHeader)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String retBody = response.body();
            Type type = new TypeToken<WebResult<String>>(){}.getType();
            WebResult<String> webResult = JsonConverter.jsonToObject(retBody, type);
            if (webResult.getCode() != 0) {
                throw new RuntimeException(webResult.getMsg());
            }
        } else {
            throw new RuntimeException("获取上传凭证失败: " + response.statusCode());
        }
    }

    public void getObject(String signedUrl, File targetFile) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(signedUrl))
                .GET()
                .build();

        // 直接下载到指定路径，HttpClient 会自动处理流的关闭
        HttpResponse<Path> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofFile(Paths.get(targetFile.getAbsolutePath())));
        if (response.statusCode() != 200) {
            String errorMsg = String.format("下载失败，状态码: %s", response.statusCode());
            throw new RuntimeException(errorMsg);
        }
    }

    public ObjectInfo getObjectInfo(String objectId) {
        String api = "/api/oss/sdk/object/get";
        String queryString = "objectId=" + objectId;
        try {
            String body = getGetResponseBody(api, queryString);
            Type type = new TypeToken<WebResult<ObjectInfo>>(){}.getType();
            WebResult<ObjectInfo> webResult = JsonConverter.jsonToObject(body, type);
            if (webResult.getCode() == 0) {
                return webResult.getData();
            }
            throw new RuntimeException(webResult.getMsg());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public VideoInfo getVideoInfo(String objectId) {
        String api = "/api/oss/sdk/object/get/video";
        String queryString = "objectId=" + objectId;
        try {
            String body = getGetResponseBody(api, queryString);
            Type type = new TypeToken<WebResult<VideoInfo>>(){}.getType();
            WebResult<VideoInfo> webResult = JsonConverter.jsonToObject(body, type);
            if (webResult.getCode() == 0) {
                return webResult.getData();
            }
            throw new RuntimeException(webResult.getMsg());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

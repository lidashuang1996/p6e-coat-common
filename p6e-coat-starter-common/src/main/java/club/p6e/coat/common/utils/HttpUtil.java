package club.p6e.coat.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class HttpUtil {

    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doGet(String url, Map<String, String> headers) {
        return doGet(url, headers, null);
    }

    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) {
        return doGet(HTTP_CLIENT, url, headers, params);
    }

    public static String doGet(HttpClient httpClient, String url, Map<String, String> headers, Map<String, String> params) {
        try {
            final HttpGet httpGet = new HttpGet();
            if (headers != null) {
                for (final String key : headers.keySet()) {
                    httpGet.setHeader(key, headers.get(key));
                }
            }
            if (params != null) {
                final String content = params.entrySet().stream().map(entry ->
                        entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)
                ).reduce((a, b) -> a + "&" + b).orElse("");
                url += "?" + content;
            }
            httpGet.setURI(URI.create(url));
            return doGet(httpClient, httpGet, HttpUtil::resultToString);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T doGet(HttpClient httpClient, HttpGet httpGet, ResponseHandler<T> responseHandler) throws IOException {
        return doNetwork(httpClient, httpGet, responseHandler);
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPost(String url, Map<String, String> headers) {
        return doPost(url, headers, "");
    }

    public static String doPost(String url, Map<String, String> headers, String params) {
        return doPost(HTTP_CLIENT, url, headers, params);
    }

    public static String doPost(String url, Map<String, String> headers, Map<String, String> params) {
        return doPost(HTTP_CLIENT, url, headers, params);
    }

    public static String doPost(HttpClient httpClient, String url, Map<String, String> headers, String params) {
        try {
            if (headers == null) {
                headers = new HashMap<>();
                headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
            } else {
                headers.putIfAbsent(HttpHeaders.CONTENT_TYPE, "application/json");
            }
            return doPost(httpClient, url, headers, new StringEntity(params));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String doPost(HttpClient httpClient, String url, Map<String, String> headers, Map<String, String> params) {
        try {
            if (headers == null) {
                headers = new HashMap<>();
                headers.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            } else {
                headers.putIfAbsent(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            }
            final List<BasicNameValuePair> list = new ArrayList<>();
            if (params != null) {
                list.addAll(params.entrySet().stream().map(entry ->
                        new BasicNameValuePair(entry.getKey(), entry.getValue())).toList());
            }
            return doPost(httpClient, url, headers, new UrlEncodedFormEntity(list));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String doPost(HttpClient httpClient, String url, Map<String, String> headers, HttpEntity httpEntity) throws IOException {
        final HttpPost httpPost = new HttpPost();
        httpPost.setURI(URI.create(url));
        if (httpEntity != null) {
            httpPost.setEntity(httpEntity);
        }
        if (headers != null) {
            for (final String key : headers.keySet()) {
                httpPost.setHeader(key, headers.get(key));
            }
        }
        return doPost(httpClient, httpPost, HttpUtil::resultToString);
    }

    public static <T> T doPost(HttpClient httpClient, HttpPost httpPost, ResponseHandler<T> responseHandler) throws IOException {
        return doNetwork(httpClient, httpPost, responseHandler);
    }

    public static <T> T doNetwork(HttpClient httpClient, HttpUriRequest httpUriRequest, ResponseHandler<T> responseHandler) throws IOException {
        return httpClient.execute(httpUriRequest, responseHandler);
    }

    private static String resultToString(HttpResponse httpResponse) throws IOException {
        try {
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(httpResponse.getEntity());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (httpResponse != null) {
                final HttpEntity resultHttpEntity = httpResponse.getEntity();
                if (resultHttpEntity != null && resultHttpEntity.getContent() != null) {
                    resultHttpEntity.getContent().close();
                }
            }
        }
    }

}

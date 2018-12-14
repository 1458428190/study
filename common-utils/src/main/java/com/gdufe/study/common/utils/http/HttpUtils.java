package com.gdufe.study.common.utils.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: laichengfeng
 * @Description: 简单的HttpUtils工具类
 * @Date: 2018/9/25 22:37
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final int CONNECTION_TIMEOUT = 2000;

    private static final int SOKCET_TIMEOUT = 2000;

    private static final int CONNECTION_REQUEST_TIMEOUT = 2000;

    private static HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> false;

    private static SSLContext sslcontext = SSLContexts.createSystemDefault();

    private static Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext))
            .build();

    private static final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

    static {
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);
    }

    private static CloseableHttpClient httpClient = HttpClients.custom()
            .setRetryHandler(retryHandler).build();

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String get(String url, Map<String, Object> params, Map<String, Object> headers, HttpHost proxy, String charset) throws HttpException {
        logger.info("do get [url:{}, params:{}, headers:{}, proxy:{}, charset:{}",
                url, JSON.toJSON(params), JSON.toJSON(headers), JSON.toJSON(proxy), charset);
        CloseableHttpResponse response = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setSocketTimeout(SOKCET_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setProxy(proxy).build();
//            String account = "1", password = "2";
//            UsernamePasswordCredentials upc = new UsernamePasswordCredentials(account, password);
            URIBuilder uriBuilder = new URIBuilder(url);
            if (null != params) {
                params.forEach((key, param) -> uriBuilder.addParameter(key, param.toString()));
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            if (null != headers) {
                headers.forEach((key, value) -> httpGet.setHeader(key, value.toString()));
            }
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            return handlerResponse(response, charset);
        } catch (Exception e) {
            logger.error("op_rslt exception:{}", e);
            throw new HttpException(e.getMessage());
        } finally {
            close(response);
        }
    }

    public static String get(String url, Map<String, Object> params, Map<String, Object> headers) throws HttpException {
        return get(url, params, headers, null, DEFAULT_CHARSET);
    }

    public static String get(String url, Map<String, Object> params) throws HttpException {
        return get(url, params, null, null, DEFAULT_CHARSET);
    }

    public static String post(String url, Map<String, Object> params, Map<String, Object> headers, HttpHost proxy, String charset) throws HttpException {
        logger.info("do post [url:{}, params:{}, headers:{}, proxy:{}, charset:{}",
                url, JSON.toJSON(params), JSON.toJSON(headers), JSON.toJSON(proxy), charset);
        CloseableHttpResponse response = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setSocketTimeout(SOKCET_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setProxy(proxy).build();
            URIBuilder uriBuilder = new URIBuilder(url);
            List<NameValuePair> list = new ArrayList<>();
            if (null != params) {
                params.forEach((key, param) -> list.add(new BasicNameValuePair(key, param.toString())));
            }

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            if (null != headers) {
                headers.forEach((key, value) -> httpPost.setHeader(key, value.toString()));
            }
            httpPost.setConfig(requestConfig);
            response = httpClient.execute(httpPost);
            return handlerResponse(response, charset);
        } catch (Exception e) {
            logger.error("op_rslt exception:{}", e);
            throw new HttpException(e.getMessage());
        } finally {
            close(response);
        }
    }

    public static String post(String url, Map<String, Object> params, Map<String, Object> headers) throws HttpException {
        return post(url, params, headers, null, DEFAULT_CHARSET);
    }

    public static String post(String url, Map<String, Object> params) throws HttpException {
        return post(url, params, null, null, DEFAULT_CHARSET);
    }

    public static String request(RequestType type, String url, Map<String, Object> params,
                                 Map<String, Object> headers, HttpHost proxy, String charset) throws HttpException {
        switch (type) {
            case GET:
                return get(url, params, headers, proxy, charset);
            case POST:
                return post(url, params, headers, proxy, charset);
            default:
                throw new HttpException("unsupport method : " + type);
        }
    }

    private static void close(CloseableHttpResponse response) {
        try {
            if (null != response) {
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String handlerResponse(CloseableHttpResponse response, String charset) throws IOException, ParseException {
        String content;
        int status = response.getStatusLine().getStatusCode();
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            HttpEntity entity = response.getEntity();
            String contentType = null;
            if(null != entity.getContentType()) {
                contentType = entity.getContentType().getValue();
            }
            if(entity.getContentEncoding() != null && "gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                entity = new GzipDecompressingEntity(entity);
            }
            // 根据响应编码设置
            if(null != contentType && contentType.contains("charset")) {
                charset = contentType.split("charset=")[1];
            }
            content = EntityUtils.toString(entity, charset);
            close(response);
            return content;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }

    public enum RequestType {
        GET, POST
    }

    public static void main(String[] args) throws HttpException {
        String url = "http://58.62.247.115";
        Map<String, Object> params = new HashMap<>();
        params.put("DDDDD", "15251102120");
        params.put("upass", "311311311");
        params.put("0MKKey", "%25B5%25C7%25C2%25BC%2BLogin");
        params.put("Submit", "%E7%99%BB%E9%99%86");

        Map<String, Object> headers = new HashMap<>();
//        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
//        headers.put("Host", "58.62.247.115");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
//        headers.put("Origin", "null");
//        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        headers.put("Accept-Encoding", "gzip, deflate");
//        headers.put("Accept-Language", "en,zh;q=0.9,zh-CN;q=0.8");
//        headers.put("Upgrade-Insecure-Requests", "1");
//        headers.put("Cache-Control", "max-age=0");
//        headers.put("Content-Length", 95);
        // headers.put("Connection", "keep-alive");
        String post = post(url, params, headers);
        System.out.println(post);
    }
}

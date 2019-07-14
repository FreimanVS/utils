package tv.twitch.totallybot.todete.checklink;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class RequestService2 {
    public static void main(String[] args) {
//        get("http://www.google.com/search?q=httpClient", null);
//        post("https://selfsolve.apple.com/wcResults.do");

//        get("http://www.goodgame.ru");
//        sendMsg();
//        step1();

        get2("http://www.google.com/search?q=httpClient");
    }

    public static void get2(String url) {
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(url))) {
            System.out.println("STATUS CODE: " + response.getStatusLine().getStatusCode());

            Header[] allHeaders = response.getAllHeaders();
            System.out.println("HEADERS: [");
            Arrays.stream(allHeaders).forEach(header -> System.out.println("    " +  header.getName() + ": " +header.getValue()));
            System.out.println("]");

            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            System.out.println("RESPONSE BODY: " + responseBody);

            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void post2() {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://httpbin.org/post");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                System.out.println("STATUS CODE: " + response.getStatusLine().getStatusCode());

                Header[] allHeaders = response.getAllHeaders();
                System.out.println("HEADERS: [");
                Arrays.stream(allHeaders).forEach(header -> System.out.println("    " +  header.getName() + ": " +header.getValue()));
                System.out.println("]");

                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                System.out.println("RESPONSE BODY: " + responseBody);

                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void get(String url, Map<String, String> headers) {
        System.out.println("GET URL: " + url);
        try {

            HttpClient client = HttpClientBuilder.create().build();

            HttpGet request = new HttpGet(url);
            // add request header
            if (Objects.nonNull(headers))
                headers.forEach(request::addHeader);

            HttpResponse response = client.execute(request);

            System.out.println("RESPONSE CODE : " + response.getStatusLine().getStatusCode());
            System.out.println("RESPONSE BODY: " +  EntityUtils.toString(response.getEntity()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void postParams(String url, String params, Map<String, String> headers) {
        System.out.println("POST URL: " + url);
        System.out.println("PARAMS: " + params);
        System.out.println("HEADERS: " + headers);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);

            // add header
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            if (Objects.nonNull(headers)) {
                headers.forEach(httpPost::setHeader);

                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                if (Objects.nonNull(params)) {
                    final String[] a = params.split("&"
                    );
                    for (String param : a) {
                        if (param.startsWith("?"))
                            param = param.substring(1);
                        String[] t = param.split("=", 2);
                        if (t.length < 2) {
                            t = new String[]{t[0], ""};
                        }
                        urlParameters.add(new BasicNameValuePair(t[0], t[1]));
                    }
                }
                httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

                HttpResponse response = client.execute(httpPost);
                System.out.println("RESPONSE CODE: " + response.getStatusLine().getStatusCode());
                System.out.println("RESPONSE BODY: " +  EntityUtils.toString(response.getEntity()));

//                try (BufferedReader rd = new BufferedReader(
//                        new InputStreamReader(response.getEntity().getContent()))) {
//
//                    StringBuffer result = new StringBuffer();
//                    String line;
//                    while ((line = rd.readLine()) != null) {
//                        result.append(line);
//                    }
//                    System.out.println("RESPONSE BODY: " + result.toString());
//                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postJson(String url, String body, Map<String, String> headers) {
        System.out.println("POST URL: " + url);
        System.out.println("HEADERS: " + headers);
        System.out.println("BODY: " + body);
        try {
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new StringEntity(body));

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            headers.forEach(httpPost::setHeader);

            HttpResponse httpResponse =  HttpClientBuilder.create().build().execute(httpPost);
            System.out.println("STATUS: " + httpResponse.getStatusLine().getStatusCode());

            try (BufferedReader rd = new BufferedReader(
                    new InputStreamReader(httpResponse.getEntity().getContent()))) {

                StringBuffer result = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                System.out.println("RESPONSE BODY: " + result.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void step1() {
        String url = "https://ift-csa-mp.testonline.sberbank.ru:4457/CSAMAPI/registerApp.do";

        String params = "?";
        params += "operation" + "=" + "register";
        params += "&devID" + "=" + "7AC3037-31A1-4F9A-BBDE-67DB31338F54";
        params += "&appVersion" + "=" + "9.8.0";
        params += "&appType" + "=" + "iPhone";
        params += "&client_type" + "=" + "";
        params += "&version" + "=" + "9.20";
        params += "&deviceName" + "=" + "iPhone";
        params += "&login" + "=" + "rozhkov_iy8x7622d6";

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put("X-Forwarded-For", "1.1.1.1");
            }
        };

        postParams(url, params, headers);
    }
}

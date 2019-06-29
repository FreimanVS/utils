import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.security.Permission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestService {

    public static Map<String, String> get(String urlString, String urlParams, String charset, String headers) {
        return send(Method.GET, urlString, urlParams, charset, headers, null);
    }

    public static Map<String, String> post(String urlString, String charset, String headers, String data) {
        return send(Method.POST, urlString, null, charset, headers, data);
    }

    public static Map<String, String> postWithData(String urlString, String charset, String headers, String data) {
        return send(Method.POST, urlString, null, charset, headers, data);
    }

    public static Map<String, String> postWithData(String urlString, String headers, String data) {
        String defaultCharset = "UTF-8";
        return send(Method.POST, urlString, null, defaultCharset, headers, data);
    }

    public static Map<String, String> postWithParams(String urlString, String urlParams, String charset, String headers) {
        return send(Method.POST, urlString, urlParams, charset, headers, null);
    }

    public static Map<String, String> postWithParams(String urlString, String urlParams, String headers) {
        String defaultCharset = "UTF-8";
        return send(Method.POST, urlString, urlParams, defaultCharset, headers, null);
    }

    public static Map<String, String> send(final Method theMethod,
                                           String urlString,
                                           final String urlParams,
                                           final String charset,
                                           final String headers,
                                           final String data) {
        try {
            if (Objects.nonNull(urlParams)) {
                final StringBuilder sb = new StringBuilder();
                final String[] params = urlParams.split("&"
                );
                for (String param : params) {
                    if (param.startsWith("?"))
                        param = param.substring(1);
                    final String[] t = param.split("=", 2);
                    sb.append(t[0]).append("=");
                    if (t.length >= 2)
                            sb.append(URLEncoder.encode(t[1], charset));
                    sb.append("&");
                }
                final String res = sb.toString();
                urlString += ("?" + res.substring(0, res.length() - 1));
            }
            System.out.println(urlString);

            final URL url = new URL(urlString);

            //ssl helper
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(theMethod.name());
//            connection.setRequestProperty("Cookie", "JSESSIONID=0000nGsbwrf9cfkSCVrhXrZMPch:1c792fqtd|rsJSESSIONID=00007UYbOyfw1uX2q4Aak6ouFoL:1c792fqtd|rsDPJSESSIONID=PBC5YS:621352531");

            if (Objects.nonNull(headers)) {
                final String[] params = headers.split("&");
                for (String param : params) {
                    if (param.startsWith("?"))
                        param = param.substring(1);
                    final String[] t = param.split("=", 2);
                    connection.addRequestProperty(t[0], t[1]);
                }
                System.out.println(headers);
            }

            if (Objects.nonNull(data)) {
                connection.setDoOutput(true);
                try (final OutputStream os = connection.getOutputStream()) {
                    os.write(data.getBytes(charset));
                    os.flush();
                }
                System.out.println(data);
            }

            final Map<String, String> result = new HashMap<>();

            final Map<String, List<String>> headerFields = connection.getHeaderFields();
            headerFields.forEach((k, v) -> {
                if (Objects.nonNull(v) && !v.isEmpty()) {
                    String s = v.toString();
                    s = s.substring(1, s.length() - 1);
                    result.put(k, s);
                }
            });

            final Permission permission = connection.getPermission();

            result.put("Permission-Name", permission.getName());
            result.put("Permission-Actions", permission.getActions());
            final int responseCode = connection.getResponseCode(); //200
            final String responseMessage = connection.getResponseMessage(); //ok
            result.put("responseCode", String.valueOf(responseCode));
            result.put("responseMessage", responseMessage);

            final String contentEncoding = connection.getContentEncoding(); //null
            result.put("contentEncoding", contentEncoding);

            try (final InputStreamReader isr = new InputStreamReader(connection.getInputStream(), charset);
                 final InputStream errors = connection.getErrorStream()) {

                StringBuilder body = new StringBuilder();
                int ch;
                while ((ch = isr.read())  != -1) {
                    body.append((char) ch);
                }

                result.put("body", body.toString());

                if (Objects.nonNull(errors)) {
                    final StringBuilder errorRespone = new StringBuilder();
                    while ((ch = errors.read())  != -1) {
                        errorRespone.append((char) ch);
                    }
                    result.put("errors", errorRespone.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static enum Method {
        GET, POST, PUT, DELETE
    }
}

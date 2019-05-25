package tv.twitch.totallybot.todete.a2;

import javax.json.Json;
import java.util.HashMap;
import java.util.Map;

public class SbrfService {

    /*
        keytool -importcert -file "I:\andersen\sberbank\sbrf1.cer" -alias "sbrf1" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -importcert -file "I:\andersen\sberbank\sbrf2.cer" -alias "sbrf2" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -importcert -file "I:\andersen\sberbank\sbrf3ios.cer" -alias "sbrf3ios" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -importcert -file "I:\andersen\sberbank\sbrfappios.cer" -alias "sbrfappios" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -importcert -file "I:\andersen\sberbank\sbrf3and.cer" -alias "sbrf3and" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -importcert -file "I:\andersen\sberbank\sbrfappand.cer" -alias "sbrfappand" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"

        keytool -delete -alias "sbrf1" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -delete -alias "sbrf2" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -delete -alias "sbrf3ios" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"
        keytool -delete -alias "sbrf3and" -keystore "D:/Java/jdk1.8.0_91/jre/lib/security/cacerts" -storepass "changeit"

        java/jdk/bin
        chrome://settings/siteData
     */

    private static final String ERIB_LOGIN_IFT_B1 = "rozhkov_iy8x7622d6";
    private static final String IOS_SESSION = "https://nginx-ios.ift-node5.testonline.sberbank.ru/sm-uko/v2/session/create";
    private static final String APP_IOS = "https://nginx-ios.ift-node5.testonline.sberbank.ru/loyaltyspasibo_registration_mb/v1/mob_bank/workflow-gate?cmd=START&name=balance";
    private static final String ANDROID_SESSION = "https://nginx-and.ift-node5.testonline.sberbank.ru/sm-uko/v2/session/create";
    private static final String APP_ANDROID = "https://nginx-and.ift-node5.testonline.sberbank.ru/loyaltyspasibo_registration_mb/v1/mob_bank/workflow-gate?cmd=START&name=balance";


    public static String getIFTB1SIGMAUFSSession() {
        System.out.println("================= 1 ===============================");
        Map<String, String> step1 = getSessionStep1();
        String mGUID = step1.get("mGUID");
        String Cookie = step1.get("Cookie");
        System.out.println("Cookie " + Cookie);
        System.out.println("================= 2 ===============================");
        getSessionStep2(mGUID);
        System.out.println("================= 3 ===============================");
        String token = getSessionStep3(mGUID);
        System.out.println("================= 4 ===============================");
        String Cookie2 = getSessionStep4(token);
        System.out.println("================= 5 ===============================");
        String token2 = getSessionStep5(Cookie2);
        System.out.println("================= 6 ===============================");
        String ufsSession = getSessionStep6(token2);
        return ufsSession;
    }

    /*// TODO: 25.05.2019 RETURN STRING

    /**
     *
     * @return
     *          String jsessionid,
     *          String mGUID;
     */
    private static Map<String, String> getSessionStep1() {
        String url = "https://ift-csa-mp.testonline.sberbank.ru:4457/CSAMAPI/registerApp.do";
        String params = "?";
        params += "operation" + "=" + "register";
        params += "&devID" + "=" + "7AC3037-31A1-4F9A-BBDE-67DB31338F54";
        params += "&appVersion" + "=" + "9.8.0";
        params += "&appType" + "=" + "iPhone";
        params += "&client_type" + "=" + "";
        params += "&version" + "=" + "9.20";
        params += "&deviceName" + "=" + "iPhone";
        params += "&login" + "=" + ERIB_LOGIN_IFT_B1;

        String headers = "?";
        headers += "Content-type" + "=" + "application/x-www-form-urlencoded";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";

        Map<String, String> response = RequestService.postWithParams(url, params, headers);
        System.out.println(response);

        String cookie = response.get("Set-Cookie").split(";", 2)[0];

        String xmlResponse = response.get("body");
        String mGUID = XmlService.getValue(xmlResponse, "mGUID");

        return new HashMap<String, String>() {
            {
                put("Cookie", cookie);
                put("mGUID", mGUID);
            }
        };
    }

    private static void getSessionStep2(String mGUID) {
        String url = "https://ift-csa-mp.testonline.sberbank.ru:4457/CSAMAPI/registerApp.do";
        String params = "?";
        params += "operation" + "=" + "confirm";
        params += "&appType" + "=" + "iPhone";
        params += "&smsPassword" + "=" + "55098";
        params += "&client_type" + "=" + "";
        params += "&version" + "=" + "9.20";
        params += "&mGUID" + "=" + mGUID;

        String charset = "UTF-8";
        String headers = "?";
        headers += "Content-type" + "=" + "application/x-www-form-urlencoded";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";

        Map<String, String> response = RequestService.postWithParams(url, params, charset, headers);
        System.out.println(response);
    }

    private static String getSessionStep3(String mGUID) {
        String url = "https://ift-csa-mp.testonline.sberbank.ru:4457/CSAMAPI/registerApp.do";
        String params = "?";
        params += "operation" + "=" + "createPIN";
        params += "&devID" + "=" + "7AC3037-31A1-4F9A-BBDE-67DB31338F54";
        params += "&password" + "=" + "123465";
        params += "&appVersion" + "=" + "9.8.0";
        params += "&appType" + "=" + "iPhone";
        params += "&smsPassword" + "=" + "55098";
        params += "&client_type" + "=" + "";
        params += "&version" + "=" + "9.20";
        params += "&deviceName" + "=" + "iPhone";
        params += "&mobileSdkData" + "=" + "1";
        params += "&mGUID" + "=" + mGUID;

        String headers = "?";
        headers += "Content-type" + "=" + "application/x-www-form-urlencoded";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";

        Map<String, String> response = RequestService.postWithParams(url, params, headers);
        System.out.println(response);
        String responseBodyXml = response.get("body");

        String token = XmlService.getValue(responseBodyXml, "token");
        return token;
    }

    private static String getSessionStep4(String token) {
        String url = "https://ift-node5-mp.testonline.sberbank.ru:4489/mobile9/postCSALogin.do";
        String params = "?";
        params += "token" + "=" + token;

        String headers = "?";
        headers += "Content-type" + "=" + "application/x-www-form-urlencoded";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";

        Map<String, String> response = RequestService.postWithParams(url, params, headers);
        System.out.println(response);
        return response.get("Set-Cookie").split(";", 2)[0];
    }

    private static String getSessionStep5(String Cookie) {
        String url = "https://ift-node5-mp.testonline.sberbank.ru:4489/mobile9/private/unifiedClientSession/getToken.do";
        String params = "?";
        params += "systemName" + "=" + "ufs";

        String headers = "?";
        headers += "Content-type" + "=" + "application/x-www-form-urlencoded";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";
        headers += "&Cookie" + "=" + Cookie;

        Map<String, String> response = RequestService.postWithParams(url, params, headers);
        System.out.println(response);

        String responseBodyXml = response.get("body");
        String token = XmlService.getValue(responseBodyXml, "token");
        return token;
    }

    private static String getSessionStep6(String token) {
        String url = IOS_SESSION;
//        String url = ANDROID_SESSION;

        String data = Json.createObjectBuilder()
                .add("token", token)
                .build().toString();

        String headers = "?";
        headers += "Content-type" + "=" + "application/json";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";
        headers += "&X-GW-Host" + "=" + "10.44.41.116:9080";

        Map<String, String> response = RequestService.postWithData(url, headers, data);
        System.out.println(response);

        String setCookie = response.get("Set-Cookie");
        String ufsSession = setCookie.split("UFS-SESSION=", 2)[1].split(";", 2)[0];
        return ufsSession;
    }

    private static void postBalanceLoyaltySpasiboRegistrationMB(String ufsSession) {
        String url = APP_IOS;
//        String url = APP_ANDROID;

        String data = Json.createObjectBuilder()
                .build().toString();

        String headers = "?";
        headers += "Content-type" + "=" + "application/json";
        headers += "&X-Forwarded-For" + "=" + "1.1.1.1";
        headers += "&Cookie" + "=" + "UFS-SESSION=" + ufsSession;

        Map<String, String> response = RequestService.postWithData(url, headers, data);
        System.out.println(response);
    }

    public static void checkBalanceLoyaltySpasiboRegistrationMBIFTB1SIGMA() {
        String ufsSession = getIFTB1SIGMAUFSSession();
        System.out.println("UFS-SESSION=" + ufsSession);

        postBalanceLoyaltySpasiboRegistrationMB(ufsSession);
    }

    public static void main(String[] args) {
        checkBalanceLoyaltySpasiboRegistrationMBIFTB1SIGMA();
    }
}

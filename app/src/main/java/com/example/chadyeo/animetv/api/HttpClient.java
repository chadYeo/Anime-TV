package com.example.chadyeo.animetv.api;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class HttpClient {
    private OkHttpClient okHttpClient;
    private OAuth2Client oAuth2Client;

    private static final String LOG_TAG = HttpClient.class.getSimpleName();
    private static final String CLIENT_ID = "viveksb007-gsxam";
    private static final String CLIENT_SECRET = "6BmShBiPcqnEHR2HA21ot3noG";

    private static String ACCESS_TOKEN;
    private static String REFRESH_TOKEN;

    private Context mContext;

    public HttpClient(Context context) {
        mContext = context;
        okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (responseCount(response) >= 3) {
                            return null;
                        }
                        if (REFRESH_TOKEN != null) {
                            OAuthResponse oAuthResponse = oAuth2Client.refreshAccessToken(REFRESH_TOKEN);
                            if (oAuthResponse.isSuccessful()) {
                                Log.w(LOG_TAG, "REFRESH_TOKEN IS: " + REFRESH_TOKEN);
                                Log.w(LOG_TAG, "ACCESS_TOKEN IS: " + ACCESS_TOKEN);
                                okHttpClient.cache().evictAll();
                                try {
                                    writeStoredAccess();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            OAuthResponse oAuthResponse = oAuth2Client.requestAccessToken();
                            if (response.isSuccessful()) {
                                ACCESS_TOKEN = oAuthResponse.getAccessToken();
                                okHttpClient.cache().evictAll();
                                try {
                                    writeStoredAccess();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        HttpUrl url = response.request().url().newBuilder().setQueryParameter("access_token", ACCESS_TOKEN).build();

                        return response.request().newBuilder().url(url).build();
                    }
                }).retryOnConnectionFailure(false).connectTimeout(2, TimeUnit.SECONDS)
                .cache(new Cache(new File(context.getCacheDir(), "HttpResponseCache"), (long)(100 * 1024 * 1024))).build();

        String refreshToken = "";
        try {
            refreshToken = getStoredRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!refreshToken.equals("")) {
            REFRESH_TOKEN = refreshToken;
            oAuth2Client = new OAuth2Client.Builder(CLIENT_ID, CLIENT_SECRET, "https://anilist.co/api/auth/access_token")
                    .grantType("refresh_token")
                    .okHttpClient(okHttpClient)
                    .build();

            String accessToken = "";
            try {
                accessToken = getStoredAccess();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (accessToken.equals("")) {
                try {
                    OAuthResponse response = oAuth2Client.refreshAccessToken(REFRESH_TOKEN);
                    Log.w(LOG_TAG, "REFRESH_TOKEN IS: " + REFRESH_TOKEN);
                    Log.w(LOG_TAG, "ACCESS_TOKEN IS: " + ACCESS_TOKEN);
                    if (response.isSuccessful()) {
                        ACCESS_TOKEN = response.getAccessToken();
                        writeStoredAccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ACCESS_TOKEN = accessToken;
            }
        } else {
            OAuth2Client.Builder builder = new OAuth2Client
                    .Builder(CLIENT_ID, CLIENT_SECRET, "https://anilist.co/api/auth/access_token")
                    .grantType("client_credentials")
                    .okHttpClient(okHttpClient);
            oAuth2Client = builder.build();

            String accessToken = "";
            try {
                accessToken = getStoredAccess();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (accessToken.equals("")) {
                try {
                    OAuthResponse response = oAuth2Client.requestAccessToken();
                    if (response.isSuccessful()) {
                        ACCESS_TOKEN = response.getAccessToken();
                        writeStoredAccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ACCESS_TOKEN = accessToken;
            }
        }
    }

    private String getStoredAccess() throws IOException {
        FileInputStream fileInputStream = mContext.openFileInput("ACCESS");
        int storedNumber;
        String code = "";

        while ((storedNumber = fileInputStream.read()) != -1) {
            String storedString = Character.toString((char)storedNumber);
            if (storedString != null) {
                code += storedString;
            }
        }

        fileInputStream.close();
        Log.w(LOG_TAG, "Get Access Token: " + code);

        return code;
    }

    private String getStoredRefresh() throws IOException {
        FileInputStream fileInputStream = mContext.openFileInput("REFRESH");
        int storedNumber;
        String code = "";

        while ((storedNumber = fileInputStream.read()) != -1) {
            String storedString = Character.toString((char)storedNumber);
            if (storedString != null) {
                code += storedString;
            }
        }

        fileInputStream.close();
        Log.w(LOG_TAG, "Get Refresh Token: " + code);

        return code;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public OAuth2Client getoAuth2Client() {
        return oAuth2Client;
    }

    public void setoAuth2Client(OAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public static String getRefreshToken() {
        return REFRESH_TOKEN;
    }

    public static void setRefreshToken(String refreshToken) {
        REFRESH_TOKEN = refreshToken;
    }

    private void writeStoredAccess() throws IOException {
        FileOutputStream fileOutputStream = mContext.openFileOutput("ACCESS", Context.MODE_PRIVATE);
        fileOutputStream.write(ACCESS_TOKEN.getBytes());
        fileOutputStream.close();
        Log.w(LOG_TAG, "Wrote Access Token: " + ACCESS_TOKEN);
    }

    private void deleteRefreshToken() throws IOException {
        FileOutputStream fileOutputStream = mContext.openFileOutput("REFRESH", Context.MODE_PRIVATE);
        fileOutputStream.write("".getBytes());
        fileOutputStream.close();
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}

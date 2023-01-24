package com.android.powerbankpad.ApiUnits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static RetrofitInstance instance = null;
    private ApiInstance myApi;

    public static class UnsafeOkHttpClient {

        public static OkHttpClient getUnsafeOkHttpClient() {
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                OkHttpClient okHttpClient = builder.build();
                return okHttpClient;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private RetrofitInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiInstance.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        myApi = retrofit.create(ApiInstance.class);
    }
    public static synchronized RetrofitInstance getInstance() {
        if (instance == null) {
            instance = new RetrofitInstance();
        }
        return instance;
    }
    public ApiInstance getMyApi() {
        return myApi;
    }
}

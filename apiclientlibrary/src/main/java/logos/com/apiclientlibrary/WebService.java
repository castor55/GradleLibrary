package logos.com.apiclientlibrary;

import android.util.Base64;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by softdev0420 on 9/21/16.
 */

public class WebService {

    private static Retrofit retrofit = null;
    private static APIInterface instance = null;
    public static String BASE_URL = "";
    public static String X_APP_KEY = "";
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String FIRM_ID = "";
    private static final int SUCCESS_CODE = 200;

    public static APIInterface getInstance() {
        if (instance == null) {
            instance = getClient().create(APIInterface.class);
        }
        return instance;
    }

    private static Retrofit getClient() {
        if (retrofit == null) {
            createRetrofit();
        }
        return retrofit;
    }

    public static void setAuthData(final String username, final String password, final String firmId) {
        USERNAME = username;
        PASSWORD = password;
        FIRM_ID = firmId;
        createRetrofit();
        instance = retrofit.create(APIInterface.class);
    }

    public static void removeAuthData() {
        createRetrofit();
        instance = retrofit.create(APIInterface.class);
    }

    private static void createRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                String credentials = USERNAME + ":" + PASSWORD;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                Request request = original.newBuilder()
                        .header("x-compass-api-key", X_APP_KEY)
                        .header("x-compass-firm-id", FIRM_ID)
                        .header("Authorization", "Basic " + credBase64)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        httpClient.addInterceptor(interceptor);

        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                .client(client)
                .build();
    }
}

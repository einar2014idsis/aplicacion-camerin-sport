package com.example.camerinosports.network;

import android.content.Context;
import android.util.Log;

import com.example.camerinosports.R;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCliente {

    private static final String TAG = "ApiCliente";
    private static Retrofit retrofit;
    private static HttpUrl baseUrlActiva;

    public static Retrofit obtener(Context context) {
        if (retrofit == null) {
            Context appContext = context.getApplicationContext();
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .connectTimeout(4, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new ApiFallbackInterceptor(appContext))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.api_base_url))
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    private static class ApiFallbackInterceptor implements Interceptor {
        private final Context context;

        ApiFallbackInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            List<HttpUrl> bases = obtenerBases(context);
            IOException ultimoError = null;

            for (HttpUrl base : ordenarBases(bases)) {
                Request request = original.newBuilder()
                        .url(cambiarServidor(original.url(), base))
                        .build();

                try {
                    Response response = chain.proceed(request);
                    baseUrlActiva = base;
                    return response;
                } catch (IOException error) {
                    ultimoError = error;
                    Log.w(TAG, "No se pudo conectar con " + base, error);

                    if (!puedeReintentar(original.method(), error)) {
                        throw error;
                    }
                }
            }

            throw ultimoError == null
                    ? new IOException("No hay URL configurada para la API")
                    : ultimoError;
        }
    }

    private static List<HttpUrl> obtenerBases(Context context) {
        ArrayList<HttpUrl> bases = new ArrayList<>();
        agregarBase(bases, context.getString(R.string.api_base_url));

        String[] alternativas = context.getResources().getStringArray(R.array.api_base_urls);
        for (String alternativa : alternativas) {
            agregarBase(bases, alternativa);
        }

        return bases;
    }

    private static void agregarBase(List<HttpUrl> bases, String valor) {
        HttpUrl url = HttpUrl.parse(valor);
        if (url == null) {
            return;
        }

        for (HttpUrl base : bases) {
            if (base.host().equals(url.host()) &&
                    base.port() == url.port() &&
                    base.scheme().equals(url.scheme())) {
                return;
            }
        }

        bases.add(url);
    }

    private static List<HttpUrl> ordenarBases(List<HttpUrl> bases) {
        if (baseUrlActiva == null || bases.isEmpty()) {
            return bases;
        }

        ArrayList<HttpUrl> ordenadas = new ArrayList<>();
        for (HttpUrl base : bases) {
            if (base.host().equals(baseUrlActiva.host()) &&
                    base.port() == baseUrlActiva.port() &&
                    base.scheme().equals(baseUrlActiva.scheme())) {
                ordenadas.add(base);
                break;
            }
        }

        for (HttpUrl base : bases) {
            boolean existe = false;
            for (HttpUrl ordenada : ordenadas) {
                if (ordenada.host().equals(base.host()) &&
                        ordenada.port() == base.port() &&
                        ordenada.scheme().equals(base.scheme())) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                ordenadas.add(base);
            }
        }

        return ordenadas;
    }

    private static HttpUrl cambiarServidor(HttpUrl original, HttpUrl base) {
        return original.newBuilder()
                .scheme(base.scheme())
                .host(base.host())
                .port(base.port())
                .build();
    }

    private static boolean puedeReintentar(String metodo, IOException error) {
        if ("GET".equalsIgnoreCase(metodo)) {
            return true;
        }

        return error instanceof ConnectException ||
                error instanceof NoRouteToHostException ||
                error instanceof UnknownHostException;
    }
}

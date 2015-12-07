package packagename.app.com.appname.core.module;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import packagename.app.com.appname.BuildConfig;
import packagename.app.com.appname.R;
import packagename.app.com.appname.core.logging.LoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@Module
public class WebserviceModule {

   public static final int CACHE_SIZE = 25 * 1024 * 1024;

   /* TODO: Add here provides methods for retrofit interfaces. */

   @Provides
   @Singleton
   OkHttpClient provideHttpClient(Context context) {
      final File cacheDirectory = new File(context.getCacheDir()
            .getAbsolutePath(), "HttpCache");
      final Cache cache = new Cache(cacheDirectory, CACHE_SIZE);
      final OkHttpClient client = new OkHttpClient();
      client.setCache(cache);
      return client;
   }

   @Provides
   @Singleton
   Picasso providePicasso(Context context, OkHttpClient client) {
      Picasso.Builder imageLoaderBuilder = new Picasso.Builder(context);
      imageLoaderBuilder.executor(Executors.newSingleThreadExecutor());
      imageLoaderBuilder.downloader(new OkHttpDownloader(client));
      imageLoaderBuilder.indicatorsEnabled(BuildConfig.IS_IDE_BUILD);
      return imageLoaderBuilder.build();
   }

   @Provides
   @Singleton
   Retrofit provideRetrofit(Context context, OkHttpClient client) {
      Retrofit.Builder builder =
            new Retrofit.Builder().baseUrl(context.getString(R.string.base_url))
                  .addConverterFactory(GsonConverterFactory.create());
      if (BuildConfig.DEBUG) {
         client.interceptors()
               .add(new LoggingInterceptor());
      }
      builder.client(client);
      return builder.build();
   }
}



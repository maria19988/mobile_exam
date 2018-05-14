package com.example.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weatherapp.api.weather.WeatherApiManager;
import com.example.weatherapp.models.CurrentWeatherInfo;
import com.example.weatherapp.models.WeatherDescription;
import com.example.weatherapp.screens.CityPickerActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class MyWeatherWidget extends AppWidgetProvider {

    public final int PERMISSION_REQUEST_CODE = 100;
    private final int CITY_ACTIVITY_REQUEST_CODE = 200;


    private Double latitude, longitude;
    private LocationManager locationManager;



    private LottieAnimationView loadingAnimationView;

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        ImageView currentConditionImageView;
        TextView currentTemperatureTextView;
        WeatherApiManager weatherApiManager;

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_weather_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        weatherApiManager = new WeatherApiManager();
        weatherApiManager.getWeatherByCityName("Beirut").enqueue(new Callback<CurrentWeatherInfo>() {
            @Override
            public void onResponse(Call<CurrentWeatherInfo> call, Response<CurrentWeatherInfo> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherInfo currentWeatherInfo = response.body();
                    if (currentWeatherInfo != null) {
                        WeatherDescription currentWeatherDescription = currentWeatherInfo.getWeatherDescriptions().get(0);

                        String currentTemp = String.valueOf(currentWeatherInfo.getTempInfo().getTemp());
                        //currentTemperatureTextView.setText(currentTemp);
                        //RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.my_weather_widget);
                        views.setTextViewText(R.id.appwidget_text, currentTemp);

                        final String iconUrl = "http://openweathermap.org/img/w/" + currentWeatherDescription.getIcon() + ".png";

                        Picasso.with(context).load(iconUrl).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                views.setImageViewBitmap(R.id.imageView, bitmap);
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                        //hideLoadingAnimation();
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherInfo> call, Throwable t) {
                //hideLoadingAnimation();
            }
        });

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    /*private void showCurrentWeather(CurrentWeatherInfo info) {
        String currentTemp = String.valueOf(info.getTempInfo().getTemp());
        //currentTemp = String.format(getString(R.string.appwidget_text, currentTemp);
        //currentTemperatureTextView.setText(currentTemp);

        *//*RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_weather_widget);
        views.setTextViewText(R.id.appwidget_text, currentTemp);*//*


        if (info.getWeatherDescriptions() != null && !info.getWeatherDescriptions().isEmpty()) {
            WeatherDescription currentWeatherDescription = info.getWeatherDescriptions().get(0);

            String weatherDescription = currentWeatherDescription.getDescription();
            weatherDescriptionTextView.setText(weatherDescription);

            String iconUrl = "http://openweathermap.org/img/w/" + currentWeatherDescription.getIcon() + ".png";
            Picasso.with(this).load(iconUrl).into(currentConditionImageView);
        }
    }*/

}


package com.example.pavelsuvit.weatherapplication.dataPresenters;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pavelsuvit.weatherapplication.R;

public class DetailedWeatherData implements Parcelable {
    private String cityId;
    private String iconNumber;
    private int currentWeather;
    private String city;
    private long time;
    private int pressure;
    private int humidity;
    private int windSpeed;
    private String timeZone;

    public DetailedWeatherData(String city, int currentWeather,
                               String iconNumber, long time, int pressure,
                               int humidity, int windSpeed) {
        this.iconNumber = iconNumber;
        this.currentWeather = currentWeather;
        this.city = city;
        this.time = time;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public DetailedWeatherData(String city, int currentWeather,
                               String iconNumber, long time, String timeZone, int pressure,
                               int humidity, int windSpeed) {
        this.iconNumber = iconNumber;
        this.currentWeather = currentWeather;
        this.city = city;
        this.time = time;
        this.timeZone = timeZone;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getIconNumber() {
        return iconNumber;
    }

    public int getCurrentWeather() {
        return currentWeather;
    }

    public String getCity() {
        return city;
    }

    public long getTime() {
        return time;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getCityId() {
        return cityId;
    }

    public int getIconDrawable() {
        int result;
        switch (this.iconNumber) {
            case "01d":
                result = R.drawable.w01d;
                break;
            case "02d":
                result = R.drawable.w02d;
                break;
            case "03d":
            case "03n":
                result = R.drawable.w03;
                break;
            case "04d":
            case "04n":
                result = R.drawable.w04;
                break;
            case "09d":
            case "09n":
                result = R.drawable.w09;
                break;
            case "10d":
                result = R.drawable.w10d;
                break;
            case "10n":
                result = R.drawable.w10n;
                break;
            case "11d":
            case "11n":
                result = R.drawable.w11;
                break;
            case "13d":
            case "13n":
                result = R.drawable.w13;
                break;
            case "50d":
            case "50n":
                result = R.drawable.w50;
                break;
            default:
                result = R.drawable.w01d;
                break;
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCityId());
        dest.writeString(getCity());
        dest.writeLong(getTime());
        dest.writeString(getTimeZone());
        dest.writeInt(getCurrentWeather());
        dest.writeString(getIconNumber());
        dest.writeInt(getPressure());
        dest.writeInt(getHumidity());
        dest.writeInt(getWindSpeed());
    }

    public static final Parcelable.Creator<DetailedWeatherData> CREATOR =
            new Parcelable.Creator<DetailedWeatherData>() {
                @Override
                public DetailedWeatherData createFromParcel(Parcel source) {
                    return new DetailedWeatherData(source);
                }

                @Override
                public DetailedWeatherData[] newArray(int size) {
                    return new DetailedWeatherData[size];
                }
            };

    private DetailedWeatherData(Parcel s) {
        this.cityId = s.readString();
        this.city = s.readString();
        this.time = s.readLong();
        this.timeZone = s.readString();
        this.currentWeather = s.readInt();
        this.iconNumber = s.readString();
        this.pressure = s.readInt();
        this.humidity = s.readInt();
        this.windSpeed = s.readInt();
    }

}

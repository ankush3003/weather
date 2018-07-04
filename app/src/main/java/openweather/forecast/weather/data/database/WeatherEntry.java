package openweather.forecast.weather.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Object used for mapping by ROOM
 */
@Entity(tableName = "weather", indices = {@Index(value = {"date"}, unique = true)})
public class WeatherEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private Date date;
    private double temp;
    private double tempMin;
    private double tempMax;
    private double pressure;
    private double seaLevel;
    private double groundLevel;
    private double humidity;

    private int weatherIconId;
    private String weatherDescription;

    private double windSpeed;
    private double windDirection;

    private String locationName;
    private double population;

    /**
     * Constructor used by OpenWeatherJsonParser
     *
     * @param date
     * @param temp
     * @param tempMin
     * @param tempMax
     * @param pressure
     * @param seaLevel
     * @param groundLevel
     * @param humidity
     * @param weatherIconId
     * @param weatherDescription
     * @param windSpeed
     * @param windDirection
     */
    @Ignore
    public WeatherEntry(Date date, double temp, double tempMin, double tempMax, double pressure, double seaLevel, double groundLevel, double humidity, int weatherIconId, String weatherDescription, double windSpeed, double windDirection, String locationName, double population) {
        this.id = id;
        this.date = date;
        this.temp = temp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.seaLevel = seaLevel;
        this.groundLevel = groundLevel;
        this.humidity = humidity;
        this.weatherIconId = weatherIconId;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.locationName = locationName;
        this.population = population;
    }

    /**
     * Constructor used by ROOM for object mapping.
     *
     * @param id
     * @param date
     * @param temp
     * @param tempMin
     * @param tempMax
     * @param pressure
     * @param seaLevel
     * @param groundLevel
     * @param humidity
     * @param weatherIconId
     * @param weatherDescription
     * @param windSpeed
     * @param windDirection
     */
    public WeatherEntry(int id, Date date, double temp, double tempMin, double tempMax, double pressure, double seaLevel, double groundLevel, double humidity, int weatherIconId, String weatherDescription, double windSpeed, double windDirection, String locationName, double population) {
        this.id = id;
        this.date = date;
        this.temp = temp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.seaLevel = seaLevel;
        this.groundLevel = groundLevel;
        this.humidity = humidity;
        this.weatherIconId = weatherIconId;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.locationName = locationName;
        this.population = population;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public double getTemp() {
        return temp;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getPressure() {
        return pressure;
    }

    public double getSeaLevel() {
        return seaLevel;
    }

    public double getGroundLevel() {
        return groundLevel;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getWeatherIconId() {
        return weatherIconId;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getPopulation() {
        return population;
    }
}

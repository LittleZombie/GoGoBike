package gogobike.egg.com.entity;


import java.io.Serializable;
import java.util.List;

import gogobike.egg.com.gogobike.RecommendedRouteActivity;

public class BikeRoute implements Serializable {

    private String origin;

    private String destination;


    private String routeName;
    private int pokemonStationNumber;
    private float kilometer;
    private int time;
    private boolean hasBikeRentalStation;
    private List<Double> latitudeList;
    private List<Double> LongitudeList;
    private int[] pokemonImageResourceId;

    private float ratingScore;
    private int imageResourceId;

    private int weight;
    private RecommendedRouteActivity.Area area;


    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getPokemonStationNumber() {
        return pokemonStationNumber;
    }

    public void setPokemonStationNumber(int pokemonStationNumber) {
        this.pokemonStationNumber = pokemonStationNumber;
    }

    public float getKilometer() {
        return kilometer;
    }

    public void setKilometer(float kilometer) {
        this.kilometer = kilometer;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isHasBikeRentalStation() {
        return hasBikeRentalStation;
    }

    public void setHasBikeRentalStation(boolean hasBikeRentalStation) {
        this.hasBikeRentalStation = hasBikeRentalStation;
    }

    public List<Double> getLatitudeList() {
        return latitudeList;
    }

    public void setLatitudeList(List<Double> latitudeList) {
        this.latitudeList = latitudeList;
    }

    public List<Double> getLongitudeList() {
        return LongitudeList;
    }

    public void setLongitudeList(List<Double> longitudeList) {
        LongitudeList = longitudeList;
    }

    public float getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(float ratingScore) {
        this.ratingScore = ratingScore;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public RecommendedRouteActivity.Area getArea() {
        return area;
    }

    public void setArea(RecommendedRouteActivity.Area area) {
        this.area = area;
    }

    public int[] getPokemonImageResourceId() {
        return pokemonImageResourceId;
    }

    public void setPokemonImageResourceId(int[] pokemonImageResourceId) {
        this.pokemonImageResourceId = pokemonImageResourceId;
    }
}

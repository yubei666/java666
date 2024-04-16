package com.example.oopandroidapi.data;

import java.io.Serializable;

public class MunicipalityData implements Serializable {

    private int year;
    private int population;
    private int increase;


    public MunicipalityData(int year, int population, int increase) {
        this.year = year;
        this.population = population;
        this.increase = increase;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getPopulation() {
        return population;
    }
    public void setPopulation(int population) {
        this.population = population;
    }
    public int getIncrease() {
        return increase;
    }
    public void setIncrease(int increase) {
        this.increase = increase;
    }

}

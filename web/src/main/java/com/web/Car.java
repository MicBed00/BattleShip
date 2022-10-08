package com.web;

public class Car {
    private String mark;
    private String model;
    private int poj;

    public Car() {
    }

    public Car(String mark, String model, int poj) {
        this.mark = mark;
        this.model = model;
        this.poj = poj;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPoj() {
        return poj;
    }

    public void setPoj(int poj) {
        this.poj = poj;
    }

    @Override
    public String toString() {
        return "Car{" +
                "mark='" + mark + '\'' +
                ", model='" + model + '\'' +
                ", poj=" + poj +
                '}';
    }
}

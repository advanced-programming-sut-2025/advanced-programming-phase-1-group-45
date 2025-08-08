package com.proj.Model.Cooking;

public class Buff {
    public String effect;
    public int durationHours;
    public float value;

    public Buff(String effect, int durationHours, float value) {
        this.effect = effect;
        this.durationHours = durationHours;
        this.value = value;
    }
}

package org.projekt.isi.dto;

import java.sql.Time;
import java.time.Duration;

public class ServicesDTO {
    private Long id;
    private String name;
    private Time duration; // zmiana typu na int
    private String priceRange;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    // getters and setters
}

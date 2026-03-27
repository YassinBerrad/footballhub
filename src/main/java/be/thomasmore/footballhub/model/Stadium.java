package be.thomasmore.footballhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Naam is verplicht.")
    private String name;

    @NotBlank(message = "Stad is verplicht.")
    private String city;

    @NotNull(message = "Capaciteit is verplicht.")
    @Min(value = 1000, message = "Capaciteit moet minstens 1000 zijn.")
    @Max(value = 150000, message = "Capaciteit mag maximum 150000 zijn.")
    private Integer capacity;

    @NotNull(message = "Prijs per uur is verplicht.")
    @Min(value = 50, message = "Prijs per uur moet minstens 50 euro zijn.")
    @Max(value = 100000, message = "Prijs per uur mag maximum 100000 euro zijn.")
    private Integer pricePerHour;

    @NotBlank(message = "Beschrijving is verplicht.")
    private String description;

    private String imageUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
package be.thomasmore.footballhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Naam is verplicht.")
    private String name;

    @NotBlank(message = "Stad is verplicht.")
    private String city;

    @NotBlank(message = "Stadion is verplicht.")
    private String stadium;

    @NotNull(message = "Oprichtingsjaar is verplicht.")
    @Min(value = 1800, message = "Oprichtingsjaar moet minstens 1800 zijn.")
    @Max(value = 2026, message = "Oprichtingsjaar mag niet in de toekomst liggen.")
    private Integer foundedYear;

    @NotBlank(message = "Image URL is verplicht.")
    private String imageUrl;

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private Collection<Player> players;

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

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<Player> players) {
        this.players = players;
    }
}
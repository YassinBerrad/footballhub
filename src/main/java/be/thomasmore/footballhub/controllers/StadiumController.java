package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Stadium;
import be.thomasmore.footballhub.repositories.ReservationRepository;
import be.thomasmore.footballhub.repositories.StadiumRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class StadiumController {

    private final StadiumRepository stadiumRepository;
    private final ReservationRepository reservationRepository;

    public StadiumController(StadiumRepository stadiumRepository,
                             ReservationRepository reservationRepository) {
        this.stadiumRepository = stadiumRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/stadiumlist")
    public String stadiumList(Model model) {
        model.addAttribute("stadiums", stadiumRepository.findAll());
        return "stadiumlist";
    }

    @GetMapping({"/stadiumdetails", "/stadiumdetails/{id}"})
    public String stadiumDetails(@PathVariable(required = false) Integer id,
                                 @RequestParam(required = false) String deleteError,
                                 Model model) {

        if (id != null) {
            Optional<Stadium> optionalStadium = stadiumRepository.findById(id);

            if (optionalStadium.isPresent()) {
                Stadium stadium = optionalStadium.get();
                model.addAttribute("stadium", stadium);

                if (deleteError != null) {
                    model.addAttribute("deleteError", deleteError);
                }

                List<Stadium> allStadiums = new ArrayList<>();
                stadiumRepository.findAllByOrderByIdAsc().forEach(allStadiums::add);

                if (!allStadiums.isEmpty()) {
                    int currentIndex = -1;

                    for (int i = 0; i < allStadiums.size(); i++) {
                        if (allStadiums.get(i).getId().equals(id)) {
                            currentIndex = i;
                            break;
                        }
                    }

                    if (currentIndex != -1) {
                        int prevIndex = currentIndex - 1;
                        int nextIndex = currentIndex + 1;

                        if (prevIndex < 0) {
                            prevIndex = allStadiums.size() - 1;
                        }

                        if (nextIndex >= allStadiums.size()) {
                            nextIndex = 0;
                        }

                        model.addAttribute("prevId", allStadiums.get(prevIndex).getId());
                        model.addAttribute("nextId", allStadiums.get(nextIndex).getId());
                    }
                }
            }
        }

        return "stadiumdetails";
    }

    @GetMapping("/stadiumcreate")
    public String stadiumCreate(Model model) {
        model.addAttribute("stadium", new Stadium());
        model.addAttribute("isEdit", false);
        return "stadiumcreate";
    }

    @PostMapping("/stadiumcreate")
    public String stadiumCreatePost(@Valid Stadium stadium,
                                    BindingResult bindingResult,
                                    @RequestParam("imageFile") MultipartFile imageFile,
                                    Model model) {

        if (imageFile == null || imageFile.isEmpty()) {
            model.addAttribute("imageError", "Afbeelding is verplicht.");
            model.addAttribute("isEdit", false);
            return "stadiumcreate";
        }

        String originalFileName = imageFile.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank() || !originalFileName.contains(".")) {
            model.addAttribute("imageError", "Ongeldige bestandsnaam.");
            model.addAttribute("isEdit", false);
            return "stadiumcreate";
        }

        String lowerFileName = originalFileName.toLowerCase();

        boolean validImageType =
                lowerFileName.endsWith(".jpg") ||
                        lowerFileName.endsWith(".jpeg") ||
                        lowerFileName.endsWith(".png") ||
                        lowerFileName.endsWith(".webp");

        if (!validImageType) {
            model.addAttribute("imageError", "Alleen JPG, JPEG, PNG of WEBP bestanden zijn toegestaan.");
            model.addAttribute("isEdit", false);
            return "stadiumcreate";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "stadiumcreate";
        }

        try {
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + extension;

            Path uploadPath = Paths.get("src/main/resources/static/img");
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.write(filePath, imageFile.getBytes());

            stadium.setImageUrl("/img/" + uniqueFileName);
        } catch (IOException e) {
            model.addAttribute("imageError", "Fout bij uploaden van de afbeelding.");
            model.addAttribute("isEdit", false);
            return "stadiumcreate";
        }

        stadiumRepository.save(stadium);

        return "redirect:/stadiumlist";
    }

    @GetMapping({"/stadiumedit", "/stadiumedit/{id}"})
    public String stadiumEdit(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Stadium> optionalStadium = stadiumRepository.findById(id);

            if (optionalStadium.isPresent()) {
                model.addAttribute("stadium", optionalStadium.get());
                model.addAttribute("isEdit", true);
                return "stadiumcreate";
            }
        }

        return "redirect:/stadiumlist";
    }

    @PostMapping("/stadiumedit")
    public String stadiumEditPost(@Valid Stadium stadium,
                                  BindingResult bindingResult,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "stadiumcreate";
        }

        Optional<Stadium> existingStadiumOptional = stadiumRepository.findById(stadium.getId());

        if (existingStadiumOptional.isEmpty()) {
            return "redirect:/stadiumlist";
        }

        Stadium existingStadium = existingStadiumOptional.get();

        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFileName = imageFile.getOriginalFilename();

            if (originalFileName == null || originalFileName.isBlank() || !originalFileName.contains(".")) {
                model.addAttribute("imageError", "Ongeldige bestandsnaam.");
                model.addAttribute("isEdit", true);
                return "stadiumcreate";
            }

            String lowerFileName = originalFileName.toLowerCase();

            boolean validImageType =
                    lowerFileName.endsWith(".jpg") ||
                            lowerFileName.endsWith(".jpeg") ||
                            lowerFileName.endsWith(".png") ||
                            lowerFileName.endsWith(".webp");

            if (!validImageType) {
                model.addAttribute("imageError", "Alleen JPG, JPEG, PNG of WEBP bestanden zijn toegestaan.");
                model.addAttribute("isEdit", true);
                return "stadiumcreate";
            }

            try {
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID() + extension;

                Path uploadPath = Paths.get("src/main/resources/static/img");
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.write(filePath, imageFile.getBytes());

                stadium.setImageUrl("/img/" + uniqueFileName);
            } catch (IOException e) {
                model.addAttribute("imageError", "Fout bij uploaden van de afbeelding.");
                model.addAttribute("isEdit", true);
                return "stadiumcreate";
            }
        } else {
            stadium.setImageUrl(existingStadium.getImageUrl());
        }

        stadiumRepository.save(stadium);

        return "redirect:/stadiumdetails/" + stadium.getId();
    }

    @PostMapping("/stadiumdelete/{id}")
    public String stadiumDelete(@PathVariable Integer id) {
        if (!stadiumRepository.existsById(id)) {
            return "redirect:/stadiumlist";
        }

        long reservationCount = reservationRepository.countByStadiumId(id);

        if (reservationCount > 0) {
            return "redirect:/stadiumdetails/" + id + "?deleteError=Dit stadium kan niet verwijderd worden omdat er nog reservaties aan gekoppeld zijn.";
        }

        stadiumRepository.deleteById(id);

        return "redirect:/stadiumlist";
    }
}
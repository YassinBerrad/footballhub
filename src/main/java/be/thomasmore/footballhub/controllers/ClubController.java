package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Club;
import be.thomasmore.footballhub.repositories.ClubRepository;
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
public class ClubController {

    private final ClubRepository clubRepository;

    public ClubController(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @GetMapping("/clublist")
    public String clubList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Integer minFoundedYear,
                           Model model) {

        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        Iterable<Club> clubs = clubRepository.findByFilter(keyword, minFoundedYear);

        model.addAttribute("clubs", clubs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minFoundedYear", minFoundedYear);

        return "clublist";
    }

    @GetMapping({"/clubdetails", "/clubdetails/{id}"})
    public String clubDetails(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Club> optionalClub = clubRepository.findById(id);

            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();

                if (!club.getActive()) {
                    return "redirect:/clublist";
                }

                model.addAttribute("club", club);

                List<Club> allClubs = new ArrayList<>();
                clubRepository.findAllByActiveTrueOrderByIdAsc().forEach(allClubs::add);

                if (!allClubs.isEmpty()) {
                    int currentIndex = -1;

                    for (int i = 0; i < allClubs.size(); i++) {
                        if (allClubs.get(i).getId().equals(id)) {
                            currentIndex = i;
                            break;
                        }
                    }

                    if (currentIndex != -1) {
                        int prevIndex = currentIndex - 1;
                        int nextIndex = currentIndex + 1;

                        if (prevIndex < 0) {
                            prevIndex = allClubs.size() - 1;
                        }

                        if (nextIndex >= allClubs.size()) {
                            nextIndex = 0;
                        }

                        model.addAttribute("prevId", allClubs.get(prevIndex).getId());
                        model.addAttribute("nextId", allClubs.get(nextIndex).getId());
                    }
                }
            }
        }

        return "clubdetails";
    }

    @GetMapping("/clubcreate")
    public String clubCreate(Model model) {
        model.addAttribute("club", new Club());
        model.addAttribute("isEdit", false);
        return "clubcreate";
    }

    @PostMapping("/clubcreate")
    public String clubCreatePost(@Valid Club club,
                                 BindingResult bindingResult,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 Model model) {

        if (imageFile == null || imageFile.isEmpty()) {
            model.addAttribute("imageError", "Afbeelding is verplicht.");
            model.addAttribute("isEdit", false);
            return "clubcreate";
        }

        String originalFileName = imageFile.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank() || !originalFileName.contains(".")) {
            model.addAttribute("imageError", "Ongeldige bestandsnaam.");
            model.addAttribute("isEdit", false);
            return "clubcreate";
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
            return "clubcreate";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "clubcreate";
        }

        try {
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + extension;

            Path uploadPath = Paths.get("src/main/resources/static/img");
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.write(filePath, imageFile.getBytes());

            club.setImageUrl("/img/" + uniqueFileName);
        } catch (IOException e) {
            model.addAttribute("imageError", "Fout bij uploaden van de afbeelding.");
            model.addAttribute("isEdit", false);
            return "clubcreate";
        }

        club.setActive(true);
        clubRepository.save(club);

        return "redirect:/clublist";
    }

    @GetMapping({"/clubedit", "/clubedit/{id}"})
    public String clubEdit(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Club> optionalClub = clubRepository.findById(id);

            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();

                if (!club.getActive()) {
                    return "redirect:/clublist";
                }

                model.addAttribute("club", club);
                model.addAttribute("isEdit", true);
                return "clubcreate";
            }
        }

        return "redirect:/clublist";
    }

    @PostMapping("/clubedit")
    public String clubEditPost(@Valid Club club,
                               BindingResult bindingResult,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "clubcreate";
        }

        Optional<Club> existingClubOptional = clubRepository.findById(club.getId());

        if (existingClubOptional.isEmpty()) {
            return "redirect:/clublist";
        }

        Club existingClub = existingClubOptional.get();

        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFileName = imageFile.getOriginalFilename();

            if (originalFileName == null || originalFileName.isBlank() || !originalFileName.contains(".")) {
                model.addAttribute("imageError", "Ongeldige bestandsnaam.");
                model.addAttribute("isEdit", true);
                return "clubcreate";
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
                return "clubcreate";
            }

            try {
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID() + extension;

                Path uploadPath = Paths.get("src/main/resources/static/img");
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.write(filePath, imageFile.getBytes());

                club.setImageUrl("/img/" + uniqueFileName);
            } catch (IOException e) {
                model.addAttribute("imageError", "Fout bij uploaden van de afbeelding.");
                model.addAttribute("isEdit", true);
                return "clubcreate";
            }
        } else {
            club.setImageUrl(existingClub.getImageUrl());
        }

        club.setActive(existingClub.getActive());
        clubRepository.save(club);

        return "redirect:/clubdetails/" + club.getId();
    }

    @PostMapping("/clubdelete/{id}")
    public String clubDelete(@PathVariable Integer id, Model model) {
        Optional<Club> optionalClub = clubRepository.findById(id);

        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();

            if (club.getPlayers() != null && !club.getPlayers().isEmpty()) {
                boolean hasActivePlayers = club.getPlayers().stream().anyMatch(Player -> Player.getActive());

                if (hasActivePlayers) {
                    model.addAttribute("deleteError", "Deze club kan niet gearchiveerd worden omdat er nog actieve spelers aan gekoppeld zijn.");
                    return clubDetails(id, model);
                }
            }

            club.setActive(false);
            clubRepository.save(club);
        }

        return "redirect:/clublist";
    }
}
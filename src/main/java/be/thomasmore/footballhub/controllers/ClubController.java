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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ClubController {

    private final ClubRepository clubRepository;

    public ClubController(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @GetMapping("/clublist")
    public String clubList(Model model) {
        model.addAttribute("clubs", clubRepository.findAll());
        return "clublist";
    }

    @GetMapping({"/clubdetails", "/clubdetails/{id}"})
    public String clubDetails(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Club> optionalClub = clubRepository.findById(id);

            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();
                model.addAttribute("club", club);

                List<Club> allClubs = new ArrayList<>();
                clubRepository.findAll().forEach(allClubs::add);

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
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "clubcreate";
        }

        clubRepository.save(club);

        return "redirect:/clublist";
    }
}
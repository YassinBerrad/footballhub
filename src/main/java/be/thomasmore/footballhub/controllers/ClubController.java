package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Club;
import be.thomasmore.footballhub.repositories.ClubRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ClubController {

    private final ClubRepository clubRepository;

    public ClubController(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @GetMapping("/clublist")
    public String clubList(Model model) {
        Iterable<Club> clubs = clubRepository.findAll();
        model.addAttribute("clubs", clubs);
        return "clublist";
    }

    @GetMapping({"/clubdetails", "/clubdetails/{id}"})
    public String clubDetails(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Club> optionalClub = clubRepository.findById(id);

            if (optionalClub.isPresent()) {
                model.addAttribute("club", optionalClub.get());

                int maxId = (int) clubRepository.count();

                int prevId = id - 1;
                int nextId = id + 1;

                if (id == 1) {
                    prevId = maxId;
                }

                if (id == maxId) {
                    nextId = 1;
                }

                model.addAttribute("prevId", prevId);
                model.addAttribute("nextId", nextId);
            }
        }

        return "clubdetails";
    }
}
package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Club;
import be.thomasmore.footballhub.repositories.ClubRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ClubController {

    private final ClubRepository clubRepository;

    public ClubController(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @GetMapping("/clubdetails")
    public String clubDetails(Model model) {
        Optional<Club> optionalClub = clubRepository.findById(1);

        optionalClub.ifPresent(club -> model.addAttribute("club", club));

        return "clubdetails";
    }
}
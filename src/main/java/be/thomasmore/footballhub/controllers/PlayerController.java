package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Player;
import be.thomasmore.footballhub.repositories.ClubRepository;
import be.thomasmore.footballhub.repositories.PlayerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;

    public PlayerController(PlayerRepository playerRepository, ClubRepository clubRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
    }

    @GetMapping("/playerlist")
    public String playerList(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Integer clubId,
                             Model model) {

        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        Iterable<Player> players = playerRepository.findByFilter(keyword, clubId);

        model.addAttribute("players", players);
        model.addAttribute("keyword", keyword);
        model.addAttribute("clubId", clubId);
        model.addAttribute("clubs", clubRepository.findAll());

        return "playerlist";
    }

    @GetMapping({"/playerdetails", "/playerdetails/{id}"})
    public String playerDetails(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Player> optionalPlayer = playerRepository.findById(id);

            if (optionalPlayer.isPresent()) {
                model.addAttribute("player", optionalPlayer.get());

                int maxId = (int) playerRepository.count();

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

        return "playerdetails";
    }
}
package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Player;
import be.thomasmore.footballhub.repositories.ClubRepository;
import be.thomasmore.footballhub.repositories.PlayerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/playercreate")
    public String playerCreate(Model model) {
        model.addAttribute("player", new Player());
        model.addAttribute("clubs", clubRepository.findAll());
        model.addAttribute("isEdit", false);
        return "playercreate";
    }

    @PostMapping("/playercreate")
    public String playerCreatePost(@Valid Player player,
                                   BindingResult bindingResult,
                                   Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clubs", clubRepository.findAll());
            model.addAttribute("isEdit", false);
            return "playercreate";
        }

        playerRepository.save(player);

        return "redirect:/playerlist";
    }

    @GetMapping({"/playeredit", "/playeredit/{id}"})
    public String playerEdit(@PathVariable(required = false) Integer id, Model model) {

        if (id != null) {
            Optional<Player> optionalPlayer = playerRepository.findById(id);

            if (optionalPlayer.isPresent()) {
                model.addAttribute("player", optionalPlayer.get());
                model.addAttribute("clubs", clubRepository.findAll());
                model.addAttribute("isEdit", true);
                return "playercreate";
            }
        }

        return "redirect:/playerlist";
    }

    @PostMapping("/playeredit")
    public String playerEditPost(@Valid Player player,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clubs", clubRepository.findAll());
            model.addAttribute("isEdit", true);
            return "playercreate";
        }

        playerRepository.save(player);

        return "redirect:/playerdetails/" + player.getId();
    }

    @PostMapping("/playerdelete/{id}")
    public String playerDelete(@PathVariable Integer id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
        }

        return "redirect:/playerlist";
    }
}
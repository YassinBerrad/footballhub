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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
                Player player = optionalPlayer.get();
                model.addAttribute("player", player);

                List<Player> allPlayers = new ArrayList<>();
                playerRepository.findAllByOrderByIdAsc().forEach(allPlayers::add);

                if (!allPlayers.isEmpty()) {
                    int currentIndex = -1;

                    for (int i = 0; i < allPlayers.size(); i++) {
                        if (allPlayers.get(i).getId().equals(id)) {
                            currentIndex = i;
                            break;
                        }
                    }

                    if (currentIndex != -1) {
                        int prevIndex = currentIndex - 1;
                        int nextIndex = currentIndex + 1;

                        if (prevIndex < 0) {
                            prevIndex = allPlayers.size() - 1;
                        }

                        if (nextIndex >= allPlayers.size()) {
                            nextIndex = 0;
                        }

                        Integer prevId = allPlayers.get(prevIndex).getId();
                        Integer nextId = allPlayers.get(nextIndex).getId();

                        model.addAttribute("prevId", prevId);
                        model.addAttribute("nextId", nextId);
                    }
                }
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
                                   @RequestParam("imageFile") MultipartFile imageFile,
                                   Model model) {

        if (imageFile.isEmpty()) {
            model.addAttribute("imageError", "Afbeelding is verplicht.");
        }

        if (bindingResult.hasErrors() || imageFile.isEmpty()) {
            model.addAttribute("clubs", clubRepository.findAll());
            model.addAttribute("isEdit", false);
            return "playercreate";
        }

        try {
            String fileName = imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/img");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageFile.getBytes());

            player.setImageUrl("/img/" + fileName);
        } catch (IOException e) {
            model.addAttribute("imageError", "Fout bij uploaden van de afbeelding.");
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
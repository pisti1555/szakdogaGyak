package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.service.game.fly_in_the_web.GameService_FITW;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/fly-in-the-web/game")
public class GameController_FITW {
    GameService_FITW service;
    UserFriendsService friendsService;
    SimpMessagingTemplate template;
    InvitationService invitationService;

    @Autowired
    public GameController_FITW(GameService_FITW service, UserFriendsService friendsService, SimpMessagingTemplate template, InvitationService invitationService) {
        this.service = service;
        this.friendsService = friendsService;
        this.template = template;
        this.invitationService = invitationService;
    }

    @GetMapping
    public String getGamePage_FITW(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/fly_in_the_web/gameMenu";
    }

    @GetMapping("/pvp")
    public String pvp_FITW(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        PvP<FITW> pvP = service.getPvP(username);
        if (pvP.getUser2() != null) {
            if (pvP.isReadyToStart()) {
                pvP.setUser1InGame(true);
                pvP.setUser2InGame(true);
                pvP.setDbUpdated(false);
            }

            return "game/fly_in_the_web/spiderweb_pvp";
        }

        return "redirect:/fly-in-the-web/lobby?error=noGameFound";
    }

    @GetMapping("/pvs")
    public String pvs_FITW(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        service.newGamePvC("pvs", principal.getName());
        return "game/fly_in_the_web/spiderweb_pvs";
    }

    @GetMapping("/pvf")
    public String pvf_FITW(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        service.newGamePvC("pvf", principal.getName());
        return "game/fly_in_the_web/spiderweb_pvf";
    }

    @GetMapping("/return-to-lobby")
    public String returnToLobby_FITW(Principal principal) {
        PvP<FITW> pvP = service.getPvP(principal.getName());
        if (principal.getName().equals(pvP.getUser1())) {
            pvP.setUser1InGame(false);
            template.convertAndSendToUser(pvP.getUser2(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/update", "return");
        } else {
            pvP.setUser2InGame(false);
            template.convertAndSendToUser(pvP.getUser1(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvP.getUser1(), "/topic/lobby/update", "return");
        }

        return "redirect:/fly-in-the-web/lobby";
    }
}
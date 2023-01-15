package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller redirecting from Folders to their appropriate Index-Sites.
 *
 * @author Fabio Plunser
 */
@RestController
@SuppressWarnings("unused")
public class RedirectController {

    @Autowired
    private PersonService personService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/index/index.html");
        return modelAndView;
    }
    @GetMapping("${swa.error.base:/error}")
    public ModelAndView error() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/error/index.html");
        return modelAndView;
    }
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/login/index.html");
        return modelAndView;
    }
    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/register/index.html");
        return modelAndView;
    }
    @GetMapping("/learn")
    public ModelAndView learn() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/learn/index.html");
        return modelAndView;
    }
    @GetMapping("/list-cards")
    public ModelAndView listCards() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/listCards/index.html");
        return modelAndView;
    }
    @GetMapping("/edit-cards")
    public ModelAndView editCards() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/editCards/index.html");
        return modelAndView;
    }
    @GetMapping("/create-deck")
    public ModelAndView createDeck() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/createDeck/index.html");
        return modelAndView;
    }
    @GetMapping("/admin")
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/admin/index.html");
        return modelAndView;
    }
    @GetMapping("/admin/show-decks")
    public ModelAndView showDecks() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/admin/showDecks/index.html");
        return modelAndView;
    }
    @GetMapping("/admin/show-cards")
    public ModelAndView showCards() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/src/admin/showCards/index.html");
        return modelAndView;
    }
}
package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class RedirectController {

    @Autowired
    private PersonService personService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login/index.html");
        return modelAndView;
    }
    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register/index.html");
        return modelAndView;
    }
    @GetMapping("/learn")
    public ModelAndView learn() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("learn/index.html");
        return modelAndView;
    }
    @GetMapping("/listCards")
    public ModelAndView listCards() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("listCards/index.html");
        return modelAndView;
    }
    @GetMapping("/admin")
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/index.html");
        return modelAndView;
    }
}
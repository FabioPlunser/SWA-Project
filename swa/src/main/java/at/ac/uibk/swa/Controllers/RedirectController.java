package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@RestController
public class RedirectController {

    @Autowired
    private CustomerService customerService;

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
}
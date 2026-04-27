package ru.tattoo.maxsim.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.UserService;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping(UserController.URL)
public class UserController extends CRUDController<User, Long> {

    public static final String URL = "/user";

    @Autowired
    private UserService userService;

    @Override
    protected String getEntityName() {
        return "admin::user";
    }

    @Override
    protected CRUDService<User, Long> getService() {
        return userService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("users", userService.findAll());
    }

    @Override
    @GetMapping("/delete/{id}")
    public String deleteEntity(@PathVariable("id") Long id,
                               @RequestParam(value = "fragment", required = false) String fragmentName,
                               Model model,
                               HttpServletRequest request) throws IOException, ParseException {
        getService().deleteById(id);
        updateSection(model);
        return getEntityName();
    }
}

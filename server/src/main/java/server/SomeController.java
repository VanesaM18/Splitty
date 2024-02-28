package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class SomeController {

    /**
     * Used for testing purposes for now
     * @return the text to be displayed
     */
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }
}

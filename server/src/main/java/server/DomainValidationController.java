package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequestMapping("/")
public class DomainValidationController {

    /**
     * Used for testing purposes for now
     * @return the text to be displayed
     */
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }


    @Autowired
    private UUID domainModelUuid;

    /**
     * validates the domain and returns its UUID as a string.
     * @return UUID of the domain model as a string.
     */
    @GetMapping("/splitty-domain")
    @ResponseBody
    public String validate() {
        return domainModelUuid.toString();
    }
}

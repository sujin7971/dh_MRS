package egov.framework.plms.main.bean.mvc.controller.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common")
public class CommonRestController {
	
    @GetMapping("/server-status")
    public ResponseEntity<String> checkServerStatus() {
        return new ResponseEntity<>("Server is running", HttpStatus.OK);
    }

    // Add any other common endpoints here
}
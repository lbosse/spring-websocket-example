package echo

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
open class Echo {

    @GetMapping("/")
    fun index(request: HttpServletRequest, response: HttpServletResponse): String {
        return "index"
    }

    @CrossOrigin
    @MessageMapping("/echo")
    @SendToUser("/topic/echo")
    fun echo(message: String): String {
        log.info("Got message: $message")
        Thread.sleep(1000) // simulated delay
        return message
    }

    companion object {
        val log = Logger.getLogger("Echo")
    }

}

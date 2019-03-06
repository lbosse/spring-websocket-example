package echo

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.xml.ws.Response

@Controller
open class Echo {

    @GetMapping("/")
    fun index(request: HttpServletRequest, response: HttpServletResponse): String {
        return "index"
    }

    /*
     * @ResponseBody annotation tells us the response is serialized into JSON and passed back into HttpResponse object. Thymeleaf will not look for a
     * template.
     */
    @PostMapping("/echo")
    @ResponseBody
    fun echoPost(@RequestBody message: NewMessage): Message {
        log.info("Got POST: $message")
        val socketMsg = Message (
            body = message.body,
            timestamp = Instant.now()
        )
        Thread.sleep(1000)
        // call echoWS with message as param to return to user over websocket
        return socketMsg
    }

    @CrossOrigin
    @MessageMapping("/echo")
    @SendToUser("/topic/echo")
    fun echoWS(message: String): String {
        log.info("Got message: $message")
        Thread.sleep(1000) // simulated delay
        return message
    }

    companion object {
        val log = Logger.getLogger("Echo")
    }

    data class Message (
        val body: String,
        val timestamp: Instant
    )
    data class NewMessage @JsonCreator constructor(
        val body: String
    )

}

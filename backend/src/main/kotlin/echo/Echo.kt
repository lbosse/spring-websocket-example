package echo

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
open class Echo {

    @Autowired
    lateinit var simpMessagingTemplate: SimpMessagingTemplate

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
            sessionId = message.sessionId,
            timestamp = Instant.now()
        )
        echoWS(socketMsg)
        return socketMsg
    }

    fun echoWS(message: Message) {
        log.info("Got message: $message")
        val destination = "/topic/" + message.sessionId + "/echo"
        log.info("destination: $destination")
        simpMessagingTemplate.convertAndSend(destination, message.body)
    }

    companion object {
        val log = Logger.getLogger("Echo")
    }

    data class Message (
        val body: String,
        val sessionId: String,
        val timestamp: Instant
    )
    data class NewMessage @JsonCreator constructor(
        val body: String,
        val sessionId: String
    )

}

package echo

import com.fasterxml.jackson.annotation.JsonCreator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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
        val es = Executors.newFixedThreadPool(2)
        //val es = Executors.newSingleThreadExecutor()
        var tasks = mutableListOf<Runnable>()
        val scan = Scanner(message.body)
        while (scan.hasNextLine()) {
            val body = scan.nextLine()
            val ct = Runnable {
                echoWS(Message(body = body, sessionId = socketMsg.sessionId, timestamp = socketMsg.timestamp))
            }
            tasks.add(ct)
        }
        tasks.map { task -> es.execute(task) }
        return socketMsg
    }

    @Synchronized
    fun echoWS(message: Message) {
        log.info("Got message: $message")
        val destination = "/topic/" + message.sessionId + "/echo"
        simpMessagingTemplate.convertAndSend(destination, message.body)
    }

    companion object {
        val log = LoggerFactory.getLogger(Echo::class.java)!!
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

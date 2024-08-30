package example.com.plugins

import example.com.model.SpeakerDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

const val API_URL = "/demo/api"

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        route(API_URL) {
            route("/v2") {
                get("/speakers/{id}") {
                    call.respond(
                        SpeakerDto(
                            id = call.parameters["id"]?.toInt(),
                            firstName = "Alex",
                            lastName = "Nozik",
                            age = 30,
                            description = "Java developer"
                        )
                    )
                }
                webSocket("/speakers") {
                    launch {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                outgoing.send(Frame.Text("YOU SAID: $text"))
                                if (text.equals("bye", ignoreCase = true)) {
                                    close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                                }
                            }
                        }
                    }
                    while (closeReason.isActive) {
                        sendSerialized(
                            SpeakerDto(
                                id = 1,
                                firstName = "Alex",
                                lastName = "Nozik",
                                age = 30,
                                description = "Java developer"
                            )
                        )
                        delay(1000)
                    }
                }
            }
        }
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

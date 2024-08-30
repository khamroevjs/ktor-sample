package example.com.plugins

import example.com.model.SpeakerDto
import example.com.toEntity
import example.com.toModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )
    val speakerService = SpeakerService(database)
    routing {
        route("/v3") {
            // Create speaker
            post("/speakers") {
                val speaker = call.receive<SpeakerDto>().toEntity()
                val id = speakerService.create(speaker)
                call.respond(HttpStatusCode.Created, id)
            }

            // Read speaker
            get("/speakers/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val speaker = speakerService.read(id)
                if (speaker != null) {
                    call.respond(HttpStatusCode.OK, speaker.toModel())
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            // Update speaker
            put("/speakers/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val speaker = call.receive<SpeakerDto>().toEntity()
                speakerService.update(id, speaker)
                call.respond(HttpStatusCode.OK)
            }

            // Delete speaker
            delete("/speakers/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                speakerService.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

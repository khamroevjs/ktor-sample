package example.com.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class SpeakerEntity(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val description: String
)

class SpeakerService(database: Database) {
    object Speakers : IntIdTable() {
        val firstName = varchar("first_name", length = 50)
        val lastName = varchar("last_name", length = 50)
        val description = varchar("description", length = 50)
        val age = integer("age")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Speakers)
        }
    }

    suspend fun create(speaker: SpeakerEntity): Int = dbQuery {
        Speakers.insert {
            it[firstName] = speaker.firstName
            it[lastName] = speaker.lastName
            it[description] = speaker.description
            it[age] = speaker.age
        }[Speakers.id].value
    }

    suspend fun read(id: Int): SpeakerEntity? {
        return dbQuery {
            Speakers.select { Speakers.id eq id }
                .map {
                    SpeakerEntity(
                        id = it[Speakers.id].value,
                        firstName = it[Speakers.firstName],
                        lastName = it[Speakers.lastName],
                        age = it[Speakers.age],
                        description = it[Speakers.description]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, speaker: SpeakerEntity) {
        dbQuery {
            Speakers.update({ Speakers.id eq id }) {
                it[firstName] = speaker.firstName
                it[lastName] = speaker.lastName
                it[age] = speaker.age
                it[description] = description
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Speakers.deleteWhere { Speakers.id.eq(id) }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


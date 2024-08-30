package example.com

import example.com.model.SpeakerDto
import example.com.plugins.SpeakerEntity

fun SpeakerEntity.toModel(): SpeakerDto = SpeakerDto(id, firstName, lastName, age, description)

fun SpeakerDto.toEntity(): SpeakerEntity = SpeakerEntity(id, firstName, lastName, age, description)

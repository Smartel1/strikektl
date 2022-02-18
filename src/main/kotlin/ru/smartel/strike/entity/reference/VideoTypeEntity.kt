package ru.smartel.strike.entity.reference

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "video_types")
class VideoTypeEntity: EntityWithNames()
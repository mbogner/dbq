package dev.mbo.dbq.db.model

import dev.mbo.dbq.db.model.base.BaseEntity
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "locks")
class Lock : BaseEntity<String> {

    @Id
    @Column(nullable = false)
    var id: String? = null

    @Column(name = "locked_at")
    var lockedAt: Instant? = null

    fun updateLockedAt() {
        lockedAt = Instant.now()
    }

    constructor()
    constructor(id: String?) {
        this.id = id
    }

    override fun getIdentifier(): String? {
        return id
    }
}
package dev.mbo.dbq.db.repo

import dev.mbo.dbq.db.model.Lock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.persistence.EntityManager
import javax.persistence.LockModeType
import javax.persistence.PersistenceContext
import javax.persistence.PersistenceException
import javax.transaction.Transactional

@Repository
class LockRepositoryImpl(
    @PersistenceContext private val em: EntityManager
) : LockRepository {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    override fun lock(
        id: String,
        delaySec: Int
    ): Boolean {
        require(delaySec > 0) { "delaySec must be >0" }
        val start = Instant.now()
        log.debug("trying to get lock {} at {}", id, start)
        var lockEntity = em.find(Lock::class.java, id, LockModeType.PESSIMISTIC_WRITE)
            ?: createLockedEntry(id)
        if (null != lockEntity.lockedAt && lockEntity.lockedAt!!.isAfter(
                Instant.now().minusSeconds(delaySec.toLong() - 1L)
            )
        ) {
            log.debug("{} should not execute", id)
            return false
        }
        lockEntity.updateLockedAt()
        lockEntity = em.merge(lockEntity)
        em.lock(lockEntity, LockModeType.PESSIMISTIC_WRITE)
        em.flush()
        log.debug("{} should execute", id)
        return true
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected fun createLockedEntry(id: String): Lock {
        try {
            log.info("lock $id doesn't exist. trying to create it")
            em.persist(Lock(id))
            em.flush()
        } catch (exc: PersistenceException) {
            log.error("could not create lock $id")
            throw exc
        }
        log.info("locking created lock $id")
        return em.find(Lock::class.java, id, LockModeType.PESSIMISTIC_WRITE)
    }
}

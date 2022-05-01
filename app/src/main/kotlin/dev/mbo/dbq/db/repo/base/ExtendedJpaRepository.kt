package dev.mbo.dbq.db.repo.base

import dev.mbo.dbq.db.model.base.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface ExtendedJpaRepository<ID : Serializable, T : BaseEntity<ID>> : JpaRepository<T, ID>,
    JpaSpecificationExecutor<T> {
    fun clear()
    fun refresh(entity: T)
}

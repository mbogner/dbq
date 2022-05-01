package dev.mbo.dbq.db.repo.base

import dev.mbo.dbq.db.model.base.BaseEntity
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import javax.persistence.EntityManager

class ExtendedJpaRepositoryImpl<ID : Serializable, T : BaseEntity<ID>> constructor(
    domainClass: JpaMetamodelEntityInformation<T, ID>,
    protected val em: EntityManager
) : SimpleJpaRepository<T, ID>(domainClass, em),
    ExtendedJpaRepository<ID, T> {

    override fun clear() {
        em.clear()
    }

    override fun refresh(entity: T) {
        em.refresh(entity)
    }
}

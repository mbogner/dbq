package dev.mbo.dbq

import dev.mbo.dbq.db.repo.base.ExtendedJpaRepositoryImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.TimeZone
import javax.annotation.PostConstruct

@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackageClasses = [
        DbqApplication::class,
    ],
    repositoryBaseClass = ExtendedJpaRepositoryImpl::class
)
@SpringBootApplication
class DbqApplication(
    @Value("\${application.timezone:UTC}") private val defaultTimezone: String
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezone))
        log.info("set default timezone to {}", TimeZone.getDefault().id)
    }

}

fun main(args: Array<String>) {
    runApplication<DbqApplication>(*args)
}

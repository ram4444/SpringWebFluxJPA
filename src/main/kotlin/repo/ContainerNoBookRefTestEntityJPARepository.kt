package main.kotlin.repo

import main.kotlin.pojo.ContainerNoBookRefTestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface ContainerNoBookRefTestEntityJPARepository : JpaRepository<ContainerNoBookRefTestEntity, Int> {

}


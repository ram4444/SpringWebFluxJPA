package main.kotlin.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import main.kotlin.repo.ContainerNoBookRefTestEntityJPARepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import main.kotlin.pojo.ContainerNoBookRefTestEntity
import main.kotlin.pojo.TestView
import main.kotlin.repo.MyViewRepository
import main.kotlin.repo.TestViewJPARepository
import org.springframework.data.domain.Example

@Service
class JPAService {
    val objectMapper = ObjectMapper().registerModule(KotlinModule())
    @Autowired
    lateinit var repo: ContainerNoBookRefTestEntityJPARepository
    @Autowired
    lateinit var viewrepo: MyViewRepository

    fun testInsert(): List<ContainerNoBookRefTestEntity> {

        val containerNoBookRefTestEntity = ContainerNoBookRefTestEntity(null, "cntr1", "bkref1", "N", null)

            repo.saveAndFlush(containerNoBookRefTestEntity)

        return listOf<ContainerNoBookRefTestEntity>(containerNoBookRefTestEntity)
    }

    fun queryNonProcessCntrBkRef(): List<ContainerNoBookRefTestEntity> {
        val containerNoBookRefTestEntity = ContainerNoBookRefTestEntity(null,null,null,"N",null)
        return repo.findAll(Example.of(containerNoBookRefTestEntity))
    }

    fun updateProcessCntrBkRefbyID(col1:String): String {
        val containerNoBookRefTestEntity = ContainerNoBookRefTestEntity(null,null,null,"N",null)
        println(col1)
        return "Success"
    }

    //Test for View
    fun queryTestView(): List<TestView> {
        return viewrepo.findAll()
    }
}
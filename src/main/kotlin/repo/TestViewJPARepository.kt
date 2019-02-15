package main.kotlin.repo

import main.kotlin.pojo.TestView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface TestViewJPARepository<TestView, Int> : Repository<TestView, Int> {
    fun findAll(): List<TestView>
    //for view we expose findAll for Query only
}

interface MyViewRepository : TestViewJPARepository<TestView, Int> {}


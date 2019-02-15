package main.kotlin.pojo

import org.springframework.data.repository.NoRepositoryBean
import javax.persistence.*

//REF:https://github.com/eugenp/tutorials/tree/master/spring-mvc-kotlin
@Entity
@Table(name="V_TCKNTCE_TEST")
data class TestView(
        @Id
        @Column(name="ID")
        val id: Int?=null,

        @Column(name="container_no")
        val container_no: String?=null,

        @Column(name="booking_ref")
        val booking_ref: String?=null
)
package main.kotlin.pojo

import javax.persistence.*

//REF:https://github.com/eugenp/tutorials/tree/master/spring-mvc-kotlin
@Entity
@Table(name="TCKNTCE_CNTRBKREF_TEST")
data class ContainerNoBookRefTestEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CNTRBKREF_TEST_SEQUENCE")
        @SequenceGenerator(name="CNTRBKREF_TEST_SEQUENCE", sequenceName = "CNTRBKREF_TEST_SEQ")
        val id: Int?=null,

        @Column(nullable = false)
        val container_no: String?=null,

        @Column(nullable = false)
        val booking_ref: String?=null,

        @Column(nullable = false)
        val process_flag: String?=null,

        @Column(nullable = false)
        val col_1: String?=null
)
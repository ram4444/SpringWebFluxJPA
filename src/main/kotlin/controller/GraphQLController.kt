package main.kotlin.controller

import graphql.schema.DataFetcher

import graphql.schema.StaticDataFetcher
import main.kotlin.graphql.GraphQLHandler
import main.kotlin.graphql.GraphQLRequest
import main.kotlin.pojo.LightComment
import main.kotlin.pojo.ContainerNoBookRefTestEntity
import main.kotlin.pojo.Response
import main.kotlin.service.JPAService
import main.kotlin.service.WebfluxJSONPlaceholderService
import main.kotlin.service.WebfluxService
import mu.KotlinLogging

import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import reactor.core.scheduler.Schedulers
import javax.annotation.PostConstruct

@RestController
class GraphQLController() {

    private val logger = KotlinLogging.logger {}

    @Autowired
    val jPAservice: JPAService = JPAService()
    @Autowired
    val webFluxDBService: WebfluxService = WebfluxService()
    @Autowired
    val webfluxJSONPlaceholderService: WebfluxJSONPlaceholderService = WebfluxJSONPlaceholderService()

    //Initiate schema from somewhere
    val schema ="""
            type Query{
                test_query: Int
                get_nonprocess_cntrref: [containerNoBookRefTestEntity]
                get_testview: [testView]
            }
            type Mutation{
                testInsert_cntrref: [containerNoBookRefTestEntity]
                update_cntrref(col_1:String):String
                update_cntrref2: Int
            }
            type containerNoBookRefTestEntity{
                id: Int
                container_no: String
                booking_ref: String
                process_flag: String
                col_1: String
            }
            type testView{
                id: Int
                container_no: String
                booking_ref: String
            }
            """

    lateinit var fetchers: Map<String, List<Pair<String, DataFetcher<out Any>>>>
    lateinit var handler:GraphQLHandler

    @PostConstruct
    fun init() {
        //initialize Fetchers
        fetchers = mapOf(
                "Query" to
                        listOf(
                                "test_query" to StaticDataFetcher(999),
                                "get_nonprocess_cntrref" to DataFetcher{jPAservice.queryNonProcessCntrBkRef()},
                                "get_testview" to DataFetcher{jPAservice.queryTestView()}

                        ),
                "Mutation" to
                        listOf(
                                "testInsert_cntrref" to DataFetcher{jPAservice.testInsert()},
                                "update_cntrref" to DataFetcher{environment ->  jPAservice.updateProcessCntrBkRefbyID(environment.getArgument("col_1"))},
                                "update_cntrref2" to StaticDataFetcher(919)
                        )
        )

        handler = GraphQLHandler(schema, fetchers)
    }

    @RequestMapping("/")
    fun pingcheck():String {
        println("ping")
        return "success"
    }

    @PostMapping("/graphql")
    fun executeGraphQL(@RequestBody request:GraphQLRequest):Map<String, Any> {

        logger.info { "GraphQL Request is received." }
        logger.debug { "request.query:" };logger.debug { request.query }
        logger.debug { "request.params:" };logger.debug { request.params }
        logger.debug { "request.operationName:" };logger.debug { request.operationName }

        val result = handler.execute(request.query, request.params, request.operationName, ctx = null)

        return mapOf("data" to result.getData<Any>())
    }

    @PostMapping("/webflux")
    fun getData(@RequestBody name:String): Mono<ResponseEntity<List<ContainerNoBookRefTestEntity>>> {
        return webFluxDBService.fetchTestEntity(name)
                //.filter { it -> it. % 2 == 0 } // can be obmitted
                .take(20) // can be obmitted
                .parallel(4) // can be obmitted
                .runOn(Schedulers.parallel())
                // insert any mapping of Flux<Entity> here
                .sequential()
                .collectList()
                .map { body -> ResponseEntity.ok().body(body) }
                .toMono()
    }

    //Sample from JSONPlaceholder
    @PostMapping("/webfluxtest")
    fun getDataFromPlaceholder(): Mono<ResponseEntity<List<Response>>> {
        return webfluxJSONPlaceholderService.fetchPosts()
                .filter { it -> it.userId % 2 == 0 }
                .take(20)
                .parallel(4)
                .runOn(Schedulers.parallel())
                .map { post -> webfluxJSONPlaceholderService.fetchComments(post.id)
                        .map { comment -> LightComment(email = comment.email, body = comment.body) }
                        .collectList()
                        .zipWith(post.toMono()) }

                .flatMap { it -> it }
                .map { result -> Response(
                        postId = result.t2.id,
                        userId = result.t2.userId,
                        title = result.t2.title,
                        comments = result.t1
                ) }
                .sequential()
                .collectList()
                .map { body -> ResponseEntity.ok().body(body) }
                .toMono()
    }
}

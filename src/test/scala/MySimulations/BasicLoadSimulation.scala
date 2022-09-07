package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicLoadSimulation extends Simulation {
  /**
   * Swagger: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllGames() = {
    exec(
      http("Get all games")
        .get("/videogame")
    )
  }

  def getGame() = {
    exec(
      http("Get game")
        .get("/videogame/1")
    )
  }

  val scn = scenario("Basic load simulation")
    .exec(getAllGames()).pause(1)
    .exec(getGame()).pause(1)
    .exec(getAllGames()).pause(1)

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(5),
      rampUsers(10).during(10)
    )
  ).protocols(httpProtocol)
}

package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FixedDurationLoadSimulation extends Simulation {
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

  val scn = scenario("Fixed duration load simulation")
    .forever {
      exec(getAllGames()).pause(1)
      .exec(getGame()).pause(1)
      .exec(getAllGames()).pause(1)
    }

  setUp(
    scn.inject(
      nothingFor(2),
      atOnceUsers(10),
      rampUsers(20).during(30),
    )
  ).protocols(httpProtocol).maxDuration(60)

}

package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RuntimeParameters extends Simulation {

  /**
   * Swagger: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  /**
   * Command line command
   *
   * Structure: mvn gatling:test -Dgatling.simulationClass=<<ClassName>>
   * Example:   mvn gatling:test -Dgatling.simulationClass=MySimulations.RuntimeParameters
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllGames() = {
    exec(
      http("Get all games")
        .get("/videogame")
    )
  }

  val scn = scenario("Runtime parameters")
    .forever {
      exec(getAllGames()).pause(1)
    }


  setUp(
    scn.inject(
      nothingFor(2),
      rampUsers(10).during(20)
    )
  ).protocols(httpProtocol)
    .maxDuration(20)

}

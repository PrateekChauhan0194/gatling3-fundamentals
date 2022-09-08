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
   * Structure (without params): mvn gatling:test -Dgatling.simulationClass=<<ClassName>>
   * Example (without params):   mvn gatling:test -Dgatling.simulationClass=MySimulations.RuntimeParameters
   *
   * Structure (with params): mvn gatling:test -Dgatling.simulationClass=<<ClassName>> -D<<ParamName>>=<<ParamValue>>
   * Example (with params):   mvn gatling:test -Dgatling.simulationClass=MySimulations.RuntimeParameters -DUSER_COUNT=10 -DRAMP_DURATION=12 -DTEST_DURATION=20
   */

  def USER_COUNT: Int = System.getProperty("USER_COUNT", "5").toInt
  def RAMP_DURATION: Int = System.getProperty("RAMP_DURATION", "10").toInt
  def TEST_DURATION: Int = System.getProperty("TEST_DURATION", "30").toInt

  before {
    println(s"Running test with ${USER_COUNT} users.")
    println(s"Ramping users over ${RAMP_DURATION} seconds.")
    println(s"Total test duration is ${TEST_DURATION} seconds.")
  }

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
      rampUsers(USER_COUNT).during(RAMP_DURATION)
    )
  ).protocols(httpProtocol)
    .maxDuration(TEST_DURATION)

}

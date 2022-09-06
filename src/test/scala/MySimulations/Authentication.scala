package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class Authentication extends Simulation {

  /**
   * Swagger: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  // Call to authenticate the user and get an auth token to make the further authenticated calls
  def authenticateUser() = {
    exec(
      http("Authenticate user")
        .post("/authenticate")
        .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}")).asJson
        .check(jsonPath("$.token").saveAs("AUTH_TOKEN"))  // Saving auth token in a session variable
    )
  }

  // Authenticated endpoint to create a new game
  def createNewGame() = {
    exec(
      http("Create new game")
        .post("/videogame")
        .header("Authorization", "Bearer #{AUTH_TOKEN}")  // Using the auth token in headers for authentication
        .body(
          StringBody("{\n  \"category\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}")
        ).asJson
    )
  }

  val scn = scenario("Authentication scenario")
    .exec(authenticateUser())
    .exec(createNewGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}

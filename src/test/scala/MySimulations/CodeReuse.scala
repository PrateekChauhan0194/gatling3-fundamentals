package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CodeReuse extends Simulation {

  val httpProtocol = http.baseUrl("https://video-game-db-fs.vercel.app/api/v1/videogames")
    .acceptHeader("application/json")

  /**
   * Reusable methods to be used in our scenario
   * @return
   */
  def getAllGames() = {
    // This will repeat the whole repeat block 2 times
    repeat(2) {
      exec(
        http("Get all video games")
          .get("/")
          .check(status.is(200))
      )
    }
  }

  def getGame(gameId: String) = {
    // This will repeat the whole repeat block 2 times with a counter variable which will increment on every iteration
    // Counter starts with 0
    repeat(5, counterName = "COUNTER") {
      exec(
        http("Get game: #{COUNTER}")
          .get("/" + gameId)
          .check(status.is(200))
      )
    }
  }

  def addGame(body: String) = {
    /**
     * @param
     * body: String
     * example: """{ "gameId": "22", "name": "Player Unknown Battleground", "category": "Battle Royal" }"""
     */
    exec(
      http("Add game")
        .post("/")
        .body(StringBody(body)).asJson
        .check(
          status.is(201),
          jsonPath("$.msg").is("Videogame added successfully")
        )
    )
  }

  def deleteGame(gameId: String) = {
    exec(
      http("Delete game")
        .delete("/" + gameId)
        .check(
          status.is(200),
          jsonPath("$").is("Game record successfully deleted!")
        )
    )
  }

  val scn = scenario("Code reuse")
    repeat(2) { // Repeat can be used in scenario also (This will repeat the getAllGames method)
      getAllGames()
    }
    .exec(getAllGames())
    .pause(2)
    .exec(getGame("1"))
//    .pause(2)
//    .exec(addGame("""{ "gameId": "22", "name": "Player Unknown Battleground", "category": "Battle Royal" }"""))
//    .pause(2)
//    .exec(getGame("22"))
    .exec { session => println(session); session }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}

package org.stratio.carrefour.poc

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.scaladsl._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.stratio.carrefour.poc.Command._

import scala.concurrent.ExecutionContext
import scala.io.StdIn._
import scala.concurrent.duration.FiniteDuration

/**
  * Created by aroa on 7/06/17.
  */
object Main {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val system = ActorSystem("poc")
    implicit val timeout = Timeout(FiniteDuration(2, "seconds"))
    implicit val materializer = ActorMaterializer()

    lazy val readJournal = PersistenceQuery(system)
      .readJournalFor("inmemory-read-journal")
      .asInstanceOf[
        ReadJournal with CurrentPersistenceIdsQuery with CurrentEventsByPersistenceIdQuery with CurrentEventsByTagQuery with EventsByPersistenceIdQuery with EventsByTagQuery]

    readJournal.eventsByPersistenceId("123", 0l,Long.MaxValue).runForeach(println)

    val actor = system.actorOf(Article.props(123))

    actor ? ChangePrice(12) map println
    actor ? Inventory(100) map println
    actor ? BuyPetition(3, 987654321) map println
    actor ? BuyPetition(10, 12345678) map println
    actor ? BuyPetition(100, 999999) map println


    console(actor)
  }


  def console(actor:ActorRef)(implicit ex:ExecutionContext, timeout: Timeout):Unit = {
    print(">>>")
    val c = readLine()
    val result = c match {
      case "buy" => actor ? BuyPetition(readInt(), 10)
      case "state" => actor ? State
      case "return" => actor ? Return(readInt(), 10)
      case "inventory" => actor ? Inventory(readInt())
    }
    result.map(println)
    console(actor)
  }
}

package org.stratio.carrefour.poc

import akka.actor.{ActorRef, Props}
import akka.persistence.PersistentActor
import org.stratio.carrefour.poc.Command._
import org.stratio.carrefour.poc.Event._

object Article {
  def props(ean:Long):Props = Props(new Article(ean))
}

trait Command

object Command {

  case class ChangePrice(price: Int) extends Command

  case class BuyPetition(n: Int, customerId: Long) extends Command

  case class Return(n: Int, customerId: Long) extends Command

  case class Inventory(n: Int) extends Command

  case object State extends Command

}

trait Event
object Event {

  case class PriceChanged(price: Int) extends Event

  case class ClientBuy(n: Int, customerId: Long, price: Int) extends Event

  case class ClientBuyRejected(n: Int, customerId: Long) extends Event

  case class ClientReturn(n: Int, customerId: Long) extends Event

  case class StockChanged(n: Int) extends Event

}

class Article(ean:Long) extends PersistentActor{

  private val stringEan = ean.toString

  private var amount:Int = 0
  private var price:Int = 0

  override def persistenceId: String = stringEan

  override def receiveRecover: Receive = {
    case event:Event => updateState(event)
  }

  override def receiveCommand: Receive = {
    case ChangePrice(newPrice) =>
      val changed = PriceChanged(newPrice)
      persist(changed)(updateState)
      sender() ! changed
    case BuyPetition(n, id) =>
      val result = if (n <= amount)
        ClientBuy(n, id, price)
      else
        ClientBuyRejected(n, id)
      persist(result)(updateState)
      sender() ! result
    case Return(n, id) =>
      val clientReturn = ClientReturn(n, id)
      persist(clientReturn)(updateState)
      sender() ! clientReturn
    case Inventory(n) =>
      val changed = StockChanged(n)
      persist(changed)(updateState)
      sender() ! changed
    case State => sender ! s"amount: $amount price: $price"
  }

  def updateState(evt: Event): Unit = {
    evt match {
      case PriceChanged(newPrice) => price = newPrice
      case ClientBuy(n, _, _) => amount = amount - n
      case ClientReturn(n, _) => amount = amount + n
      case StockChanged(n) => amount = n
      case _ =>
    }
    sender ! evt
  }
}

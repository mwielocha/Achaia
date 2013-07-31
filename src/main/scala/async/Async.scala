package async

import akka.actor.ActorSystem

/**
 * author mikwie
 *
 */
object Async {

  lazy val system = ActorSystem.create()

}

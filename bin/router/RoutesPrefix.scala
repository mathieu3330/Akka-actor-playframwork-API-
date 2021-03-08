// @GENERATOR:play-routes-compiler
// @SOURCE:D:/perso/play-samples-play-java-hello-world-tutorial/conf/routes
// @DATE:Thu Mar 04 14:35:49 CET 2021


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}

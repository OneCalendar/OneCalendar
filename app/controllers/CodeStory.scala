package controllers

import play.api.mvc._

object CodeStory extends Controller {

    def ugo = Action { implicit request =>
        println("[CODESTORY]request : %s".format(request))
        Ok("bourdon.ugo@gmail.com")
    }
}

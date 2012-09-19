package controllers

import play.api.mvc.Controller
import dao.configuration.injection.MongoConfiguration

trait OneCalendarController extends Controller {
    implicit val mongoConfigProd: MongoConfiguration = MongoConfiguration("OneCalendar")
}
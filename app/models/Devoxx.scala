/*
 * Copyright 2012 OneCalendar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import org.codehaus.jackson.annotate._

case class DevoxxTag(name: String)

case class DevoxxSpeakers(speakerUri: String, speaker: String)

@JsonIgnoreProperties(ignoreUnknown = true)
case class DevoxxPresentation(tags: Seq[DevoxxTag], summary: String, id: Long,
                              speakerUri: String, title: String, speaker: String,
                              track: String, experience: String, speakers: Seq[DevoxxSpeakers],
                              room: Option[String])


@JsonIgnoreProperties(ignoreUnknown = true)
case class DevoxxSchedule(id: Option[Long], partnerSlot: Option[Boolean], fromTime: Option[String], code: Option[String],
                          note: Option[String], toTime: Option[String], kind: Option[String], room: Option[String],
                          presentationUri: Option[String], speaker: Option[String], title: Option[String],
                          speakerUri: Option[String]
                             )

@JsonIgnoreProperties(ignoreUnknown = true)
case class DevoxxEvents(to:String, id:Long, enabled:Boolean, location:String, description:String, name:String, from:String)

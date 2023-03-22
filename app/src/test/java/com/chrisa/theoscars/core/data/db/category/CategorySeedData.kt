/*
 * Copyright 2023 Chris Anderson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisa.theoscars.core.data.db.category

object CategorySeedData {
    val data = """
[
  {
    "id": 1,
    "aliasId": 6,
    "name": "ACTOR"
  },
  {
    "id": 2,
    "aliasId": 7,
    "name": "ACTRESS"
  },
  {
    "id": 3,
    "aliasId": 10,
    "name": "ART DIRECTION"
  },
  {
    "id": 4,
    "aliasId": 4,
    "name": "CINEMATOGRAPHY"
  },
  {
    "id": 5,
    "aliasId": 10,
    "name": "DIRECTING (Comedy Picture)"
  },
  {
    "id": 6,
    "aliasId": 10,
    "name": "DIRECTING (Dramatic Picture)"
  },
  {
    "id": 7,
    "aliasId": 21,
    "name": "ENGINEERING EFFECTS"
  },
  {
    "id": 8,
    "aliasId": 1,
    "name": "OUTSTANDING PICTURE"
  },
  {
    "id": 9,
    "aliasId": 2,
    "name": "UNIQUE AND ARTISTIC PICTURE"
  },
  {
    "id": 10,
    "aliasId": 11,
    "name": "WRITING (Adaptation)"
  },
  {
    "id": 11,
    "aliasId": 11,
    "name": "WRITING (Original Story)"
  },
  {
    "id": 12,
    "aliasId": 11,
    "name": "WRITING (Title Writing)"
  },
  {
    "id": 14,
    "aliasId": 10,
    "name": "DIRECTING"
  },
  {
    "id": 15,
    "aliasId": 11,
    "name": "WRITING"
  },
  {
    "id": 16,
    "aliasId": 1,
    "name": "OUTSTANDING PRODUCTION"
  },
  {
    "id": 17,
    "aliasId": 15,
    "name": "SOUND RECORDING"
  },
  {
    "id": 18,
    "aliasId": 13,
    "name": "SHORT SUBJECT (Cartoon)"
  },
  {
    "id": 19,
    "aliasId": 14,
    "name": "SHORT SUBJECT (Comedy)"
  },
  {
    "id": 20,
    "aliasId": 14,
    "name": "SHORT SUBJECT (Novelty)"
  },
  {
    "id": 21,
    "aliasId": 10,
    "name": "ASSISTANT DIRECTOR"
  },
  {
    "id": 22,
    "aliasId": 22,
    "name": "FILM EDITING"
  },
  {
    "id": 23,
    "aliasId": 16,
    "name": "MUSIC (Scoring)"
  },
  {
    "id": 24,
    "aliasId": 17,
    "name": "MUSIC (Song)"
  },
  {
    "id": 25,
    "aliasId": 10,
    "name": "DANCE DIRECTION"
  },
  {
    "id": 26,
    "aliasId": 11,
    "name": "WRITING (Screenplay)"
  },
  {
    "id": 27,
    "aliasId": 8,
    "name": "ACTOR IN A SUPPORTING ROLE"
  },
  {
    "id": 28,
    "aliasId": 9,
    "name": "ACTRESS IN A SUPPORTING ROLE"
  },
  {
    "id": 29,
    "aliasId": 14,
    "name": "SHORT SUBJECT (Color)"
  },
  {
    "id": 30,
    "aliasId": 14,
    "name": "SHORT SUBJECT (One-reel)"
  },
  {
    "id": 31,
    "aliasId": 14,
    "name": "SHORT SUBJECT (Two-reel)"
  },
  {
    "id": 33,
    "aliasId": 16,
    "name": "MUSIC (Original Score)"
  },
  {
    "id": 34,
    "aliasId": 4,
    "name": "CINEMATOGRAPHY (Black-and-White)"
  },
  {
    "id": 35,
    "aliasId": 4,
    "name": "CINEMATOGRAPHY (Color)"
  },
  {
    "id": 36,
    "aliasId": 21,
    "name": "SPECIAL EFFECTS"
  },
  {
    "id": 37,
    "aliasId": 10,
    "name": "ART DIRECTION (Black-and-White)"
  },
  {
    "id": 38,
    "aliasId": 10,
    "name": "ART DIRECTION (Color)"
  },
  {
    "id": 39,
    "aliasId": 11,
    "name": "WRITING (Original Screenplay)"
  },
  {
    "id": 40,
    "aliasId": 12,
    "name": "DOCUMENTARY (Short Subject)"
  },
  {
    "id": 41,
    "aliasId": 16,
    "name": "MUSIC (Music Score of a Dramatic Picture)"
  },
  {
    "id": 42,
    "aliasId": 16,
    "name": "MUSIC (Scoring of a Musical Picture)"
  },
  {
    "id": 43,
    "aliasId": 1,
    "name": "OUTSTANDING MOTION PICTURE"
  },
  {
    "id": 44,
    "aliasId": 5,
    "name": "DOCUMENTARY"
  },
  {
    "id": 45,
    "aliasId": 16,
    "name": "MUSIC (Music Score of a Dramatic or Comedy Picture)"
  },
  {
    "id": 46,
    "aliasId": 11,
    "name": "WRITING (Original Motion Picture Story)"
  },
  {
    "id": 47,
    "aliasId": 5,
    "name": "DOCUMENTARY (Feature)"
  },
  {
    "id": 48,
    "aliasId": 1,
    "name": "BEST MOTION PICTURE"
  },
  {
    "id": 49,
    "aliasId": 11,
    "name": "WRITING (Motion Picture Story)"
  },
  {
    "id": 50,
    "aliasId": 19,
    "name": "COSTUME DESIGN (Black-and-White)"
  },
  {
    "id": 51,
    "aliasId": 19,
    "name": "COSTUME DESIGN (Color)"
  },
  {
    "id": 52,
    "aliasId": 21,
    "name": "SPECIAL FOREIGN LANGUAGE FILM AWARD"
  },
  {
    "id": 53,
    "aliasId": 11,
    "name": "WRITING (Story and Screenplay)"
  },
  {
    "id": 56,
    "aliasId": 2,
    "name": "FOREIGN LANGUAGE FILM"
  },
  {
    "id": 57,
    "aliasId": 11,
    "name": "WRITING (Screenplay--Adapted)"
  },
  {
    "id": 58,
    "aliasId": 11,
    "name": "WRITING (Screenplay--Original)"
  },
  {
    "id": 60,
    "aliasId": 19,
    "name": "COSTUME DESIGN"
  },
  {
    "id": 61,
    "aliasId": 14,
    "name": "SHORT SUBJECT (Live Action)"
  },
  {
    "id": 62,
    "aliasId": 11,
    "name": "WRITING (Screenplay--based on material from another medium)"
  },
  {
    "id": 63,
    "aliasId": 11,
    "name": "WRITING (Story and Screenplay--written directly for the screen)"
  },
  {
    "id": 64,
    "aliasId": 15,
    "name": "SOUND"
  },
  {
    "id": 65,
    "aliasId": 16,
    "name": "MUSIC (Music Score--substantially original)"
  },
  {
    "id": 66,
    "aliasId": 16,
    "name": "MUSIC (Scoring of Music--adaptation or treatment)"
  },
  {
    "id": 67,
    "aliasId": 1,
    "name": "BEST PICTURE"
  },
  {
    "id": 68,
    "aliasId": 15,
    "name": "SOUND EFFECTS"
  },
  {
    "id": 69,
    "aliasId": 21,
    "name": "SPECIAL VISUAL EFFECTS"
  },
  {
    "id": 70,
    "aliasId": 16,
    "name": "MUSIC (Original Music Score)"
  },
  {
    "id": 71,
    "aliasId": 16,
    "name": "MUSIC (Original Score--for a motion picture [not a musical])"
  },
  {
    "id": 72,
    "aliasId": 16,
    "name": "MUSIC (Score of a Musical Picture--original or adaptation)"
  },
  {
    "id": 73,
    "aliasId": 17,
    "name": "MUSIC (Song--Original for the Picture)"
  },
  {
    "id": 74,
    "aliasId": 11,
    "name": "WRITING (Story and Screenplay--based on material not previously published or produced)"
  },
  {
    "id": 75,
    "aliasId": 17,
    "name": "MUSIC (Original Song Score)"
  },
  {
    "id": 76,
    "aliasId": 11,
    "name": "WRITING (Story and Screenplay--based on factual material or material not previously published or produced)"
  },
  {
    "id": 77,
    "aliasId": 16,
    "name": "MUSIC (Original Dramatic Score)"
  },
  {
    "id": 78,
    "aliasId": 16,
    "name": "MUSIC (Scoring: Adaptation and Original Song Score)"
  },
  {
    "id": 79,
    "aliasId": 13,
    "name": "SHORT SUBJECT (Animated)"
  },
  {
    "id": 81,
    "aliasId": 16,
    "name": "MUSIC (Scoring: Original Song Score and Adaptation -or- Scoring: Adaptation)"
  },
  {
    "id": 82,
    "aliasId": 13,
    "name": "SHORT FILM (Animated)"
  },
  {
    "id": 83,
    "aliasId": 14,
    "name": "SHORT FILM (Live Action)"
  },
  {
    "id": 84,
    "aliasId": 11,
    "name": "WRITING (Screenplay Adapted from Other Material)"
  },
  {
    "id": 85,
    "aliasId": 17,
    "name": "MUSIC (Original Song)"
  },
  {
    "id": 87,
    "aliasId": 6,
    "name": "ACTOR IN A LEADING ROLE"
  },
  {
    "id": 88,
    "aliasId": 7,
    "name": "ACTRESS IN A LEADING ROLE"
  },
  {
    "id": 89,
    "aliasId": 17,
    "name": "MUSIC (Original Song Score and Its Adaptation or Adaptation Score)"
  },
  {
    "id": 90,
    "aliasId": 11,
    "name": "WRITING (Screenplay Written Directly for the Screen--based on factual material or on story material not previously published or produced)"
  },
  {
    "id": 91,
    "aliasId": 21,
    "name": "VISUAL EFFECTS"
  },
  {
    "id": 94,
    "aliasId": 16,
    "name": "MUSIC (Adaptation Score)"
  },
  {
    "id": 95,
    "aliasId": 11,
    "name": "WRITING (Screenplay Based on Material from Another Medium)"
  },
  {
    "id": 96,
    "aliasId": 11,
    "name": "WRITING (Screenplay Written Directly for the Screen)"
  },
  {
    "id": 97,
    "aliasId": 17,
    "name": "MUSIC (Original Song Score and Its Adaptation -or- Adaptation Score)"
  },
  {
    "id": 99,
    "aliasId": 14,
    "name": "SHORT FILM (Dramatic Live Action)"
  },
  {
    "id": 100,
    "aliasId": 18,
    "name": "MAKEUP"
  },
  {
    "id": 101,
    "aliasId": 15,
    "name": "SOUND EFFECTS EDITING"
  },
  {
    "id": 102,
    "aliasId": 17,
    "name": "MUSIC (Original Song Score or Adaptation Score)"
  },
  {
    "id": 103,
    "aliasId": 11,
    "name": "WRITING (Screenplay Based on Material Previously Produced or Published)"
  },
  {
    "id": 104,
    "aliasId": 16,
    "name": "MUSIC (Original Musical or Comedy Score)"
  },
  {
    "id": 105,
    "aliasId": 15,
    "name": "SOUND EDITING"
  },
  {
    "id": 106,
    "aliasId": 3,
    "name": "ANIMATED FEATURE FILM"
  },
  {
    "id": 107,
    "aliasId": 11,
    "name": "WRITING (Adapted Screenplay)"
  },
  {
    "id": 108,
    "aliasId": 15,
    "name": "SOUND MIXING"
  },
  {
    "id": 109,
    "aliasId": 18,
    "name": "MAKEUP AND HAIRSTYLING"
  },
  {
    "id": 110,
    "aliasId": 20,
    "name": "PRODUCTION DESIGN"
  },
  {
    "id": 111,
    "aliasId": 2,
    "name": "INTERNATIONAL FEATURE FILM"
  },
  {
    "id": 112,
    "aliasId": 6,
    "name": "Actor in a Leading Role"
  },
  {
    "id": 113,
    "aliasId": 8,
    "name": "Actor in a Supporting Role"
  },
  {
    "id": 114,
    "aliasId": 7,
    "name": "Actress in a Leading Role"
  },
  {
    "id": 115,
    "aliasId": 9,
    "name": "Actress in a Supporting Role"
  },
  {
    "id": 116,
    "aliasId": 3,
    "name": "Animated Feature Film"
  },
  {
    "id": 117,
    "aliasId": 4,
    "name": "Cinematography"
  },
  {
    "id": 118,
    "aliasId": 19,
    "name": "Costume Design"
  },
  {
    "id": 119,
    "aliasId": 10,
    "name": "Directing"
  },
  {
    "id": 120,
    "aliasId": 5,
    "name": "Documentary Feature Film"
  },
  {
    "id": 121,
    "aliasId": 12,
    "name": "Documentary Short Film"
  },
  {
    "id": 122,
    "aliasId": 22,
    "name": "Film Editing"
  },
  {
    "id": 123,
    "aliasId": 2,
    "name": "International Feature Film"
  },
  {
    "id": 124,
    "aliasId": 18,
    "name": "Makeup and Hairstyling"
  },
  {
    "id": 125,
    "aliasId": 16,
    "name": "Music (Original Score)"
  },
  {
    "id": 126,
    "aliasId": 17,
    "name": "Music (Original Song)"
  },
  {
    "id": 127,
    "aliasId": 1,
    "name": "Best Picture"
  },
  {
    "id": 128,
    "aliasId": 20,
    "name": "Production Design"
  },
  {
    "id": 129,
    "aliasId": 13,
    "name": "Short Film (Animated)"
  },
  {
    "id": 130,
    "aliasId": 14,
    "name": "Short Film (Live Action)"
  },
  {
    "id": 131,
    "aliasId": 15,
    "name": "Sound"
  },
  {
    "id": 132,
    "aliasId": 21,
    "name": "Visual Effects"
  },
  {
    "id": 133,
    "aliasId": 11,
    "name": "Writing (Adapted Screenplay)"
  },
  {
    "id": 134,
    "aliasId": 11,
    "name": "Writing (Original Screenplay)"
  }
]
""".trim()
}

import myaa.subkt.ass.*
import myaa.subkt.tasks.*
import myaa.subkt.tasks.Mux.*
import myaa.subkt.tasks.utils.getMkvInfo
//import myaa.subkt.tasks.Nyaa.*
import java.awt.Color
import java.time.*

plugins {
  id("myaa.subkt")
}

var engDialogue : Array<String> = arrayOf<String>(
  "Main",
  "Thoughts",
  "Flashback",
  "Secondary",
  "Narrator",
  "Note"
)

var esSign : Array<String> = arrayOf<String>(
  "OPLetreros",
  "Credits",
  "Title",
  "Captions",
  "WeAreTrad",
  "WeAreKara",
  "BelieveTrad",
  "BelieveKara",
  "TOPPU",
  "Top",
  "Kanji",
  "Karaoke",
  "Kanji-furigana",
  "Translation",
  "We-Go-Karaoke",
  "We-Go-Translation",
  "HikariRom",
  "HikariTradu"
)

fun EventLine.styleContainsAny (strings: Array<String>) : Boolean {
  for (s in strings) {
    if (this.style.contains(s)) return true;
  }
  return false;
}

fun EventLine.isDialogue () : Boolean {
  return this.styleContainsAny(engDialogue);
}

fun EventLine.isSpanishDialogue () : Boolean {
  return !this.styleContainsAny(esSign);
}

fun escapeTitle (title: String) : String {
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
        return title.replace("\"", "\\\"");
    }
    return title;
}

subs {
  readProperties("sub.properties", "chapter.properties", "title.properties", "publicrepo.properties")
  episodes(getList("episodes"))

  val increaseLayer by task<ASS> {
    from(get("ensubs"))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  merge {
    from(increaseLayer.item())

    fromIfPresent(getList("typesetting"), ignoreMissingFiles = true)

    if (propertyExists("OP") && !propertyExists("noOP")) {
      from(get("OP")) {
        syncSourceLine("sync", EventLineAccessor.EFFECT)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get("mergefile"))
  }

  // Removes all the dialoge lines from English subs
  val dubWithKaraoke by task<ASS> {
    from(merge.item())
    ass {
      events.lines.removeIf {
        it.isDialogue ()
      }
    }
  }

  // Removes all the dialoge and karaoke lines from English subs
  val dubWithoutKaraoke by task<ASS> {
    from(get("ensubs"))
    ass {
      events.lines.removeIf {
        it.isDialogue ()
      }
    }
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)
  }

  chapters{
    from(get("chapters"))
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (German subs)
  val increaseLayer_de by task<ASS> {
    from(get("desubs"))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Portuguese subs)
  val increaseLayer_pt by task<ASS> {
    from(get("ptsubs"))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Merge subs with karaoke (German subs)
  val merge_de by task<Merge> {
    from(increaseLayer_de.item())

    if (propertyExists("OP_de")) {
      from(get("OP_de")) {
        syncSourceLine("sync", EventLineAccessor.EFFECT)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get("mergefile_de"))
  }

  // Merge subs with karaoke (Portuguese subs)
  val merge_pt by task<Merge> {
    from(increaseLayer_pt.item())

    if (propertyExists("OP_pt")) {
      from(get("OP_pt")) {
        syncSourceLine("sync", EventLineAccessor.EFFECT)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get("mergefile_pt"))
  }

  // Removes all non dialogue lines from Spanish subtitle
  val spanishDub by task<ASS> {
    from(get("spsubs"))
    ass {
      events.lines.removeIf {
        it.isSpanishDialogue ()
      }
    }
  }

  // Helper task to merge all language subs needing merging and output to Final Subs folder
  val mergeAll by task<DefaultSubTask> {
    dependsOn(merge.item())
    if (file(get("desubs")).exists()) {
      dependsOn(merge_de.item())
    }
    if (file(get("ptsubs")).exists()) {
      dependsOn(merge_pt.item())
    }
  }

  mux {
    skipUnusedFonts(true)
    // Uncomment this line if the script stops due to font missing some glyphs or try to use other fonts.
    onMissingGlyphs(ErrorMode.WARN)

    title(escapeTitle(get("title").get()))

    from(get("video")) {
      video {
        lang("jpn")
        default(true)
        trackOrder(0)
      }
      audio {
          if (track.lang == "jpn") {
              name("Japanese")
              trackOrder(1)
              default(true)
            } else {
                name("English")
                trackOrder(2)
                default(false)
              }
        }
      includeChapters(false)
      attachments { include(false) }
      subtitles { include(false) }
    }

    from(merge.item()) {
      tracks {
        name("English")
        lang("eng")
        default(true)
      }
    }

    // Add spanish audio if it exists
    if (file(get("esaudio")).exists()) {
      from(get("esaudio")) {
        tracks {
          name("Spanish Dub")
          lang("es")
          default(false)
        }
      }
    }

    // Signs and Songs subtitle for English Dub if English dub exists
    val mkvInfo = getMkvInfo(file(get("video")))
    if (mkvInfo.audio_tracks.size > 1) {
      if (propertyExists("removekaraoke")) {
        from(dubWithoutKaraoke.item()) {
            tracks {
              name("Signs and Songs")
              lang("eng")
              default(false)
            }
        }
        } else {
          from(dubWithKaraoke.item()) {
            tracks {
              name("Signs and Songs")
              lang("eng")
              default(false)
            }
          }
        }
    }

    // French Subtitles
    if (file(get("frsubs")).exists()) {
      from(get("frsubs")) {
        tracks {
          name("French")
          lang("fr")
          default(false)
        }
      }
    }

    // Arabic Subtitles
    //if (file(get("arsubs")).exists()) {
    //  from(get("arsubs")) {
    //    tracks {
    //      name("Arabic")
    //      lang("ar")
    //      default(false)
    //    }
    //  }

    //  attach(get("arfonts")) {
    //    includeExtensions("ttf", "otf")
    //  }
    //}

    // German Subtitles
    if (file(get("desubs")).exists()) {
      from(merge_de.item()) {
        tracks {
          name("German")
          lang("de")
          default(false)
        }
      }
    }

    // Italian Subtitles
    if (file(get("itsubs")).exists()) {
      from(get("itsubs")) {
        tracks {
          name("Italian")
          lang("it")
          default(false)
        }
      }
    }

    // Portuguese Subtitles
    if (file(get("ptsubs")).exists()) {
      from(merge_pt.item()) {
        tracks {
          name("Portuguese")
          lang("pt")
          default(false)
        }
      }
    }

    // Spanish Subtitles
    if (file(get("spsubs")).exists()) {
      from(get("spsubs")) {
        tracks {
          name("Spanish")
          lang("es")
          default(false)
        }
      }

      // Signs and Songs subtitle for Spanish dub
      if (file(get("esaudio")).exists()) {
        from(spanishDub.item()) {
          tracks {
            name("Spanish Signs and Songs")
            lang("es")
            default(false)
          }
        }
      }

      attach(get("spfonts")) {
        includeExtensions("ttf", "otf")
      }
    }

    chapters(chapters.item()) {
      lang("eng")
    }

    attach(get("commonfonts")) {
      includeExtensions("ttf", "otf")
    }

    attach(get("epfonts")) {
      includeExtensions("ttf", "otf")
    }

    if (propertyExists("OP")) {
      attach(get("opfonts")) {
        includeExtensions("ttf", "otf")
      }
    }

    out(get("muxfile"))
  }

  // Mux whole arc using this task. Example: `./gradlew muxBatch.wano` where 'wano' is arckey for the arc 'Wano'
  batches(getMap("batches", "episodes"))
  batchtasks {
    val mergeBatch by task<DefaultSubTask> {
      dependsOn(mergeAll.batchItems())
    }

    val muxBatch by task<DefaultSubTask> {
      dependsOn(mux.batchItems())
    }
  }
}

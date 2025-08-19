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

fun EventLine.styleContainsAny (strings: Array<String>) : Boolean {
  for (s in strings) {
    if (this.style.contains(s)) return true;
  }
  return false;
}

fun EventLine.isDialogue () : Boolean {
  return this.styleContainsAny(engDialogue);
}

fun escapeTitle (title: String) : String {
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
        return title.replace("\"", "\\\"");
    }
    return title;
}

subs {
  readProperties("sub.properties", "chapter.properties", "title.properties", "publicrepo.properties", "publish.properties")
  episodes(getList("episodes").get() + getList("extraepisodes").get())

  fun getPrefix(): String {
    return when {
      arg("ex") != null -> "extended_"
      arg("alt") != null -> "alternate_"
      else -> ""
    }
  }

  val ensubs = getPrefix() + "ensubs"
  val en_cc_subs = getPrefix() + "en_cc_subs"
  val frsubs = getPrefix() + "frsubs"
  val essubs = getPrefix() + "essubs"
  val esdubsubs = getPrefix() + "esdubsubs"
  val arsubs = getPrefix() + "arsubs"
  val desubs = getPrefix() + "desubs"
  val itsubs = getPrefix() + "itsubs"
  val ptsubs = getPrefix() + "ptsubs"
  val plsubs = getPrefix() + "plsubs"
  val trsubs = getPrefix() + "trsubs"
  val cssubs = getPrefix() + "cssubs"
  val rusubs = getPrefix() + "rusubs"
  val video = getPrefix() + "video"
  val muxfile = getPrefix() + "muxfile"
  val torrentfile = getPrefix() + "torrentfile"
  val chapter = getPrefix() + "chapters"

  val mergefile = getPrefix() + "mergefile"
  val mergefile_ar = getPrefix() + "mergefile_ar"
  val mergefile_en_cc = getPrefix() + "mergefile_en_cc"
  val mergefile_de = getPrefix() + "mergefile_de"
  val mergefile_pt = getPrefix() + "mergefile_pt"
  val mergefile_pl = getPrefix() + "mergefile_pl"
  val mergefile_it = getPrefix() + "mergefile_it"
  val mergefile_tr = getPrefix() + "mergefile_tr"
  val mergefile_cs = getPrefix() + "mergefile_cs"
  val mergefile_ru = getPrefix() + "mergefile_ru"

  val increaseLayer by task<ASS> {
    from(get(ensubs))

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

    fromIfPresent(
      if (arg("ex") != null) getList("extended_typesetting") else getList("typesetting")
      , ignoreMissingFiles = true
    )

    // noOP and noED are properties set in the public subtitles repository to
    // not merge new songs with old subtitle files or vice-versa.
    // They should never be set in the actual internal subtitles repository.

    if (propertyExists("OP") && !propertyExists("noOP")) {
      from(get("OP")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }

    if (propertyExists("ED") && !propertyExists("noED")) {
      from(get("ED")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }


    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile))
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
    from(get(ensubs))
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
    from(get(chapter))
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (CC subs)
  val increaseLayer_en_cc by task<ASS> {
    from(get(en_cc_subs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }
  // Merge subs with karaoke (CC subs)
  val merge_en_cc by task<Merge> {
    from(increaseLayer_en_cc.item())

    if (propertyExists("OP_en_cc") && !propertyExists("noOP")) {
      from(get("OP_en_cc")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_en_cc") && !propertyExists("noED")) {
      from(get("ED_en_cc")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_en_cc))
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (German subs)
  val increaseLayer_de by task<ASS> {
    from(get(desubs))

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
    from(get(ptsubs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Italian subs)
  val increaseLayer_it by task<ASS> {
    from(get(itsubs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Arabic subs)
  val increaseLayer_ar by task<ASS> {
    from(get(arsubs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Polish subs)
  val increaseLayer_pl by task<ASS> {
    from(get(plsubs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Turkish subs)
  val increaseLayer_tr by task<ASS> {
    from(get(trsubs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Czech subs)
  val increaseLayer_cs by task<ASS> {
    from(get(cssubs))

    ass {
      events.lines.forEach {
        if (it.isDialogue ()) {
          it.layer += 50
        }
      }
    }
  }

  // Increase the layer of dialogue lines by 50 to prevent sign overlapping the dialogues (Russian subs)
  val increaseLayer_ru by task<ASS> {
    from(get(rusubs))

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

    if (propertyExists("OP_de") && !propertyExists("noOP")) {
      from(get("OP_de")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_de") && !propertyExists("noED")) {
      from(get("ED_de")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_de))
  }

  // Removes all the dialoge lines from German subs
  val dubWithKaraokeDe by task<ASS> {
    from(merge_de.item())
    ass {
      events.lines.removeIf {
        it.isDialogue ()
      }
    }
  }

  // Removes all the dialoge and karaoke lines from English subs
  val dubWithoutKaraokeDe by task<ASS> {
    from(get(desubs))
    ass {
      events.lines.removeIf {
        it.isDialogue ()
      }
    }
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)
  }

  // Merge subs with karaoke (Portuguese subs)
  val merge_pt by task<Merge> {
    from(increaseLayer_pt.item())

    if (propertyExists("OP_pt") && !propertyExists("noOP")) {
      from(get("OP_pt")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_pt") && !propertyExists("noED")) {
      from(get("ED_pt")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_pt))
  }

  // Merge subs with karaoke (Italian subs)
  val merge_it by task<Merge> {
    from(increaseLayer_it.item())

    if (propertyExists("OP_it") && !propertyExists("noOP")) {
      from(get("OP_it")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_it") && !propertyExists("noED")) {
      from(get("ED_it")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_it))
  }

  // Merge subs with karaoke (Arabic subs)
  val merge_ar by task<Merge> {
    from(increaseLayer_ar.item())

    if (propertyExists("OP_ar") && !propertyExists("noOP")) {
      from(get("OP_ar")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_ar") && !propertyExists("noED")) {
      from(get("ED_ar")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_ar))
  }

  // Merge subs with karaoke (Polish subs)
  val merge_pl by task<Merge> {
    from(increaseLayer_pl.item())

    if (propertyExists("OP_pl") && !propertyExists("noOP")) {
      from(get("OP_pl")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_pl") && !propertyExists("noED")) {
      from(get("ED_pl")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_pl))
  }

  // Merge subs with karaoke (Turkish subs)
  val merge_tr by task<Merge> {
    from(increaseLayer_tr.item())

    if (propertyExists("OP_tr") && !propertyExists("noOP")) {
      from(get("OP_tr")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_tr") && !propertyExists("noED")) {
      from(get("ED_tr")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_tr))
  }

  // Merge subs with karaoke (Czech subs)
  val merge_cs by task<Merge> {
    from(increaseLayer_cs.item())

    if (propertyExists("OP_cs") && !propertyExists("noOP")) {
      from(get("OP_cs")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_cs") && !propertyExists("noED")) {
      from(get("ED_cs")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_cs))
  }

  // Merge subs with karaoke (Russian subs)
  val merge_ru by task<Merge> {
    from(increaseLayer_ru.item())

    if (propertyExists("OP_ru") && !propertyExists("noOP")) {
      from(get("OP_ru")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("OP", EventLineAccessor.ACTOR)
      }
    }
    if (propertyExists("ED_ru") && !propertyExists("noED")) {
      from(get("ED_ru")) {
        syncSourceLine("sync", EventLineAccessor.ACTOR)
        syncTargetLine("ED", EventLineAccessor.ACTOR)
      }
    }

    onStyleConflict(ErrorMode.FAIL)
    includeExtraData(false)
    includeProjectGarbage(false)
    removeComments(true)

    out(get(mergefile_ru))
  }


  // Helper task to merge all language subs needing merging and output to Final Subs folder
  val mergeAll by task<DefaultSubTask> {
    dependsOn(merge.item())
    if (file(get(desubs)).exists()) {
      dependsOn(merge_de.item())
    }
    if (file(get(ptsubs)).exists()) {
      dependsOn(merge_pt.item())
    }
    if (file(get(itsubs)).exists()) {
      dependsOn(merge_it.item())
    }
    if (file(get(arsubs)).exists()) {
      dependsOn(merge_ar.item())
    }
    if (file(get(plsubs)).exists()) {
      dependsOn(merge_pl.item())
    }
    if (file(get(trsubs)).exists()) {
      dependsOn(merge_tr.item())
    }
    if (file(get(cssubs)).exists()) {
      dependsOn(merge_cs.item())
    }
    if (file(get(rusubs)).exists()) {
      dependsOn(merge_ru.item())
    }
  }

  mux {
    skipUnusedFonts(true)
    // Uncomment this line if the script stops due to font missing some glyphs or try to use other fonts.
    onMissingGlyphs(ErrorMode.WARN)

    title(escapeTitle(get("title").get()))

    from(get(video)) {
      video {
        lang("jpn")
        default(true)
        trackOrder(0)
      }
      audio {
          if (audio().size == 1 && track.lang != "jpn") {
            throw GradleException("Japanese audio not found!")
          }
          if (track.lang == "jpn") {
              name("Japanese")
              trackOrder(1)
              default(true)
          } else if (track.lang == "eng") {
              name("English")
              trackOrder(2)
              default(false)
          } else if (track.lang == "spa") {
              name("Spanish Dub")
              val track_order = if (audio().size > 2) 3 else 2
              trackOrder(track_order)
              default(false)
          } else if (track.lang == "ger") {
              name("German Dub")
              val track_order = if (audio().size > 3) 4 else if (audio().size > 2) 3 else 2
              trackOrder(track_order)
              default(false)
          }
      includeChapters(false)
      attachments { include(false) }
      subtitles { include(false) }
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

    // Add german audio if it exists
    if (file(get("deaudio")).exists()) {
      from(get("deaudio")) {
        tracks {
          name("German Dub")
          lang("de")
          default(false)
        }
      }
    }

    // English Subtitle
    from(merge.item()) {
      tracks {
        name("English")
        lang("eng")
        default(true)
      }
    }


    // Signs and Songs subtitle for English Dub if English dub exists
    val mkvInfo = getMkvInfo(file(get(video)))
    if (mkvInfo.audio_tracks.size > 1) {
      val signsSongsTask =
        if (propertyExists("removekaraoke"))
          dubWithoutKaraoke
        else
          dubWithKaraoke

      from(signsSongsTask.item()) {
          tracks {
            name("Signs and Songs")
            lang("eng")
            default(false)
            forced(true)
          }
      }

      // CC Subtitles
      if (file(get(en_cc_subs)).exists()) {
        from(merge_en_cc.item()) {
          tracks {
            name("CC")
            lang("eng")
            default(false)
          }
        }
      }

    }

    // French Subtitles
    if (file(get(frsubs)).exists()) {
      from(get(frsubs)) {
        tracks {
          name("French")
          lang("fr")
          default(false)
        }
      }
    }

    // Arabic Subtitles
    if (file(get(arsubs)).exists()) {
     from(merge_ar.item()) {
       tracks {
         name("Arabic")
         lang("ar")
         default(false)
       }
     }

     attach(get("arfonts")) {
       includeExtensions("ttf", "otf")
     }
    }

    // Polish Subtitles
    if (file(get(plsubs)).exists()) {
     from(merge_pl.item()) {
       tracks {
         name("Polish")
         lang("pl")
         default(false)
       }
     }

     attach(get("plfonts")) {
       includeExtensions("ttf", "otf")
     }
    }

    // German Subtitles
    if (file(get(desubs)).exists()) {
      from(merge_de.item()) {
        tracks {
          name("German")
          lang("de")
          default(false)
        }
      }
    }

    // German Dub Subtitles
    if (file(get("deaudio")).exists()) {
      val signsSongsTaskDe =
        if (propertyExists("removekaraokede"))
          dubWithoutKaraokeDe
        else
          dubWithKaraokeDe

      from(signsSongsTaskDe.item()) {
          tracks {
            name("German Dub")
            lang("de")
            default(false)
            forced(true)
          }
      }

    }

    // Italian Subtitles
    if (file(get(itsubs)).exists()) {
      from(merge_it.item()) {
        tracks {
          name("Italian")
          lang("it")
          default(false)
        }
      }
    }

    // Portuguese Subtitles
    if (file(get(ptsubs)).exists()) {
      from(merge_pt.item()) {
        tracks {
          name("Portuguese")
          lang("pt")
          default(false)
        }
      }
    }

    // Spanish Subtitles
    if (file(get(essubs)).exists()) {
      from(get(essubs)) {
        tracks {
          name("Spanish")
          lang("es")
          default(false)
        }
      }

      attach(get("esfonts")) {
        includeExtensions("ttf", "otf")
      }
    }

    // Spanish Dub Subtitles
    if (file(get(esdubsubs)).exists()) {
      from(get(esdubsubs)) {
        tracks {
          name("Spanish Signs and Songs")
          lang("es")
          default(false)
          forced(true)
        }
      }

      attach(get("esfonts")) {
        includeExtensions("ttf", "otf")
      }
    }

    // Turkish Subtitles
    if (file(get(trsubs)).exists()) {
      from(merge_tr.item()) {
        tracks {
          name("Turkish")
          lang("tr")
          default(false)
        }
      }
    }

    // Czech Subtitles
    if (file(get(cssubs)).exists()) {
      from(merge_cs.item()) {
        tracks {
          name("Czech")
          lang("cs")
          default(false)
        }
      }
    }

    // Russian Subtitles
    if (file(get(rusubs)).exists()) {
      from(merge_ru.item()) {
        tracks {
          name("Russian")
          lang("ru")
          default(false)
        }
      }

     attach(get("rufonts")) {
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

    if (propertyExists("ED")) {
      attach(get("edfonts")) {
        includeExtensions("ttf", "otf")
      }
    }

    out(get(muxfile))
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

  torrent {
      trackers(getList("trackers"))
      from(mux.item())
      out(get(torrentfile))
  }
}

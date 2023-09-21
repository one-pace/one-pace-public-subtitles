# One Pace Public Subtitles
This repository is a mirror of the [One Pace project](https://onepace.net)'s internal subtitles repository for episodes which have already been released. Here, you can find the latest versions of subtitle files, including possible post-release fixes that are not in the release versions of the episodes. Such fixes can include corrections to spelling or grammar mistakes that slipped through during the intial release, but they can also be updates to term translations or flashback lines, or even entire new subtitle editing or translation checking passes. This repository also includes subtitles in non-English languages, even if they were only added after the initial release and might not be available via torrent yet.

## How do I use this repository?
If all you want is to download the latest subtitles, you can grab them from the [`main/Release/Final Subs`](main/Release/Final%20Subs/) folder if you want English, German, or Portuguese subs, and from the respective arc's/episode's folder in [`main`](main/) otherwise. If you need more details (e.g. if you plan on translating the subtitles to another language or want to remux the episodes yourself), you can read on.

We use [SubKt](https://github.com/Myaamori/SubKt) to manage our subtitles and mux our episodes. SubKt handles tasks like merging in OP translations, adding Matroska chapters, attaching all required fonts, and muxing the final `.mkv` file.

To use SubKt, you need the JDK 11 (yes, it needs to be that version since there's issues with later ones) and (if you want to mux files) and a recent version of [mkvmerge](https://mkvtoolnix.download/downloads.html). If both of these are installed, you can run command-line commands like `./gradlew mergeAll.romancedawn_01` from the `main` folder to generate merged subtitle files, or `./gradlew mux.romancedawn_01` to mux the subtitles into a video. The latter command requires the respective video file to be in the respective episode's folder. For example, to run `mux.romancedawn_01`, the video for Romance Dawn 1 would need to be at the path `main/01 Romance Dawn/01/romancedawn 01.mkv`. The merged subs and muxed video will then appear in the `main/Release/Final Subs` and `main/Release` folders respectively.

Most users will not want to go through all of this trouble just to generate the merged subtitles, so we set up a GitHub Actions workflow that automatically merges all subtitles and uploads them in the `Final Subs` folder. So, if you just want the latest subtitles, you can grab the files from there. Note, however, that the required fonts aren't included with those subtitle files. If you have problems with missing fonts, you can either mux with SubKt to attach them, or manually grab or install the relevant fonts from the font folders in `main/Other`.

Only the English, German, and Portuguese subtitles use SubKt to merge in the opening translations, so only those language subs are found in the `Final Subs` folder. The remaining languages have the opening translations directly in each of the episode subtitles, so you can grab those subtitles directly from those folders.

### Why are so many arcs missing English subtitle files?
We did not always use a GitHub repository with SubKt to manage our subtitle files. Our internal subtitle repository only contains the subtitles which have been added to GitHub since we migrated to it. Many of the older arcs are set to be redone from scratch anyway, so there's no use in having the old subtitles in our repository.

Still, it's probably helpful for users to have the subtitles for all episodes in one place, so a dump of all English subtitles from all previous releases has been uploaded to the `Final Subs` folder. That way, people looking to simply download all of our subtitles can grab them from there.

### Some of the English subtitles in the Final Subs folder are missing the opening translation?
Yes, this is the case for subtitle files which are either not 1080p yet, or are old and do not yet have OP sync points. These arcs will be updated eventually, and this will be fixed then, but until then the OP merge for those arcs has been disabled to prevent the merge from failing. Adding sync points and/or resampling subtitles or OP translations just for this public repo wouldn't have been worth the effort, sorry.

### Some episode's subtitles appear twice in the Final Subs folder? (or similar issues)
Things like this can happen when metadata like the episode's chapters or resolution is changed. When in doubt, just pick the newer file.

In general, keep in mind that this repository is fully automated so some issues might pop up occasionally. From time to time we'll go through it and clean up some broken files, but we can't guarantee it to never have issues. If you spot any bigger problems like subtitles not syncing up with the video in the latest corresponding release, let us know and we'll take care of it.

## How can I contribute or report issues?
Instead of making PRs or issues here, please reach out to us on our Discord server's feedback forum first, and make sure to read the guidelines there. Most notably, some of these arcs are set to undergo large reworks, so there's no point in reporting small issues for them.

If you want to contribute in a larger fashion like helping with subtitle editing, translation checking, or other tasks, check out the recruitment channel on our Discord server. However, if you want to translate our subtitles into another language, note that we can only accept translations that were done from Japanese or at least checked against the original Japanese script. So, if you don't know Japanese, you're still free to translate our subtitles, but we can't ship them with official releases. Though if there already is a subtitle team for your language, you can see if they need help. If you do know Japanese, reach out to us in the recruitment channel!

## Can I use your work for my own project?
Sure, you can use our subtitles for anything you want. Though if you publish your project somewhere, we'd appreciate it if you credited the One Pace project.


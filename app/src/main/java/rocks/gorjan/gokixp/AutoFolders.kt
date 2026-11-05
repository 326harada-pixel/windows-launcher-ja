package rocks.gorjan.gokixp

/**
 * Which user folder a program belongs in, for "sort into folders automatically".
 *
 * Order of evidence: an exact package id (apps this fork's owner actually has), then a package
 * prefix (a company's apps), then words in the package id, then the category the app declares
 * itself (Android's ApplicationInfo.category, which most apps leave undefined). Anything
 * not recognised returns null and stays where it is - a wrong guess is worse than no guess.
 */
object AutoFolders {
    const val GAME = "ゲーム"
    const val SHOP = "ショッピング"
    const val MONEY = "金融"
    const val IMAGE = "画像処理"
    const val HOBBY = "趣味"
    const val MANGA = "漫画"
    const val HEALTH = "健康"
    const val VOD = "VOD"
    const val SNS = "SNS"
    const val TOOL = "ツール"
    const val FOOD = "食事"
    const val RACE = "公営競技"

    val ALL = listOf(GAME, SHOP, MONEY, IMAGE, HOBBY, MANGA, HEALTH, VOD, SNS, TOOL, FOOD, RACE)

    private fun ids(category: String, vararg packages: String) = packages.associateWith { category }

    private val exact: Map<String, String> = buildMap {
        putAll(ids(GAME, "com.arina.blockpuzzle", "com.github.stenzek.duckstation", "com.explusalpha.Snes9xPlus", "com.nianticlabs.pokemongo", "jp.pokemon.pokemontcgp"))
        putAll(ids(SHOP, "com.amazon.mShop.android.shopping", "com.alibaba.aliexpresshd", "com.alibaba.intl.android.apps.poseidon", "com.einnovation.temu", "com.kouzoh.mercari",
            "jp.co.fablic.fril", "jp.co.yahoo.android.yauction", "jp.jmty.app2", "jp.co.rakuten.android", "jp.co.joshin.Joshin", "jp.co.jiraffe.magi", "com.daisojapan",
            "jp.co.aeonst.app.myaeon", "jp.co.family.familymart_app", "jp.co.lawson.android", "jp.co.sej.app", "jp.co.sej.sevennow.app", "com.coconala.android.portal"))
        putAll(ids(MONEY, "co.kyash", "com.bitflyer.android.bfwallet", "com.paypal.android.p2pmobile", "com.transferwise.android", "com.smbc_card.vpass", "jp.co.rakuten.pay",
            "jp.co.rakuten_bank.rakutenbank", "jp.co.kyotoshinkin.bankapp", "jp.co.shigabank.passbook.android", "jp.mufg.bk.openaccount.app", "jp.ne.paypay.android.app", "jp.ponta.myponta",
            "jp.nanaco.android", "waon.app", "jp.xaas.bun2card", "jp.co.onetapbuy.jpstock", "jp.co.grmn.kabutasu", "jp.co.grmn.fxdemo", "com.wealthnavi.amami", "jp.co.lifenet_seimei.mypage",
            "com.nttdata.hokenjpki.Android", "jp.co.freee.lionrock", "com.komorebi.kakeibo", "jp.kanmu.vandle.android", "com.linepaycorp.talaria", "jp.jicc_webkaiji.jicc_app",
            "com.google.android.apps.walletnfcrel", "com.blockchainvault"))
        putAll(ids(IMAGE, "com.canva.editor", "com.photoroom.app", "jp.ne.ibis.ibispaintx.app", "jp.pxv.android.sketch", "com.handycloset.android.eraser", "com.handycloset.android.photolayers",
            "com.simplemobilephotoresizer", "com.cam001.selfie361", "com.youcam.ai.beauty.art.maker.generator", "com.reaimagine.colorizeit", "k_independent.java_conf.gr.jp.custom_crop",
            "com.wondershare.filmorago", "com.inverseai.video_converter", "com.lemon.lvoverseas", "com.amazon.clouddrive.photos", "ai.kaiber.mobile", "com.openai.sora", "jp.co.familymart.famimaphoto"))
        putAll(ids(RACE, "jp.boatrace.android.boatraceapp", "jp.go.jra.JRA_officialApp_android", "jp.co.netdreamers.netkeiba", "jp.winticket.app", "jp.co.epaddock.spat4"))
        putAll(ids(HOBBY, "jp.co.sammy.MySloForAndroid",
            "com.maetimes.android.pokekara", "jp.co.dkkaraoke.denmokumini01", "ai.moises", "com.amazon.mp3", "jp.co.ayahadio", "jp.co.duo7.vr.iriam"))
        putAll(ids(MANGA, "com.access_company.android.sh_jumpplus", "com.comic_fuz", "com.comic_gardo", "com.jumptoon", "com.square_enix.android_googleplay.mangaup_jp", "jp.kakao.piccoma",
            "jp.booklive.reader2", "jp.co.amutus.mechacomic.android.mangaapp", "jp.co.kodansha.android.magazinepocket", "jp.co.shogakukan.sunday_webry", "jp.naver.linemanga.android",
            "jp.co.dwango.seiga.manga.android", "com.amazon.kindle", "jp.co.tokyo_ip.SideBooks"))
        putAll(ids(HEALTH, "com.fitbit.FitbitMobile", "com.aadhk.lite.bptracker", "com.snorelab.app", "com.google.android.apps.fitness", "com.google.android.apps.healthdata", "com.xiaomi.wearable", "jp.runwalkmaker"))
        putAll(ids(VOD, "tv.abema", "jp.happyon.android", "com.amazon.avod.thirdpartyclient", "jp.hamitv.hamiand1"))
        putAll(ids(SNS, "com.twitter.android", "com.instagram.android", "com.instagram.barcelona", "com.facebook.katana", "com.ss.android.ugc.tiktok.lite", "com.ss.android.ugc.trill", "com.whatsapp",
            "jp.naver.line.android", "com.linecorp.lineoa", "com.discord", "social.mixi", "mu.note", "jp.co.airfront.android.a2chMate", "com.imgur.mobile", "jp.softbank.mb.plusmessage"))
        putAll(ids(FOOD, "com.ubercab.eats", "com.yoshinoya.android.yoshinoya_official", "jp.co.matsuyafoods.officialapp.dis", "jp.co.mcdonalds.android", "jp.co.zensho.sukiyamoap", "jp.co.zensho.nakau",
            "jp.co.komeda.ua.android", "jp.co.kura_corpo", "jp.co.rairaitei.app.apprairaitei", "jp.co.tenkaippin", "jp.co.ohsho.app", "jp.co.chateraise.android.members", "com.coke.cokeon",
            "jp.jihanpi", "jp.co.duskin.misdocardapps"))
        putAll(ids(TOOL, "com.anthropic.claude", "com.openai.chatgpt", "com.deepseek.chat", "com.poe.android", "co.aienglish", "com.google.android.apps.authenticator2", "com.github.android", "com.termux",
            "com.brave.browser", "com.dropbox.android", "com.alphainventor.filemanager", "com.lb.app_manager", "ru.zdevs.zarchiver", "org.fdroid.fdroid", "org.zwanoo.android.speedtest",
            "com.teamviewer.teamviewer.market.mobile", "com.estmob.android.sendanywhere", "com.digibites.accubattery", "com.zacharee1.systemuituner", "com.rise.automatic.autoclicker.clicker",
            "yumekan.android.devcalc", "com.komorebi.SimpleCalendar", "com.google.android.keep", "com.microsoft.office.excel", "com.adobe.reader", "com.google.android.apps.translate",
            "com.google.android.apps.chromecast.app", "com.theswitchbot.switchbot", "com.tplink.iot", "global.nature.remo", "com.ionitech.airscreen", "net.typeblog.shelter",
            "com.foxdebug.acodefree", "com.meihillman.ringtonemaker", "jwa.or.jp.tenkijp3", "jp.gomisuke.app.Gomisuke0010", "com.hopefactory2021.fakegpslocation", "com.taxiboard.app",
            "com.dena.automotive.taxibell", "jp.co.toyotaconnected.tlbsapp", "com.eneos.ssapp", "jp.showa_shell_dmp.shellpass", "jp.co.westjr.android.icocaapp", "jp.go.soumu.mkpf.mkpfmypage",
            "org.videolan.vlc", "video.downloader.videodownloader", "com.utorrent.client", "com.google.android.apps.wear.companion", "ar.com.lichtmaier.antenas", "jp.co.nipponantenna.BSCompass",
            "info.justoneplanet.android.inputmethod.japanese", "com.universal777.yunimemo", "com.clover.strcntmemo", "com.x0.strai.secondfrep", "com.x0.strai.secondkey",
            "com.jamworks.dynamicspot", "com.amazon.dee.app", "com.google.android.apps.googleassistant", "com.google.android.apps.wearables.maestro.companion", "com.kddi.kdla.jp", "sc.luup.luup",
            "com.indeed.resume", "pi.browser"))
        put("net.jalan.android", HOBBY)
    }

    // A company's whole family of apps
    private val prefixes: List<Pair<String, String>> = listOf(
        "jp.co.japannetbank." to MONEY, "jp.japanpost.jp_bank." to MONEY, "com.felicanetworks." to MONEY,
        "jp.sammynet." to HOBBY, "com.fujifilm." to IMAGE, "com.google.android.apps.docs." to TOOL, "com.handycloset." to IMAGE,
    )

    // Words in the package id that are reliable enough on their own
    private val keywords: List<Pair<String, String>> = listOf(
        "manga" to MANGA, "comic" to MANGA, "keiba" to RACE, "keirin" to RACE, "boatrace" to RACE, "autorace" to RACE, "bank" to MONEY, "securities" to MONEY, "stock" to MONEY, "game" to GAME,
    )

    // ApplicationInfo.CATEGORY_* (GAME 0, AUDIO 1, VIDEO 2, IMAGE 3, SOCIAL 4, NEWS 5, MAPS 6, PRODUCTIVITY 7)
    private val declared: Map<Int, String> = mapOf(0 to GAME, 2 to VOD, 3 to IMAGE, 4 to SNS, 7 to TOOL)

    fun categoryOf(packageName: String, declaredCategory: Int = -1): String? {
        exact[packageName]?.let { return it }
        prefixes.firstOrNull { packageName.startsWith(it.first) }?.let { return it.second }
        val lower = packageName.lowercase()
        keywords.firstOrNull { lower.contains(it.first) }?.let { return it.second }
        return declared[declaredCategory]
    }
}

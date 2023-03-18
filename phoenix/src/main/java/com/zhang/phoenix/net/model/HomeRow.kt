package com.zhang.phoenix.net.model

import androidx.annotation.LayoutRes
import com.zhang.phoenix.R
import com.zhang.phoenix.app.categoryDateText
import com.zhang.phoenix.ui.home.uistate.HomeMetroRow
import org.json.JSONObject


/**
 * 拼图
 */
data class HomePuzzle(val medias: List<MediaModel>)

/**
 * 专题
 */
data class HomeZhuantiRow(val left: Category, val right: Category)

interface HomeWeightRow {
    val layoutId: Int
}

/**
 * 媒体均分
 */
data class HomeMediaWeightRow(val medias: List<MediaModel>, @LayoutRes override val layoutId: Int) :
    HomeWeightRow

/**
 * 分类均分
 */
data class HomeCategoryWeightRow(
    val medias: List<Category>,
    @LayoutRes override val layoutId: Int
) : HomeWeightRow

/**
 * 最新上线
 */
data class HomeLatestRow(val categories: List<Category>)

interface HomeRow {
    val type: Int

    companion object {

        /**
         * 文字标题
         */
        const val TITLE = 0

        /**
         * 分类：应用行
         */
        const val CATEGORY = 2

        const val NEWEST_TITLE = 55

        const val END = 8

        @JvmStatic
        fun read(data: JSONObject): List<Any> {
            val homeItems = data.getJSONObject("body").getJSONObject("home").getJSONObject("items")
            val metro = HomeMetroRow(
                zixuntoutiao = homeItems.readMediaModels("zixuntoutiao")!!,
                rebobang = homeItems.readMediaModel("rebobang")!!,
                zhongwentai = LiveTai(homeItems.getJSONObject("zhongwentai")),
                zixuntai = LiveTai(homeItems.getJSONObject("zixuntai")),
                tuijian8 = homeItems.readMediaModel("tuijian8")!!,
            )
            val categories = HomeCategoryRow(
                homeItems.getJSONObject("popular_cate")
                    .getJSONArray("cateArray").map<JSONObject, Category> {
                        Category(it)
                    }, CATEGORY
            )


            val zhuantiTitle = HomeTitleRow("专题策划")
            val zhuantiRow = HomeZhuantiRow(
                left = Category(
                    homeItems.getJSONObject("topic1")
                        .getJSONArray("cateArray").getJSONObject(0)
                ), right = Category(
                    homeItems.getJSONObject("topic2")
                        .getJSONArray("cateArray").getJSONObject(0)
                )
            )

            val newest = HomeLatestRow(homeItems.getJSONObject("newest_cate")
                .getJSONArray("cateArray").map<JSONObject, Category> {
                    Category(it)
                })

            val puzzleMedia = listOf<MediaModel>(
                homeItems.readMediaModel("tuijian12")!!,
                homeItems.readMediaModel("tuijian13")!!,
                homeItems.readMediaModel("tuijian14")!!,
                homeItems.readMediaModel("tuijian15")!!,
                homeItems.readMediaModel("tuijian16")!!
            )

            val topCates = homeItems.getJSONObject("top_cate")
                .getJSONArray("cateArray").map<JSONObject, Category> {
                    Category(it)
                }

            return listOf(
                metro,
                categories,
                HomeMediaWeightRow(
                    listOf<MediaModel>(
                        homeItems.readMediaModel("tuijian9")!!,
                        homeItems.readMediaModel("tuijian10")!!,
                        homeItems.readMediaModel("tuijian11")!!
                    ), R.layout.pnx_home_row_weight3_card
                ),
                zhuantiTitle,
                zhuantiRow,
                HomeTitleRow(
                    homeItems.getJSONObject("newest_cate").getString("name"),
                    NEWEST_TITLE
                ),
                newest,
                HomeTitleRow(homeItems.getJSONObject("theme_title").getString("name")),
                HomePuzzle(puzzleMedia),
                HomeTitleRow(homeItems.getJSONObject("top_cate").getString("name")),
                HomeCategoryWeightRow(topCates.subList(0, 4), R.layout.pnx_home_row_weight4_card),
                HomeCategoryWeightRow(topCates.subList(4, 8), R.layout.pnx_home_row_weight4_card),
                object : HomeRow {
                    override val type: Int = END
                }
            )
        }


    }
}

/**
 * 媒体类型
 */
class MediaModel(private val source: JSONObject) {
    val id: String? = source.readString("id")
    val mediaId: String? = source.readString("mediaId")
    val name: String? = source.readString("name")
    val episodeBaseId: String? = source.readString("episodeBaseId")
    val images: List<String> = source.getJSONArray("posters").map<String, String> {
        it
    }
}

class LiveTai(private val source: JSONObject) {
    val code: String? = source.readString("code")
    val name: String? = source.readString("name")
    val type: String? = source.readString("type")
    val playUrl: String? = source.readString("playUrl")
}

class Category(private val source: JSONObject) {
    val id: String? = source.readString("id")
    val cateId: String? = source.readString("cateId")
    val channel: String? = source.readString("channel")
    val name: String? = source.readString("name")
    val description: String? = source.readString("description")
    val image: String? = source.readFirstPosters()
    val updateTime:String? = source.readLongDate("updateTime").categoryDateText()
}

data class HomeCategoryRow(val categories: List<Category>, override val type: Int) : HomeRow

data class HomeTitleRow(val title: String, override val type: Int = HomeRow.TITLE) : HomeRow




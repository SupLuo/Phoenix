package bas.droid.rva

data class Configs(
    val headerItemAsFlow: Boolean = false,
    val headerDisplayWhenEmpty: Boolean = true,
    val footerItemAsFlow: Boolean = false,
    val footerDisplayWhenEmpty: Boolean = true,
    val loadMoreItemAsFlow: Boolean = false,
    val loadMoreDisplayWhenEmpty: Boolean = false,
    val loadMoreDisplayWhenEndState: Boolean = true
)

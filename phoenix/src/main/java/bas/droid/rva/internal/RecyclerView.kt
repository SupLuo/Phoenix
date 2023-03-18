//package bas.droid.powerfuladapter
//
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.StaggeredGridLayoutManager
//
//
///**
// * [RecyclerView]的内容是否铺满；
// * 注意本方法需要延迟使用，当adapter设置数据之后，由于itemview还没有创建和附加到viewtree中，在此时调用该方法得到的结果是不准确的
// *
// * @param default 用于其他LayoutManager情况时返回的默认值，默认true，认为满一页
// */
//fun RecyclerView.isContentFullPage(default: Boolean = true): Boolean {
//    val manager = layoutManager
//    if (manager is LinearLayoutManager) {
//        return manager.isContentFullPage()
//    } else if (manager is StaggeredGridLayoutManager) {
//        return manager.isContentFullPage()
//    } else if (manager is androidx.leanback.widget.GridLayoutManager) {
//        return manager.isContentFullPage(default)
//    }
//    return default
//}
//
///**
// * 内容是否铺满整页
// */
//fun LinearLayoutManager.isContentFullPage(): Boolean {
//    return findLastCompletelyVisibleItemPosition() + 1 != itemCount || findFirstCompletelyVisibleItemPosition() != 0
//}
//
//
///**
// * 内容是否铺满整页
// */
//fun StaggeredGridLayoutManager.isContentFullPage(): Boolean {
//    val positions = IntArray(spanCount)
//    this.findLastCompletelyVisibleItemPositions(positions)
//    return (positions.maxOrNull() ?: 0) + 1 != itemCount
//}
//
///**
// * 本方法并不准确，只是简单排查
// */
//fun androidx.leanback.widget.GridLayoutManager.isContentFullPage(default: Boolean = true): Boolean {
//    if (itemCount < 0)//没有数据
//        return false
//
//    val fistView = findViewByPosition(0)
//    val lastView = findViewByPosition(itemCount - 1)
//    if (fistView != null && lastView != null) {
//        //如果第一项和最后一项对应的view都找到了，说明在同一个页面中同时显示了第一个和最后一个，那多半是没满一页的
//        return false
//    }
//    return default
//}
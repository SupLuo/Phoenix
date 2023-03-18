package bas.droid.rva.adapter

import bas.droid.rva.adapter.internal.CommonAdapterHelper
import bas.droid.rva.Power

class PowerfulDataAdapter(
    power: Power,
    layoutId: Int = -1,
    helper: CommonAdapterHelper = CommonAdapterHelper()
) : DataAdapter(layoutId, helper), Power by power
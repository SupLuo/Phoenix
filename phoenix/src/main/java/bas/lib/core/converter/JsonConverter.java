package bas.lib.core.converter;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Lucio on 2021/7/21.
 */
public interface JsonConverter {

    @Nullable <T> T toObject(@Nullable String json, Class<T> clazz);

    @Nullable <T> T toObject(@Nullable String json, Type type);

    /**
     * 转换成列表
     * @param json
     * @param tClass item class类型，比如List<String>,则 tClass为String.class
     * @param <T>
     * @return
     */
    @Nullable <T> List<T> toObjectList(@Nullable String json, Class<T> tClass);

    @Nullable <T> String toJson(@Nullable T obj, @NotNull Class<T> tClass);

    @Nullable <T> String toJson(@Nullable T obj, @NotNull Type type);
}

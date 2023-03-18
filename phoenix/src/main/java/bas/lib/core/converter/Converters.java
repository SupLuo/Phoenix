package bas.lib.core.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

import bas.lib.core.converter.moshi.MoshiConverter;
import bas.lib.core.lang.ClassesKt;

/**
 * Created by Lucio on 2021/7/21.
 */
public class Converters {

    private static JsonConverter mJsonConverter;

    private Converters() {
    }

    static {
        try {
            //初始化默认JsonConverter，优先使用Jackson(Kotlin版本)
            if (ClassesKt.isClassExists("com.squareup.moshi.Moshi")) {
                System.out.println("Converters：使用MoshiConverter");
                mJsonConverter = new MoshiConverter();
            }
            else {
                System.out.println("Converters：null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setJsonConverter(@NotNull JsonConverter converter) {
        mJsonConverter = converter;
    }

    public static JsonConverter getJsonConverter() {
        return mJsonConverter;
    }

    @Nullable
    public static <T> T toObject(@Nullable String json, Class<T> clazz) {
        return mJsonConverter.toObject(json, clazz);
    }

    @Nullable
    public static <T> List<T> toObjectList(@Nullable String json, Class<T> clazz) {
        return mJsonConverter.toObjectList(json, clazz);
    }

    public static <T> T toObject(@Nullable String json, Type type) {
        return mJsonConverter.toObject(json, type);
    }

    /**
     * 不建议直接使用该方法，该方法会直接使用obj.getClass()作为序列化的类型
     * todo 待测试
     * @param obj
     * @return
     */
    @Nullable
    public static String toJson(@Nullable Object obj) {
        if(obj == null)
            return null;
        return mJsonConverter.toJson(obj, obj.getClass());
    }

    @Nullable
    public static String toJson(@Nullable Object obj, @NotNull Class<?> tClass) {
        return mJsonConverter.toJson(obj, tClass);
    }

    @Nullable
    public static String toJson(@Nullable Object obj, @NotNull Type type) {
        return mJsonConverter.toJson(obj, type);
    }

}

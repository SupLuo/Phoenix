package com.zhang.phoenix.net.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ResType.VIDEO, ResType.PICTURES})
@Retention(RetentionPolicy.SOURCE)
public @interface ResType {
    int VIDEO = 3;
    int PICTURES = 5;

}

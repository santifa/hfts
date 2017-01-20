package org.santifa.hfts.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.santifa.hfts.core.nif.MetaNamedEntity;

/**
 * Created by ratzeputz on 20.01.17.
 */
public class NifHelper {

    public static String getEntityName(String s) {
        if (StringUtils.contains(s, "sentence-")) {
            return StringUtils.substringAfterLast(s, "sentence-").toLowerCase();
        } else {
            return StringUtils.substringAfterLast(s, "/").toLowerCase();
        }
    }


    public static String getSurfaceForm(String text, MetaNamedEntity entity) {
        return StringUtils.substring(text, entity.getStartPosition(),
                entity.getStartPosition() + entity.getLength()).toLowerCase();
    }
}

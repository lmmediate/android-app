package com.hes.easysales.easysales.utilities;

import static com.hes.easysales.easysales.Config.DIXY_SHOP_ID;
import static com.hes.easysales.easysales.Config.PEREKRESTOK_SHOP_ID;
import static com.hes.easysales.easysales.Config.URL_SALES_SHOP;

/**
 * Created by sinopsys on 3/30/18.
 */

public class ShopsUtil {

    public static String getShopNameById(int id) {
        switch (id) {
            case DIXY_SHOP_ID:
                return Dixy.name;
            case PEREKRESTOK_SHOP_ID:
                return Perekrestok.name;
            default:
                return "";
        }
    }

    public static String getShopAliasById(int id) {
        switch (id) {
            case DIXY_SHOP_ID:
                return Dixy.alias;
            case PEREKRESTOK_SHOP_ID:
                return Perekrestok.alias;
            default:
                return "";
        }
    }

    public static String getShopUrlWithItemsById(int id) {
        switch (id) {
            case DIXY_SHOP_ID:
                return URL_SALES_SHOP + Dixy.alias;
            case PEREKRESTOK_SHOP_ID:
                return URL_SALES_SHOP + Perekrestok.alias;
            default:
                return "";
        }
    }

    private static class Dixy {
        private static String name = "Дикси";
        private static String alias = "dixy";
    }

    private static class Perekrestok {
        private static String name = "Перекресток";
        private static String alias = "perekrestok";
    }

}


// EOF

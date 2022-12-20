package com.example.demo.src.category;

public class CategoryDaoSqls {
    public static String SELECT_CATEGORIES = "select main.mainCategoryIdx, main.categoryName as mainCategoryName, mid.middleCategoryIdx, mid.categoryName as middleCategoryName, sub.subCategoryIdx, sub.categoryName as subCategoryName\n" +
                                            "from Main_Category as main\n" +
                                            "left outer join Middle_Category as mid\n" +
                                            "on main.mainCategoryIdx = mid.mainCategoryIdx\n" +
                                            "left outer join Sub_Category as sub\n" +
                                            "on mid.middleCategoryIdx = sub.middleCategoryIdx\n" +
                                            "and main.status = 'A' and mid.status = 'A' and sub.status = 'A';";
    public static String SELECT_MAIN_CATEGORY_PRODUCTS = "select Product.productIdx, createdAt, img.imageUrl, title, location, price, isFreeShip, isSafepay, tradeStatus, isFav\n" +
                                                        "from Product\n" +
                                                        "left outer join (select productIdx, imageUrl\n" +
                                                        "\t\t\t\tfrom Product_Image\n" +
                                                        "\t\t\t\twhere status = 'A'\n" +
                                                        "\t\t\t\tgroup by productIdx) as img\n" +
                                                        "on Product.productIdx = img.productIdx\n" +
                                                        "where mainCategoryIdx = ?\n" +
                                                        "and status = 'A';";
    public static String SELECT_MID_CATEGORY_PRODUCTS = "select Product.productIdx, createdAt, img.imageUrl, title, location, price, isFreeShip, isSafepay, tradeStatus, isFav\n" +
                                                        "from Product\n" +
                                                        "left outer join (select productIdx, imageUrl\n" +
                                                        "\t\t\t\tfrom Product_Image\n" +
                                                        "\t\t\t\twhere status = 'A'\n" +
                                                        "\t\t\t\tgroup by productIdx) as img\n" +
                                                        "on Product.productIdx = img.productIdx\n" +
                                                        "where middleCategoryIdx = ?\n" +
                                                        "and status = 'A';";
    public static String SELECT_SUB_CATEGORY_PRODUCTS = "select Product.productIdx, createdAt, img.imageUrl, title, location, price, isFreeShip, isSafepay, tradeStatus, isFav\n" +
                                                        "from Product\n" +
                                                        "left outer join (select productIdx, imageUrl\n" +
                                                        "\t\t\t\tfrom Product_Image\n" +
                                                        "\t\t\t\twhere status = 'A'\n" +
                                                        "\t\t\t\tgroup by productIdx) as img\n" +
                                                        "on Product.productIdx = img.productIdx\n" +
                                                        "where subCategoryIdx = ?\n" +
                                                        "and status = 'A';";

}

package com.example.demo.src.product;

public class ProductDaoSqls {
    public static String SELECT_PRODUCTS = "select prd.productIdx, prdImg.imageUrl, prd.isSafepay, prd.title, prd.price, prd.location, prd.contents, prd.createdAt, fav.favCnt, prd.isFav\n" +
                                            "from Product as prd\n" +
                                            "left outer join (select * \n" +
                                            "\t\t\tfrom Product_Image\n" +
                                            "\t\t\tgroup by productIdx) as prdImg\n" +
                                            "on prd.productIdx = prdImg.productIdx\n" +
                                            "left outer join (select productIdx, count(userIdx) as favCnt\n" +
                                            "\t\t\t\tfrom Favorite\n" +
                                            "\t\t\t\tgroup by productIdx) as fav\n" +
                                            "on prd.productIdx = fav.productIdx\n" +
                                            "where prd.status = 'A';";

    public static String SELECT_PRODUCTS_BY_TITLE = "select prd.productIdx, prdImg.imageUrl, prd.isSafepay, prd.title, prd.price, prd.location, prd.contents, prd.createdAt, fav.favCnt, prd.isFav\n" +
                                                    "from Product as prd\n" +
                                                    "inner join (select * \n" +
                                                    "\t\t\tfrom Product_Image\n" +
                                                    "\t\t\tgroup by productIdx) as prdImg\n" +
                                                    "on prd.productIdx = prdImg.productIdx\n" +
                                                    "left outer join (select productIdx, count(userIdx) as favCnt\n" +
                                                    "\t\t\t\tfrom Favorite\n" +
                                                    "\t\t\t\tgroup by productIdx) as fav\n" +
                                                    "on prd.productIdx = fav.productIdx\n" +
                                                    "where prd.title like ?\n" +
                                                    "and prd.status = 'A';";

    public static String SELECT_PRODUCT = "select prd.productIdx, prd.price, prd.title, prd.location, prd.createdAt, prd.productStatus, prd.quantity, prd.isFreeShip, prd.isChangable, fav.favCnt, chat.chatCnt, contents, prd.isSafepay, prd.isFav\n" +
                                        "from Product as prd\n" +
                                        "left outer join (select productIdx, count(userIdx) as favCnt\n" +
                                        "\t\t\t\tfrom Favorite\n" +
                                        "\t\t\t\tgroup by productIdx) as fav\n" +
                                        "on prd.productIdx = fav.productIdx\n" +
                                        "left outer join (select productIdx, count(memberIdx) as chatCnt\n" +
                                        "\t\t\t\tfrom ChatRoom\n" +
                                        "\t\t\t\tgroup by productIdx) as chat\n" +
                                        "on prd.productIdx = chat.productIdx\n" +
                                        "where prd.productIdx = ?\n" +
                                        "and prd.status = 'A';";
    public static String SELECT_PRODUCT_IMAGES = "select productIdx, imageUrl\n" +
                                                "from Product_Image\n" +
                                                "where productIdx = ?\n" +
                                                "and status = 'A';";
    public static String SELECT_PRODUCT_TAGS = "select prdTag.productIdx, prdTag.hashtagIdx, tag.tagName\n" +
                                                "from Product_Tag as prdTag\n" +
                                                "inner join (select hashtagIdx, tagName\n" +
                                                "\t\t\tfrom Hashtag) as tag\n" +
                                                "on prdTag.hashtagIdx = tag.hashtagIdx\n" +
                                                "where productIdx = ?\n" +
                                                "and prdTag.status = 'A';";
    public static String INSERT_PRODUCT = "INSERT INTO Product (`title`, `mainCategoryIdx`, `middleCategoryIdx`, `subCategoryIdx`, `location`, `productStatus`, `isChangable`, `price`, `isFreeShip`, `contents`, `quantity`, `isSafepay`, `userIdx`) \n" +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    public static String UPDATE_PRODUCT = "UPDATE Product \n" +
            "SET title = ?,\n" +
            "mainCategoryIdx = ?,\n" +
            "middleCategoryIdx = ?,\n" +
            "subCategoryIdx = ?,\n" +
            "location = ?,\n" +
            "productStatus = ?,\n" +
            "isChangable = ?,\n" +
            "price = ?,\n" +
            "isFreeShip = ?,\n" +
            "contents = ?,\n" +
            "quantity = ?,\n" +
            "isSafepay = ?\n" +
            "WHERE (productIdx = ?);";
    public static String CHECK_PRODUCT = "select title, mainCategoryIdx, middleCategoryIdx, subCategoryIdx, location, \n" +
            "productStatus, isChangable, price, isFreeShip,\n" +
            "contents, quantity, isSafepay\n" +
            "from Product\n" +
            "where productIdx = ?;";

}

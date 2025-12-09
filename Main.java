import java.time.Instant;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DBConnection db = new DBConnection();
        DataRetriever dr = new DataRetriever(db);

        
        // a) getAllCategories()
        
        System.out.println("\n===== Test getAllCategories() =====");
        List<Category> categories = dr.getAllCategories();
        for (Category c : categories) {
            System.out.println(c);
        }


        
        // b) getProductList(page, size)
        
        System.out.println("\n===== Test getProductList(page, size) =====");

        System.out.println("\n-- Page = 1, Size = 10 --");
        dr.getProductList(1, 10).forEach(System.out::println);

        System.out.println("\n-- Page = 1, Size = 5 --");
        dr.getProductList(1, 5).forEach(System.out::println);

        System.out.println("\n-- Page = 1, Size = 3 --");
        dr.getProductList(1, 3).forEach(System.out::println);

        System.out.println("\n-- Page = 2, Size = 2 --");
        dr.getProductList(2, 2).forEach(System.out::println);


        
        // c) getProductsByCriteria(productName, categoryName, creationMin, creationMax)
        
        System.out.println("\n===== Test getProductsByCriteria(filtres simples) =====");

        System.out.println("\n-- productName = \"Dell\" --");
        dr.getProductsByCriteria("Dell", null, null, null).forEach(System.out::println);

        System.out.println("\n-- categoryName = \"info\" --");
        dr.getProductsByCriteria(null, "info", null, null).forEach(System.out::println);

        System.out.println("\n-- productName = \"iPhone\", categoryName = \"mobile\" --");
        dr.getProductsByCriteria("iPhone", "mobile", null, null).forEach(System.out::println);

        System.out.println("\n-- creationMin = 2024-02-01, creationMax = 2024-03-01 --");
        dr.getProductsByCriteria(
                null,
                null,
                Instant.parse("2024-02-01T00:00:00Z"),
                Instant.parse("2024-03-01T00:00:00Z")
        ).forEach(System.out::println);

        System.out.println("\n-- productName = \"Samsung\", categoryName = \"bureau\" --");
        dr.getProductsByCriteria(
                "Samsung",
                "bureau",
                null,
                null
        ).forEach(System.out::println);

        System.out.println("\n-- productName = \"Sony\", categoryName = \"informatique\" --");
        dr.getProductsByCriteria(
                "Sony",
                "informatique",
                null,
                null
        ).forEach(System.out::println);

        System.out.println("\n-- categoryName = \"audio\", creationMin = 2024-01-01, creationMax = 2024-12-01 --");
        dr.getProductsByCriteria(
                null,
                "audio",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-12-01T00:00:00Z")
        ).forEach(System.out::println);


        
        // d) getProductsByCriteria(..., page, size)
        
        System.out.println("\n===== Test filtres + pagination =====");

        System.out.println("\n-- page=1, size=10 (no filters) --");
        dr.getProductsByCriteria(null, null, null, null, 1, 10).forEach(System.out::println);

        System.out.println("\n-- productName = \"Dell\", page=1, size=5 --");
        dr.getProductsByCriteria("Dell", null, null, null, 1, 5).forEach(System.out::println);

        System.out.println("\n-- categoryName = \"informatique\", page=1, size=10 --");
        dr.getProductsByCriteria(null, "informatique", null, null, 1, 10)
                .forEach(System.out::println);
    }
}

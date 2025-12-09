import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    
    // 1. Lister TOUTES les catégories
    
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT id, name FROM product_category ORDER BY id";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                categories.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    
    // 2. Pagination simple : getProductList(page, size)
    
    public List<Product> getProductList(int page, int size) {
        List<Product> products = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql = """
                SELECT p.id, p.name, p.price, p.creation_datetime,
                       c.id AS cat_id, c.name AS cat_name
                FROM product p
                JOIN product_category c ON c.product_id = p.id
                ORDER BY p.id
                LIMIT ? OFFSET ?
                """;

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Category category = new Category(
                            rs.getInt("cat_id"),
                            rs.getString("cat_name")
                    );

                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getTimestamp("creation_datetime").toInstant(),
                            category
                    );

                    products.add(product);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    
    // 3. getProductsByCriteria (sans pagination)
    
    public List<Product> getProductsByCriteria(
            String productName,
            String categoryName,
            Instant creationMin,
            Instant creationMax
    ) {
        List<Product> products = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT p.id, p.name, p.price, p.creation_datetime,
                       c.id AS cat_id, c.name AS cat_name
                FROM product p
                JOIN product_category c ON c.product_id = p.id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        // --- Filtres dynamiques ---
        if (productName != null) {
            sql.append(" AND p.name ILIKE ? ");
            params.add("%" + productName + "%");
        }

        if (categoryName != null) {
            sql.append(" AND c.name ILIKE ? ");
            params.add("%" + categoryName + "%");
        }

        if (creationMin != null) {
            sql.append(" AND p.creation_datetime >= ? ");
            params.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            sql.append(" AND p.creation_datetime <= ? ");
            params.add(Timestamp.from(creationMax));
        }

        sql.append(" ORDER BY p.id ");

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Bind params
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Category category = new Category(
                        rs.getInt("cat_id"),
                        rs.getString("cat_name")
                );

                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getTimestamp("creation_datetime").toInstant(),
                        category
                );

                products.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    // 4. FILTRES + PAGINATION combinés
    
    public List<Product> getProductsByCriteria(
            String productName,
            String categoryName,
            Instant creationMin,
            Instant creationMax,
            int page,
            int size
    ) {
        // 1) Filtrer d'abord
        List<Product> filtered = getProductsByCriteria(
                productName,
                categoryName,
                creationMin,
                creationMax
        );

        // 2) Paginer ensuite
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, filtered.size());

        if (fromIndex >= filtered.size()) {
            return new ArrayList<>(); // page vide
        }

        return filtered.subList(fromIndex, toIndex);
    }
}

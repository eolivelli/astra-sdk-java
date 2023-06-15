package com.datastax.astra.sdk.quickstart.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.quickstart.AbstractSdkTest;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.data.CqlVector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AstraVectorSearchPreviewTest extends AbstractSdkTest {

    private static AstraClient astraClient;

    /** Pojo to map the result of the query. */
    private record Product(String productId, String productName, Object vector) {}

    @BeforeAll
    public static void init() {
        loadRequiredEnvironmentVariables();
        astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_APPLICATION_TOKEN)   // credentials are mandatory
                .withDatabaseId(ASTRA_DB_ID)             // identifier of the database
                .withDatabaseRegion(ASTRA_DB_REGION)     // connection is different for each dc
                .enableCql()                             // as stateful, connection is not always establish
                .enableDownloadSecureConnectBundle()     // secure connect bundles can be downloaded
                .withCqlKeyspace(ASTRA_DB_KEYSPACE)      // target keyspace
                .build();
        createSchema(astraClient.cqlSession());
    }

    @Test
    public void demoVectorPreview() {
        Assertions.assertTrue(findProductById(astraClient.cqlSession(), "invalid").isEmpty());
        findProductById(astraClient.cqlSession(), "pf1843")
        .ifPresent(product -> {
            System.out.println("Product Found ! looking for similar products");
            findAllSimilarProducts(astraClient.cqlSession(), product).forEach(System.out::println);
        });
    }

    @AfterAll
    public static void cleanUp() {
        astraClient.close();
    }

    private static void createSchema(CqlSession cqlSession) {
        // Create a Table with Embeddings
        astraClient.cqlSession().execute("" +
                "CREATE TABLE IF NOT EXISTS pet_supply_vectors (" +
                "    product_id     TEXT PRIMARY KEY," +
                "    product_name   TEXT," +
                "    product_vector vector<float, 14>)");
        System.out.println("Table created.");

        // Create a Search Index
        astraClient.cqlSession().execute("" +
                "CREATE CUSTOM INDEX IF NOT EXISTS idx_vector " +
                "ON pet_supply_vectors(product_vector) " +
                "USING 'StorageAttachedIndex'");
        System.out.println("Index Created.");

        // Insert rows
        astraClient.cqlSession().execute("" +
                "INSERT INTO pet_supply_vectors (product_id, product_name, product_vector) " +
                "VALUES ('pf1843','HealthyFresh - Chicken raw dog food',[1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0])");
        astraClient.cqlSession().execute("" +
                "INSERT INTO pet_supply_vectors (product_id, product_name, product_vector) " +
                "VALUES ('pf1844','HealthyFresh - Beef raw dog food',[1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0])");
        astraClient.cqlSession().execute("" +
                "INSERT INTO pet_supply_vectors (product_id, product_name, product_vector) " +
                "VALUES ('pt0021','Dog Tennis Ball Toy',[0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0])");
        astraClient.cqlSession().execute("" +
                "INSERT INTO pet_supply_vectors (product_id, product_name, product_vector) " +
                "VALUES ('pt0041','Dog Ring Chew Toy',[0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0])");
        astraClient.cqlSession().execute("" +
                "INSERT INTO pet_supply_vectors (product_id, product_name, product_vector) " +
                "VALUES ('pf7043','PupperSausage Bacon dog Treats',[0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1])");
        astraClient.cqlSession().execute("" +
                "INSERT INTO pet_supply_vectors (product_id, product_name, product_vector) " +
                "VALUES ('pf7044','PupperSausage Beef dog Treats',[0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0])");
        System.out.println("Rows inserted.");
    }

    private Optional<Product> findProductById(CqlSession cqlSession, String productId) {
        Row row = cqlSession.execute(SimpleStatement
                        .builder("SELECT * FROM pet_supply_vectors WHERE product_id = ?")
                        .addPositionalValue(productId).build()).one();
        return (row != null) ? Optional.of(row).map(this::mapRowAsProduct) : Optional.empty();
    }

    private List<Product> findAllSimilarProducts(CqlSession cqlSession, Product orginal) {
        return cqlSession.execute(SimpleStatement
                        .builder("SELECT * FROM pet_supply_vectors ORDER BY product_vector ANN OF ? LIMIT 2;")
                        .addPositionalValue(orginal.vector)
                        .build())
                .all()
                .stream()
                .filter(row -> !row.getString("product_id").equals(orginal.productId))
                .map(this::mapRowAsProduct)
                .toList();
    }

    private Product mapRowAsProduct(Row row) {
        return new Product(
                row.getString("product_id"),
                row.getString("product_name"),
                row.getObject("product_vector"));
    }


}

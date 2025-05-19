package com.codingchallenge;

import com.codingchallenge.dto.internal.BestProductPriceResult;
import com.codingchallenge.dto.outgoing.GetShoppingListDto;
import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.model.Product;
import com.codingchallenge.model.User;
import com.codingchallenge.model.User.CartItem;
import com.codingchallenge.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BestPricesTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PriceEntryRepository priceEntryRepository;
    @Autowired
    private DiscountEntryRepository discountEntryRepository;
    @Autowired
    private ShoppingListRepository shoppingListRepository;
    private String testUserId;
    private String testProductId;
    private User testUser;
    private Product testProduct;

    @Test
    void contextLoads() {
    }

    @BeforeAll
    void beforeAllTest() {

    }

    @AfterEach
    void afterEachTest() {
        userRepository.deleteById(testUserId);
        productRepository.deleteById(testProductId);
        shoppingListRepository.deleteAll();
    }

    @BeforeEach
    void beforeEachTest() {
        testUser = new User(null, "testUser", "password", List.of(), null);
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId();
        testProduct = new Product(null, "testProductName", "testProductCategory", "testProductBrand", 500.0, "g");
        testProduct = productRepository.save(testProduct);
        testProductId = testProduct.getProductId();

        testUser.setShoppingCart(List.of(new CartItem(testProductId, 1)));
        testUser.setLastCartUpdate(LocalDateTime.now());
        userRepository.save(testUser);
    }

    @Test
    void betterWithDiscount() {
        String testProductProductId = testProduct.getProductId();

        PriceEntry betterPriceEntry = new PriceEntry(null, testProductProductId, 100.0, "RON", "Store1",
                                                     LocalDate.now(), 1.0, "kg", "100.0 RON/kg", 100.0);
        PriceEntry worsePriceEntry = new PriceEntry(null, testProductProductId, 95.0, "RON", "Store2", LocalDate.now(),
                                                    1.0, "kg", "95.0 RON/kg", 95.0);

        DiscountEntry discountEntry = new DiscountEntry(null, testProductProductId, LocalDate.now().minusDays(3),
                                                        LocalDate.now().plusDays(3), 10, "Store1",
                                                        LocalDate.now().minusDays(5));
        Double expectedPrice = betterPriceEntry.getPrice() - (betterPriceEntry.getPrice() * discountEntry.getPercentageOfDiscount() / 100);
        runPriceTest(betterPriceEntry, worsePriceEntry, List.of(discountEntry), expectedPrice);
    }

    @Test
    void betterWithoutDiscount() {
        PriceEntry betterPriceEntry = new PriceEntry(null, testProductId, 90.0, "RON", "Store1", LocalDate.now(), 1.0,
                                                     "kg", "90.0 RON/kg", 90.0);
        PriceEntry worsePriceEntry = new PriceEntry(null, testProductId, 115.0, "RON", "Store2", LocalDate.now(), 1.0,
                                                    "kg", "115.0 RON/kg", 115.0);

        DiscountEntry discountEntry = new DiscountEntry(null, testProductId, LocalDate.now().minusDays(3),
                                                        LocalDate.now().plusDays(3), 10, "Store2",
                                                        LocalDate.now().minusDays(3));
        Double expectedPrice = betterPriceEntry.getPrice();
        runPriceTest(betterPriceEntry, worsePriceEntry, List.of(discountEntry), expectedPrice);
    }

    @Test
    void noActiveDiscount() {

        PriceEntry betterPriceEntry = new PriceEntry(null, testProductId, 95.0, "RON", "Store2", LocalDate.now(), 1.0,
                                                     "kg", "95.0 RON/kg", 95.0);
        PriceEntry worsePriceEntry = new PriceEntry(null, testProductId, 100.0, "RON", "Store1", LocalDate.now(), 1.0,
                                                    "kg", "100.0 RON/kg", 100.0);

        DiscountEntry discountEntry = new DiscountEntry(null, testProductId, LocalDate.now().minusDays(20),
                                                        LocalDate.now().minusDays(15), 10, "Store1",
                                                        LocalDate.now().minusDays(25));
        Double expectedPrice = betterPriceEntry.getPrice();
        runPriceTest(betterPriceEntry, worsePriceEntry, List.of(discountEntry), expectedPrice);
    }

    @Test
    void bothDiscount() {
        PriceEntry betterPriceEntry = new PriceEntry(null, testProductId, 100.0, "RON", "Store1", LocalDate.now(), 1.0,
                                                     "kg", "100.0 RON/kg", 100.0);
        PriceEntry worsePriceEntry = new PriceEntry(null, testProductId, 100.0, "RON", "Store2", LocalDate.now(), 1.0,
                                                    "kg", "100.0 RON/kg", 100.0);
        priceEntryRepository.save(betterPriceEntry);
        priceEntryRepository.save(worsePriceEntry);

        DiscountEntry discountEntry = new DiscountEntry(null, testProductId, LocalDate.now().minusDays(3),
                                                        LocalDate.now().plusDays(3), 10, "Store1",
                                                        LocalDate.now().minusDays(5));
        DiscountEntry discountEntry2 = new DiscountEntry(null, testProductId, LocalDate.now().minusDays(3),
                                                         LocalDate.now().plusDays(3), 10, "Store2",
                                                         LocalDate.now().minusDays(5));
        discountEntryRepository.save(discountEntry);
        discountEntryRepository.save(discountEntry2);
        Double expectedPrice = betterPriceEntry.getPrice() - (betterPriceEntry.getPrice() * discountEntry.getPercentageOfDiscount() / 100);
        ResponseEntity<GetShoppingListDto> result = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users/" + testUserId + "/shopping-list", null, GetShoppingListDto.class

        );
        GetShoppingListDto shoppingList = result.getBody();
        assert shoppingList != null;
        HashMap<String, List<BestProductPriceResult>> bestPriceResult = shoppingList.getProducts();
        String resultStoreName = bestPriceResult.keySet().iterator().next();
        BestProductPriceResult bestProductPriceResult = bestPriceResult.get(resultStoreName).getFirst();
        assertTrue(bestProductPriceResult.getStoreName()
                       .equals(betterPriceEntry.getStoreName()) || bestProductPriceResult.getStoreName()
            .equals(worsePriceEntry.getStoreName()));
        cleanUp(betterPriceEntry, worsePriceEntry, List.of(discountEntry, discountEntry2));
    }

    @Test
    public void thirdStoreDiscount() {
        PriceEntry betterPriceEntry = new PriceEntry(null, testProductId, 99.0, "RON", "Store1", LocalDate.now(), 1.0,
                                                     "kg", "99.0 RON/kg", 99.0);
        PriceEntry worsePriceEntry = new PriceEntry(null, testProductId, 100.0, "RON", "Store2", LocalDate.now(), 1.0,
                                                    "kg", "100.0 RON/kg", 100.0);

        DiscountEntry discountEntry = new DiscountEntry(null, testProductId, LocalDate.now().minusDays(3),
                                                        LocalDate.now().plusDays(3), 10, "Store3",
                                                        LocalDate.now().minusDays(5));
        Double expectedPrice = betterPriceEntry.getPrice();
        runPriceTest(betterPriceEntry, worsePriceEntry, List.of(discountEntry), expectedPrice);
    }

    @Test
    public void nonExistentProducts() {
        testUser.setShoppingCart(List.of(new CartItem("nonExistingId", 1), new CartItem("nonExistingId2", 1)));
        testUser.setLastCartUpdate(LocalDateTime.now());
        userRepository.save(testUser);

        ResponseEntity<GetShoppingListDto> result = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users/" + testUserId + "/shopping-list", null, GetShoppingListDto.class

        );
        assertEquals(HttpStatus.OK, result.getStatusCode());

        GetShoppingListDto shoppingList = result.getBody();
        assert shoppingList != null;
        assertEquals(testUserId, shoppingList.getUserId());

        // Empty list should be returned
        HashMap<String, List<BestProductPriceResult>> bestPriceResult = shoppingList.getProducts();
        assertEquals(0, bestPriceResult.size());
    }

    @Test
    public void sameStore() {
        PriceEntry betterPriceEntry = new PriceEntry(null,
                                                     testProductId,
                                                     99.0,
                                                     "RON",
                                                     "Store1",
                                                     LocalDate.now(),
                                                     1.0,
                                                     "kg",
                                                     "99.0 RON/kg",
                                                     99.0);
        PriceEntry worsePriceEntry = new PriceEntry(null,
                                                    testProductId,
                                                    100.0,
                                                    "RON",
                                                    "Store1",
                                                    LocalDate.now(),
                                                    1.0,
                                                    "kg",
                                                    "100.0 RON/kg",
                                                    100.0);

        Double expectedPrice = betterPriceEntry.getPrice();
        runPriceTest(betterPriceEntry, worsePriceEntry, List.of(), expectedPrice);
    }

    @Test
    public void zeroDiscount() {
        PriceEntry betterPriceEntry = new PriceEntry(null,
                                                     testProductId,
                                                     99.0,
                                                     "RON",
                                                     "Store1",
                                                     LocalDate.now(),
                                                     1.0,
                                                     "kg",
                                                     "99.0 RON/kg",
                                                     99.0);
        PriceEntry worsePriceEntry = new PriceEntry(null,
                                                    testProductId,
                                                    100.0,
                                                    "RON",
                                                    "Store2",
                                                    LocalDate.now(),
                                                    1.0,
                                                    "kg",
                                                    "100.0 RON/kg",
                                                    100.0);

        DiscountEntry discountEntry = new DiscountEntry(null,
                                                        testProductId,
                                                        LocalDate.now().minusDays(3),
                                                        LocalDate.now().plusDays(3),
                                                        0,
                                                        "Store2",
                                                        LocalDate.now().minusDays(5));
        Double expectedPrice = betterPriceEntry.getPrice();
        runPriceTest(betterPriceEntry, worsePriceEntry, List.of(discountEntry), expectedPrice);
    }

    @Test
    public void samePrice() {
        PriceEntry betterPriceEntry = new PriceEntry(null,
                                                     testProductId,
                                                     100.0,
                                                     "RON",
                                                     "Store1",
                                                     LocalDate.now(),
                                                     1.0,
                                                     "kg",
                                                     "100.0 RON/kg",
                                                     100.0);
        PriceEntry worsePriceEntry = new PriceEntry(null,
                                                    testProductId,
                                                    100.0,
                                                    "RON",
                                                    "Store2",
                                                    LocalDate.now(),
                                                    1.0,
                                                    "kg",
                                                    "100.0 RON/kg",
                                                    100.0);
        priceEntryRepository.save(betterPriceEntry);
        priceEntryRepository.save(worsePriceEntry);

        Double expectedPrice = betterPriceEntry.getPrice();

        ResponseEntity<GetShoppingListDto> result = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users/" + testUserId + "/shopping-list", null, GetShoppingListDto.class

        );

        // or Store1 or Store2
        assertEquals(HttpStatus.OK, result.getStatusCode());
        GetShoppingListDto shoppingList = result.getBody();
        assert shoppingList != null;
        HashMap<String, List<BestProductPriceResult>> bestPriceResult = shoppingList.getProducts();
        String resultStoreName = bestPriceResult.keySet().iterator().next();
        BestProductPriceResult bestProductPriceResult = bestPriceResult.get(resultStoreName).getFirst();
        assertTrue(bestProductPriceResult.getStoreName()
                       .equals(betterPriceEntry.getStoreName()) || bestProductPriceResult.getStoreName()
            .equals(worsePriceEntry.getStoreName()));
        cleanUp(betterPriceEntry, worsePriceEntry, List.of());

    }

    private void cleanUp(PriceEntry betterPriceEntry, PriceEntry worsePriceEntry, List<DiscountEntry> discountEntry) {
        priceEntryRepository.delete(betterPriceEntry);
        priceEntryRepository.delete(worsePriceEntry);
        discountEntryRepository.deleteAll(discountEntry);
    }

    public void runPriceTest(PriceEntry betterPriceEntry,
                             PriceEntry worsePriceEntry,
                             List<DiscountEntry> discountEntries,
                             Double expectedPrice) {
        // Create test data
        priceEntryRepository.save(betterPriceEntry);
        priceEntryRepository.save(worsePriceEntry);
        discountEntryRepository.saveAll(discountEntries);
        User user = userRepository.findById(testUserId).orElseThrow();
        user.getShoppingCart().add(new CartItem(testProductId, 1));
        user.setLastCartUpdate(LocalDateTime.now());
        userRepository.save(user);

        // Call the API to generate and get the best prices
        ResponseEntity<GetShoppingListDto> result = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users/" + testUserId + "/shopping-list", null, GetShoppingListDto.class

        );

        assertEquals(HttpStatus.OK, result.getStatusCode());

        GetShoppingListDto shoppingList = result.getBody();
        assert shoppingList != null;
        assertEquals(testUserId, shoppingList.getUserId());
        // get the products
        HashMap<String, List<BestProductPriceResult>> bestPriceResult = shoppingList.getProducts();
        String resultStoreName = bestPriceResult.keySet().iterator().next();
        BestProductPriceResult bestProductPriceResult = bestPriceResult.get(resultStoreName).getFirst();

        assertEquals(betterPriceEntry.getStoreName(), resultStoreName);
        assertEquals(betterPriceEntry.getPrice(), bestProductPriceResult.getOriginalPrice());
        assertEquals(expectedPrice, bestProductPriceResult.getEffectivePrice());

        cleanUp(betterPriceEntry, worsePriceEntry, discountEntries);
    }
}

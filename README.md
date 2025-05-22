# Price-Comparator-Backend

## Overview of the Project's Structure

```
src
├───main
│   ├───java
│   │   └───com
│   │       └───codingchallenge
│   │           ├───controller
│   │           ├───controlleradvice
│   │           ├───dto
│   │           │   ├───incoming
│   │           │   ├───internal
│   │           │   └───outgoing
│   │           ├───exception
│   │           ├───mapper
│   │           ├───model
│   │           ├───repository
│   │           ├───service
│   │           └───validation
│   └───resources
│       └───data
└───test
    └───java
        └───com
            └───codingchallenge
```

I followed the standard Gradle project structure, which is a common convention in Java projects. The `src` directory contains the `main` and `test` directories, which are further divided into `java` and `resources` directories. The `java` directory contains the Java source code, while the `resources` directory contains non-code resources such as csv files. The `test` directory contains the test source code.

My source code is organized into packages based on their functionality. The `controller` package contains the REST controllers, the `service` package contains the service classes, the `repository` package contains the repository interfaces (daos), and so on. This organization helps to keep the codebase clean and maintainable.

## How to Run the Project

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Make sure you have MongoDB, Java 21 and Gradle installed on your machine.

4. Run the following command to build and run the project:
   ```bash
    ./gradlew bootRun
   ```
5. The application will start running on `http://localhost:8080`.

## Assumptions and Simplifications

- The CSV files are well-formed and contain valid data.
- A user only has a single shopping cart.
- Notifications contain the best price for each product in the shopping cart or for all products if empty, while price alerts contain the best and latest price for the target product, and are only sent when the price falls below the target price.
- The application is designed to handle a limited number of products and prices. If the number of products or prices exceeds a certain limit, the application may not perform optimally.
- Notifications are sent to the console. In a production environment, you would typically send notifications to a messaging system or a notification service.
- User authentication only includes a simple hash-based authentication. In a production environment, you would typically use a more secure authentication mechanism, such as OAuth2 or JWT.
- The application does not include a user interface. It is designed to be used as a backend service, and you would typically build a frontend application to interact with it.
- The application does not include any caching mechanisms. In a production environment, you would typically use a caching layer to improve performance.
- For each entity there is a single outgoing DTO. In a production environment, you would typically have multiple outgoing DTOs for each entity, depending on the use case.

## Endpoints

### User Registration

- **POST** `/api/users/register`

  - Request Body: `{"name": "string", "password": "string"}`

  - Response: 200 OK Body: `{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}`

### User Login

- **POST** `/api/users/login`

  - Request Body: `{"name": "string", "password": "string"}`

  - Response: 200 OK Body: `true` if login is successful, `false` otherwise

### Get All Users

- **GET** `/api/users`

  - Response: 200 OK Body: `[{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}]`

### Get User by ID

- **GET** `/api/users/{id}`

  - Response: 200 OK Body: `{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}` or 404 Not Found if user not found

### Update User

- **PUT** `/api/users/{id}`

  - Request Body: `{"name": "string", "oldPassword": "string", "password": "string"}`

  - Response: 200 OK Body: `{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}` or 404 Not Found if user not found

### Delete User

- **DELETE** `/api/users/{id}`
  - Response: 204 No Content

### Add Product to Shopping Cart

- **POST** `/api/users/{userId}/cart`

  - Request Body: `{"productId": "string", "quantity": int}`

  - Response: 200 OK Body: `{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}` or 404 Not Found if user not found

### Remove Product from Shopping Cart

- **DELETE** `/api/users/{userId}/cart`

  - Request Body: `{"productId": "string", "quantity": int}`

  - Response: 200 OK Body: `{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}` or 404 Not Found if user not found

### Clear Shopping Cart

- **DELETE** `/api/users/{userId}/cart`

  - Response: 200 OK Body: `{"id": "string", "name": "string", "shoppingCart": list, lastCartUpdate: date}` or 404 Not Found if user not found

### Generate Shopping List

- **POST** `/api/users/{userId}/shopping-list`

  - Shopping lists contain the best price for each product in the user's shopping cart, while also taking into consideration discounts.
    The products are separated by store, and the best price is selected for each product.

  - Shopping lists can only be created through users.

  - If the shopping cart is empty, the shopping list will contain the best price for each product in the database.

  - Request Body: `{"products": [{"productId": "string", "quantity": int}]}`

  - Response: 200 OK Body: `{"id": "string", "userId": "string", "name": string, "createdDate": date, "products": map}` or 404 Not Found if user not found

### Get All Shopping Lists

- **GET** `/api/shopping-lists`

  - Response: 200 OK Body: `[{"id": "string", "userId": "string", "name": string, "createdDate": date, "products": map}]`

### Get Shopping List by ID

- **GET** `/api/shopping-lists/{id}`

  - Response: 200 OK Body: `{"id": "string", "userId": "string", "name": string, "createdDate": date, "products": map}` or 404 Not Found if shopping list not found

### Delete Shopping List

- **DELETE** `/api/shopping-lists/{id}`

  - Response: 204 No Content

### Get All Products

- **GET** `/api/products`

  - Response: 200 OK Body: `[{"productId": "string", "productName": "string", "productCategory": string, "brand": string, "packageQuantity": double, "packageUnit": string}]`

### Get Product by ID

- **GET** `/api/products/{id}`

  - Response: 200 OK Body: `{"productId": "string", "productName": "string", "productCategory": string, "brand": string, "packageQuantity": double, "packageUnit": string}` or 404 Not Found if product not found

### Create Product

- **POST** `/api/products`

  - Request Body: `{"productName": "string", "productCategory": string, "brand": string, "packageQuantity": double, "packageUnit": string}`

  - Response: 200 OK Body: `{"productName": "string", "productCategory": string, "brand": string, "packageQuantity": double, "packageUnit": string}`

### Update Product

- **PUT** `/api/products/{id}`

  - Request Body: `{"productName": "string", "productCategory": string, "brand": string, "packageQuantity": double, "packageUnit": string}`

  - Response: 200 OK Body: `{"productName": "string", "productCategory": string, "brand": string, "packageQuantity": double, "packageUnit": string}` or 404 Not Found if product not found

### Delete Product

- **DELETE** `/api/products/{id}`

  - Response: 204 No Content or 404 Not Found if product not found

### Get All Price Entries

- **GET** `/api/price-entries`

  - Response: 200 OK Body: `[{"id": "string", "productId": "string", "price": double, "currency": "string", "storeName": string, "date": date, "packageQuantity": double, "packageUnit": string, "valuePerUnitDisplay": string, "valuePerUnit": double}]`

### Get Price Entry by ID

- **GET** `/api/price-entries/{id}`

  - Response: 200 OK Body: `{"id": "string", "productId": "string", "price": double, "currency": "string", "storeName": string, "date": date, "packageQuantity": double, "packageUnit": string, "valuePerUnitDisplay": string, "valuePerUnit": double}` or 404 Not Found if price entry not found

### Create Price Entry

- **POST** `/api/price-entries`

  - Request Body: `{"productId": "string", "price": double, "currency": "string", "storeName": string, "date": date, "packageQuantity": double, "packageUnit": string}`

  - Response: 200 OK Body: `{"id": "string", "productId": "string", "price": double, "currency": "string", "storeName": string, "date": date, "packageQuantity": double, "packageUnit": string}`

### Update Price Entry

- **PUT** `/api/price-entries/{id}`

  - Request Body: `{"productId": "string", "price": double, "currency": "string", "storeName": string, "date": date, "packageQuantity": double, "packageUnit": string}`

  - Response: 200 OK Body: `{"id": "string", "productId": "string", "price": double, "currency": "string", "storeName": string, "date": date, "packageQuantity": double, "packageUnit": string}` or 404 Not Found if price entry not found

### Delete Price Entry

- **DELETE** `/api/price-entries/{id}`

  - Response: 204 No Content or 404 Not Found if price entry not found

### Get All Price Alerts

- **GET** `/api/price-alerts`

  - Response: 200 OK Body: `[{"id": "string", "targetPrice": double, "productId": "string", "userId": "string", "message": string}]`

### Get Price Alert by ID

- **GET** `/api/price-alerts/{id}`

  - Response: 200 OK Body: `{"id": "string", "targetPrice": double, "productId": "string", "userId": "string", "message": string}` or 404 Not Found if price alert not found

### Create Price Alert

- **POST** `/api/price-alerts`

  - Request Body: `{"targetPrice": double, "productId": "string", "userId": "string", "message": string}`

  - Response: 200 OK Body: `{"id": "string", "targetPrice": double, "productId": "string", "userId": "string", "message": string}`

### Update Price Alert

- **PUT** `/api/price-alerts/{id}`

  - Request Body: `{"targetPrice": double, "productId": "string", "userId": "string", "message": string}`

  - Response: 200 OK Body: `{"id": "string", "targetPrice": double, "productId": "string", "userId": "string", "message": string}` or 404 Not Found if price alert not found

### Delete Price Alert

- **DELETE** `/api/price-alerts/{id}`

  - Response: 204 No Content or 404 Not Found if price alert not found

### Get All Discount Entries

- **GET** `/api/discount-entries`

  - Response: 200 OK Body: `[{"id": "string", "productId": "string", "percentageOfDiscount": int, "fromDate": date, "toDate": date, "storeName": string, "date": date}]`

### Get Discount Entry by ID

- **GET** `/api/discount-entries/{id}`

  - Response: 200 OK Body: `{"id": "string", "productId": "string", "percentageOfDiscount": int, "fromDate": date, "toDate": date, "storeName": string, "date": date}` or 404 Not Found if discount entry not found

### Create Discount Entry

- **POST** `/api/discount-entries`

  - Request Body: `{"productId": "string", "percentageOfDiscount": int, "fromDate": date, "toDate": date, "storeName": string}`

  - Response: 200 OK Body: `{"id": "string", "productId": "string", "percentageOfDiscount": int, "fromDate": date, "toDate": date, "storeName": string}`

### Update Discount Entry

- **PUT** `/api/discount-entries/{id}`

  - Request Body: `{"productId": "string", "percentageOfDiscount": int, "fromDate": date, "toDate": date, "storeName": string}`

  - Response: 200 OK Body: `{"id": "string", "productId": "string", "percentageOfDiscount": int, "fromDate": date, "toDate": date, "storeName": string}` or 404 Not Found if discount entry not found

### Delete Discount Entry

- **DELETE** `/api/discount-entries/{id}`

  - Response: 204 No Content

## Testing

### Integration Tests

- The BestPricesTest integration test is located in the `src/test/java/com/codingchallenge` directory. It tests the mongoDB aggregation pipeline for the best prices using different products, discounts, and stores.

## Other Notable Features

### Automated CSV File Processing

- The application automatically processes CSV files located in the `src/main/resources/data` directory. The scheduled CsvScheduledTask
  runs every 24 hours, it checks for new files in the directory and calls the DataImportService to process them. Discount and price files are processed and stored in the database. The names of all processed files are also stored in the database to avoid reprocessing.

### Automated Shopping Cart Cleanup

- The CartCleanupService is a scheduled task that runs every 24 hours. It checks for users who have not updated their shopping cart in the last 24 hours and empties their shopping cart. This helps to keep the database clean and free of unused data.

### Notifications (Adverts)

- The NotificationService is responsible for sending daily notifications to users. The notifications contain the best price for each product in the user's shopping cart. The notifications are sent to the console, but in a production environment, you would typically send them to a messaging system or a notification service.

## Demo

Click the image below to watch the video:

[![Watch the video](https://img.youtube.com/vi/i6p14KRKJX4/1.jpg)](https://www.youtube.com/watch?v=i6p14KRKJX4)

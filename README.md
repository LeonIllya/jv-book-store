#              📚 Online Book Store 📚
___
Welcome to BookStore — your perfect destination for all book lovers📱. 
We've designed this platform to make it easy for you to find🔎, purchase📋, 
and organize your book collection🛒. Whether you read occasionally or 
devour books one after another, BookStore📖 simplifies the process and 
brings your new favorite reads right to you.
___
## 🔧 Technologies and tools used
This project is built using a robust technology stack designed to ensure
reliability, scalability, and security. Here’s a brief overview of 
the technologies that power BookStore:

- **Java:** 17 version
- **Spring Boot:** Simplifies the development and deployment process.
- **Spring Security:** Provides comprehensive security for your application.
- **Spring Data JPA:** Manages and accesses data with ease.
- **JWT:** Handles secure authentication and authorization.
- **Lombok:** Reduces boilerplate code with annotations.
- **MapStruct:** Automates the mapping of Java beans.
- **Swagger:** Enhances API documentation and testing.
- **MySQL:** Reliable relational database management system.
- **Liquibase:** Manages database schema changes.
- **Docker:** Streamlines development and deployment in isolated environments.
- **Docker Testcontainers:** Facilitates integration testing with a MySQL container.
___
## How to Run the Project
___

### Prerequisites:
- **Java 17** or higher
- **Maven** for dependency management
- **Docker** and **Docker Compose** for setting up the environment

### Steps to Launch Application:

1. **Clone the repository:**
   ```bash
   git clone git@github.com:your-username/jv-book-store.git
   cd jv-book-store
   
2. **Set up the environment by creating a ```.env``` file with the following variables:**
   ```
   MYSQLDB_USER=<your_username>
   MYSQLDB_PASSWORD=<your_password>
   MYSQLDB_DATABASE=<your_database_name>
   MYSQLDB_LOCAL_PORT=<your_local_port>
   MYSQLDB_DOCKER_PORT=<your_docker_port>
   
   SPRING_LOCAL_PORT=<your_spring_local_port>
   SPRING_DOCKER_PORT=<your_spring_docker_port>
   DEBUG_PORT=<your_debug_port>
    ```
3. **Build and start the containers using Docker Compose:**
    ```
    docker-compose up --build
   ```
4. The application will be accessible at ```http://localhost:<YOUR_PORT>/api```.

### Running Tests:
   To run unit and integration tests using Testcontainers, execute:
   ```
   mvn clean test
   ```

## 💻 Project endpoints:
___
### 🔑 Authentication Controller - endpoints for user registration and authentication of registered users. HTTP method Endpoint Description

| HTTP method |       Endpoint        |           Description           |
|:-----------:|:---------------------:|:-------------------------------:|
|    POST     | `/auth/registration ` |       Register a new user       |
|    POST     |    `/auth/login `     |   Login as a registered user    |

---
### 📔 BookController - endpoints for managing books.
| HTTP method |    Endpoint    | Role  |    Description    |
|:-----------:|:--------------:|:-----:|:-----------------:|
|     GET     |   `/books `    |  ALL  |   Get all books   |
|     GET     | `/books/{id}`  |  ALL  |  Get book by Id   |
|    POST     |   `/books `    | ADMIN |  Save a new book  |
|   DELETE    | `/books/{id} ` | ADMIN | Delete book by Id |
|     PUT     | `/books/{id} ` | ADMIN | Update book by Id |

---
### 📑 Category Controller - endpoints for managing categories.
| HTTP method |    Endpoint    | Role  |           Description            |
|:-----------:|:--------------:|:-----:|:--------------------------------:|
|     GET     |   `/categories `    |  ALL  | Get all categories |
|     GET     | `/categories/{id}`  |  ALL  |        Get category by Id        |
|     GET     | `/categories/{id}/books`  |  ALL  |   Get all books by category Id   |
|    POST     |   `/categories`    | ADMIN |         Save a new category        |
|   DELETE    | `/categories/{id} ` | ADMIN |        Delete category by Id         |
|     PUT     | `/categories/{id} ` | ADMIN |        Update category by Id         |

---
### 🛒 Shopping Cart Controller - endpoints for managing shopping cart.
| HTTP method |              Endpoint              | Role  |           Description            |
|:-----------:|:----------------------------------:|:-----:|:--------------------------------:|
|    POST     |             `/cart `             |  USER  | Add book to shopping cart |
|     GET     |     `/cart`    |  USER  |       Get shopping cart with books        |
|   DELETE    | `/cart/cart-items/{cartItemId}` | USER |       Delete book from shopping cart    |
|     PUT     |          `/cart/cart-items/{cartItemId}`          | USER |       Update book quantity in shopping cart        |

---
### 📜 Order Controller - endpoints for managing orders.

| HTTP method |              Endpoint              | Role  |           Description            |
|:-----------:|:----------------------------------:|:-----:|:--------------------------------:|
|     GET     |             `/orders `             |  USER  | Get order history |
|     GET     |     `/orders/{orderId}/items`      |  USER  |        Get order items by order id        |
|     GET     | `/orders/{orderId}/items/{itemId}` | USER |        Get order item by order and item id    |
|    POST     |          `/orders`          | USER |        Place a new order and clear the cart        |
|    PATCH    |          `/orders/{id} `           | ADMIN |       Update order status       |

___
## 🌟 Challenges and Solutions

One of the key challenges was integrating various components into a unified system. 
Ensuring smooth interaction between all the technologies required careful planning and testing. 
By applying a systematic approach and leveraging my experience, I successfully overcame 
these difficulties and created a stable and functional platform.

___
## 🌟 Possible improvements

Here are some ideas for enhancing BookStore:

- Enhanced User Experience: Continuously refine the interface and functionality based on user feedback.
- Personalized Recommendations: Implement algorithms to suggest books based on user preferences.
- Mobile Optimization: Ensure full responsiveness across all devices.
- Community Features: Add forums or book clubs to engage users more deeply.

---
## 🎥 Video Demonstration
Watch a 2-4 minute demo of BookStore in action.

[![project work](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR824tdHrx6ANj1OF6TE59RZUrnVBpwe4vcZA&s)](https://www.loom.com/share/5b09a297332a4661bdd2e2b1d1484b05?sid=860c54d7-bac4-4354-9470-888949539ed8)
___

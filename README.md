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
## 💪 Challenges and Solutions

Understanding the project's architecture: this was my first experience working on a project of this scale. 
It was challenging to bring all the elements together, grasp the connections between them, 
and integrate them effectively. However, through persistent effort, extensive research, 
and consultations with experienced professionals, I was able to develop an appropriate structure. 
Additionally, working with Spring Security was a particularly tough challenge. Despite its complexity, 
I managed to understand its configuration for application integration. 
Although it was difficult, I must admit that creating this project was an incredibly rewarding experience. 
I'm looking forward to further opportunities to practice and refine my skills.

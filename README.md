# Library Management System

A backend-only Library Management System built using Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, and MySQL.

The system allows library members to register, login, borrow books, return books, and manage overdue fines through secure REST APIs.

## Features

- Member registration and login
- JWT-based authentication and authorization
- Password encryption using BCrypt
- Book management
- Track total and available book quantities
- Members can borrow books only when available quantity is greater than 0
- Available quantity decreases when a book is borrowed
- Available quantity increases when a book is returned
- Every borrowed book has a return deadline
- Fine is calculated when a book is returned after the deadline
- Members cannot borrow books if their outstanding fine exceeds a predefined limit

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- BCrypt

## Authentication

The application uses JWT-based authentication with Spring Security.

The authentication flow is:

Register → Login → JWT Generated → Send JWT → Validate JWT → Access Protected API

The JWT token must be sent with protected requests using the Authorization header:

Authorization: Bearer <JWT_TOKEN>

## Borrowing Logic

A member can borrow a book only when the available quantity is greater than 0.

Available Quantity > 0
↓
Borrow Book
↓
Available Quantity - 1

If the available quantity is 0, the borrowing request is rejected.

Available Quantity = 0
↓
Cannot Borrow

Whenever a member borrows a book, a borrowing record is created containing the borrow date and return deadline.

## Fine System

Every borrowed book has a deadline.

If the member returns the book after the deadline, a fine is calculated based on the number of overdue days.

Fine = Overdue Days × Fine Per Day

Example:

Deadline: 28-Aug-2026
Return Date: 31-Aug-2026
Overdue Days: 3
Fine Per Day: ₹10
Total Fine: ₹30

## Borrowing Restriction

The system has a predefined maximum outstanding fine limit.

If the member's outstanding fine exceeds this limit, the member will not be allowed to borrow another book.

Outstanding Fine <= Fine Limit
↓
Borrow Allowed

Outstanding Fine > Fine Limit
↓
Borrow Blocked

Once the member clears the outstanding fine and becomes eligible again, borrowing will be allowed.

## Main Entities

### Member

Stores library member information such as:

- ID
- Name
- Email
- Encrypted Password
- Role

### Book

Stores book information such as:

- ID
- Title
- Author
- Total Quantity
- Available Quantity

### Borrow

Stores borrowing information such as:

- ID
- Member
- Book
- Borrow Date
- Deadline
- Return Date
- Fine
- Return Status

## API Endpoints

### Authentication

POST /auth/register

POST /auth/login

### Books

POST /books

GET /books

GET /books/{id}

### Borrowing

POST /borrows/{bookId}

GET /borrows/my

POST /borrows/{borrowId}/return

## Security

Spring Security is used to protect the application.

Passwords are encrypted using BCryptPasswordEncoder.

JWT is used to authenticate users for protected endpoints.

Main security components:

- JwtService
- JwtAuthenticationFilter
- CustomUserDetailsService
- SecurityConfiguration

## Project Structure

src/main/java/com/example/library

├── Controller
│   ├── AuthController
│   ├── MemberController
│   ├── BookController
│   └── BorrowController
│
├── Service
│   ├── AuthService
│   ├── MemberService
│   ├── BookService
│   └── BorrowService
│
├── Repository
│   ├── MemberRepository
│   ├── BookRepository
│   └── BorrowRepository
│
├── Entity
│   ├── Member
│   ├── Book
│   └── Borrow
│
├── DTO
│   ├── MemberRequestDto
│   ├── LoginRequestDto
│   ├── BookRequestDto
│   └── BorrowResponseDto
│
├── Security
│   ├── JwtService
│   ├── JwtAuthenticationFilter
│   ├── CustomUserDetailsService
│   └── SecurityConfiguration
│
└── LibraryApplication

## Database Configuration

Configure the MySQL database in application.properties.

spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your-secret-key
jwt.expiration=86400000

Create the database using:

CREATE DATABASE library_db;

## How to Run

Clone the repository:

git clone <repository-url>

Open the project in IntelliJ IDEA, Eclipse, STS, or VS Code.

Configure the MySQL database and application.properties.

Build the project:

mvn clean install

Run the application:

mvn spring-boot:run

The application will run on:

http://localhost:8080

## Testing Flow

The complete application flow can be tested using Postman or any REST API client.

Register Member
↓
Login
↓
Get JWT Token
↓
Send JWT with Protected Requests
↓
View Books
↓
Borrow Book
↓
Check Borrow Details
↓
Return Book
↓
Calculate Fine if Overdue
↓
Apply Borrowing Restriction if Fine Exceeds Limit

## Future Improvements

- Admin role and admin dashboard
- Book search and filtering
- Book categories
- Book reservation system
- Email notifications for approaching deadlines
- Online fine payment
- Swagger/OpenAPI documentation
- Pagination and sorting
- Unit and integration testing
- Docker support

## License

This project is created for educational and portfolio purposes.
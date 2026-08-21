# Library Management System

A backend-only Library Management System built using Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, and MySQL.

The system allows library members to register, login, borrow books, return books, and manage overdue fines through secure REST APIs.

## Features

- Member registration and login
- JWT-based authentication 
- Password encryption using BCrypt
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

## License

This project is created for educational and portfolio purposes.
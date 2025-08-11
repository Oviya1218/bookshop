# Pahana Edu Bookshop

---
## About
The aim of this project is to develop and implement an easy-to-use, secure, and effective computerized billing system for maintaining customers' accounts for Pahana Edu, the best bookshop in Colombo City. The system will replace the current manual procedure with a efficient Java-based, menu-driven application that will maintain the customer records, item information, and billing details effectively.  
<img width="827" height="373" alt="image" src="https://github.com/user-attachments/assets/1bd544ec-b73e-4c6d-bc14-234b0b12dba3" />

---

## Features

- 📖 Browse, and purchase books
- 👤 User registration and login system
- 🛒 Select books with quantity and buy online
- 🧾 Generate printable payment receipts (PDF)
- 🧑‍💼 Admin panel for book, order and user management
  - Add, update, or remove books
  - Adjust quantity and prices
  - Manage users

---

## Technologies Used

- **Java 8** with Servlets  
- **Maven** for build and dependency management  
- **Apache Tomcat** as the web server  
- **MySQL** and **PostgreSQL** as databases  
- **iTextPDF** for PDF generation  
- **JUnit 5** and **Mockito** for testing  
- **Selenium** for browser automation testing  

---

###  🗃️ Dummy Database Initialization

STEP 1: Open MySQL Workbench

STEP 2: Login

STEP 3: Copy paste the following MySql Commands and execute

```MySQL
create database if not exists onlinebookstore;

use onlinebookstore;

create table if not exists books(barcode varchar(100) primary key, name varchar(100), author varchar(100), price int, quantity int);

create table if not exists users(username varchar(100) primary key,password varchar(100), firstname varchar(100),
    lastname varchar(100),address text, phone varchar(100),mailid varchar(100),usertype int);
    
-- Table to store the order header (who placed, how much, when)
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    total_amount DOUBLE,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
);
CREATE TABLE IF NOT EXISTS completed_orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    total_amount DOUBLE,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE IF NOT EXISTS completed_order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    book_barcode VARCHAR(100),
    quantity INT,
    price DOUBLE,
    FOREIGN KEY (order_id) REFERENCES completed_orders(order_id),
    FOREIGN KEY (book_barcode) REFERENCES books(barcode)
);

-- Table to store each item in the order
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    book_barcode VARCHAR(100),
    quantity INT,
    price DOUBLE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (book_barcode) REFERENCES books(barcode)
);

insert into books values('9780134190563','The Go Programming Language','Alan A. A. Donovan and Brian W. Kernighan',400,8);
insert into books values('9780133053036','C++ Primer','Stanley Lippman and Josée Lajoie and Barbara Moo',976,13);
insert into books values('9781718500457','The Rust Programming Language','Steve Klabnik and Carol Nichols',560,12);
insert into books values('9781491910740','Head First Java','Kathy Sierra and Bert Bates and Trisha Gee',754,23);
insert into books values('9781492056300','Fluent Python','Luciano Ramalho',1014,5);
insert into books values('9781720043997','The Road to Learn React','Robin Wieruch',239,18);
insert into books values('9780132350884','Clean Code: A Handbook of Agile Software Craftsmanship','Robert C Martin',288,3);
insert into books values('9780132181273','Domain-Driven Design','Eric Evans',560,28);
insert into books values('9781951204006','A Programmers Guide to Computer Science','William Springer',188,4);
insert into books values('9780316204552','The Soul of a New Machine','Tracy Kidder',293,30);
insert into books values('9780132778046','Effective Java','Joshua Bloch',368,21);
insert into books values('9781484255995','Practical Rust Projects','Shing Lyu',257,15);
insert into users values('demo','demo','Demo','User','Demo Home','42502216225','demo@gmail.com',2);
insert into users values('Admin','Admin','Mr.','Admin','Haldia WB','9584552224521','admin@gmail.com',1);

commit;
```
STEP 4: create a new sql tab for executing queries and copy pase below code and execute

```MySQL
ALTER TABLE completed_orders
ADD COLUMN status VARCHAR(20) DEFAULT 'pending';

```
---

## ====  Importing and Running The Project Through Eclipse EE ====

Step 0: Open Eclipse Enterprise Edition. 

Step 1: Click On File > Import > Git > Projects From Git > Clone Uri > Paste The Repository Url as:
```https://github.com/Oviya1218/bookshop``` > Select master Branch > Next > Next > Finish.

Step 2: Right Click on Project > Run as > Maven Build > In the goals field enter "clean install" > apply > run

Step 3: [Only If Tomcat Server is not configured in Eclipse] : Right Click On Project > Run As > Run On Server > Select Tomcat V8.0 > (Select Tomcat V8.0 Installation Location If Asked) Next > Add onlinebookstore > Finish.

Step 4: Right Click on Project > Run as > Run on server

### == Login details ==
---
** Admin **
---
username :- Admin
password :- Admin

---
 ** User **
---
username :- demo
password :- demo

---
# Thank You


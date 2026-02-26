CREATE DATABASE OrderDB;
USE OrderDB;
CREATE TABLE Customers (
    customer_id INT PRIMARY KEY,
    customer_name VARCHAR(100),
    email VARCHAR(100)
);
CREATE TABLE Products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100),
    price DECIMAL(10,2)
);
CREATE TABLE Orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    quantity INT,
    order_date DATE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
INSERT INTO Customers VALUES (1, 'Rahul', 'rahul@email.com');
INSERT INTO Customers VALUES (2, 'Anita', 'anita@email.com');
INSERT INTO Products VALUES (101, 'Laptop', 60000);
INSERT INTO Products VALUES (102, 'Phone', 20000);
INSERT INTO Products VALUES (103, 'Headphones', 2000);
INSERT INTO Orders VALUES (1, 1, 101, 1, '2026-02-01');
INSERT INTO Orders VALUES (2, 1, 103, 2, '2026-02-02');
INSERT INTO Orders VALUES (3, 2, 102, 1, '2026-02-03');
INSERT INTO Orders VALUES (4, 2, 103, 3, '2026-02-05');
SELECT 
    c.customer_name,
    o.order_id,
    p.product_name,
    p.price,
    o.quantity,
    (p.price * o.quantity) AS total_amount,
    o.order_date
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
JOIN Products p ON o.product_id = p.product_id
ORDER BY o.order_date DESC;
SELECT *
FROM (
    SELECT 
        o.order_id,
        c.customer_name,
        (p.price * o.quantity) AS total_amount
    FROM Orders o
    JOIN Customers c ON o.customer_id = c.customer_id
    JOIN Products p ON o.product_id = p.product_id
) AS OrderTotals
WHERE total_amount = (
    SELECT MAX(p.price * o.quantity)
    FROM Orders o
    JOIN Products p ON o.product_id = p.product_id
);
SELECT customer_name
FROM Customers
WHERE customer_id = (
    SELECT customer_id
    FROM Orders
    GROUP BY customer_id
    ORDER BY COUNT(order_id) DESC
    LIMIT 1
);

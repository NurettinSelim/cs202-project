# CS 202: Semester Project

**Fall 2024**

## Important Information

- **Project Groups Sheet:** [C202 - Fall 2024 - Project Group](#)

### Deadlines

- **Part 1:** 4th of December 2024, 11:59 PM
- **Part 2:** 18th of December 2024, 11:59 PM

> **Note:** The deadlines are very strict and it will **NOT** be changed under any conditions.

You can submit your project with **ONLY 3 DAYS delay**, but be aware that for each day (even 1 minute past the deadline) you will receive a **PENALTY (-10)** for each day.

**Friendly Reminder:** On the Internet, there exist similar projects. Copying and submitting any of this work will result in a **Penalty (-100)** without any further discussion or considerations.

There will be a demo session (Day, Time, and Location will be announced), where you are required to present your work in about 15 minutes and answer some questions in person (**NO ONLINE DEMO SESSION**).

### TAs’ Email Addresses

- **Amin Alamdari:** [amin.alamdari@ozu.edu.tr](mailto:amin.alamdari@ozu.edu.tr)
- **Betul Seyhan:** [betul.seyhan@ozu.edu.tr](mailto:betul.seyhan@ozu.edu.tr)
- **Tuğçe Ozgirgin:** [tugce.ozgirgin@ozu.edu.tr](mailto:tugce.ozgirgin@ozu.edu.tr)

---

## Description

In this project, you are expected to design and develop a hotel management system using a MySQL database involving querying the database via Java JDBC. The application must include a console (terminal) menu-driven interface implemented using Java and JDBC for database interactions.

You should implement your database design by considering the functional dependencies (for instance, functional dependencies regarding Room Management, User Information, Booking Management, Housekeeping Schedule), and your database should be in **3NF**. For example, each room should have a unique Room ID that determines attributes like Room Type, Price, and Status, ensuring that all related information is consistently linked and managed. Remember that since this is a database course, you are expected to solve as many challenges as you can using SQL. **Hacky-Java based solutions will not be graded!!**

### Key Requirements for the Application

1. **Console (Terminal) Menu-Driven Interface:**
   - Your Java application must run on the console with a menu for all requirements of the project.
   - The menu should allow users to select various options, including:
     - Adding a new record (e.g., booking, user account).
     - Modifying or deleting records.
     - Viewing data (e.g., room availability, user information).
     - Exiting the application.
   - Each selection from the menu should prompt the user for the required inputs, perform the requested action, and then return to the main menu.
   - The process should continue until the user enters the specified number for EXIT.

   *Figure 1: An example of a well-designed Menu. Note that this is a simple example; your design should have other options.*

2. **Error Handling:**
   - The application should be user-friendly, ensuring that it completely handles all errors and exceptions.
   - There should be no run-time errors or unhandled exceptions.
   - Errors regarding connecting to the database must be handled, providing informative messages to users if connections fail.

3. **Database Design and Functionality:**
   - Your database design should be in **3NF**, taking into consideration the functional dependencies.
   - Implement relevant entities and endpoints using Java.
   - The hotel management system should include the following functionalities:
     - Separate user types (guest, administrator, receptionist, housekeeping).
     - Booking management, check-in, and check-out processes.
     - Room management by administrators.
     - Housekeeping schedules managed by receptionists.
   - Your design should utilize SQL queries for data management rather than hacky Java-based solutions.

4. **No Unauthorized Administrator Creation:**
   - Administrator accounts must be added only by the Database Manager or already be present in the DB.

5. **User Experience:**
   - Ensure the application provides clear prompts and feedback for user actions.
   - The console menu must clearly indicate available options and guide users appropriately.

6. **General Features of the Hotel Management System:**
   - Bookings, housekeeping schedules, different user types, room management, and payments.
   - Only administrators can create, delete, or modify rooms and their features.
   - Receptionists manage booking requests and confirm or deny them based on availability.
   - Housekeeping can view room availability and maintain cleanliness schedules, ensuring rooms are clean before being booked.

### Project Phases

The project consists of two phases:

- **Phase 1:**
  - Form your project groups and design and implement the database for this application.
  - You have to form groups of 2 for this project.
  - Your report should include:
    - An ER Diagram of your Database
    - Necessary DML and DDL statements
    - A brief explanation of your design decisions

- **Phase 2:**
  - Implement the back-end application.
  - Your application should connect and access the database.
  - Note that you should do your search on the hotel management system and add new features to this system.

### Design Report Requirements

Your design report requires the following tasks:

- Finding out the information requirements of the Hotel Management System. Determine the constraints and domains. Determine the entities and relations.
- Identifying the properties of the entities and their domains.
- Determining the identifier of each entity (i.e., find the primary key).
- Drawing the Entity-Relationship (E-R) diagram.
- Deciding what the base relations are.
- Drawing the Functional Dependency diagrams.
- Making sure your relations are in 3NF.
- Deciding what the referential integrity constraints are (identify foreign keys).
- Deciding which deletion integrity rules to use (restrict, set to NULL, or cascade).
- Considering frequent access, come up with a physical database schema.
- Create the database on a DBMS.
- Specifying DDL statements in SQL (internal, external, conceptual level).
- Specifying the queries (needed for the transactions you determined in step 1) using SQL.
- Write SQL insertion, deletion, modification, and select statements (DML statements).

In this project, you should create a new schema/database in your MySQL server and implement the entities and back-end Java application. The system should be implemented as the backend for a Hotel Management System application. You are responsible for designing a Hotel Management System. This system contains users, rooms, bookings, and housekeeping schedules. Each of these components has unique semantics, and part of these semantics are described below:

### Mandatory Requirements of the Hotel Management System

- **Separate User Types:** At least four user types:
  - Guest
  - Administrator
  - Receptionist
  - Housekeeping
- **Room Management:** Only Administrators can create, delete or modify a room with its features
- **Unique Rooms:** Each room should have a unique name and may have different types (e.g., one bed, two bed, family-sized). Your design should allow us to create different rooms.
- **Booking Capability:** Guests should be able to book rooms in any time frame for any amount of people depending on the availability of rooms. For example, the user can make a reservation for four people and can book two double-bed rooms or one room for four people if it is available.
- **Check-in and Check-out:**
  - Guests can check in if it is confirmed by a receptionist, and check out if they complete their payment.
  - They can make the payment in advance or while checking out.
- **Room Availability Management:**
  - Receptionists can list the availability of the rooms in the system and the bookings requested by the guests.
  - Receptionists can confirm room bookings or deny them according to booking requirements and available rooms.
- **No Overbooking:** The same room should not be overbooked. During the customer’s stay, there is no other reservation in that room.
- **Administrator Privileges:** Administrators can cancel any booking they want if the user hasn’t made any payment.
- **Administrator Account Management:** Administrator accounts must already be present in the DB, or a DB Manager should only be able to add them - in other words, you cannot create an Administrator account from the end-points.
- **Housekeeping Access:**
  - Housekeeping can only view room availability but nothing about guest information.
  - Housekeeping is scheduled by Receptionists and kept as a schedule.
  - Housekeeping cleans the rooms after the bookings and keeps track of cleaned rooms.
  - **Secret Check:** Only book clean rooms.


---

## Submission Guideline for Part 1

1. **Compress Files:**
   - Please compress all your files and submit a single zip file.

2. **Zip File Naming:**
   - The name of your zip file should look like the following:
     - `Group X Project Part 1.zip`

3. **Submission Structure:**
   - Your submission should have the following structure:
     - **Group X-Project Part 1.zip**
       - `Report.pdf`
       - `DDL.sql` (CREATE TABLE statements)
       - `DML.sql` (INSERT statements)

4. **Database Design Compliance:**
   - Your Database design must comply with **3NF**.

5. **Report:**
   - Write a report detailing your design decisions.
   - Your report **MUST** include:
     - An ER diagram representing your database design
     - Your functional dependencies
     - A description of your design decisions and why you made them
     - The names of the group members.

6. **Penalty for Non-compliance:**
   - Failing to comply with these guidelines will result in a **PENALTY (-5)** for each part.

7. **DDL File:**
   - Your submission must include a DDL file with relevant CREATE TABLE statements.

---

## Submission Guideline for Part 2

1. **Compress Files:**
   - Please compress all your files and submit a single zip file.

2. **Zip File Naming:**
   - The name of your zip file should look like the following:
     - `Group X Project Part 2.zip`

3. **Submission Structure:**
   - Your submission should have the following structure:
     - **Group X Project Part 2.zip**
       - `Report.pdf`
       - `DDL.sql` (CREATE TABLE statements)
       - `DML.sql` (INSERT statements)
       - A folder including your Java file(s)

4. **Report:**
   - Write a report detailing your design decisions regarding your code.
   - Your report **MUST** include:
     - An ER diagram representing your database design
     - Your functional dependencies
     - A description of your design decisions and why you made them (Part 1 report + your Java code)
     - The names of the group members.

5. **Penalty for Non-compliance:**
   - Failing to comply with these guidelines will result in a **PENALTY (-5)** for each part.

6. **DDL File:**
   - Your submission must include a DDL file with relevant CREATE TABLE statements.

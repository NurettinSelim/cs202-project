# Additional Notes

**CS 202: Semester Project**  
**Fall 2024**

## Important Information

**Project Groups Sheet:** C202 - Fall 2024 - Project Group

### Deadlines

- **Part 1:** 4th of December 2024, 11:59 PM
- **Part 2:** 18th of December 2024, 11:59 PM

The deadlines are very strict and it will **NOT** be changed under any conditions.

You can submit your project with **ONLY 3 DAYS** delay, but be aware that for each day (EVEN 1 MINUTE PAST THE DEADLINE) you will receive a **PENALTY (-10)** for each day.

**Friendly Reminder:** On the Internet, there exist similar projects. Copying and submitting any of this work will result in a **Penalty (-100)** without any further discussion or considerations.

There will be a demo session (Day, Time, and Location will be announced), during which you are required to present your work in about 15 minutes and answer some questions in person (**NO ONLINE DEMO SESSION**).

### TAs’ Email Addresses

- **Amin Alamdari:** amin.alamdari@ozu.edu.tr
- **Betul Seyhan:** betul.seyhan@ozu.edu.tr
- **Tuğçe Ozgirgin:** tugce.ozgirgin@ozu.edu.tr

## Important Considerations

- **Hotel Entity:**
  - There should be a Hotel entity with suitable attributes (e.g., `hotelID`, `hotelName`).
  - Each Hotel has its own Employees.
  - Multiple hotels are allowed.

- **Payments:**
  - You are required to track the payments.

- **ISA Relationship:**
  - We recommend using the ISA relationship where it is required.

- **User Types:**
  - There must be separate user types. **At least four user types:**
    - **Guest**
    - **Administrator**
    - **Receptionist**
    - **Housekeeping**

- **Project Requirements:**
  - Your project **MUST** satisfy the main project description, which is already uploaded and available on the LMS.

- **Java Application:**
  - Should have a main menu where the user chooses their type from some options.
  - Handle all possible errors, such as invalid input.
  - Include an **EXIT** option to terminate the program.

- **Secondary Menu:**
  - After selecting the user type, a secondary menu should pop up based on the requirements and capabilities of each user.
  - **Note:** No authentication is required; assume users are trustworthy.
  - The secondary menu should have relevant options for the specific user and an **EXIT** option to terminate the current menu and return to the initial menu.

### Menu Options

#### Guest Menu

- Add New Booking
- View Available Rooms
- View My Bookings
- Cancel Booking

#### Administrator Menu

- Add Room
- Delete Room (Be careful here! It is tricky!)
- Manage Room Status
- Add User Account
- View User Accounts
- Generate Revenue Report
- View All Booking Records
- View All Housekeeping Records
- View Most Booked Room Types
- View All the Employees with Their Roles

#### Receptionist Menu

- Add New Booking
- Modify Booking
- Delete Booking
- View Bookings
- Process Payment
- Assign Housekeeping Task
- View All Housekeepers Records and Their Availability

#### Housekeeping Menu

- View Pending Housekeeping Tasks
- View Completed Housekeeping Tasks
- Update Task Status to Completed
- View My Cleaning Schedule

### Important Notes

1. **ER Diagram:**
   - For drawing the ER Diagram, you can use tools such as Microsoft Excel, Draw.io, LucidChart, SmartDraw, or any other application or software you know.
   - Hand-written ER diagrams will **not** be accepted.

2. **ER Diagram Style:**
   - Your ER Diagram’s style **SHOULD (IS REQUIRED)** be based on what the professor taught you during lectures.
   - Other styles of ER Drawing are **NOT** accepted.

3. **Submission Guidelines:**
   - You **SHOULD (IS REQUIRED)** follow the Submission Guidelines for Part 1 and 2 (mentioned in the project’s main description).

4. **Error Handling:**
   - The application **SHOULD** be user-friendly, ensuring that it completely handles all errors and exceptions.
   - There should be no run-time errors or unhandled exceptions.
   - Errors regarding connecting to the database must be handled, providing informative messages to users if connections fail.
   - Each run-time error will result in a **Penalty (-5)**.

### Additional Requirements

The options mentioned above are the minimum requirements of this project. You are **REQUIRED** to add additional features and queries to this project.

Don’t worry, it is not all about penalties. We offer you some **Extra Points**:

- If you apply complicated and useful queries correctly, you will receive **Extra Points (+10)**.
- If you go further and design a window-based application, you will receive **Extra Points (+10)**.

Team Name: Technova
Team Members:
Member 1: Meenu Sara Jaimson - Amal Jyothi College Of Engineering
Member 2: Sophia Mary Joice - Amal Jyothi College Of Engineering

Hosted Project Link:
http://localhost:8000/#

Project Description:
EcoCraftConnect is a specialized e-commerce marketplace designed for the sustainable crafting community. 
It enables users to buy and sell upcycled materials, craft waste, and eco-friendly supplies through an intuitive, Amazon-inspired interface.

The Problem Statement:
Millions of tons of potentially usable craft waste (fabric scraps, glass jars, wood offcuts) end up in landfills because there is no centralized platform to connect "waste generators" with "creators." 
Hobbyists often struggle to find affordable, small-batch recycled materials for their projects.

The Solution
EcoCraftConnect provides a full-stack platform where sellers can easily list their scrap materials and buyers can search for specific eco-supplies.
By giving "waste" a second life, the project promotes a circular economy and reduces the environmental footprint of the crafting industry.

Technical Details:
Technologies/Components Used
For Software:
Languages used: Java (Backend), HTML5, CSS3, JavaScript (Frontend), SQL (Database)

Frameworks used: None (Built using native Java com.sun.net.httpserver for high performance and zero overhead)

Libraries used: Google Gson (JSON Parsing), MySQL Connector/J (Database Connectivity)

Tools used: Notepad/VS Code, Git, MySQL Server, Command Prompt/PowerShell

Features:
Amazon-Inspired UI/UX: A clean, responsive navigation bar with a centered search experience and dynamic product grids.

Real-time Search: Instantly filter marketplace listings based on user queries using SQL LIKE parameters.

Dual-View System: Seamless switching between the "Buyer Marketplace" and the "Seller Portal" using Single Page Application (SPA) logic.

Persistent Storage: A robust MySQL backend ensures that listed products remain available even after the server restarts.

Animated Transitions: Smooth CSS-keyframe animations for tab switching and product card loading to improve user engagement.

Implementation:
For Software:
Installation
1.Clone the Repository:
Bash
git clone https://github.com/yourusername/EcoCraftConnect.git
cd EcoCraftConnect
2.Setup Database:
Open MySQL Shell and run the commands found in database.sql.
3.Add Libraries:
Ensure gson-2.10.1.jar and mysql-connector-j-8.3.0.jar are placed in the /lib folder.

Run
Compile the Java Backend:
Bash
javac -cp "lib/*" Main.java
Start the Server:
Bash
java -cp "lib/*;." Main
Access the Site:
Open browser to http://localhost:8000

Project Documentation:
For Software:
<img width="1366" height="683" alt="Screenshot (67)" src="https://github.com/user-attachments/assets/62d59e79-0f4e-4ec9-9f6b-2ff574ea0cd9" />
<img width="1366" height="680" alt="Screenshot (68)" src="https://github.com/user-attachments/assets/9be60464-e41c-4dc8-b876-c940b7ca3eb0" />
<img width="1366" height="677" alt="Screenshot (69)" src="https://github.com/user-attachments/assets/b6747412-56f5-4b4a-8f4d-469ddefc6565" />
<img width="1366" height="677" alt="Screenshot (70)" src="https://github.com/user-attachments/assets/6a38df15-3341-4135-9dc3-908c15547566" />
<img width="1366" height="688" alt="Screenshot (71)" src="https://github.com/user-attachments/assets/4dd02386-0901-4aad-85cd-8f09a6c20426" />

Diagrams
System Architecture:
The system uses a Client-Server Architecture. The Frontend (HTML/JS) sends fetch requests to a Java-based API.
The Java server processes these requests and performs CRUD operations on a MySQL database, returning data in JSON format via the Gson library.

Application Workflow:
User enters the site -> Requests products -> Server fetches from MySQL ->User switches to "Sell" -> User submits form-> Server executes SQL INSERT -> Home view refreshes.

Additional Documentation:
API Documentation:
Base URL: http://localhost:8000

Endpoints:
GET /api/products
Description: Retrieves all products or filtered products based on search.
Parameters:
search (string): [Optional] The keyword to filter product names.

Response:

JSON
{
  "status": "success",
  "data": [
    { "name": "Scrap Fabric", "category": "Scrap", "price": 5.00, "img": "url" }
  ]
}
POST /api/sell

Description: Lists a new material in the marketplace.

Request Body:

JSON
{
  "name": "Oak Wood Planks",
  "category": "First-Hand",
  "price": 15.50,
  "img": "image_url_here"
}
Response:

JSON
{
  "status": "success",
  "message": "Product Listed Successfully"
}
Project Demo:
Video:
https://github.com/user-attachments/assets/a0cf7cdf-7a9f-4f46-ac30-a24bad732b93



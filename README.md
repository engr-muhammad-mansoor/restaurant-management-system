# Restaurant Management System

A comprehensive **Restaurant Management System** backend built with Spring Boot that enables restaurant owners to manage their restaurants, tables, and reservations efficiently. The system supports customer reservations with automated email notifications, payment processing, and OAuth2 authentication.

## 🚀 Features

### Authentication & Authorization
- **JWT-based Authentication**: Secure token-based authentication for API access
- **OAuth2 Integration**: Google OAuth2 login support
- **Role-based Access Control**: Separate roles for Owners and Customers
- **Email OTP Verification**: Account activation and password reset via OTP
- **Password Management**: Secure password change and reset functionality

### Restaurant Management
- Create, update, and delete restaurants
- Support for multiple restaurant types (Restaurant, Pub, Cafe)
- Restaurant search functionality with keyword filtering
- Pagination support for restaurant listings
- Active/inactive restaurant status management

### Table Management
- Add, update, and delete tables
- Configure table capacity and pricing (deposit amount, no-show fee)
- Check table availability for specific time ranges
- Get unreserved tables for a given time slot

### Reservation Management
- Create reservations with conflict detection
- Reservation status tracking (PENDING, CONFIRMED, CANCELLED)
- Search reservations by email or mobile number
- Filter reservations by restaurant, table, date range, and status
- Automated email notifications for reservations
- Payment/deposit processing
- No-show detection and management

### Additional Features
- **Email Service**: Automated emails for reservations, OTP, and password reset
- **RESTful API**: Well-structured REST endpoints
- **API Documentation**: Swagger/OpenAPI documentation available
- **Global Exception Handling**: Centralized error handling
- **Pagination**: Support for paginated results across all endpoints
- **CORS Configuration**: Configurable CORS settings

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2.8
- **Language**: Java 17
- **Database**: MySQL 8
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Authentication**: OAuth2 (Google)
- **Email**: Spring Mail (SMTP)
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Build Tool**: Maven
- **Other**: Lombok, Thymeleaf

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)
- **Email Account** (Gmail or SMTP server) for email functionality
- **Google OAuth Credentials** (optional, for OAuth2 login)

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd restaurant-management-system
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE reservation_app;
```

### 3. Configuration

Update the `src/main/resources/application.properties` file with your configuration:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/reservation_app
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
application.security.jwt.secret-key=your-secret-key
jwt.secret=your-jwt-secret

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# OAuth2 Configuration (Optional)
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the `BackendApplication.java` class directly from your IDE.

The application will start on `http://localhost:8082`

## 📚 API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8082/swagger-ui.html
```

The API documentation provides:
- Complete endpoint list
- Request/response schemas
- Authentication requirements
- Try-it-out functionality

## 🔐 API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register a new user (Owner)
- `POST /api/auth/authenticate` - Login and get JWT token
- `POST /api/auth/logout` - Logout and invalidate token
- `PUT /api/auth/forget-password` - Request password reset OTP
- `PUT /api/auth/reset-password` - Reset password with OTP
- `PUT /api/auth/activate-account` - Activate account with OTP
- `GET /api/auth/user/{userId}` - Get user details (Owner only)
- `PUT /api/auth/user/{userId}` - Update user details (Owner only)
- `PUT /api/auth/change-password/{userId}` - Change password (Owner only)

### Restaurants (`/api/restaurants`)
- `POST /api/restaurants` - Add a new restaurant (Owner only)
- `GET /api/restaurants?userId={id}` - Get restaurants by owner (Owner only)
- `GET /api/restaurants/all` - Get all active restaurants (Public)
- `GET /api/restaurants/search?keyword={keyword}` - Search restaurants (Public)
- `PUT /api/restaurants?restaurantId={id}` - Update restaurant (Owner only)
- `DELETE /api/restaurants?restaurantId={id}` - Delete restaurant (Owner only)

### Tables (`/api/tables`)
- `POST /api/tables` - Add a new table (Owner only)
- `GET /api/tables?restaurantId={id}` - Get tables by restaurant (Public)
- `GET /api/tables/unreserved?restaurantId={id}&startDateTime={}&endDateTime={}` - Get available tables (Public)
- `PUT /api/tables?tableId={id}` - Update table (Owner only)
- `DELETE /api/tables?tableId={id}` - Delete table (Owner only)

### Reservations (`/api/reservations`)
- `POST /api/reservations` - Create a new reservation (Public)
- `GET /api/reservations?email={email}` - Get reservations by email (Public)
- `GET /api/reservations?mobile={mobile}` - Get reservations by mobile (Public)
- `GET /api/reservations/{restaurantId}` - Get reservations by restaurant (Owner only)
- `GET /api/reservations/{restaurantId}/{tableId}` - Get reservations by restaurant and table (Owner only)
- `PUT /api/reservations?reservationId={id}` - Cancel reservation (Public)
- `POST /api/reservations/payment?reservationId={id}&amount={amount}` - Process payment (Public)

## 🗄️ Database Schema

### Entities

- **User**: Stores user information (Owners/Customers)
  - Roles: OWNER, CUSTOMER
  - OTP verification status
  - Password (encrypted)

- **Restaurant**: Restaurant details
  - Types: RESTAURANT, PUB, CAFE
  - Active/inactive status
  - Owner relationship

- **Table**: Table configuration
  - Capacity, deposit amount, no-show fee
  - Restaurant relationship

- **Reservation**: Reservation records
  - Status: PENDING, CONFIRMED, CANCELLED
  - Customer information
  - Time range (start/end datetime)
  - Prepayment status

- **UserOtp**: OTP storage for verification
- **Token**: JWT token management

## 🔒 Security

- JWT tokens for stateless authentication
- Password encryption using BCrypt
- Role-based access control (RBAC)
- CORS configuration
- SQL injection prevention via JPA
- XSS protection

## 📝 Usage Examples

### Register a New Owner

```bash
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "phoneNumber": "+1234567890",
  "address": "123 Main St",
  "role": "OWNER"
}
```

### Create a Restaurant

```bash
POST /api/restaurants
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Fine Dining Restaurant",
  "address": "456 Restaurant Ave",
  "phoneNumber": "+1234567891",
  "type": "RESTAURANT",
  "ownerId": 1
}
```

### Create a Reservation

```bash
POST /api/reservations
Content-Type: application/json

{
  "customerName": "Jane Smith",
  "customerEmail": "jane@example.com",
  "customerPhone": "+1234567892",
  "tableId": 1,
  "startDateTime": "2024-12-25T19:00:00",
  "endDateTime": "2024-12-25T21:00:00"
}
```

## 🧪 Testing

Run tests using Maven:

```bash
mvn test
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── restaurant/management/system/backend/
│   │       ├── controllers/     # REST Controllers
│   │       ├── services/        # Business Logic
│   │       ├── repositories/    # Data Access Layer
│   │       ├── entities/        # JPA Entities
│   │       ├── DTOs/            # Data Transfer Objects
│   │       ├── security/        # Security Configuration
│   │       ├── handling/        # Exception Handlers
│   │       └── utils/           # Utility Classes
│   └── resources/
│       ├── application.properties
│       └── templates/           # Thymeleaf Templates
└── test/                        # Test Files
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is open source and available under the MIT License.

## 👤 Author

**Your Name**
- Email: your.email@example.com
- LinkedIn: [Your LinkedIn Profile]
- GitHub: [Your GitHub Profile]

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- All contributors and open-source libraries used in this project

## 📞 Support

For support, email your.email@example.com or create an issue in the repository.

---

**Note**: Remember to update the `application.properties` file with your own configuration values before running the application. Never commit sensitive information like passwords, API keys, or JWT secrets to version control.


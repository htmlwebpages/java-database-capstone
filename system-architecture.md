Section1: Application Architecture
  This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two       
  databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate 
  repositories. MySQL uses JPA entities while MongoDB uses document models.

Section2: 
  1. User Interface Layer
    At the top of the architecture are the clients, which include browser-based users accessing Thymeleaf dashboards (Admin, Doctor, Patient) and external clients such as mobile apps or frontend modules consuming     REST APIs. This layer is responsible only for user interaction and request initiation.

  2. Controller Layer

    Requests from the UI layer enter the application through the Controller Layer.MVC Controllers handle browser requests and return Thymeleaf HTML views.REST Controllers handle API requests and return JSON           responses. Controllers act as request coordinators, performing input validation and delegating processing to services.

  3. Service Layer

    The Service Layer contains the core business logic of the system. It enforces business rules, manages workflows (such as validating doctor availability before booking appointments), and coordinates multiple       repository calls. This layer ensures separation of concerns, making the application maintainable and testable.

  4. Repository Layer

    The Service Layer interacts with the Repository Layer, which abstracts all database operations.Repositories are implemented using Spring Data, allowing developers to perform CRUD operations without writing        boilerplate SQL or database-specific code.

  5. Database Layer

    The system uses multiple databases, each optimized for specific needs:
      MySQL stores structured, relational data such as users, doctors, appointments, and roles.
      MongoDB stores flexible, document-based data like prescriptions that may evolve over time.
      This polyglot persistence approach improves scalability and flexibility.

  6. Model Binding

    Data retrieved from databases is mapped into Java model objects.
    MySQL data is mapped to JPA entities using @Entity.
    MongoDB data is mapped to document models using @Document.
    These models provide a consistent object-oriented representation across all layers.

  7. Response Flow

    Finally, the processed data flows back upward:
      In MVC flows, models are sent to Thymeleaf templates and rendered as HTML.
      In REST flows, models or DTOs are serialized into JSON and returned to clients.
      This completes the end-to-end request–response lifecycle.

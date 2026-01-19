# Smart Clinic System – Database Schema Design

This document describes the database design for the Smart Clinic System.
The system uses **MySQL** for structured relational data and **MongoDB** for flexible, document-based data.

---

## MySQL Database Design

Relational data such as users, doctors, and appointments require strong consistency, constraints, and relationships.  
Therefore, MySQL is used for core system entities.

---

### 1. patients Table

Stores patient account and profile information.

| Column Name     | Data Type        | Constraints                          |
|-----------------|------------------|--------------------------------------|
| patient_id      | BIGINT           | PRIMARY KEY, AUTO_INCREMENT          |
| name            | VARCHAR(100)     | NOT NULL                             |
| email           | VARCHAR(150)     | NOT NULL, UNIQUE                     |
| password        | VARCHAR(255)     | NOT NULL                             |
| phone           | VARCHAR(15)      | UNIQUE                               |
| created_at      | TIMESTAMP        | DEFAULT CURRENT_TIMESTAMP            |

**Justification:**  
Email is unique to avoid duplicate accounts. Password is stored as a hashed value.

---

### 2. doctors Table

Stores doctor profiles and professional details.

| Column Name       | Data Type        | Constraints                          |
|-------------------|------------------|--------------------------------------|
| doctor_id         | BIGINT           | PRIMARY KEY, AUTO_INCREMENT          |
| name              | VARCHAR(100)     | NOT NULL                             |
| specialization    | VARCHAR(100)     | NOT NULL                             |
| email             | VARCHAR(150)     | NOT NULL, UNIQUE                     |
| phone             | VARCHAR(15)      | UNIQUE                               |
| availability      | BOOLEAN          | DEFAULT TRUE                         |

**Justification:**  
Doctors are managed by admins and can mark availability without deleting profiles.

---

### 3. appointments Table

Manages appointment scheduling between patients and doctors.

| Column Name     | Data Type        | Constraints                          |
|-----------------|------------------|--------------------------------------|
| appointment_id  | BIGINT           | PRIMARY KEY, AUTO_INCREMENT          |
| patient_id      | BIGINT           | NOT NULL, FOREIGN KEY                |
| doctor_id       | BIGINT           | NOT NULL, FOREIGN KEY                |
| appointment_time| DATETIME         | NOT NULL                             |
| status          | VARCHAR(30)      | DEFAULT 'BOOKED'                     |

**Foreign Keys:**
- `patient_id` → patients(patient_id)
- `doctor_id` → doctors(doctor_id)

**Justification:**  
Foreign keys ensure data integrity between patients and doctors.

---

### 4. admin Table

Stores admin users responsible for managing the system.

| Column Name     | Data Type        | Constraints                          |
|-----------------|------------------|--------------------------------------|
| admin_id        | BIGINT           | PRIMARY KEY, AUTO_INCREMENT          |
| username        | VARCHAR(100)     | NOT NULL, UNIQUE                     |
| password        | VARCHAR(255)     | NOT NULL                             |
| role            | VARCHAR(50)      | DEFAULT 'ADMIN'                      |

**Justification:**  
Admins require separate authentication and authorization from doctors and patients.

---

## MongoDB Collection Design

MongoDB is used for flexible and evolving data that may contain nested structures.

---

### Collection: prescriptions

Stores prescription details generated after consultations.

**Why MongoDB?**
- Prescriptions vary in structure
- Supports nested medicines and dosage information
- Easy schema evolution

---

### Sample Document (JSON)

```json
{
  "_id": "65fa23ab91e4c1123a9f0021",
  "appointmentId": 1025,
  "patient": {
    "patientId": 12,
    "name": "John Doe"
  },
  "doctor": {
    "doctorId": 5,
    "name": "Dr. Smith",
    "specialization": "Cardiology"
  },
  "medications": [
    {
      "name": "Aspirin",
      "dosage": "75mg",
      "frequency": "Once daily",
      "duration": "7 days"
    },
    {
      "name": "Atorvastatin",
      "dosage": "10mg",
      "frequency": "Once nightly",
      "duration": "30 days"
    }
  ],
  "notes": "Monitor blood pressure regularly",
  "createdAt": "2025-01-10T10:30:00Z"
}

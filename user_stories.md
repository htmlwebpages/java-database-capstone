# User Stories

This document captures the user stories for the Hospital Management System, covering Admin, Patient, and Doctor roles.

---

## Admin User Stories

### US-ADM-01: Admin Login
**As an Admin**,  
I want to securely log in to the system,  
so that I can manage users and system configurations.

**Acceptance Criteria:**
- Admin can log in using valid credentials
- Invalid login attempts are rejected with an error message

---

### US-ADM-02: Manage User Access
**As an Admin**,  
I want to create, update, and deactivate doctor and patient accounts,  
so that only authorized users can access the system.

**Acceptance Criteria:**
- Admin can add new users
- Admin can activate or deactivate accounts
- Changes are reflected immediately

---

### US-ADM-03: View System Data
**As an Admin**,  
I want to view all appointments and user details,  
so that I can monitor system usage and performance.

**Acceptance Criteria:**
- Admin can view all registered doctors and patients
- Admin can view appointment statistics

---

## Patient User Stories

### US-PAT-01: Patient Registration
**As a Patient**,  
I want to register an account,  
so that I can book and manage medical appointments online.

**Acceptance Criteria:**
- Patient can register using valid details
- Duplicate email registrations are prevented

---

### US-PAT-02: Book Appointment
**As a Patient**,  
I want to book an appointment with a doctor,  
so that I can receive medical consultation.

**Acceptance Criteria:**
- Patient can view available doctors and time slots
- Appointment is confirmed only if the slot is available

---

### US-PAT-03: Manage Appointments
**As a Patient**,  
I want to view and cancel my appointments,  
so that I can manage my schedule effectively.

**Acceptance Criteria:**
- Patient can view upcoming and past appointments
- Patient can cancel future appointments

---

### US-PAT-04: View Medical Records
**As a Patient**,  
I want to view my prescriptions and medical history,  
so that I can track my health information.

**Acceptance Criteria:**
- Patient can view prescriptions linked to completed appointments

---

## Doctor User Stories

### US-DOC-01: Doctor Login
**As a Doctor**,  
I want to log in securely,  
so that I can manage my appointments and availability.

**Acceptance Criteria:**
- Doctor login is authenticated
- Unauthorized access is blocked

---

### US-DOC-02: Manage Availability
**As a Doctor**,  
I want to set and update my availability,  
so that patients can book appointments only when I am available.

**Acceptance Criteria:**
- Doctor can add, update, and remove available slots
- Changes are visible to patients in real time

---

### US-DOC-03: View Appointments
**As a Doctor**,  
I want to view my scheduled appointments,  
so that I can prepare for patient consultations.

**Acceptance Criteria:**
- Doctor can see daily and upcoming appointments
- Appointment details include patient information

---

### US-DOC-04: Add Prescription
**As a Doctor**,  
I want to add prescriptions after consultations,  
so that patients can access their treatment details.

**Acceptance Criteria:**
- Doctor can add prescription details
- Prescription is saved and visible to the patient

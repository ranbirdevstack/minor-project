






CREATE DATABASE IF NOT EXISTS medical_appointment_db;

USE medical_appointment_db;




CREATE TABLE IF NOT EXISTS users (

    user_id INT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,

    full_name VARCHAR(150) NOT NULL,

    contact_number VARCHAR(15),

    role VARCHAR(30) NOT NULL,

    password VARCHAR(255) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    availability_status BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

) ENGINE=InnoDB;



CREATE TABLE IF NOT EXISTS doctors (

    doctor_id INT AUTO_INCREMENT PRIMARY KEY,

    user_id INT NOT NULL UNIQUE,

    doctor_name VARCHAR(150) NOT NULL,

    specialization VARCHAR(100) NOT NULL,

    contact_number VARCHAR(15),

    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    working_hours VARCHAR(100),

    working_days VARCHAR(200) NOT NULL,

    availability_status BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_doctors_users
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

) ENGINE=InnoDB;



CREATE TABLE IF NOT EXISTS patients (

    patient_id INT AUTO_INCREMENT PRIMARY KEY,

    registered_by INT NOT NULL,

    full_name VARCHAR(150) NOT NULL,

    age INT NOT NULL,

    gender VARCHAR(20) NOT NULL,

    contact_number VARCHAR(15) NOT NULL,

    address TEXT,

    blood_group VARCHAR(5),

    registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_patients_registered_by
        FOREIGN KEY (registered_by)
        REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

) ENGINE=InnoDB;




CREATE TABLE IF NOT EXISTS appointments (

    appointment_id INT AUTO_INCREMENT PRIMARY KEY,

    patient_id INT NOT NULL,

    doctor_id INT NOT NULL,

    appointment_date DATE NOT NULL,

    appointment_time TIME NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'Pending',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointments_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_appointment_date (appointment_date),

    INDEX idx_appointment_doctor_date
        (doctor_id, appointment_date, appointment_time),

    INDEX idx_appointment_patient (patient_id)

) ENGINE=InnoDB;




CREATE TABLE IF NOT EXISTS prescriptions (

    prescription_id INT AUTO_INCREMENT PRIMARY KEY,

    appointment_id INT NOT NULL UNIQUE,

    patient_id INT NOT NULL,

    doctor_id INT NOT NULL,

    issue_date DATE NOT NULL,

    medicine_and_dosage TEXT NOT NULL,

    clinical_notes TEXT NOT NULL,

    CONSTRAINT fk_prescriptions_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_prescriptions_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_prescriptions_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_prescription_patient (patient_id),

    INDEX idx_prescription_doctor (doctor_id),

    INDEX idx_prescription_date (issue_date)

) ENGINE=InnoDB;



CREATE TABLE IF NOT EXISTS medical_history (

    history_id INT AUTO_INCREMENT PRIMARY KEY,

    patient_id INT NOT NULL,

    doctor_id INT NOT NULL,

    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    symptoms TEXT NOT NULL,

    diagnosis TEXT NOT NULL,

    treatment_notes TEXT NOT NULL,

    CONSTRAINT fk_history_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_history_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_history_patient (patient_id),

    INDEX idx_history_doctor (doctor_id),

    INDEX idx_history_recorded_at (recorded_at)

) ENGINE=InnoDB;




CREATE TABLE IF NOT EXISTS follow_up_recommendations (

    follow_up_id INT AUTO_INCREMENT PRIMARY KEY,

    appointment_id INT NOT NULL,

    patient_id INT NOT NULL,

    doctor_id INT NOT NULL,

    recommended_date DATE NOT NULL,

    instruction TEXT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'Pending',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_followup_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_followup_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_followup_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_followup_patient (patient_id),

    INDEX idx_followup_doctor (doctor_id),

    INDEX idx_followup_status (status)

) ENGINE=InnoDB;




CREATE TABLE IF NOT EXISTS medicine_reminders (

    reminder_id INT AUTO_INCREMENT PRIMARY KEY,

    patient_id INT NOT NULL,

    prescription_id INT NOT NULL,

    medicine_name VARCHAR(200) NOT NULL,

    dosage VARCHAR(100) NOT NULL,

    scheduled_at DATETIME NOT NULL,

    interval_hours INT NOT NULL,

    snooze_until DATETIME NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'Pending',

    CONSTRAINT fk_reminders_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_reminders_prescription
        FOREIGN KEY (prescription_id)
        REFERENCES prescriptions(prescription_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_reminder_patient (patient_id),

    INDEX idx_reminder_prescription (prescription_id),

    INDEX idx_reminder_scheduled (scheduled_at),

    INDEX idx_reminder_status (status)

) ENGINE=InnoDB;



CREATE TABLE IF NOT EXISTS medicine_dose_history (

    history_id INT AUTO_INCREMENT PRIMARY KEY,

    reminder_id INT NOT NULL,

    patient_id INT NOT NULL,

    medicine_name VARCHAR(200) NOT NULL,

    dosage VARCHAR(100) NOT NULL,

    scheduled_at DATETIME NOT NULL,

    given_at DATETIME NOT NULL,

    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_dose_history_reminder
        FOREIGN KEY (reminder_id)
        REFERENCES medicine_reminders(reminder_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_dose_history_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_dose_patient (patient_id),

    INDEX idx_dose_reminder (reminder_id),

    INDEX idx_dose_status (status)

) ENGINE=InnoDB;



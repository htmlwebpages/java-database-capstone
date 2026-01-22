import { API_BASE_URL } from "../../js/config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor';

export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);
        const data = await response.json();
        return data.doctors;    
    }
    catch (error) {
        console.error(error);
        return [];
    }
}

export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE",
        });
        const data = await response.json();
        return{
            success: data.success,
            message: data.message,
        };
    }
    catch (error) {
        console.error(error);
        return{
            success: false,
            message: "Failed to delete the doctor",
        };
    }
}

export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(doctor),
        });

        const data = await response.json();
        return{
            success: data.success,
            message: data.message,
        };
    }
    catch (error) {
        console.error(error);
        return{
            success: false,
            message: "Failed to save the doctor."
        };
    }
}

export async function filterDoctors(name, time, speciality) {
    try {
        const response = await fetch(`${DOCTOR_API}/filter/${name}/${time}/${speciality}`);
        if(response.ok) {
            const data = await response.json();
            return data;
        }
        else{
            console.error("Failed to filter doctors.");
            return {
                doctors: []
            };
        }
    }
    catch (error) {
        alert("Something went wrong while filtering the doctors!");
        return {
            doctors: []
        }
    }
}
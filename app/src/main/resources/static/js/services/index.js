import { openModal } from "../js/components/modals.js";
import { API_BASE_URL } from "../js/config/config.js";

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    if(adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }
    const doctorBtn = document.getElementById('doctorLogin');
    if(doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

window.adminLoginHandler = async function () {
    try {
        const username = document.getElementById("adminUsername").value;
        const password = document.getElementById("adminPassword").value;
        
        const admin = {username, password};

        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { "Content-Type": "application.json"},
            body: JSON.stringify(data),
        });

        if(response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole("admin");
        }
        else{
            alert("Invalid Credentials!!");
        }
    } 
    catch (error) {
        alert("Something went wrong. Please try again!")
    }
};

window.doctorLoginHandler = async function () {
    try {
        const username = document.getElementById("doctorUsername").value;
        const password = document.getElementById("doctorPassword").value;

        const doctor = {username, password};

        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data),
        });

        if(response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole("doctor");
        }
        else {
            alert("Invalid Credentials!!")
        }
    }
    catch(error) {
        alert("Something went wrong. Please try again!")
    }
};
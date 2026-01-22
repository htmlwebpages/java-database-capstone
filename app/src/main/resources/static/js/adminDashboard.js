import { openModal } from "../js/components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../js/services/doctorServices.js";
import { createDoctorCard } from "../js/components/doctorCard.js";

const addDoctorBtn = document.getElementById("addDocBtn");
if(addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => {
        openModal('addDoctor');
    })
}

windows.onload = function () {
    loadDoctorCards();
};

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";
        
        doctors.forEach((doctor) => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    }
    catch (error) {
        console.error("Error loading doctors", error);
    }
}

document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpeciality").addEventListener("change", filterDoctorsOnChange);

async function filterDoctorsOnChange() {
    try {
        const name = document.getElementById("searchBar").value || null;
        const time = document.getElementById("filterTime") || null;
        const speciality = document.getElementById("filterSpeciality") || null;
        
        const doctors = await filterDoctors(name, time, speciality);
        if(doctors && doctors.length > 0) {
            renderDoctorCards(doctors);
        }
        else{
            const contentDiv = document.getElementById("content");
            contentDiv.innerHTML = "<p>No Doctors found with the given filters</p>";
        }
    }
    catch (error) {
        alert("Error filtering doctors");
        console.error(error);
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    })
}

window.adminAddDoctor = async function () {
    const name = document.getElementById("docName").value;
    const email = document.getElementById("docEmail").value;
    const phone = document.getElementById("docPhone").value;
    const password = document.getElementById("docPassword").value;
    const speciality = document.getElementById("docSpeciality").value;
    
    const availability = [];
    document.querySelectorAll("input[name='availability']:checked")
        .forEach((checkbox) => {
            availability.push(checkbox.value);
        }
    );

    const token = localStorage.getItem("token");
    if(!token) {
        alert("Admin not authenticated!");
        return;
    }

    const doctor = {name, email, phone, password, speciality};
    try {
        const result = await saveDoctor();
        if(result.success) {
            alert(result.message);
            location.reload();
        }
        else {
            alert(result.message);
        }
    }
    catch (error) {
        alert("Failed to add doctor.");
        console.error(error);
    }
}
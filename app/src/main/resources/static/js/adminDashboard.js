import { openModal } from "../js/components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../js/services/doctorServices.js";
import { createDoctorCard } from "../js/components/doctorCard.js";

window.onload = function () {
    loadDoctorCards();
    document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
    document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
    document.getElementById("filterSpeciality")?.addEventListener("change", filterDoctorsOnChange);
};

async function loadDoctorCards() {
    try {
      const doctors = await getDoctors();
  
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";
  
      if (!Array.isArray(doctors) || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
        return;
      }
  
      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
  
    } catch (error) {
      console.error("Failed to load doctors:", error);
    }
  }
  

function renderDoctorCards(doctors = []) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (doctors.length === 0) {
        contentDiv.innerHTML = "<p>No Doctors found with the given filters</p>";
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

async function filterDoctorsOnChange() {
    try {
        const name = document.getElementById("searchBar")?.value || null;
        const time = document.getElementById("filterTime")?.value || null;
        const speciality = document.getElementById("filterSpeciality")?.value || null;

        const doctors = await filterDoctors(name, speciality, time);
        renderDoctorCards(doctors);
    } catch (error) {
        alert("Error filtering doctors");
        console.error(error);
    }
}

window.adminAddDoctor = async function () {
    const name = document.getElementById("doctorName").value;
    const email = document.getElementById("doctorEmail").value;
    const phone = document.getElementById("doctorPhone").value;
    const password = document.getElementById("doctorPassword").value;
    const speciality = document.getElementById("specialization").value;

    const availability = [];
    document.querySelectorAll("input[name='availability']:checked")
        .forEach(cb => availability.push(cb.value));

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Admin not authenticated!");
        return;
    }

    const doctor = { name, email, phone, password, speciality, availability };

    try {
        const result = await saveDoctor(doctor, token);
        alert(result.message);
        if (result.success) {
            location.reload();
        }
    } catch (error) {
        alert("Failed to add doctor.");
        console.error(error);
    }
};

window.adminAddDoctor = adminAddDoctor;
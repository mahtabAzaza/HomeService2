/* -------------------- تایمر -------------------- */

let timeLeft = 600; // 10 دقیقه = 600 ثانیه

const timerElement = document.getElementById("timer");

function startTimer() {
    const interval = setInterval(() => {

        if (timeLeft <= 0) {
            clearInterval(interval);
            alert("Time is up! Payment expired.");
            return;
        }

        let minutes = Math.floor(timeLeft / 60);
        let seconds = timeLeft % 60;

        timerElement.textContent =
            `Time left:$ {minutes}: $ {seconds < 10 ? "0" : ""} $ {seconds}`;

        timeLeft--;

    }, 1000);
}

/* -------------------- CAPTCHA -------------------- */

function loadCaptcha() {
    fetch("/captcha")
        .then(response => response.blob())
        .then(imageBlob => {
            const imageURL = URL.createObjectURL(imageBlob);
            document.getElementById("captchaImage").src = imageURL;
        });
}

/* -------------------- PAYMENT SUBMIT -------------------- */

function submitPayment() {

    const cardNumber = document.getElementById("cardNumber").value;
    const cvv2 = document.getElementById("cvv2").value;
    const expiry = document.getElementById("expiry").value;
    const password = document.getElementById("password").value;
    const captcha = document.getElementById("captchaInput").value;

    const data = {
        cardNumber,
        cvv2,
        expiry,
        password,
        captcha
    };

    fetch("/wallet/charge", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(res => res.text())
        .then(result => {
            alert(result);
        })
        .catch(error => {
            alert("Error: " + error);
        });
}

/* -------------------- INIT -------------------- */

document.addEventListener("DOMContentLoaded", function () {

    startTimer();
    loadCaptcha();

    document.getElementById("payBtn")
        .addEventListener("click", submitPayment);

});
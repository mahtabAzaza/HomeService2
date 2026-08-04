/* -------------------- STATE -------------------- */

let timeLeft = 600;
let timerExpired = false;
const token = new URLSearchParams(window.location.search).get("token");

/* -------------------- TIMER -------------------- */

function startTimer() {
    const timerEl = document.getElementById("timer");
    const interval = setInterval(() => {
        if (timeLeft <= 0) {
            clearInterval(interval);
            timerExpired = true;
            timerEl.textContent = "Time is up! Payment expired.";
            document.getElementById("payBtn").disabled = true;
            return;
        }
        const m = Math.floor(timeLeft / 60);
        const s = timeLeft % 60;
        timerEl.textContent = `Time left: ${m}:${s < 10 ? "0" : ""}${s}`;
        timeLeft--;
    }, 1000);
}

/* -------------------- SESSION / CAPTCHA -------------------- */

function loadSession() {
    if (!token) {
        setStatus("Invalid payment link: missing token.", "red");
        return;
    }

    fetch("/customer/payments/" + token)
        .then(res => {
            if (!res.ok) return res.text().then(t => { throw new Error(t); });
            return res.json();
        })
        .then(data => {
            document.getElementById("captchaImage").src =
                "data:image/png;base64," + data.captchaImage;
            document.getElementById("captchaInput").value = "";
            document.getElementById("amountDisplay").textContent =
                "Amount: " + data.amount;
            document.getElementById("payBtn").disabled = timerExpired;
        })
        .catch(err => setStatus(err.message || "Failed to load session.", "red"));
}

/* -------------------- HELPERS -------------------- */

function setStatus(msg, color) {
    const el = document.getElementById("statusMsg");
    el.textContent = msg;
    el.style.color = color || "#333";
}

/* -------------------- PAYMENT SUBMIT -------------------- */

function submitPayment() {
    if (timerExpired) {
        setStatus("Payment time has expired.", "red");
        return;
    }
    if (!token) {
        setStatus("Invalid payment link.", "red");
        return;
    }

    const cardNumber = document.getElementById("cardNumber").value.trim();
    const cvv2       = document.getElementById("cvv2").value.trim();
    const expiry     = document.getElementById("expiry").value.trim();
    const password   = document.getElementById("password").value;
    const captcha    = document.getElementById("captchaInput").value.trim();

    if (!cardNumber || !cvv2 || !expiry || !password || !captcha) {
        setStatus("Please fill in all fields.", "red");
        return;
    }

    document.getElementById("payBtn").disabled = true;
    setStatus("Processing...", "#555");

    fetch("/customer/payments/" + encodeURIComponent(token), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ cardNumber, cvv2, expiry, password, captcha })
    })
        .then(res => res.json().then(data => ({ ok: res.ok, data })))
        .then(({ ok, data }) => {
            if (ok) {
                setStatus("Wallet charged successfully!", "green");
            } else {
                setStatus(data.message || JSON.stringify(data), "red");
                document.getElementById("payBtn").disabled = false;
                loadSession();
            }
        })
        .catch(() => {
            setStatus("Network error. Please try again.", "red");
            document.getElementById("payBtn").disabled = false;
        });
}

/* -------------------- INIT -------------------- */

document.addEventListener("DOMContentLoaded", function () {
    startTimer();
    loadSession();

    document.getElementById("captchaImage").addEventListener("click", loadSession);
    document.getElementById("payBtn").addEventListener("click", submitPayment);
});

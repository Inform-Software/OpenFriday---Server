function ensureLogin() {
    if (getCookie("username").isEmpty()) {
        window.location.href = "/web/login.html";
        return false;
    }
    return true;
}

function ensureAdmin() {
    if (!ensureLogin()) {
        return;
    }
    axios
        .post("/rest/user/isadmin/" + getCookie("userID"))
        .then(function (response) {
            if (!response.data) {
                window.location.href = "/web/start.html";
            }
        })
}

function logout() {
    document.cookie = "username=''; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    window.location.href = "/";
}

function setCookie(name, value) {
    document.cookie = name + "=" + value + ";path=/;samesite=lax";
}

// returns the value of the cookie with name "name" if set or empty string if not
function getCookie(name) {
    const fullname = name + "=";
    const ca = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(fullname) === 0) {
            return c.substring(fullname.length, c.length);
        }
    }
    return "";
}

String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

const obj_to_map = ( obj => {
    const mp = new Map;
    Object.keys ( obj ). forEach (k => { mp.set(k, obj[k]) });
    return mp;
});

if (!getCookie("username").isEmpty()) {
    window.location.href = "/web/start.html";
}

let adminLogin = new Vue({
    el: "#admin-login-box",
    data: {
        isVisible: false,
        errormessage: ""
    },
    methods: {
        // tries to login an admin
        loginAdmin: function () {
            let username = $("#adminName").val();
            let password = $("#adminPassword").val();

            // return if any field is left empty
            if (username.isEmpty() || password.isEmpty())
                return;

            axios
                .post("/rest/login/admin",{
                    name: username,
                    password: $.md5(password)
                })
                .then(function(response){
                    console.log(response)
                    if (response.data){
                        setCookie("username", response.data.name.toLowerCase());
                        setCookie("userID", response.data.id);
                        window.location.href = "/web/start.html";
                    }
                })
                .catch(function (error) {
                    if (error.response && error.response.status === 418) {
                        adminLogin.errormessage = "Name oder Passwort falsch."
                    }
                    else {
                        console.log(error);
                    }
                })
        }
    }
});

let content = new Vue({
    el: "#content",
    data: {
        isDisabled: adminLogin.isVisible,
        userLoginErrorMessage: ""
    },
    methods: {
        // logs in a user
        loginUser: function () {
            let username = $("#name").val();

            if (username.isEmpty()) {
                return;
            }

            if (!username.match(/[a-zA-Z][a-zA-Z]+\.[a-zA-Z][a-zA-Z]+/)) {
                content.userLoginErrorMessage = "Der Name hat die falsche Form.";
                return;
            }

            axios
                .post("/rest/login/user/", {name: username})
                .then(function(response){
                    setCookie("username", response.data.name.toLowerCase());
                    setCookie("userID", response.data.id);
                    window.location.href = "/web/start.html";
                })
                .catch(function (error) {
                    if (error.response && error.response.status === 418) {
                        content.userLoginErrorMessage = "Name nicht verfügbar!";
                    }
                    else {
                        console.log(error);
                    }
                })
        }
    }
});

// toggles boolean controlling if adminLoginBox is shown
function toggleAdminLogin() {
    adminLogin.isVisible = !adminLogin.isVisible;
    content.isDisabled = adminLogin.isVisible;
    adminLogin.errormessage = "";
    $("#adminName").val("");
    $("#adminPassword").val("");
}
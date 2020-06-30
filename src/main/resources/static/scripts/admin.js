ensureAdmin();

const user = JSON.parse(getCookie("user"));

let content = new Vue({
    el: "#content",
    data: {
        workshopAmount: 0,
        currentPassword: "",
        newPassword: "",
        newPasswordAgain: "",
        errorMessage: "",
        successMessage: "",
        username: user.name
    },
    mounted() {
        axios
            .post("/rest/workshop/getall")
            .then(function (response) {
                content.workshopAmount = response.data.length;
            })

        if (this.username === "admin") {
            this.errorMessage = "Passwort kann für den Standard-Admin nicht geändert werden.";
        }
    },
    methods: {
        changePassword: function() {
            if (this.currentPassword.isEmpty() || this.newPassword.isEmpty() || this.newPasswordAgain.isEmpty()) {
                this.errorMessage = "Fülle alle Felder aus!";
                return;
            }

            if (this.newPassword !== this.newPasswordAgain) {
                this.errorMessage = "Die neuen Passwörter stimmen nicht überein!";
                return;
            }

            axios
                .post("/rest/user/changePassword/" + user.id, [$.md5(this.currentPassword), $.md5(this.newPassword)])
                .then(function (response) {
                    console.log(response)
                    if (response.data === true) {
                        content.errorMessage = "";
                        content.successMessage = "Passwort erfolgreich geändert.";
                    }
                    else {
                        content.errorMessage = "Das aktuelle Passwort ist falsch."
                    }
                    content.currentPassword = content.newPassword = content.newPasswordAgain = "";
                })
        }
    }
});
ensureAdmin();

let stompClient = null;
connect();

const username = getCookie("username");



// Websocket functions

function connect() {

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = () => {};

    stompClient.connect({}, onConnected, onError);
}


function onAdminMessageReceived(payload) {
    content.allAdmins = JSON.parse(payload.body);
}

function onConnected() {
    stompClient.subscribe('/topic/admins', onAdminMessageReceived);
}


function onError(error) {
    console.log("Websocket: Could not connect to server.")
}



// Vue instances

let content = new Vue({
    el: "#content",
    data: {
        allAdmins: "",
        username: username,
        isDisabled: false
    },
    mounted() {
        axios
            .post("/rest/user/getAdmins")
            .then(function (response) {
                content.allAdmins = response.data;
            })
    },
    methods: {
        deleteAdmin: function (id) {
            axios
                .post("/rest/user/deleteAdmin/" + id)
                .then(function (response) {
                    alert("Administrator gelöscht.");
                })
        }
    }
})

let adminBox = new Vue({
    el: "#admin-box",
    data: {
        isVisible: false,
        errormessage: "",
        name: "",
        password: "",
        passwordAgain: ""
    },
    methods: {
        addAdmin: function () {
            if (this.name.isEmpty() || this.password.isEmpty() || this.passwordAgain.isEmpty()) {
                this.errormessage = "Bitte fülle alle Felder aus!";
                return;
            }

            if (this.password !== this.passwordAgain) {
                this.errormessage = "Die Passwörter stimmen nicht überein!";
                return;
            }

            axios
                .post("/rest/user/addadmin", {
                    name: this.name,
                    password: $.md5(this.password)
                })
                .then(function (response) {
                    alert("Admin hinzugefügt.");
                    toggleAdminBox();
                })
        }
    }
})



// other functions

function toggleAdminBox() {
    adminBox.errormessage = "";
    adminBox.name = "";
    adminBox.password = "";
    adminBox.passwordAgain = "";
    adminBox.isVisible = !adminBox.isVisible;
    content.isDisabled = !content.isDisabled;
}
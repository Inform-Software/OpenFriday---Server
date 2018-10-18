function login() {
    var username = $("#username").val();
    console.log("Setting username to " + username);
    setCookie("name", username.toLowerCase(), 30);
    window.location.href ="/web/select.html";
}

var vm = new Vue({
    el: '#content',
    data: {
        hint: "",
        current: ""
    },
    mounted() {
        if(getCookie("name") !== "") {
            hint = "Du bist bereits eingeloggt. Klicke <a href=\"/web/select.html\">hier</a>, um deine Workshops zu wählen!";
            window.location.href = "/web/select.html";
            console.log("Cookie is set.");
        } else {
            console.log("Cookie is not set.");
            console.log(getCookie("name"));
        }
    }
})

$("#username").keyup(function(event) {
    if (event.keyCode === 13) {
        login();
    }
});
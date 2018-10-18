var stompClient = null;
var userdata = null;

if (getUserId() !== -1) {
    getUser(getUserId()).then(function (response) {
        userdata = response;
    });
}

function ensureLogin() {
    if(getCookie("name") === "") {
        window.location.href = "/web/index.html";
        return true;
    }
    return false;
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({
        'name': $("#name").val()
    }));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function getUserId() {
    var userId = getCookie("userId");
    if (userId != "") {
        return userId;
    }
    return -1;
}

function getCookie(cookieName) {
    var name = cookieName + "=";
    var decodedCookieList = decodeURIComponent(document.cookie);
    var cookieList = decodedCookieList.split(';');
    for (var i = 0; i < cookieList.length; i++) {
        var cookie = cookieList[i].trim().toString();
        if (!cookie.includes('=')) {
            console.warn("Invalid cookie");
            continue;
        }
        var parts = cookie.split('=', 2);
        if (parts[0] == cookieName) {
            return parts[1];
        }
    }
    return "";
}

function getWorkshop(id) {
    return axios
        .get("/rest/workshop/" + id)
        .then(function (response) {
            return response;
        })
        .catch(function (error) {
            return false;
        });
}

function getUser(id) {
    return axios
        .get("/rest/user/" + id)
        .then(function (response) {
            return response;
        })
        .catch(function (error) {
            return false;
        });
}

function fetchGetRequest(url) {
    return axios
        .get(url)
        .then(function (response) {
            return response;
        })
        .catch(function (error) {
            console.error(error);
            return false;
        });
}

function fetchPostRequest(url, params) {
    return axios
        .post(url, params)
        .then(function (response) {
            return response;
        })
        .catch(function (error) {
            console.error(error);
            return false;
        });
}

function fetchPutRequest(url, params) {
    return axios
        .put(url, params)
        .then(function (response) {
            return response;
        })
        .catch(function (error) {
            console.error(error);
            return false;
        });
}

function fetchDeleteRequest(url) {
    return axios
        .delete(url)
        .then(function (response) {
            return response;
        })
        .catch(function (error) {
            console.error(error);
            return false;
        });
}

function setCookie(name, value, days) {
    var date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    var expiration = "expires=" + date.toUTCString();
    document.cookie = name + "=" + value + ";" + expiration + ";path=/";
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });

});
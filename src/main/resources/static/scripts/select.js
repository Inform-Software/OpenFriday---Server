ensureLogin();

var vm = new Vue({
    el: '#content',
    data: {
        workshops: "",
        name: getCookie("name")
    },
    mounted() {
        axios
            .get("/rest/workshop")
            .then(function (response) {
                vm.workshops = response.data;
                window.setTimeout(colorElements, 50)
                $("#loading").fadeOut();
            })
    }
});

function handleClick(element) {
    var url = "/rest/workshop/" + $(element).attr('id') + "/vote/" + vm.name + "/1";
    console.log(url);
    axios
    .get(url)
    .then(function (response) {
        if(response === false) return;
        vm.workshops = response.data;
        window.setTimeout(colorElements, 50);
    });
}

function colorElements() {
    var w = document.getElementsByClassName("workshop");
    for(i = 0; i < w.length; i++) {
        var element = w[i];
        var selected = false;
        vm.workshops[i].votes.forEach(function(vote) {
            if(vote.name === vm.name) selected = true;
        });
        vm.workshops[i].selected = selected;
    }
}

$(".confirm").on('click', function() {
    window.location.href = "/web/workshops.html";
});
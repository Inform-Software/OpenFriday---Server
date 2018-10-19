ensureLogin();

var vm = new Vue({
    el: '#content',
    data: {
        timetable: "",
        status: ""
    }, 
    mounted() {
        axios
        .get("/rest/status")
        .then(function(response) {
            vm.status = response.data;
        });

        axios
        .get("/rest/timetable")
        .then(function(response) {
            $("#loading").fadeOut();
            vm.timetable = response.data;
        });
    }
});


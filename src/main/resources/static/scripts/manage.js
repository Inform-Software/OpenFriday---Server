ensureLogin();

var vm = new Vue({
    el: '#content',
    data: {
        workshops: "",
        name: getCookie("name").toLowerCase()
    },
    mounted() {
        refreshWorkshops();
    }
});

var input = new Vue({
    el: '#inputbox',
    data: {
        hideInput: true,
        intentIsCreate: true,
        id: 0,
        index: 0
    },
    mounted() {
        refreshWorkshops();
    }
});

function refreshWorkshops() {
    axios
        .get("/rest/workshop")
        .then(function (response) {
            vm.workshops = response.data;
        })
}

function createWorkshop(name, description, topic) {
    axios
        .put("/rest/workshop", {
            name: name,
            description: description,
            topic: topic,
            creator: vm.name
        }).then(function (response) {
            refreshWorkshops();
        })
}

function showWorkshopInput() {
    input.intentIsCreate = true;
    input.hideInput = false;
    $("#name").val("");
    $("#description").val("");
    $("#topic").val("");
}

function deleteWorkshop(id) {
    axios
        .delete("/rest/workshop/" + id)
        .then(function (response) {
            refreshWorkshops();
        });
}

function editWorkshop(element) {
    input.intentIsCreate = false;
    input.id = $(element).attr('id');
    input.index = $(element).attr('index');
    input.hideInput = false;

    var index = $(element).attr('index');
    $("#name").val(vm.workshops[index].name);
    $("#description").val(vm.workshops[index].description);
    $("#topic").val(vm.workshops[index].topic);
    return false; //Hide default context menu
}

function confirmDeleteWorkshop(element) {
    var id =  $(element).parent().attr('id');
    var result = confirm("Möchtest du diesen Workshop (ID:" + id + ") wirklich löschen?");
    if(result === true) {
        deleteWorkshop(id);
    }
}

function confirmInput() {
    var name = $("#name").val();
    var description = $("#description").val();
    var topic = $("#topic").val();
    if(input.intentIsCreate) {
        axios
        .put("/rest/workshop", {
            name: name,
            description: description,
            topic: topic,
            creator: vm.name
        })
        .then(function(response) {
            refreshWorkshops();
        })
    } else {
        axios
        .put("/rest/workshop/" + input.id, {
            name: name,
            description: description,
            topic: topic,
            creator: vm.name
        })
        .then(function(response) {
            refreshWorkshops();
        }) 
    }
    input.hideInput = true;
}

function closeInputbox() {
    input.hideInput = true;
}

$(document).keyup(function(e) {
    if (e.key === "Escape") { 
        if(!input.hideInput) {
            input.hideInput = true;
        }
   }
});

$("#description").keyup(function(event) {
    if (event.keyCode === 13) {
        confirmInput();
    }
});
ensureLogin();

let stompClient = null;
connect();
const username = getCookie("username");
const userID = getCookie("userID");

let content = new Vue({
    el: "#content",
    data: {
        username: username,
        userID: userID,
        admin: false,       // if the current user is an admin
        slots: "",          // all slots available
        userSlots: "",      // the slots the user is available
        // toggle: false,
        isDisabled: false,  // used to set the disabledDiv class on the html container
        workshops: "",      // list of all available workshops
        votes: ""           // all votes of the user
    },
    mounted() {
        axios
            .post("/rest/user/isadmin/" + userID)
            .then(function(response){
                content.admin = response.data;
            })
        axios
            .post("/rest/slot/getall")
            .then(function (response){
                content.slots = response.data;
            })
        axios
            .post("/rest/user/getslots/" + userID)
            .then(function (response) {
                content.userSlots = response.data;

                // set checkboxes checked
                updateCheckboxes();
            })
        axios
            .post("/rest/user/getvotes/" + userID)
            .then(function (response) {
                content.votes = obj_to_map(response.data);
            })
        axios
            .post("/rest/workshop/getall")
            .then(function (response) {
                content.workshops = response.data;
                for (let i = 0; i < content.workshops.length; i++) {
                    content.workshops[i].slots = content.workshops[i].slots.sort((a, b) => a.name.localeCompare(b.name))
                }
            })
    },
    methods: {
        saveUserSlots: function () {
            let slotIds = [];
            let checkboxes = document.getElementsByName("time-slot");
            for (let i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].checked) {
                    slotIds.push(checkboxes[i].id);
                }
            }
            axios
                .post("/rest/user/setslots/" + userID, slotIds)
                .catch(function () {
                    alert("Fehler: Zeiten konnten nicht gespeichert werden.")
                })
        },
        deleteWorkshop: function (workshop) {
            if (confirm("Möchtest du den Workshop\n" + workshop.name + "\nwirklich löschen?")) {
                axios
                    .post("/rest/workshop/delete/" + workshop.id)
                    .then(function () {
                        // stompClient.send("/app/ws/updateworkshops", {}, "");
                        alert("Workshop gelöscht!");
                    });
            }
        },
        openEditBox: function (workshop) {
            workshopBox.editing = true;
            workshopBox.workshopId = workshop.id;
            workshopBox.workshopName = workshop.name;
            workshopBox.workshopDescription = workshop.description;
            workshopBox.workshopSlots = workshop.slots;
            workshopBox.workshopCreator = workshop.creator;
            toggleWorkshopBox();

            // update checkboxes
            updateCheckboxesWorkshopBox();

        },
        voteForWorkshop: function (workshop) {
            console.log("Voting...")
            axios
                .post("/rest/user/vote/" + userID + "/" + workshop.id)
                .then(function (response) {
                    console.log("Voting done")
                    content.votes = obj_to_map(response.data);
                })
        }
    }
})

let workshopBox = new Vue({
    el: "#workshop-box",
    data: {
        isVisible: false,
        editing: false,
        workshopId: "",
        workshopName: "",
        workshopDescription: "",
        workshopCreator: "",
        slots: "",
        errormessage: "",
        workshopSlots: ""
    },
    created: function() {
        // get time slots from server
        axios
            .post("/rest/slot/getall")
            .then(function (response){
                workshopBox.slots = response.data;
            })
    },
    methods: {
        // Tells the server to add a new workshop with the data given in the popup-box
        addWorkshop: function () {

            if (this.workshopName.isEmpty()) {
                this.errormessage = "Bitte gib einen Namen für den Workshop an!";
                return;
            }
            if (this.workshopDescription.isEmpty()) {
                this.errormessage = "Bitte gib eine Beschreibung für den Workshop an!";
                return;
            }

            // get ids of selected timeslots
            let slotIds = [];
            let checkboxes = document.getElementsByName("time-slot-w");
            for (let i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].checked) {
                    let id = checkboxes[i].id.split(':')
                    slotIds.push(id[1]);
                }
            }

            if (!slotIds.length) {
                this.errormessage = "Bitte gib mindestens einen Zeitslot für den Workshop an!";
                return;
            }

            // get slots with selected ids
            let selectedSlots = [];
            for (let i = 0; i < this.slots.length; i++) {
                if ($.inArray(this.slots[i].id.toString(), slotIds) !== -1) {
                    // console.log("SlotID: " + this.slots[i].id);
                    selectedSlots.push(this.slots[i]);
                }
            }

            if (workshopBox.editing) {
                axios
                    .post("/rest/workshop/edit/" + this.workshopId, {
                        id: this.workshopId,
                        creator: this.workshopCreator,
                        name: this.workshopName,
                        description: this.workshopDescription,
                        slots: selectedSlots
                    })
                    .then(function () {
                        workshopBox.closeBox();
                    })
            }
            else {
                axios
                    .post("/rest/workshop/add", {
                        creator: username,
                        name: this.workshopName,
                        description: this.workshopDescription,
                        slots: selectedSlots
                    })
                    .then(function () {
                        workshopBox.closeBox();
                    })
            }
        },
        closeBox: function () {
            toggleWorkshopBox();
            // stompClient.send("/app/ws/updateworkshops", {}, "");
            alert("Workshop gespeichert!")
        }
    }
})

function toggleWorkshopBox() {
    if (workshopBox.isVisible) {
        workshopBox.workshopId = "";
        workshopBox.workshopName = "";
        workshopBox.workshopDescription = "";
        workshopBox.errormessage = "";
        workshopBox.editing = false;

        // reset checkboxes
        let checkboxes = document.getElementsByName("time-slot-w");
        for (let i = 0; i < checkboxes.length; i++) {
            checkboxes[i].checked = false;
        }
    }

    workshopBox.isVisible = !workshopBox.isVisible;
    content.isDisabled = workshopBox.isVisible;
}

/**
 * Getting the checkboxes in the mounted function directly will sometimes result in calling the getElementsByName function before the checkboxes are ready.
 * This results in an empty result and thus the loop is skipped.
 * This function is the workaround: if the result is empty, the function is called again at next animation frame (which is usually running at around 60Hz).
 */
function updateCheckboxes() {
    let checkboxes = document.getElementsByName("time-slot");
    if (!checkboxes.length) {
        window.requestAnimationFrame(updateCheckboxes);
    }else {
        for (let i = 0; i < checkboxes.length; i++) {
            for (let j = 0; j < content.userSlots.length; j++) {
                if (content.userSlots[j].id == checkboxes[i].id) {
                    checkboxes[i].checked = true;
                }
            }
        }
    }
}

function updateCheckboxesWorkshopBox() {
    let checkboxes = document.getElementsByName("time-slot-w");
    if (!checkboxes.length) {
        window.requestAnimationFrame(updateCheckboxesWorkshopBox);
    }else {
        for (let i = 0; i < checkboxes.length; i++) {
            for (let j = 0; j < workshopBox.workshopSlots.length; j++) {
                if (workshopBox.workshopSlots[j].id == checkboxes[i].id.split(':')[1]) {
                    checkboxes[i].checked = true;
                }
            }
        }
    }
}

function connect() {

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}


function onWorkshopMessageReceived(payload) {
    // console.log("Websocket: Received message from server.")
    content.workshops = JSON.parse(payload.body);
    for (let i = 0; i < content.workshops.length; i++) {
        content.workshops[i].slots = content.workshops[i].slots.sort((a, b) => a.name.localeCompare(b.name))
    }
}

function onConnected() {
    stompClient.subscribe('/topic/workshops', onWorkshopMessageReceived);
}


function onError(error) {
    console.log("Websocket: Could not connect to server.")
}
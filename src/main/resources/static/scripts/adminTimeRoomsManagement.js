ensureAdmin();

let stompClient = null;
connect();

const user = JSON.parse(getCookie("user"));



// Websocket functions

function connect() {

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}


function onSlotMessageReceived(payload) {
    content.slots = JSON.parse(payload.body);
    roomBox.slots = content.slots;
}

function onRoomMessageReceived(payload) {
    content.rooms = JSON.parse(payload.body);
    for (let i = 0; i < content.rooms.length; i++) {
        content.rooms[i].timeslots = content.rooms[i].timeslots.sort((a, b) => a.name.localeCompare(b.name))
    }
}

function onConnected() {
    stompClient.subscribe('/topic/slots', onSlotMessageReceived);
    stompClient.subscribe('/topic/rooms', onRoomMessageReceived);
}


function onError(error) {
    console.log("Websocket: Could not connect to server.")
}



// Vue instances

let content = new Vue({
    el: "#content",
    data: {
        slots: "",
        rooms: "",
        isDisabled: false
    },
    mounted() {
        axios
            .post("/rest/slot/getall")
            .then(function (response) {
                content.slots = response.data;
            })

        axios
            .post("/rest/room/getall")
            .then(function (response) {
                content.rooms = response.data;
                for (let i = 0; i < content.rooms.length; i++) {
                    content.rooms[i].timeslots = content.rooms[i].timeslots.sort((a, b) => a.name.localeCompare(b.name))
                }
            })
    },
    methods: {
        deleteSlot: function (slot) {
            if (confirm("Möchtest du den Slot\n" + slot.name + "\nwirklich löschen?")) {
                axios
                    .post("/rest/slot/delete/" + slot.id)
                    .then(function () {
                        // alert("Slot gelöscht!");
                        console.log("Raum gelöscht")
                    });
            }
        },
        deleteRoom: function (room) {
            if (confirm("Möchtest du den raum\n" + room.name + "\nwirklich löschen?")) {
                axios
                    .post("/rest/room/delete/" + room.id)
                    .then(function () {
                        // alert("Raum gelöscht!");
                        console.log("Raum gelöscht")
                    });
            }
        },
        editSlot: function (slot) {
            slotBox.slot = slot;
            slotBox.editing = true;
            slotBox.name = slot.name;
            toggleSlotBox();
        },
        editRoom: function (room) {
            roomBox.editing = true;
            roomBox.room = room;
            roomBox.name = room.name;
            roomBox.size = room.size + "";
            updateCheckboxes();
            toggleRoomBox();
        }
    }
})

let slotBox = new Vue({
    el: "#slot-box",
    data: {
        isVisible: false,
        editing: false,
        name: "",
        errormessage: "",
        slot: ""
    },
    mounted() {

    },
    methods: {
        addSlot: function () {
            if (slotBox.name.isEmpty()) {
                slotBox.errormessage = "Bitte gib einen Namen für den Slot an.";
                return;
            }
            if (!slotBox.name.match(/[0-9]+:[0-9][0-9][ ]*-[ ]*[0-9]+:[0-9][0-9]/)) {
                slotBox.errormessage = "Der Name hat nicht die richtige Form!";
                return;
            }

            if (slotBox.editing) {
                axios
                    .post("/rest/slot/add", {
                        id: this.slot.id,
                        name: this.name
                    })
                    .then(function () {
                        alert("Slot gespeichert.");
                        toggleSlotBox();
                    })
            }
            else {
                axios
                    .post("/rest/slot/add", {name: slotBox.name})
                    .then(function () {
                        alert("Slot hinzugefügt.");
                        toggleSlotBox();
                    })
            }
        }
    }

})

let roomBox = new Vue({
    el: "#room-box",
    data: {
        isVisible: false,
        editing: false,
        errormessage: "",
        name: "",
        size: "",
        slots: "",
        room: ""
    },
    mounted() {
        axios
            .post("/rest/slot/getall")
            .then(function (response) {
                roomBox.slots = response.data;
            })
    },
    methods: {
        addRoom: function () {
            if (roomBox.name.isEmpty() || roomBox.size.isEmpty()) {
                slotBox.errormessage = "Bitte fülle alle Felder aus.";
                return;
            }

            // get ids of selected timeslots
            let slotIds = [];
            let checkboxes = document.getElementsByName("time-slot");
            for (let i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].checked) {
                    slotIds.push(checkboxes[i].id);
                }
            }

            // get slots with selected ids
            let selectedSlots = [];
            for (let i = 0; i < this.slots.length; i++) {
                if ($.inArray(this.slots[i].id.toString(), slotIds) !== -1) {
                    // console.log("SlotID: " + this.slots[i].id);
                    selectedSlots.push(this.slots[i]);
                }
            }

            if (roomBox.editing) {
                axios
                    .post("/rest/room/add", {
                        id: this.room.id,
                        name: this.name,
                        size: this.size,
                        timeslots: selectedSlots
                    })
                    .then(function () {
                        alert("Raum gespeichert.");
                        toggleRoomBox();
                    })
            }
            else {
                axios
                    .post("/rest/room/add", {
                        name: roomBox.name,
                        size: roomBox.size,
                        timeslots: selectedSlots
                    })
                    .then(function () {
                        alert("Raum hinzugefügt.");
                        toggleRoomBox();
                    })
            }
        }
    }
})



// other functions

function toggleSlotBox() {
    if (slotBox.isVisible) {
        // reset inputs
        slotBox.name = "";
        slotBox.errormessage = "";
        slotBox.editing = false;
    }
    slotBox.isVisible = !slotBox.isVisible;
    content.isDisabled = !content.isDisabled;
}

function toggleRoomBox() {
    if (roomBox.isVisible) {
        // reset inputs
        roomBox.name = "";
        roomBox.size = "";
        roomBox.room = "";
        let checkboxes = document.getElementsByName("time-slot");
        for (let i = 0; i < checkboxes.length; i++) {
            checkboxes[i].checked = false;
        }
        roomBox.errormessage = "";
        roomBox.editing = false;
    }
    roomBox.isVisible = !roomBox.isVisible;
    content.isDisabled = !content.isDisabled;
}

function updateCheckboxes() {
    let checkboxes = document.getElementsByName("time-slot");
    if (!checkboxes.length) {
        window.requestAnimationFrame(updateCheckboxes);
    }else {
        for (let i = 0; i < checkboxes.length; i++) {
            for (let j = 0; j < roomBox.room.timeslots.length; j++) {
                if (roomBox.room.timeslots[j].id == checkboxes[i].id) {
                    checkboxes[i].checked = true;
                }
            }
        }
    }
}
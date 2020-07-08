ensureAdmin();

let stompClient = null;
connect();

let solverStatusInterval = null;

let content = new Vue({
    el: "#content",
    data: {
        plan: {
            workshops: [],
            timeslots: [],
            rooms: []
        },
        isSolving: false,
        time: 10
    },
    computed: {
        unusedWorkshops: function () {
            return this.plan.workshops.filter(ws => !ws.room && !ws.timeslot)
        }
        // timestamp_string: function () {
        //     return this.timestamp.getDate() + "." + this.timestamp.getMonth() + "." + this.timestamp.getFullYear() + ", "
        //         + this.timestamp.getHours() + ":" + String(this.timestamp.getMinutes()).padStart(2, "0");
        // }
    },
    mounted() {
        axios
            .post("/rest/plan/get")
            .then(function (response) {
                content.plan = response.data;
                for (let i = 0; i < content.plan.workshops.length; i++) {
                    content.plan.workshops[i].possibleTimeslots = content.plan.workshops[i].possibleTimeslots.sort((a, b) => a.name.localeCompare(b.name))
                }
            })
    },
    methods: {
        savePlan: function () {
            axios
                .post("/rest/plan/save", content.plan)
                .then(function (response) {
                    // content.timestamp = new Date(response.data);
                    alert("Planung gespeichert");
                })
        },
        optimizePlan: function () {
            axios
                .post("/rest/plan/optimize")
                .then(function (response) {
                    content.isSolving = response.data !== "NOT_SOLVING";
                    solverStatusInterval = setInterval(getSolverStatus, 1000);
                })
        },
        stopPlanning: function () {
            clearInterval(solverStatusInterval);
            content.time = 10;
            axios
                .post("/rest/plan/stop")
                .then(function (response) {
                    content.isSolving = response.data !== "NOT_SOLVING";
                })
        },
        inArray: function (element, array) {
            return $.inArray(element, array);
        }
    }
})



// other functions

function dragFromList (ev) {
    // console.log("Start ID: " + ev.target.id)
    ev.dataTransfer.setData("text", ev.target.id);
}

function dragFromTable (ev) {
    ev.dataTransfer.setData("text", ev.target.id + ":" + ev.target.parentElement.id);
}

function dropInTable (ev) {
    ev.preventDefault();

    if (!ev.target.id.includes(":")) {
        // this is the case if the new workshop is dropped on the existing one
        return;
    }

    let [slotID, roomID] = ev.target.id.split(':');
    if (content.plan.workshops.filter(ws => ws.room && ws.room.id == roomID && ws.timeslot && ws.timeslot.id == slotID).length > 0) {
        return; // do nothing if there is already a workshop at this position; is called if new workshop is dropped in td but not on old workshop in this td
    }

    // check if room is not available in this slot
    if ($.inArray(parseInt(slotID), content.plan.rooms.filter(room => room.id == roomID)[0].timeslots.map(slot => slot.id)) < 0) {
        console.log("Room not available.")
        return;
    }

    let data = ev.dataTransfer.getData("text");
    if (!data.includes(":")) { // true if origin of dropped item is the list of unused workshops
        let workshop = content.plan.workshops.find(ws => ws.id == data);
        workshop.room = content.plan.rooms.find(r => r.id == roomID);
        workshop.timeslot = content.plan.timeslots.find(t => t.id == slotID);
    }
    else {
        data = data.split(':');
        let workshop = content.plan.workshops.find(ws => ws.id == data[0]);
        workshop.room = content.plan.rooms.find(r => r.id == roomID);
        workshop.timeslot = content.plan.timeslots.find(t => t.id == slotID);
    }
    content.$forceUpdate();
}

function dropInList (ev) {
    ev.preventDefault();
    let data = ev.dataTransfer.getData("text").split(':'); // 0: workshopID, 1: slotID, 2: roomID
    // check if workshop already in list => if yes do nothing
    let workshop = content.plan.workshops.find(ws => ws.id == data[0]);
    workshop.room = null;
    workshop.timeslot = null;
    content.$forceUpdate();
}

function containsWorkshop(nestedMap, workshopID) {
    for (let value of nestedMap.values()) {
        for (let otherValue of value.values()) {
            if (otherValue === workshopID) {
                return true;
            }
        }
    }
    return false;
}

function connect() {

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    // stompClient.debug = () => {};

    stompClient.connect({}, onConnected, onError);
}


function onPlanMessageReceived(payload) {
    content.plan = JSON.parse(payload.body);
    for (let i = 0; i < content.plan.workshops.length; i++) {
        content.plan.workshops[i].possibleTimeslots = content.plan.workshops[i].possibleTimeslots.sort((a, b) => a.name.localeCompare(b.name))
    }
}

function onConnected() {
    stompClient.subscribe('/topic/plan', onPlanMessageReceived);
}


function onError(error) {
    console.log("Websocket: Could not connect to server.")
}

function getSolverStatus() {
    content.time -= 1;
    axios
        .post("/rest/plan/getStatus")
        .then(function (response) {
            content.isSolving = response.data !== "NOT_SOLVING";
        })
    if (!content.isSolving) {
        content.time = 10;
        clearInterval(solverStatusInterval);
    }
}
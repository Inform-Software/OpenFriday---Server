ensureAdmin();

let content = new Vue({
    el: "#content",
    data: {
        slots: "",
        rooms: "",
        workshops: "",
        plan: new Map(),
        unusedWorkshops: ""
    },
    mounted() {
        axios
            .post("/rest/slot/getall")
            .then(function (response) {
                content.slots = response.data;
                for (let i = 0; i < content.slots.length; i++) {
                    content.plan.set(content.slots[i].id, new Map());
                }
            })
        axios
            .post("/rest/room/getall")
            .then(function (response) {
                content.rooms = response.data;
            })
        axios
            .post("/rest/workshop/getall")
            .then(function (response) {
                content.workshops = content.unusedWorkshops = response.data;
            })
    },
    methods: {

    }
})



// other functions

function dragFromList (ev) {
    console.log("Start ID: " + ev.target.id)
    ev.dataTransfer.setData("text", ev.target.id);
}

function dragFromTable (ev) {
    ev.dataTransfer.setData("text", ev.target.id + ":" + ev.target.parentElement.id);
}

function dropInTable (ev) {
    ev.preventDefault();

    let keys = ev.target.id.split(':');
    if (content.plan.get(keys[0]).has[keys[1]]) {
        return; // do nothing if there is already a workshop at this position
    }

    let data = ev.dataTransfer.getData("text");
    if (!data.includes(":")) { // true if origin of dropped item is the list of unused workshops
        content.unusedWorkshops = content.unusedWorkshops.filter(ws => ws.id !== data); // remove workshop from unused workshops

        // content.plan.set(keys[0], content.plan.get(keys[0]).set(keys[1], data)); // add workshop to table
        content.plan.get(keys[0]).set(keys[1], data);
    }
    else {
        data = data.split(':');
        content.plan.get(data[1]).delete(data[2]); // remove workshop from plan
        // content.plan.set(keys[0], content.plan.get(keys[0]).set(keys[1], data[0])); // add workshop to plan
        content.plan.get(keys[0]).set(keys[1], data[0]);
        content.$forceUpdate();
    }
}

function dropInList (ev) {
    ev.preventDefault();
    let data = ev.dataTransfer.getData("text").split(':'); // 0: workshopID, 1: slotID, 2: roomID
    // check if workshop already in list => if yes do nothing
    if (content.unusedWorkshops.filter(ws => ws.id === data[0]).length) {
        return;
    }

    let tmp = content.workshops.filter(ws => ws.id === data[0])[0]; // add workshop to unused workshops
    content.unusedWorkshops.push(tmp);

    // content.plan.set(data[1], content.plan.get(data[1]).delete(data[2])); // remove workshop from table
    content.plan.get(data[1]).delete(data[2]);
    // ev.target.appendChild(document.getElementById(data));
}